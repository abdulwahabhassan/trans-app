package ng.gov.imostate.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.Mapper
import ng.gov.imostate.R
import ng.gov.imostate.databinding.FragmentOutStandingPaymentBinding
import ng.gov.imostate.model.domain.TransactionData
import ng.gov.imostate.printer.AsyncBluetoothEscPosPrint
import ng.gov.imostate.printer.AsyncEscPosPrint
import ng.gov.imostate.printer.AsyncEscPosPrinter
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import ng.gov.imostate.viewmodel.OutstandingPaymentFragmentViewModel
import timber.log.Timber
import www.sanju.motiontoast.MotionToastStyle
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class OutStandingPaymentFragment : Fragment() {
    private var _binding: FragmentOutStandingPaymentBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    lateinit var viewModel: OutstandingPaymentFragmentViewModel
    var selectedDevice: BluetoothConnection? = null
    var driverName: String? = ""
    private var vehicleCategory: String? = ""
    private var lastPaymentDate: String? = ""
    var vehiclePlatesNumber: String? = ""
    var vehicleId: String? = ""
    var dateFrom: String? = ""
    var dateTo: String? = ""
    var outstandingBalance: Double? = 0.00
    var amountToPay: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOutStandingPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, appViewModelFactory).get(OutstandingPaymentFragmentViewModel::class.java)

        driverName = arguments?.getString(MainActivity.DRIVER_NAME_KEY)
        vehiclePlatesNumber = arguments?.getString(MainActivity.VEHICLE_PLATES_NUMBER_KEY)
        vehicleCategory = arguments?.getString(MainActivity.VEHICLE_CATEGORY_KEY)
        lastPaymentDate = arguments?.getString(MainActivity.LAST_PAYMENT_DATE_KEY)
        vehicleId = arguments?.getString(MainActivity.VEHICLE_ID_NUMBER_KEY)
        dateFrom = arguments?.getString(NfcReaderResultFragment.DATE_FROM_KEY)
        dateTo = arguments?.getString(NfcReaderResultFragment.DATE_TO_KEY)
        outstandingBalance = arguments?.getDouble(NfcReaderResultFragment.OUTSTANDING_BALANCE)


        with(binding) {

            if (outstandingBalance == 0.00) {
                outstandingBalanceTV.setBackgroundResource(R.drawable.balance_text_green_bg)
                outstandingBalanceTV.setTextColor(getResources().getColor(R.color.green))
            } else {
                outstandingBalanceTV.setBackgroundResource(R.drawable.balance_text_red_bg)
                outstandingBalanceTV.setTextColor(getResources().getColor(R.color.red))
            }
            outstandingBalanceTV.text = "Outstanding balance ₦${AppUtils.formatCurrency(outstandingBalance.toString())}"

            //format dates
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val dateToPayFrom = dateFrom?.let { sdf.parse(it) }
            val dateToPayTo = dateTo?.let { sdf.parse(it) }

            //calculate number of selected days from first selected date to second selected date
            val daysDifference = dateToPayTo?.time!! - dateToPayFrom?.time!!
            val numOfSelectedDays = TimeUnit.DAYS.convert(daysDifference, TimeUnit.MILLISECONDS)

            Timber.d("selected start pay date: $dateToPayFrom")
            Timber.d("selected end pay date: $dateToPayTo")
            Timber.d("number of days selected: $numOfSelectedDays")

            //array to hold valid days
            val validDaysArray = arrayListOf<String>()

            numOfSelectedDays.downTo(0).forEach {
                val date = LocalDate.parse(dateTo).minusDays(it)
                    .format(DateTimeFormatter.ofPattern("E, dd MMM yyyy", Locale.UK))
                Timber.d("numOfDays: $it date: ${date}")
                //exempt saturdays and sundays
                if (!date.contains("Sat", true) && !date.contains("Sun", true)) {
                    validDaysArray.add(date)
                }
            }
            Timber.d("valid days array: $validDaysArray")

            val numOfEligibleDaysOwed = validDaysArray.size

            viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                val rate = vehicleCategory?.let { viewModel.getRateInDatabase(it) }
                if (rate?.amount != null) {
                    amountToPay = rate.amount.toDouble().times(numOfEligibleDaysOwed.toDouble())
                    amountToPayTV.text = "₦${AppUtils.formatCurrency(amountToPay.toString())}"
                }
                periodTV.text = "${AppUtils.formatDateToFullDate(dateFrom ?: "")} - ${AppUtils.formatDateToFullDate(dateTo ?: "")}"
            }


            backArrowIV.setOnClickListener {
                findNavController().popBackStack()
            }

            markAsPaidBTN.setOnClickListener {
                if (amountToPay != 0.00) {
                    viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                        //verify that agent has enough balance left to collect payment from vehicle
                        val lastSyncBalance = viewModel.getInitialUserPreferences().currentWalletBalance
                        val transactions = Mapper.mapListOfTransactionEntityToListOfTransaction(
                            viewModel.getAllTransactionsInDatabase()
                        )
                        val totalOfflineTransactionAmount = transactions.sumOf { it.amount ?: 0.00 }
                        val actualBalance = lastSyncBalance - totalOfflineTransactionAmount
                        Timber.d("lastSyncBalance: $lastSyncBalance " +
                                "totalOfflineTransactionAmount: $totalOfflineTransactionAmount " +
                                "actualBalance: $actualBalance"
                        )
                        //if balance is more than amount to be paid by vehicle, allow the payment,
                        //otherwise deny it
                        if (actualBalance > amountToPay) {
                            if (getBluetoothDevice() != null) {
                                //do payment and navigate to success fragment with vehicle unique identifier
                                AppUtils.showProgressIndicator(true, binding.progressIndicator)
                                AppUtils.showView(false, binding.markAsPaidBTN)
                                if (vehicleId != null) {
                                    //insert/update newly created transaction to app's database
                                    //to be synced later to cloud database/server
                                        val transaction =
                                            viewModel.getTransactionInDatabase(vehicleId!!)
                                                ?.let { transactionEntity ->
                                                    Mapper.mapTransactionEntityToTransaction(
                                                        transactionEntity
                                                    )
                                                }
                                    Timber.d("$transaction")
                                    //if transaction already exists in database, update it else
                                    //insert a new transaction
                                    if (transaction != null) {
                                        //update the current 'amount' and 'date to' in database
                                        val transactionData = transaction.copy(
                                            to = dateTo,
                                            amount = (transaction.amount ?: 0.00) + amountToPay
                                        )
                                        viewModel.insertTransactionToDatabase(transactionData)
                                    } else {
                                        viewModel.insertTransactionToDatabase(
                                            TransactionData(vehicleId, dateTo, amountToPay)
                                        )
                                    }

                                    printBluetooth()
                                    val action = OutStandingPaymentFragmentDirections
                                        .actionOutStandingPaymentFragmentToSuccessFragment(
                                            vehicleId!!,
                                            vehiclePlatesNumber,
                                            driverName,
                                            dateTo,
                                            vehicleCategory.toString()
                                        )
                                    findNavController().navigate(action)
                                } else {
                                    AppUtils.showToast(
                                        requireActivity(),
                                        "Vehicle ID is unknown",
                                        MotionToastStyle.ERROR
                                    )
                                }
                            } else {
                                val alertDialog = AlertDialog.Builder(requireContext())
                                alertDialog.setTitle("No printer found")
                                alertDialog.setMessage("Check that bluetooth is on")
                                alertDialog.setNegativeButton("OK") { p0, _ -> p0?.dismiss() }
                                val alert = alertDialog.create()
                                alert.setCanceledOnTouchOutside(false)
                                alert.show()
                            }
                        } else {
                            AppUtils.showToast(
                                requireActivity(),
                                "Insufficient wallet balance, fund your wallet",
                                MotionToastStyle.ERROR
                            )
                        }
                    }
                } else {
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Invalid amount")
                    alertDialog.setMessage("This amount is invalid")
                    alertDialog.setNegativeButton("OK") { p0, _ -> p0?.dismiss() }
                    val alert = alertDialog.create()
                    alert.setCanceledOnTouchOutside(false)
                    alert.show()
                }

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                TransactionsFragment.PERMISSION_BLUETOOTH,
                TransactionsFragment.PERMISSION_BLUETOOTH_ADMIN,
                TransactionsFragment.PERMISSION_BLUETOOTH_CONNECT,
                TransactionsFragment.PERMISSION_BLUETOOTH_SCAN ->
                    printBluetooth()
            }
        }
    }

    private fun getBluetoothDevice(): BluetoothConnection? {
        val bluetoothDevicesList = BluetoothPrintersConnections().list
        return if (bluetoothDevicesList?.isNullOrEmpty() == false) {
            selectedDevice = bluetoothDevicesList[0]
            selectedDevice
        } else {
            selectedDevice
        }
    }

    private fun printBluetooth() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH),
                TransactionsFragment.PERMISSION_BLUETOOTH
            )
        } else if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                TransactionsFragment.PERMISSION_BLUETOOTH_ADMIN
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                TransactionsFragment.PERMISSION_BLUETOOTH_CONNECT
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                TransactionsFragment.PERMISSION_BLUETOOTH_SCAN
            )
        } else {
            AsyncBluetoothEscPosPrint(
                requireContext(),
                object : AsyncEscPosPrint.OnPrintFinished() {
                    override fun onError(asyncEscPosPrinter: AsyncEscPosPrinter?, codeException: Int) {
                        Timber.e("AsyncEscPosPrint.OnPrintFinished : An error occurred !")
                    }

                    override fun onSuccess(asyncEscPosPrinter: AsyncEscPosPrinter?) {
                        Timber.i("AsyncEscPosPrint.OnPrintFinished : Print is finished !")
                    }
                }
            )
                .execute(this.getAsyncEscPosPrinter(selectedDevice))
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getAsyncEscPosPrinter(printerConnection: DeviceConnection?): AsyncEscPosPrinter? {
        val format = SimpleDateFormat("'Date' yyyy-MM-dd 'Time' HH:mm:ss")
        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)
        return printer.addTextToPrint(
            """
            [C]<img>${
                PrinterTextParserImg.bitmapToHexadecimalString(
                    printer,
                    requireContext().getResources()
                        .getDrawableForDensity(
                            R.drawable.imo_logo,
                            DisplayMetrics.DENSITY_MEDIUM
                        )
                )
            }</img>
            [L]
            [C]<u><font size='big'>Tax Receipt</font></u>
            [L]
            [C]${format.format(Date())}
            [C]
            [C]================================
            [L]
            [L]<b>AMOUNT</b>[R]500.99 NGN
            [L]
            [L]<b>VAT</b>[R]10.00 NGN
            [L]
            [C]--------------------------------
            [R]TOTAL CHARGE :[R]510.99 NGN
            [R]TAX :[R]1.50 NGN
            [R]OUTSTANDING BAL : $vehicleCategory NGN
            [L]
            [C]================================
            [L]
            [C]<font color='bg-black'><b>Details</b></font>
            [L]
            [L]Vehicle ID: $vehicleId
            [L]Driver: $driverName
            
            """.trimIndent()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
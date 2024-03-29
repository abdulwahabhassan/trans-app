package ng.gov.imostate.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ng.gov.imostate.R
import ng.gov.imostate.adapter.CollectionsAdapter
import ng.gov.imostate.adapter.TransactionsAdapter
import ng.gov.imostate.databinding.FragmentTransactionsBinding
import ng.gov.imostate.databinding.LayoutCollectionDetailsBinding
import ng.gov.imostate.databinding.LayoutTransactionDetailsBinding
import ng.gov.imostate.model.domain.Collection
import ng.gov.imostate.model.domain.Transaction
import ng.gov.imostate.model.domain.TransactionType
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.printer.AsyncBluetoothEscPosPrint
import ng.gov.imostate.printer.AsyncEscPosPrint
import ng.gov.imostate.printer.AsyncEscPosPrinter
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import ng.gov.imostate.viewmodel.TransactionsFragmentViewModel
import timber.log.Timber
import www.sanju.motiontoast.MotionToastStyle
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TransactionsFragment : Fragment() {

    private val args: TransactionsFragmentArgs by navArgs()
    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var transactionsAdapter: TransactionsAdapter
    private lateinit var collectionsAdapter: CollectionsAdapter
    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    //view model
    private lateinit var viewModel: TransactionsFragmentViewModel
    var selectedDevice: BluetoothConnection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchView.queryHint = "Search Transactions"

        viewModel = ViewModelProvider(
            this,
            appViewModelFactory
        ).get(TransactionsFragmentViewModel::class.java)

        initRV()

        initUI()

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                when (args.transactionType) {
                    TransactionType.AGENT_TRANSACTION.name -> {
                        when (val result = viewModel.transactions.value) {
                            is ViewModelResult.Success -> {
                                val filteredTransactions = result.data.filter { transaction ->
                                    transaction.accountFrom?.contains(newText.toString(), true) == true ||
                                            transaction.accountTo?.contains(newText.toString(), true) == true ||
                                            transaction.internalReference?.contains(newText.toString(), true) == true ||
                                            transaction.vehicleFrom?.vehiclePlates?.contains(newText.toString(), true) == true ||
                                            transaction.createdAt?.substring(0, 10)?.contains(newText.toString(), true) == true ||
                                            transaction.amount?.contains(newText.toString(), true) == true
                                }
                                transactionsAdapter.submitList(filteredTransactions)
                            }
                            else -> {}
                        }
                    }
                    TransactionType.VEHICLE_TRANSACTION.name -> {
                        when (val result = viewModel.collections.value) {
                            is ViewModelResult.Success -> {
                                val filteredCollection = result.data.filter { collection ->
                                    collection.amount?.contains(newText.toString(), true) == true ||
                                    collection.status?.contains(newText.toString(), true) == true ||
                                    collection.vehicleID.toString().contains(newText.toString(), true) ||
                                    collection.date.toString().contains(newText.toString(), true) ||
                                    collection.internalReference.toString().contains(newText.toString(), true)
                                }
                                collectionsAdapter.submitList(filteredCollection)
                            }
                        }
                    }

                }
                return true
            }

        })

        binding.buttonBluetoothBrowse.setOnClickListener {
            browseBluetoothDevice()
        }
        binding.generateLastReceiptBTN.setOnClickListener {
            printBluetooth()
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
                PERMISSION_BLUETOOTH,
                PERMISSION_BLUETOOTH_ADMIN,
                PERMISSION_BLUETOOTH_CONNECT,
                PERMISSION_BLUETOOTH_SCAN ->
                    printBluetooth()
            }
        }
    }

    private fun browseBluetoothDevice() {
        val bluetoothDevicesList = BluetoothPrintersConnections().list
        if (bluetoothDevicesList != null) {
            val items = arrayOfNulls<String>(bluetoothDevicesList.size + 1)
            items[0] = "Default printer"
            var i = 0
            for (device in bluetoothDevicesList) {
                items[++i] = device.device.name
            }
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Bluetooth printer selection")
            alertDialog.setItems(
                items
            ) { dialogInterface, i ->
                val index = i - 1
                selectedDevice = if (index == -1) {
                    null
                } else {
                    bluetoothDevicesList[index]
                }
            }
            val alert = alertDialog.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()
        } else {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("No printer found")
            alertDialog.setMessage("Check that bluetooth is on")
            alertDialog.setNegativeButton("OK") { p0, _ -> p0?.dismiss() }
            val alert = alertDialog.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()
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
                PERMISSION_BLUETOOTH
            )
        } else if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                PERMISSION_BLUETOOTH_ADMIN
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                PERMISSION_BLUETOOTH_CONNECT
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                PERMISSION_BLUETOOTH_SCAN
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

        var textToPrint = ""

        when (args.transactionType) {
            TransactionType.AGENT_TRANSACTION.name -> {
                val transaction = transactionsAdapter.currentList[0]
                textToPrint = """
            [C]<img>${
                    PrinterTextParserImg.bitmapToHexadecimalString(
                        printer,
                        requireContext().getResources()
                            .getDrawableForDensity(
                                R.drawable.imo_logo,
                                DisplayMetrics.DENSITY_LOW
                            )
                    )
                }</img>
            [L]
            [C]<b><font size='normal'>RECEIPT (Reprint)</font></b>
            [L]
            [C]${format.format(Date())}
            [C]
            [C]================================
            [L]
            [L]INTERNAL REF [R]${transaction.internalReference}
            [L]AMOUNT [R]${transaction.amount} NGN
            [L]DATE [R]${transaction.createdAt?.substring(0, 10)
                    ?.let { AppUtils.formatDateToFullDate(it) }}
            [L]SENDER [R]${transaction.accountFrom ?: "-"}
            [L]RECIPIENT [R]${transaction.accountTo ?: "-"}
            ${if (transaction.vehicleFrom != null) {"""[L]VEHICLE [R]${transaction.vehicleFrom.vehiclePlates ?: "-"}
            [L]DRIVER [R]${transaction.vehicleFrom.driverName ?: "-"}"""} else { "" }}
            [L]
            [C]================================
            [L]
            """.trimIndent()

            }

            TransactionType.VEHICLE_TRANSACTION.name -> {
                val collection = collectionsAdapter.currentList[0]
                textToPrint = """
            [C]<img>${
                    PrinterTextParserImg.bitmapToHexadecimalString(
                        printer,
                        requireContext().getResources()
                            .getDrawableForDensity(
                                R.drawable.imo_logo,
                                DisplayMetrics.DENSITY_LOW
                            )
                    )
                }</img>
            [L]
            [C]<b><font size='normal'>RECEIPT (Reprint)</font></b>
            [L]
            [C]${format.format(Date())}
            [C]
            [C]================================
            [L]
            [L]TRANSACTION ID [R]${collection.transactionID ?: "-"}
            [L]INTERNAL REF [R]${collection.internalReference}
            [L]AMOUNT [R]${collection.amount} NGN
            [L]STATUS [R]${collection.status ?: "-"}
            [L]DATE [R]${collection.date?.let { AppUtils.formatDateToFullDate(it) }}
            [L]VEHICLE ID [R]${collection.vehicleID ?: "-"}
            [L]DAYS [R]${collection.daysCount ?: "-"}
            [L]
            [C]================================
            [L]
            """.trimIndent()
            }

        }

        return printer.addTextToPrint(textToPrint)
    }

    private fun initRV() {
        when (args.transactionType) {
            TransactionType.AGENT_TRANSACTION.name -> {
                transactionsAdapter = TransactionsAdapter{ position: Int, transaction: Transaction ->
                    showTransactionDetailsDialog(transaction)
                }
                binding.transactionsRV.adapter = transactionsAdapter
            }
            TransactionType.VEHICLE_TRANSACTION.name -> {
                collectionsAdapter = CollectionsAdapter{ position: Int, collection: Collection ->
                    showCollectionDetailsDialog(collection)
                }
                binding.transactionsRV.adapter = collectionsAdapter
            }
        }
    }

    //show bottomSheetDialog to select transaction type from options
    private fun showCollectionDetailsDialog(collection: Collection){

        val binding = LayoutCollectionDetailsBinding.inflate(
            LayoutInflater.from(requireContext()),
            this.binding.root,
            false
        )

        val transactionDetailsSheet = BottomSheetDialog(requireContext())
        transactionDetailsSheet.setContentView(binding.root)
        transactionDetailsSheet.dismissWithAnimation = true

        if (transactionDetailsSheet.isShowing) {
            transactionDetailsSheet.dismiss()
        }
        transactionDetailsSheet.show()

        with(binding) {
            binding.doneBTN.setOnClickListener {
                transactionDetailsSheet.dismiss()
            }
            internalRefTV.text = collection.internalReference
            statusTV.text = collection.status
            amountTV.text = collection.amount
            dateTV.text = collection.date?.let { AppUtils.formatDateToFullDate(it) }
            daysCountTV.text = collection.daysCount
        }

    }

    //show bottomSheetDialog to select transaction type from options
    private fun showTransactionDetailsDialog(transaction: Transaction){

        val binding = LayoutTransactionDetailsBinding.inflate(
            LayoutInflater.from(requireContext()),
            this.binding.root,
            false
        )

        val transactionDetailsSheet = BottomSheetDialog(requireContext())
        transactionDetailsSheet.setContentView(binding.root)
        transactionDetailsSheet.dismissWithAnimation = true

        if (transactionDetailsSheet.isShowing) {
            transactionDetailsSheet.dismiss()
        }
        transactionDetailsSheet.show()

        with(binding) {
            binding.doneBTN.setOnClickListener {
                transactionDetailsSheet.dismiss()
            }
            if (transaction.vehicleFrom != null) {
                //vehicle transaction
                senderTV.text = transaction.vehicleFrom?.driverName
                receiverTV.text = transaction.accountTo
                amountTV.text = transaction.amount
                dateTV.text = transaction.createdAt?.substring(0, 10)
                    ?.let { AppUtils.formatDateToFullDate(it) }
                vehiclePlatesTV.text = transaction.vehicleFrom?.vehiclePlates
            } else {
                //vendor transaction
                senderTV.text = transaction.accountFrom
                receiverTV.text = transaction.accountTo
                amountTV.text = transaction.amount?.let { AppUtils.formatCurrency(it) }
                dateTV.text = transaction.createdAt?.substring(0, 10)
                    ?.let { AppUtils.formatDateToFullDate(it) }
                vehiclePlatesLabelTV.visibility = GONE
                vehiclePlatesTV.visibility = GONE
            }
        }
    }

    private fun initUI() {
        when (args.transactionType) {
            TransactionType.AGENT_TRANSACTION.name -> {
                viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        AppUtils.showView(false, binding.transactionsRV)
                        AppUtils.showProgressIndicator(true, binding.progressIndicator)
                        viewModel.transactions.collect { viewModelResult ->
                            when (viewModelResult) {
                                is ViewModelResult.Success -> {
                                    Timber.d("${viewModelResult.data}")
                                    AppUtils.showProgressIndicator(false, binding.progressIndicator)
                                    if (viewModelResult.data.isNotEmpty()) {
                                        transactionsAdapter.submitList(viewModelResult.data)
                                        AppUtils.showView(true, binding.transactionsRV)
                                        AppUtils.showView(true, binding.generateLastReceiptBTN)
                                        AppUtils.showView(false, binding.noTransactionTV)
                                    } else {
                                        AppUtils.showView(true, binding.noTransactionTV)
                                    }
                                }
                                is ViewModelResult.Error -> {
                                    AppUtils.showToast(requireActivity(), viewModelResult.errorMessage, MotionToastStyle.ERROR)
                                    AppUtils.showView(false, binding.transactionsRV)
                                    AppUtils.showView(false, binding.generateLastReceiptBTN)
                                    AppUtils.showProgressIndicator(false, binding.progressIndicator)
                                    AppUtils.showView(true, binding.noTransactionTV)
                                }
                                is ViewModelResult.Loading -> {
                                    AppUtils.showProgressIndicator(true, binding.progressIndicator)
                                }
                            }
                        }
                    }
                }
            }
            TransactionType.VEHICLE_TRANSACTION.name -> {
                viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        AppUtils.showView(false, binding.transactionsRV)
                        AppUtils.showProgressIndicator(true, binding.progressIndicator)
                        when (val viewModelResult = viewModel.getInitialUserPreferences().token?.let { token ->
                            viewModel.getVehicle(token, args.vehicleId.toString()) }!!
                        ) {
                            is ViewModelResult.Success -> {
                                Timber.d("${viewModelResult.data?.vehicle?.collection}")
                                if (viewModelResult.data?.vehicle?.collection?.isEmpty() == true) {
                                    AppUtils.showToast(requireActivity(), "No transaction record", MotionToastStyle.INFO)
                                    AppUtils.showView(true, binding.noTransactionTV)
                                } else {
                                    collectionsAdapter.submitList(viewModelResult.data?.vehicle?.collection)
                                    AppUtils.showView(false, binding.noTransactionTV)
                                    AppUtils.showView(true, binding.transactionsRV)
                                    AppUtils.showView(true, binding.generateLastReceiptBTN)
                                }
                            }
                            is ViewModelResult.Error -> {
                                AppUtils.showToast(requireActivity(), viewModelResult.errorMessage, MotionToastStyle.ERROR)
                                AppUtils.showView(false, binding.transactionsRV)
                                AppUtils.showView(false, binding.generateLastReceiptBTN)
                                AppUtils.showView(true, binding.noTransactionTV)
                            }
                        }
                        AppUtils.showProgressIndicator(false, binding.progressIndicator)
                    }
                }
            }
        }

        with(binding) {
            backArrowIV.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val PERMISSION_BLUETOOTH = 1
        const val PERMISSION_BLUETOOTH_ADMIN = 2
        const val PERMISSION_BLUETOOTH_CONNECT = 3
        const val PERMISSION_BLUETOOTH_SCAN = 4
    }

}
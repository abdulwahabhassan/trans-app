package ng.gov.imostate.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.R
import ng.gov.imostate.databinding.FragmentNfcReaderResultBinding
import ng.gov.imostate.model.domain.Data
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import ng.gov.imostate.viewmodel.NfcReaderResultFragmentViewModel
import ng.gov.imostate.viewmodel.SharedNfcViewModel
import timber.log.Timber
import www.sanju.motiontoast.MotionToastStyle
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class NfcReaderResultFragment : Fragment() {

    private var _binding: FragmentNfcReaderResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var data: Data
    private val sharedNfcViewModel: SharedNfcViewModel by activityViewModels()
    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    //view model
    private lateinit var viewModel: NfcReaderResultFragmentViewModel
    var outstandingBalance: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = Data(
            arguments?.getString(MainActivity.DRIVER_NAME_KEY) ?: "",
            arguments?.getString(MainActivity.VEHICLE_ID_NUMBER_KEY) ?: "",
            arguments?.getString(MainActivity.LAST_PAYMENT_DATE_KEY)?: "",
            arguments?.getString(MainActivity.VEHICLE_CATEGORY_KEY) ?: "",
            arguments?.getString(MainActivity.VEHICLE_PLATES_NUMBER_KEY) ?: ""
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNfcReaderResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n", "NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            appViewModelFactory
        ).get(NfcReaderResultFragmentViewModel::class.java)

        with(binding) {
            payOutstandingBTN.setOnClickListener {
                showDatePicker()
            }
            backArrowIV.setOnClickListener {
                findNavController().popBackStack()
            }

            outstandingBalancePeriodTV.text = "${AppUtils.formatDateToFullDate(data.lpd)} - ${AppUtils.getCurrentFullDate()}"

            Timber.d("$data")

            driverTV.text = data.name
            vehiclePlatesTV.text = data.vpn
            lastPaymentDateTV.text = AppUtils.formatDateToFullDate(data.lpd)

            val dateStr = data.lpd
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val lastPayDate = sdf.parse(dateStr)

            val currentDate = sdf.parse(AppUtils.getCurrentDate())

            val daysDifference = currentDate?.time!! - lastPayDate?.time!!
            val numOfDaysSinceLastPaid = TimeUnit.DAYS.convert(daysDifference, TimeUnit.MILLISECONDS) - 1 //minus one to exempt last pay date

            Timber.d("last pay date: $lastPayDate")
            Timber.d("current date: $currentDate")
            Timber.d("number of days since last paid: $numOfDaysSinceLastPaid")

            val daysOwedArray = arrayListOf<String>()

            numOfDaysSinceLastPaid.downTo(0).forEach {
                val date = LocalDate.now(ZoneId.of("Africa/Lagos")).minusDays(it)
                    .format(DateTimeFormatter.ofPattern("E, dd MMM yyyy", Locale.UK))
                Timber.d("numOfDays: $it date: ${date}")
                if (!date.contains("Sat", true) && !date.contains("Sun", true)) {
                    //exempt saturdays and sundays
                    daysOwedArray.add(date)
                }
            }
            Timber.d("$daysOwedArray")

            val numOfEligibleDaysOwed = daysOwedArray.size
            Timber.d("${daysOwedArray.size}")

            viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                val rate = viewModel.getRateInDatabase(data.vc)
                if (rate?.amount != null) {
                    outstandingBalance = rate.amount.toDouble().times(numOfEligibleDaysOwed.toDouble())
                    if (outstandingBalance == 0.00) {
                        outstandingBalanceTV.setBackgroundResource(R.drawable.balance_text_green_bg)
                        outstandingBalanceTV.setTextColor(getResources().getColor(R.color.green))
                        lastPaymentDateTV.setTextColor(getResources().getColor(R.color.green))
                        payOutstandingBTN.visibility = INVISIBLE
                        outstandingBalancePeriodTV.visibility = INVISIBLE
                    } else {
                        outstandingBalanceTV.setBackgroundResource(R.drawable.balance_text_red_bg)
                        outstandingBalanceTV.setTextColor(getResources().getColor(R.color.red))
                        lastPaymentDateTV.setTextColor(getResources().getColor(R.color.red))
                        payOutstandingBTN.visibility = VISIBLE
                        outstandingBalancePeriodTV.visibility = VISIBLE
                    }
                    outstandingBalanceTV.text = "Outstanding Balance: ₦${AppUtils.formatCurrency(outstandingBalance)}"
                    outstandingBalanceTV.visibility = VISIBLE
                } else {
                    AppUtils.showToast(requireActivity(), "Rate is unavailable", MotionToastStyle.ERROR)
                }

            }


        }

    }

    private fun showDatePicker() {
        val builder : MaterialDatePicker.Builder<Pair<Long, Long>> =
            MaterialDatePicker.Builder.dateRangePicker()

        builder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        builder.setTitleText("HOLIDAYS WILL BE EXCLUDED")
        val calendar = Calendar.getInstance()

        //format last payment date
        val dateStr = data.lpd
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val lastPayDate = sdf.parse(dateStr)

        //set selected dates to reflect outstanding days since the last payment, exempt the last
        //paid date from the selection by adding (86400000 * 2) milliseconds (2 days) to
        //the last paid date so that the first selection is the next day after the last paid date
        val startDate = lastPayDate?.time?.plus(86400000 * 2)
        val endDate = Date().time

        Timber.d("start date: $startDate end date: $endDate calendarMilli: ${calendar.timeInMillis}")

        builder.setSelection(Pair(startDate, calendar.timeInMillis))
        val picker = builder.build()
        picker.show(childFragmentManager, picker.toString())

        picker.addOnPositiveButtonClickListener {

            //declare variables to hold selected dates
            val dateFrom: String
            val dateTo: String

            //set selected dates
            val first: Long? = it.first
            val firstDate = Date(first!!)
            val second: Long? = it.second
            val secondDate = Date(second!!)

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            //if selected first date does not match actual/valid start date to begin payment from,
            //show invalid date selection dialog
            Timber.d("first: ${firstDate.time}")
            val selectedStartDate = sdf.format(firstDate.time)
            val compulsoryStartDate = sdf.format(startDate?.minus(86400000)) //minus one day to match actual start date
            Timber.d("required start date: $compulsoryStartDate selected start date: $selectedStartDate")

            //if selected second date does not match actual/valid end date to make payment
            //show invalid date selection dialog
            Timber.d("first: ${secondDate.time}")
            val selectedEndDate = sdf.format(secondDate.time)
            val compulsoryEndDate = sdf.format(endDate)
            Timber.d("required end date: $compulsoryEndDate selected end date: $selectedEndDate")

            when {
                selectedStartDate != compulsoryStartDate -> {
                    showInvalidStartDateDialog(compulsoryStartDate)
                }
                selectedEndDate > compulsoryEndDate -> {
                    showInvalidEndDateDialog(compulsoryEndDate)
                }
                else -> {
                    //format selected dates
                    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    dateFrom = format.format(firstDate)
                    dateTo = format.format(secondDate)

                    Timber.d("dateFrom: $dateFrom dateTO: $dateTo")

                    val bundle = Bundle().also { bundle ->
                        bundle.putString(MainActivity.DRIVER_NAME_KEY, data.name)
                        bundle.putString(MainActivity.VEHICLE_ID_NUMBER_KEY, data.vid)
                        bundle.putString(MainActivity.LAST_PAYMENT_DATE_KEY, data.lpd)
                        bundle.putString(MainActivity.VEHICLE_CATEGORY_KEY, data.vc)
                        bundle.putString(MainActivity.VEHICLE_PLATES_NUMBER_KEY, data.vpn)
                        bundle.putDouble(OUTSTANDING_BALANCE, outstandingBalance)
                        bundle.putString(DATE_FROM_KEY, dateFrom)
                        bundle.putString(DATE_TO_KEY, dateTo)
                    }

                    //do api call
                    findNavController().navigate(R.id.outStandingPaymentFragment, bundle,
                        NavOptions.Builder().setLaunchSingleTop(true).build())
                }
            }
        }
    }

    private fun showInvalidEndDateDialog(compulsoryEndDate: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Invalid date selection")
            .setMessage("Valid date selection must not exceed today (${AppUtils.formatDateToFullDate(compulsoryEndDate)})")
            .setNegativeButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun showInvalidStartDateDialog(compulsoryStartDate: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Invalid date selection")
            .setMessage("Valid date selection must begin from the next day (${AppUtils.formatDateToFullDate(compulsoryStartDate)}) after last payment")
            .setNegativeButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val DATE_FROM_KEY = "dateFrom"
        const val DATE_TO_KEY = "dateTo"
        const val OUTSTANDING_BALANCE = "outstandingBalance"
    }

}
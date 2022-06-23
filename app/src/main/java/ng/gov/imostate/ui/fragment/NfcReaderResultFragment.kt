package ng.gov.imostate.ui.fragment

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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.*
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.Mapper
import ng.gov.imostate.R
import ng.gov.imostate.database.entity.VehicleCurrentEntity
import ng.gov.imostate.databinding.FragmentNfcReaderResultBinding
import ng.gov.imostate.model.domain.Data
import ng.gov.imostate.ui.activity.MainActivity
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import ng.gov.imostate.viewmodel.NfcReaderResultFragmentViewModel
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
    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    //view model
    private lateinit var viewModel: NfcReaderResultFragmentViewModel
    var outstandingBalance: Double = 0.00
    private val sdfUtc: SimpleDateFormat
    get() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        //dates used with MaterialDatePicker must be formatted to UTC time zone
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat
    }
    private val sdfLocal: SimpleDateFormat
        get() {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            //dates used with MaterialDatePicker must be formatted to UTC time zone
            dateFormat.timeZone = TimeZone.getTimeZone("Africa/Lagos")
            return dateFormat
        }
    private var vehicleInDatabase: VehicleCurrentEntity?  = null

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = Data(
            arguments?.getString(MainActivity.VEHICLE_ID_NUMBER_KEY) ?: "",
            arguments?.getString(MainActivity.LAST_PAYMENT_DATE_KEY)?.let { lpd ->
                AppUtils.decryptLastPaidDate(lpd) } ?: "",
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

            binding.payOutstandingBTN.setOnClickListener {
                //add 1 day to current time of last paid date to exempt last paid date from current
                //payment
                val paymentStartDate = Date(sdfUtc.parse(data.lpd)?.time?.plus(TimeUnit.DAYS.toMillis(1))!!)
                goToOutstandingPaymentScreen(sdfLocal.format(paymentStartDate), AppUtils.getCurrentDate())
            }

            openCalendarTV.setOnClickListener {
                //ux
                AppUtils.showView(false, openCalendarTV)
                AppUtils.showProgressIndicator(true, progressIndicator)
                payOutstandingBTN.isEnabled = false
                viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                    //check first if agent is allowed to collect from outside routes
                    Timber.d("collection settings: ${viewModel.getInitialUserPreferences().collectionSetting}")
                    Timber.d("${viewModel.getInitialUserPreferences().collectionSetting}")
                    when (viewModel.getInitialUserPreferences().collectionSetting) {
                        "Yes" -> {
                            showDatePicker()
                        }
                        else -> {
                            //check if vehicle belongs to agent's route
                        //show picker if yes else don't show
                            vehicleInDatabase = viewModel.getVehicleFromCurrentEnumerationInDatabase(data.vpn)
                            Timber.d("vehicle in database $vehicleInDatabase")
                            if (vehicleInDatabase != null) {
                                val vehicle = Mapper.mapVehicleCurrentEntityToVehicle(
                                    vehicleInDatabase!!
                                )
                                val vehicleRoutes = vehicle.vehicleRoutes
                                Timber.d("vehicle routes $vehicleRoutes")
                                //intersect both lists of route ids and if the result contains at least one
                                //common element, show date picker
                                if (vehicleRoutes != null)  {
                                    val agentRoutes = Mapper.mapListOfAgentRouteEntityToListOfAgentRoute(
                                        viewModel.getAllAgentsRouteInDatabase()
                                    )
                                    Timber.d("agent routes $agentRoutes")

                                    val common = agentRoutes.map { it.routeId }
                                        .intersect(vehicleRoutes.map { it.routeID })
                                    Timber.d("common $common")
                                    if (common.isNotEmpty()) {
                                        showDatePicker()
                                    } else {
                                        if (viewModel.getInitialUserPreferences().collectionSetting.equals("Warn", true)) {
                                            AlertDialog.Builder(requireContext())
                                                .setTitle("Warning")
                                                .setMessage("This vehicle has no route that you are allowed to collect payment from")
                                                .setNegativeButton("Abort") { dialog, _ ->
                                                    dialog.dismiss()
                                                }
                                                .setPositiveButton("Continue") { dialog, _ ->
                                                    dialog.dismiss()
                                                    showDatePicker()
                                                }.show()
                                        } else if (viewModel.getInitialUserPreferences().collectionSetting.equals("No", true)) {
                                            showInCompatibleRoutes()
                                        }
                                    }
                                } else {
                                    AlertDialog.Builder(requireContext())
                                        .setTitle("No Routes Found")
                                        .setMessage("This vehicle has no route assigned")
                                        .setNegativeButton("Ok") { dialog, _ ->
                                            dialog.dismiss()
                                        }.show()
                                }
                            } else {
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Vehicle Not Found")
                                    .setMessage("vehicle not found in database")
                                    .setNegativeButton("Ok") { dialog, _ ->
                                        dialog.dismiss()
                                    }.show()
                            }

                        }
                    }
                }
            }
            backArrowIV.setOnClickListener {
                findNavController().popBackStack()
            }

            outstandingBalancePeriodTV.text = "${AppUtils.formatDateToFullDate(sdfUtc.format(sdfUtc.parse(data.lpd)?.time?.plus(TimeUnit.DAYS.toMillis(1))))} - ${AppUtils.getCurrentFullDate()}"

            Timber.d("$data")

            vehiclePlatesTV.text = data.vpn
            vehicleCategoryTV.text = data.vc
            lastPaymentDateTV.text = AppUtils.formatDateToFullDate(data.lpd)

            val dateStr = data.lpd
            val lastPayDate = sdfUtc.parse(dateStr)

            val currentDate = sdfUtc.parse(AppUtils.getCurrentDate())

            val daysDifference = currentDate?.time!! - lastPayDate?.time!!
            val numOfDaysSinceLastPaid = TimeUnit.DAYS.convert(daysDifference, TimeUnit.MILLISECONDS) - 1 //minus one to exempt last pay date

            Timber.d("last pay date: $lastPayDate")
            Timber.d("current date: $currentDate")
            Timber.d("number of days since last paid: $numOfDaysSinceLastPaid")

            viewLifecycleOwner.lifecycleScope.launchWhenResumed {

                val daysOwedArray = arrayListOf<String>()

                //get all holidays(tax free days) in database
                var holidays = Mapper.mapListOfTaxFreeDayEntityToListOfTaxFreeDay(
                    viewModel.getAllHolidaysInDatabase()
                )

                Timber.d("All Holidays in database $holidays")

                Timber.d("All Holidays dates in database ${holidays.map { it.date }}")

                //get and initialize vehicleInDatabase globally if not already initialized
                if (vehicleInDatabase == null) {
                    vehicleInDatabase = viewModel.getVehicleFromCurrentEnumerationInDatabase(data.vpn)
                }

                Timber.d("vehicle in database $vehicleInDatabase")

                //get vehicle routes
                val vehicleRoutes = vehicleInDatabase?.vehicleRoutes?.let { routes ->
                    routes.map { route -> route.routeID }
                } ?: emptyList()

                Timber.d("Vehicle's routes id $vehicleRoutes")

                //filter holidays by those which fall on a route that belongs to the vehicle's routes
                //i.e if an holiday falls on any of a vehicle's route, count that as a tax-free day
                //for the vehicle
                holidays = holidays.filter { holiday -> vehicleRoutes.contains(holiday.routeId) }

                Timber.d("Filtered Holidays based on vehicle's route $holidays")

                val holidayDates = holidays.map { holiday -> holiday.date?.let { date -> AppUtils.formatDateToFullDate(date) } }
                Timber.d("Filtered Holiday dates based on vehicle's route $holidayDates")


                numOfDaysSinceLastPaid.downTo(0).forEach {
                    val date = LocalDate.now(ZoneId.of("Africa/Lagos")).minusDays(it)
                        .format(DateTimeFormatter.ofPattern("E, dd MMM yyyy", Locale.UK))
                    Timber.d("numOfDays: $it date: ${date}")
                    //exempt saturdays and sundays
                    if (!date.contains("Sat", true) && !date.contains("Sun", true)) {
                        //exempt holidays
                        if (!holidayDates.contains(date)) {
                            daysOwedArray.add(date)
                        } else {
                            Timber.d("found an holiday $date")
                        }
                    }
                }
                Timber.d("days owed (excluding holidays) $daysOwedArray")

                val numOfEligibleDaysOwed = daysOwedArray.size
                Timber.d("number of days owed ${daysOwedArray.size}")

                val rate = viewModel.getRateInDatabase(data.vc)
                if (rate?.amount != null) {
                    outstandingBalance = rate.amount.toDouble().times(numOfEligibleDaysOwed.toDouble())
                    if (outstandingBalance == 0.00) {
                        outstandingBalanceTV.setBackgroundResource(R.drawable.balance_text_green_bg)
                        outstandingBalanceTV.setTextColor(getResources().getColor(R.color.green))
                        lastPaymentDateTV.setTextColor(getResources().getColor(R.color.green))
                        payOutstandingBTN.visibility = INVISIBLE
                        outstandingBalancePeriodTV.visibility = INVISIBLE
                        openCalendarTV.visibility = INVISIBLE
                    } else {
                        outstandingBalanceTV.setBackgroundResource(R.drawable.balance_text_red_bg)
                        outstandingBalanceTV.setTextColor(getResources().getColor(R.color.red))
                        lastPaymentDateTV.setTextColor(getResources().getColor(R.color.red))
                        payOutstandingBTN.visibility = VISIBLE
                        outstandingBalancePeriodTV.visibility = VISIBLE
                        //if instalments are allowed show text, else hide text
                        if (viewModel.getInitialUserPreferences().instalmentsSetting == false) {
                            openCalendarTV.visibility = VISIBLE
                        } else {
                            openCalendarTV.visibility = INVISIBLE
                        }
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

        //format last payment date in UTC timezone
        val lastPayDate = sdfUtc.parse(data.lpd)
        Timber.d("lpd date ${lastPayDate?.time}")

        //set selected dates to reflect outstanding days since the last payment, exempt the last
        //paid date from the selection by adding (86400000) milliseconds (1 day) to
        //the last paid date so that the first selection is the next day after the last paid date
        val startDate = lastPayDate?.time?.plus(TimeUnit.DAYS.toMillis(1))
        val endDate = MaterialDatePicker.todayInUtcMilliseconds()

        Timber.d("start date: $startDate timeNow: $endDate")

        val dateValidatorMin: CalendarConstraints.DateValidator? = startDate?.let {
            DateValidatorPointForward
                .from(it)
        }
        val dateValidatorMax: CalendarConstraints.DateValidator = DateValidatorPointBackward.before(endDate)
        val dateValidators = CompositeDateValidator.allOf(listOf(dateValidatorMin, dateValidatorMax))
        builder.setCalendarConstraints(
            CalendarConstraints.Builder()
                .setValidator(dateValidators)
                .build()
        )

        builder.setSelection(Pair(startDate, endDate))
        val picker = builder.build()
        picker.show(childFragmentManager, picker.toString())

        picker.addOnDismissListener {
            //ux
            AppUtils.showView(true, binding.openCalendarTV)
            AppUtils.showProgressIndicator(false, binding.progressIndicator)
            binding.payOutstandingBTN.isEnabled = true
        }

        picker.addOnPositiveButtonClickListener {
            //set selected dates
            val first: Long? = it.first
            val firstDate = Date(first!!)
            val second: Long? = it.second
            val secondDate = Date(second!!)

            //if selected first date does not match actual/valid start date to begin payment from,
            //show invalid date selection dialog
            Timber.d("first: ${firstDate.time}")
            val selectedStartDate = sdfUtc.format(firstDate.time)
            val compulsoryStartDate = sdfUtc.format(startDate)
            Timber.d("required start date: $compulsoryStartDate selected start date: $selectedStartDate")

            //if selected second date does not match actual/valid end date to make payment
            //show invalid date selection dialog
            Timber.d("first: ${secondDate.time}")
            val selectedEndDate = sdfUtc.format(secondDate.time)
            val compulsoryEndDate = sdfUtc.format(endDate)
            Timber.d("required end date: $compulsoryEndDate selected end date: $selectedEndDate")

            viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                //format selected dates to local time zone and string pattern
                val dateFrom = sdfLocal.format(firstDate)
                val dateTo = sdfLocal.format(secondDate)

                val allowInstalments = viewModel.getInitialUserPreferences().instalmentsSetting
                Timber.d("Instalment: $allowInstalments")
                when {
                    selectedStartDate != compulsoryStartDate -> {
                        showInvalidStartDateDialog(compulsoryStartDate)
                    }
                    selectedEndDate > compulsoryEndDate -> {
                        showInvalidEndDateDialog(compulsoryEndDate)
                    }
                    (allowInstalments == false) && (selectedEndDate != AppUtils.getCurrentDate()) -> {
                        showInstalmentsNotAllowed(compulsoryStartDate, compulsoryEndDate)
                    }
                    (allowInstalments == false) && (selectedEndDate == AppUtils.getCurrentDate()) -> {
                        goToOutstandingPaymentScreen(dateFrom, dateTo)
                    }
                    allowInstalments == true -> {
                        goToOutstandingPaymentScreen(dateFrom, dateTo)
                    }
                }
            }


        }
    }

    private fun goToOutstandingPaymentScreen(dateFrom: String, dateTo: String) {
        Timber.d("dateFrom: $dateFrom dateTO: $dateTo")

        val bundle = Bundle().also { bundle ->
            bundle.putString(MainActivity.VEHICLE_ID_NUMBER_KEY, data.vid)
            bundle.putString(MainActivity.LAST_PAYMENT_DATE_KEY, data.lpd)
            bundle.putString(MainActivity.VEHICLE_CATEGORY_KEY, data.vc)
            bundle.putString(MainActivity.VEHICLE_PLATES_NUMBER_KEY, data.vpn)
            bundle.putDouble(OUTSTANDING_BALANCE, outstandingBalance)
            bundle.putString(DATE_FROM_KEY, dateFrom)
            bundle.putString(DATE_TO_KEY, dateTo)
        }

        findNavController().navigate(R.id.outStandingPaymentFragment, bundle,
            NavOptions.Builder().setLaunchSingleTop(true).build())
    }

    private fun showInstalmentsNotAllowed(compulsoryStartDate: String, compulsoryEndDate: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Instalments are not allowed")
            .setMessage("Please pay all outstanding balance between ${AppUtils
                .formatDateToFullDate(compulsoryStartDate)} and ${AppUtils
                .formatDateToFullDate(compulsoryEndDate)}")
            .setNegativeButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun showInvalidEndDateDialog(compulsoryEndDate: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Invalid end date")
            .setMessage("Valid date selection must not exceed today (${AppUtils
                .formatDateToFullDate(compulsoryEndDate)})")
            .setNegativeButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun showInvalidStartDateDialog(compulsoryStartDate: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Invalid start date")
            .setMessage("Valid date selection must begin from the next day (${AppUtils
                .formatDateToFullDate(compulsoryStartDate)}) after last payment")
            .setNegativeButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun showInCompatibleRoutes() {
        AlertDialog.Builder(requireContext())
            .setTitle("Incompatible routes")
            .setMessage("This vehicle has no route that you are allowed to collect payment from")
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
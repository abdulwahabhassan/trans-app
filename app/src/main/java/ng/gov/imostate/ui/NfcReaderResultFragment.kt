package ng.gov.imostate.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.R
import ng.gov.imostate.databinding.FragmentNfcReaderResultBinding
import ng.gov.imostate.model.domain.Data
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = Data(
            arguments?.getString(MainActivity.DRIVER_NAME_KEY) ?: "",
            arguments?.getString(MainActivity.VEHICLE_ID_NUMBER_KEY) ?: "",
            arguments?.getString(MainActivity.LAST_PAYMENT_DATE_KEY)?: "",
            arguments?.getDouble(MainActivity.OUTSTANDING_BAL_KEY) ?: 0.00,
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

    @SuppressLint("SetTextI18n")
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

            if (data.ob == 0.00) {
                outstandingBalanceTV.setBackgroundResource(R.drawable.balance_text_green_bg)
                outstandingBalanceTV.setTextColor(getResources().getColor(R.color.green))
                payOutstandingBTN.visibility = INVISIBLE
            } else {
                outstandingBalanceTV.setBackgroundResource(R.drawable.balance_text_red_bg)
                outstandingBalanceTV.setTextColor(getResources().getColor(R.color.red))
                payOutstandingBTN.visibility = VISIBLE
            }

            outstandingBalancePeriodTV.text = "${AppUtils.formatDateToFullDate(data.lpd)} - ${AppUtils.getCurrentFullDate()}"

            Timber.d("$data")

            driverTV.text = data.name
            vehiclePlatesTV.text = data.vpn
            lastPaymentDateTV.text = AppUtils.formatDateToFullDate(data.lpd)
            outstandingBalanceTV.text = "Outstanding Balance: ₦${AppUtils.formatCurrency(data.ob)}"

        }

    }

    private fun showDatePicker() {
        val builder : MaterialDatePicker.Builder<Pair<Long, Long>> =
            MaterialDatePicker.Builder.dateRangePicker()

        builder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        builder.setTitleText("Pay till")
        val calendar = Calendar.getInstance()
        builder.setSelection(Pair(calendar.timeInMillis, calendar.timeInMillis))
        val picker = builder.build()
        picker.show(childFragmentManager, picker.toString())

        picker.addOnPositiveButtonClickListener {
            val dateFrom: String
            val dateTo: String

            val first: Long? = it.first
            val firstDate = Date(first!!)
            val second: Long? = it.second
            val secondDate = Date(second!!)

            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            dateFrom = format.format(firstDate)
            dateTo = format.format(secondDate)

            val bundle = Bundle().also { bundle ->
                bundle.putString(MainActivity.DRIVER_NAME_KEY, data.name)
                bundle.putString(MainActivity.VEHICLE_ID_NUMBER_KEY, data.vid)
                bundle.putString(MainActivity.LAST_PAYMENT_DATE_KEY, data.lpd)
                bundle.putDouble(MainActivity.OUTSTANDING_BAL_KEY, data.ob)
                bundle.putString(MainActivity.VEHICLE_PLATES_NUMBER_KEY, data.vpn)
                bundle.putString(DATE_FROM_KEY, dateFrom)
                bundle.putString(DATE_TO_KEY, dateTo)
            }

            //do api call
            findNavController().navigate(R.id.outStandingPaymentFragment, bundle,
                NavOptions.Builder().setLaunchSingleTop(true).build())

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val DATE_FROM_KEY = "dateFrom"
        const val DATE_TO_KEY = "dateTo"
    }

}
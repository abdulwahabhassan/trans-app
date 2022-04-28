package ng.gov.imostate.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.R
import ng.gov.imostate.databinding.FragmentScannedResultBinding
import ng.gov.imostate.model.Data
import ng.gov.imostate.util.AppUtils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ScannedResultFragment : Fragment() {

    private var _binding: FragmentScannedResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var data: Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = Data(
            arguments?.getString(MainActivity.DRIVER_NAME_KEY) ?: "",
            arguments?.getString(MainActivity.VEHICLE_REGISTRATION_NUMBER_KEY) ?: "",
            arguments?.getString(MainActivity.LAST_PAYMENT_DATE_KEY)?: "",
            arguments?.getLong(MainActivity.OUTSTANDING_BAL_KEY) ?: 0
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScannedResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            payOutstandingPaymentBTN.setOnClickListener {
                showDatePicker()
            }
            backArrowIV.setOnClickListener {
                findNavController().popBackStack()
            }

            Timber.d("$data")
            driverTV.text = data.name
            vehicleRegistrationTV.text = data.vrn
            lastPaymentDateTV.text = data.lpd
            outstandingBalanceTV.text = "Balance: ₦${AppUtils.formatCurrency(data.ob)}"
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
                bundle.putString(MainActivity.VEHICLE_REGISTRATION_NUMBER_KEY, data.vrn)
                bundle.putString(MainActivity.LAST_PAYMENT_DATE_KEY, data.lpd)
                bundle.putLong(MainActivity.OUTSTANDING_BAL_KEY, data.ob)
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

}
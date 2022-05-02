package ng.gov.imostate.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.util.Pair
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ng.gov.imostate.R
import ng.gov.imostate.databinding.FragmentScannedResultBinding
import ng.gov.imostate.model.domain.Data
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ScannedResultFragment : Fragment() {

    private var _binding: FragmentScannedResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var data: Data
    private val sharedNfcViewModel: SharedNfcViewModel by activityViewModels()
    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    //view model
    private lateinit var viewModel: ScannedResultFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = Data(
            arguments?.getString(MainActivity.DRIVER_NAME_KEY) ?: "",
            arguments?.getString(MainActivity.VEHICLE_REGISTRATION_NUMBER_KEY) ?: "",
            arguments?.getString(MainActivity.LAST_PAYMENT_DATE_KEY)?: "",
            arguments?.getDouble(MainActivity.OUTSTANDING_BAL_KEY) ?: 0.00
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

        viewModel = ViewModelProvider(
            this,
            appViewModelFactory
        ).get(ScannedResultFragmentViewModel::class.java)

        //observeNfcMode()

        with(binding) {
//            syncTagBTN.setOnClickListener {
//                val nfcMode = sharedNfcViewModel.getNfcMode()
//                if (nfcMode == NfcMode.WRITE.name) {
//                    sharedNfcViewModel.setNfcMode(NfcMode.READ)
//                } else {
//                    sharedNfcViewModel.setNfcMode(NfcMode.WRITE)
//                }
//            }
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

            outstandingBalancePeriodTV.text = "${AppUtils.formatDate(data.lpd)} - ${AppUtils.formatDate(AppUtils.getCurrentDate())}"

            Timber.d("$data")
            driverTV.text = data.name
            vehicleLicenseTV.text = data.vrn
            lastPaymentDateTV.text = data.lpd
            outstandingBalanceTV.text = "Outstanding Balance: ₦${AppUtils.formatCurrency(data.ob)}"

        }

    }

//    private fun observeNfcMode() {
//        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                sharedNfcViewModel.nfcMode.collect { nfcMode ->
//                    if (nfcMode == NfcMode.WRITE.name) {
//                        binding.nfcWriteModeLAV.playAnimation()
//                        binding.nfcWriteModeLAV.visibility = VISIBLE
//                        binding.syncTagBTN.text = "STOP SYNC"
//                    } else {
//                        binding.nfcWriteModeLAV.pauseAnimation()
//                        binding.nfcWriteModeLAV.visibility = INVISIBLE
//                        binding.syncTagBTN.text = "SYNC TAG"
//                    }
//                }
//            }
//        }
//    }

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
                bundle.putDouble(MainActivity.OUTSTANDING_BAL_KEY, data.ob)
                bundle.putString(VEHICLE_ID, data.vrn)
                bundle.putString(DATEFROM, dateFrom)
                bundle.putString(DATETO, dateTo)
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
        const val DATEFROM = "dateFrom"
        const val DATETO = "dateTo"
        const val VEHICLE_ID = "id"
    }

}
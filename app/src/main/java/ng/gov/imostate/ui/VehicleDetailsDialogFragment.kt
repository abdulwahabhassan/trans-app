package ng.gov.imostate.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.R
import ng.gov.imostate.databinding.FragmentVehicleDetailsDialogBinding
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import ng.gov.imostate.viewmodel.VehicleDetailsDialogFragmentViewModel
import javax.inject.Inject

@AndroidEntryPoint
class VehicleDetailsDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentVehicleDetailsDialogBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    //view model
    lateinit var viewModel: VehicleDetailsDialogFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVehicleDetailsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            appViewModelFactory
        ).get(VehicleDetailsDialogFragmentViewModel::class.java)

        with(binding) {

            dateOnBoardedTV.text = arguments?.getString(MainActivity.DATE_ONBOARDED_KEY)
            vehiclePlatesTV.text = arguments?.getString(MainActivity.VEHICLE_PLATES_NUMBER_KEY)
            vehicleTypeTV.text = arguments?.getString(MainActivity.VEHICLE_CATEGORY_KEY)
            vehicleLicenseExpiryDateTV.text = arguments?.getString(MainActivity.VEHICLE_LICENSE_EXPIRY_DATE_KEY)
            lastPaidDateTV.text = arguments?.getString(MainActivity.LAST_PAYMENT_DATE_KEY)
            val vehicleId = arguments?.getString(MainActivity.VEHICLE_ID_NUMBER_KEY)

            seeTransactionsBTN.setOnClickListener {

//                when (viewLastTransactionBTN.text.toString()) {
//                    TAG_VEHICLE_TEXT -> {
//                        val action = VehicleDetailsDialogFragmentDirections
//                            .actionVehicleDetailsDialogFragmentToTagVehicleFragment(
//                                "200",
//                                AppUtils.getCurrentDate(),
//                                arguments?.getString(MainActivity.DRIVER_NAME_KEY),
//                                arguments?.getString(MainActivity.VEHICLE_ID_NUMBER_KEY),
//                                arguments?.getString(MainActivity.VEHICLE_PLATES_NUMBER_KEY)
//                            )
//                        findNavController().navigate(action)
//                    }
//                    VIEW_LAST_TRANSACTION -> {
//                //make api call to get last transaction for this vehicle if it exists
//                val bundle = Bundle().also {
//                    it.putString(MainActivity.DRIVER_NAME_KEY, arguments?.getString(MainActivity.DRIVER_NAME_KEY))
//                    it.putString(MainActivity.VEHICLE_ID_NUMBER_KEY, arguments?.getString(MainActivity.VEHICLE_ID_NUMBER_KEY))
//                    it.putString(MainActivity.VEHICLE_PLATES_NUMBER_KEY, arguments?.getString(MainActivity.VEHICLE_PLATES_NUMBER_KEY))
//                    it.putString(MainActivity.LAST_PAYMENT_DATE_KEY, arguments?.getString(MainActivity.LAST_PAYMENT_DATE_KEY))
//                }
//
//                //navigate if last transactions exists else show toast
//                findNavController().navigate(
//                    R.id.nfcReaderResultFragment,
//                    bundle,
//                    NavOptions.Builder().setLaunchSingleTop(true).build()
//                )
////                        //show toast if no transaction record exists and show button to tag vehicle
////                        AppUtils.showToast(requireActivity(), "No transaction record found", MotionToastStyle.ERROR)
////                        showTagVehicleButton(true)
//                    }
//                }
            }
        }
    }

//    private fun showTagVehicleButton(show: Boolean) {
//        if (show) {
//            binding.viewLastTransactionBTN.text = "TAG VEHICLE"
//        } else {
//            binding.viewLastTransactionBTN.text = "VIEW LAST TRANSACTION"
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

//    companion object {
//        const val TAG_VEHICLE_TEXT = "TAG VEHICLE"
//        const val VIEW_LAST_TRANSACTION = "VIEW LAST TRANSACTION"
//    }
}
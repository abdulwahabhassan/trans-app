package ng.gov.imostate.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.R
import ng.gov.imostate.databinding.FragmentFindVehicleDialogBinding
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.ui.activity.MainActivity
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import ng.gov.imostate.viewmodel.FindVehicleDialogFragmentViewModel
import timber.log.Timber
import www.sanju.motiontoast.MotionToastStyle
import javax.inject.Inject


@AndroidEntryPoint
class FindVehicleDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFindVehicleDialogBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    //view model
    val viewModel: FindVehicleDialogFragmentViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFindVehicleDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backArrowIV.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.continueBTN.setOnClickListener {
            if (validateField()) {
                viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                    AppUtils.showProgressIndicator(true, binding.progressIndicator)
                    AppUtils.showView(false, binding.continueBTN)

                    val vehicleIdentifier = binding.platesNumberET.text.toString()

                    when (val viewModelResult = viewModel.getInitialUserPreferences().token?.let {
                        viewModel.getVehicle(it, vehicleIdentifier)
                    }!!) {
                        is ViewModelResult.Success -> {
                           if (viewModelResult.data?.vehicle != null) {
                               Timber.d("$viewModelResult")
                               val bundle = Bundle().also { bundle ->
                                   bundle.putString(
                                       MainActivity.DATE_ONBOARDED_KEY,
                                       viewModelResult.data.vehicle?.createdAt?.substring(0, 10)
                                           ?.let { it1 -> AppUtils.formatDateToFullDate(it1) }
                                   )
                                   bundle.putString(
                                       MainActivity.VEHICLE_PLATES_NUMBER_KEY,
                                       viewModelResult.data.vehicle?.vehiclePlates
                                   )
                                   bundle.putString(
                                       MainActivity.VEHICLE_ID_NUMBER_KEY,
                                       viewModelResult.data.vehicle?.id.toString()
                                   )
                                   bundle.putString(
                                       MainActivity.VEHICLE_CATEGORY_KEY,
                                       viewModelResult.data.vehicle?.type
                                   )
                                   bundle.putString(
                                       MainActivity.LAST_PAYMENT_DATE_KEY,
                                       viewModelResult.data.vehicle?.lastPaid?.let { it1 ->
                                           AppUtils.formatDateToFullDate(it1)
                                       }
                                   )
                                   bundle.putString(
                                       MainActivity.VEHICLE_LICENSE_EXPIRY_DATE_KEY,
                                       viewModelResult.data.vehicle?.vehicleLicenceExpDate?.let { it1 ->
                                           AppUtils.formatDateToFullDate(it1)
                                       }
                                   )
                               }
                               findNavController().navigate(
                                   R.id.vehicleDetailsDialogFragment, bundle,
                                   NavOptions.Builder().setLaunchSingleTop(true).setPopUpTo(R.id.homeFragment, false).build())
                           } else {
                               AppUtils.showToast(requireActivity(), "Vehicle not found", MotionToastStyle.ERROR)
                               Timber.d("$viewModelResult")
                           }
                       }
                       is ViewModelResult.Error -> {
                           Timber.d("$viewModelResult")
                           AppUtils.showToast(requireActivity(), viewModelResult.errorMessage, MotionToastStyle.ERROR)
                           AppUtils.showProgressIndicator(false, binding.progressIndicator)
                           AppUtils.showView(true, binding.continueBTN)
                       }
                   }
                }
            }
        }
    }

    private fun validateField(): Boolean {
        var success = true
        if (binding.platesNumberET.text.toString().isEmpty()) {
            binding.vehicleDetailsTIP.error = "Please fill vehicle plates number"
            success = false
        } else {
            binding.vehicleDetailsTIP.error = ""
        }
        return success
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
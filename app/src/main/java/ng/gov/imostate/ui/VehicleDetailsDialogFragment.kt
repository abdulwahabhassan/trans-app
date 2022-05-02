package ng.gov.imostate.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.R
import ng.gov.imostate.databinding.FragmentVehicleDetailsDialogBinding
import ng.gov.imostate.model.request.GetVehicleRequest
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.AddVehicleFragmentViewModel
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import ng.gov.imostate.viewmodel.VehicleDetailsDialogFragmentViewModel
import www.sanju.motiontoast.MotionToastStyle
import javax.inject.Inject


@AndroidEntryPoint
class VehicleDetailsDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentVehicleDetailsDialogBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    //view model
    lateinit var viewModel: VehicleDetailsDialogFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

        binding.backArrowIV.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.continueBTN.setOnClickListener {

            viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                AppUtils.showProgressIndicator(true, binding.progressIndicator)
                AppUtils.showView(false, binding.continueBTN)
                val bundle = Bundle().also { bundle ->
                    bundle.putString(MainActivity.DRIVER_NAME_KEY, "Amaechi Barnabas")
                    bundle.putString(MainActivity.VEHICLE_REGISTRATION_NUMBER_KEY,
                        binding.vehicleLicenseET.text.toString()
                    )
                    bundle.putString(MainActivity.LAST_PAYMENT_DATE_KEY, "2022-04-09")
                    bundle.putDouble(MainActivity.OUTSTANDING_BAL_KEY, 0.00)
                }

                val result = viewModel.getInitialUserPreferences().token?.let { token ->
                    viewModel.getVehicle(
                        token,
                        GetVehicleRequest("1"))
                }!!
                when (result) {
                    is ViewModelResult.Success -> {
                        AppUtils.showToast(requireActivity(), result.data.toString(), MotionToastStyle.SUCCESS)
                        findNavController().navigate(
                            R.id.scannedResultFragment, bundle,
                            NavOptions.Builder().setLaunchSingleTop(true).build())
                    }
                    is ViewModelResult.Error -> {
                        AppUtils.showToast(requireActivity(), result.errorMessage, MotionToastStyle.ERROR)
                        findNavController().navigate(
                            R.id.scannedResultFragment, bundle,
                            NavOptions.Builder().setLaunchSingleTop(true).build())
                        AppUtils.showProgressIndicator(false, binding.progressIndicator)
                        AppUtils.showView(true, binding.continueBTN)
                    }
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
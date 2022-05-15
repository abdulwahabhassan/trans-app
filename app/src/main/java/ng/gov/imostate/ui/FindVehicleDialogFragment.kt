package ng.gov.imostate.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.R
import ng.gov.imostate.databinding.FragmentFindVehicleDialogBinding
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
    lateinit var viewModel: FindVehicleDialogFragmentViewModel

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

        viewModel = ViewModelProvider(
            this,
            appViewModelFactory
        ).get(FindVehicleDialogFragmentViewModel::class.java)

        binding.backArrowIV.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.continueBTN.setOnClickListener {
            if (validateField()) {
                viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                    AppUtils.showProgressIndicator(true, binding.progressIndicator)
                    AppUtils.showView(false, binding.continueBTN)

//                    val vehicles = viewModel.getAllVehiclesInDatabase()
//                    Timber.d("$vehicles")
//
//                    val drivers = viewModel.getAllDriversInDatabase()
//                    Timber.d("$drivers")

                    val vehicleIdentifier = binding.platesNumberET.text.toString()

                    val vehicle = viewModel.findVehicleDriverRecordInDatabase(vehicleIdentifier)
                    Timber.d("$vehicle")

                    if(vehicle?.vehiclePlates == vehicleIdentifier) {
                        //debug purpose
                        val bundle = Bundle().also { bundle ->
                            bundle.putString(DRIVER_NAME_KEY, vehicle.driver?.firstName + " " + vehicle.driver?.lastName)
                            bundle.putString(VEHICLE_IDENTIFIER_KEY, vehicle.vehiclePlates)
                            bundle.putString(VEHICLE_TYPE_KEY, vehicle.type)
                            bundle.putString(VEHICLE_LICENSE_EXP_DATE_KEY,
                                vehicle.vehicleLicenceExpDate
                            )
                        }
                        //

                        findNavController().navigate(
                                R.id.vehicleDetailsDialogFragment, bundle,
                                NavOptions.Builder().setLaunchSingleTop(true).setPopUpTo(R.id.homeFragment, false).build())
                    } else {
                        AppUtils.showToast(
                            requireActivity(),
                            "Vehicle not found in database",
                            MotionToastStyle.ERROR
                        )
                    }
                    AppUtils.showProgressIndicator(false, binding.progressIndicator)
                    AppUtils.showView(true, binding.continueBTN)
                }
            }
        }
    }

    fun validateField(): Boolean {
        var success = true
        if (binding.platesNumberET.text.toString().isEmpty()) {
            binding.vehicleDetailsTIP.error = "Please fill vehicle license"
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

    companion object {
        const val VEHICLE_TYPE_KEY = "vehicleType"
        const val VEHICLE_IDENTIFIER_KEY = "vehicleIdentifier"
        const val DRIVER_NAME_KEY = "driverName"
        const val VEHICLE_LICENSE_EXP_DATE_KEY = "vehicleLicenseExpiryDate"
    }

}
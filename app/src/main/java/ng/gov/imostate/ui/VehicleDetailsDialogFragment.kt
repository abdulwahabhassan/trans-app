package ng.gov.imostate.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.R
import ng.gov.imostate.databinding.FragmentVehicleDetailsDialogBinding
import ng.gov.imostate.model.domain.TransactionType
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import ng.gov.imostate.viewmodel.FindVehicleDialogFragmentViewModel
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
    val viewModel: FindVehicleDialogFragmentViewModel by activityViewModels()
    private var dateOnBoarded: String? = null
    private var vehiclePlates: String? = null
    private var vehicleLastPaidDate: String? = null
    private var vehicleCategory: String? = null
    private var vehicleLicenseExp: String? = null
    private var vehicleIdNumber: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVehicleDetailsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateOnBoarded = arguments?.getString(MainActivity.DATE_ONBOARDED_KEY)
        vehiclePlates = arguments?.getString(MainActivity.VEHICLE_PLATES_NUMBER_KEY)
        vehicleCategory = arguments?.getString(MainActivity.VEHICLE_CATEGORY_KEY)
        vehicleLicenseExp = arguments?.getString(MainActivity.VEHICLE_LICENSE_EXPIRY_DATE_KEY)
        vehicleLastPaidDate = arguments?.getString(MainActivity.LAST_PAYMENT_DATE_KEY)
        vehicleIdNumber = arguments?.getString(MainActivity.VEHICLE_ID_NUMBER_KEY)

        with(binding) {

            if (
                vehicleIdNumber.isNullOrEmpty() ||
                vehiclePlates.isNullOrEmpty() ||
                vehicleCategory.isNullOrEmpty() ||
                vehicleLastPaidDate.isNullOrEmpty()
            ) {
                binding.syncVehicleTagTV.visibility = INVISIBLE
            }

            backArrowIV.setOnClickListener {
                findNavController().popBackStack()
            }

            syncVehicleTagTV.setOnClickListener {
                val action = VehicleDetailsDialogFragmentDirections.actionVehicleDetailsDialogFragmentToSuccessFragment(
                    vehicleIdNumber.toString(),
                    vehiclePlates,
                    vehicleLastPaidDate?.let { date -> AppUtils.formatFullDateToDate(date) },
                    vehicleCategory
                )
                findNavController().navigate(action)
            }

            dateOnBoardedTV.text = dateOnBoarded ?: "-"
            vehiclePlatesTV.text = vehiclePlates ?: "-"
            vehicleTypeTV.text = vehicleCategory ?: "-"
            vehicleLicenseExpiryDateTV.text = vehicleLicenseExp ?: "-"
            lastPaidDateTV.text = vehicleLastPaidDate ?: "-"

            seeTransactionsBTN.setOnClickListener {
                val action = VehicleDetailsDialogFragmentDirections
                    .actionVehicleDetailsDialogFragmentToTransactionsFragment(
                        TransactionType.VEHICLE_TRANSACTION.name,
                        vehicleIdNumber
                    )
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
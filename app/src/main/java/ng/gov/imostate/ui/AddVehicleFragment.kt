package ng.gov.imostate.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.checkbox.MaterialCheckBox
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.databinding.FragmentAddVehicleBinding
import ng.gov.imostate.databinding.FragmentHomeBinding

@AndroidEntryPoint
class AddVehicleFragment : Fragment() {

    private var _binding: FragmentAddVehicleBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddVehicleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showMinimal()

        with(binding) {

            backArrowIV.setOnClickListener {
                findNavController().popBackStack()
            }

            minimalCB.setOnClickListener { cb ->
                if (fullCB.isChecked) {
                    fullCB.isChecked = false
                }
                (cb as MaterialCheckBox).isChecked = true
                showMinimal()
            }

            fullCB.setOnClickListener { cb ->
                if (minimalCB.isChecked) {
                    minimalCB.isChecked = false
                }
                (cb as MaterialCheckBox).isChecked = true
                showFull()
            }
        }
    }

    private fun showFull() {
        with(binding) {
            roadWorthinessExpiryDateTV.visibility = VISIBLE
            roadWorthinessExpiryDateTIL.visibility = VISIBLE

            vehicleInsuranceExpiryDateTV.visibility = VISIBLE
            vehicleInsuranceExpiryDateTIL.visibility = VISIBLE

            vehicleLicenseExpiryDateTV.visibility = VISIBLE
            vehicleLicenseExpiryDateTIL.visibility = VISIBLE

            bloodGroupTV.visibility = VISIBLE
            bloodGroupTIL.visibility = VISIBLE

            maritalStatusTV.visibility = VISIBLE
            maritalStatusTIL.visibility = VISIBLE

            engineNumberTV.visibility = VISIBLE
            engineNumberTIL.visibility = VISIBLE

            chasisNumberTV.visibility = VISIBLE
            chasisNumberTIL.visibility = VISIBLE

            stateOfRegistrationTV.visibility = VISIBLE
            stateOfRegistrationTIL.visibility = VISIBLE
        }
    }

    private fun showMinimal() {
        with(binding) {
            roadWorthinessExpiryDateTV.visibility = GONE
            roadWorthinessExpiryDateTIL.visibility = GONE

            vehicleInsuranceExpiryDateTV.visibility = GONE
            vehicleInsuranceExpiryDateTIL.visibility = GONE

            vehicleLicenseExpiryDateTV.visibility = GONE
            vehicleLicenseExpiryDateTIL.visibility = GONE

            bloodGroupTV.visibility = GONE
            bloodGroupTIL.visibility = GONE

            maritalStatusTV.visibility = GONE
            maritalStatusTIL.visibility = GONE

            engineNumberTV.visibility = GONE
            engineNumberTIL.visibility = GONE

            chasisNumberTV.visibility = GONE
            chasisNumberTIL.visibility = GONE

            stateOfRegistrationTV.visibility = GONE
            stateOfRegistrationTIL.visibility = GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
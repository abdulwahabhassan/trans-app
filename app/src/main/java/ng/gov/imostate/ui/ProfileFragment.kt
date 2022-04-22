package ng.gov.imostate.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ng.gov.imostate.databinding.FragmentAddVehicleBinding
import ng.gov.imostate.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            vehicleLicenseTV.text = "IMGS73"
            vehicleRegistrationTV.text = "IMO-737-OWR"
            vehicleColorTV.text = "Yellow"
            vehicleTypeTV.text = "Toyota"
            driverTV.text = "John Obi"
            lastPaymentDateTV.text = "02/01/22"
        }

    }


}
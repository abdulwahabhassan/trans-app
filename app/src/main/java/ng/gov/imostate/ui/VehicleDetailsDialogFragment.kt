package ng.gov.imostate.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ng.gov.imostate.databinding.FragmentVehicleDetailsDialogBinding


class VehicleDetailsDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentVehicleDetailsDialogBinding? = null
    private val binding get() = _binding!!

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
        binding.backArrowIV.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.continueBTN.setOnClickListener {
            val action = VehicleDetailsDialogFragmentDirections.actionVehicleDetailsDialogFragmentToScannedResultFragment()
            findNavController().navigate(action)
        }
    }

}
package ng.gov.imostate.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ng.gov.imostate.databinding.FragmentDailyRatesDialogBinding
import ng.gov.imostate.util.AppUtils


class DailyRatesDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDailyRatesDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDailyRatesDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            dailyRatesTV.text = AppUtils.getCurrentFullDate()

            backArrowIV.setOnClickListener {
                findNavController().popBackStack()
            }

            doneBTN.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
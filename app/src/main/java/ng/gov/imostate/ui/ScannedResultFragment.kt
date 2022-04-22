package ng.gov.imostate.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import ng.gov.imostate.databinding.FragmentScanBinding
import ng.gov.imostate.databinding.FragmentScannedResultBinding
import java.text.SimpleDateFormat
import java.util.*


class ScannedResultFragment : Fragment() {


    private var _binding: FragmentScannedResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScannedResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.payOutstandingPaymentTV.setOnClickListener {
            showDatePicker()
        }
        binding.backArrowIV.setOnClickListener {
            findNavController().popBackStack()
        }
    }

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

            //do api call
        }
    }

}
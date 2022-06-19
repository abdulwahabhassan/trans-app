package ng.gov.imostate.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ng.gov.imostate.databinding.FragmentPaymentBinding


class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            backArrowIV.setOnClickListener {
                findNavController().popBackStack()
            }

            makePaymentBTN.setOnClickListener {
                amountTIP.error = ""
                val amount = amountET.text.toString()
                if (amount.isNotEmpty() && amount.toDouble() != 0.00) {
                    val action =
                        PaymentFragmentDirections.actionPaymentFragmentToFundWalletFragment(
                            amount
                        )
                    findNavController().navigate(action)
                } else {
                    amountTIP.error = "Please enter valid amount"
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
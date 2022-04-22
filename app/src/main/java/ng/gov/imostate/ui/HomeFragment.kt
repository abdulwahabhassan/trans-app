package ng.gov.imostate.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ng.gov.imostate.util.Mock
import ng.gov.imostate.model.Transaction
import ng.gov.imostate.adapter.TransactionsAdapter
import ng.gov.imostate.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var transactionsAdapter: TransactionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRV()

        initUI()
    }

    private fun initRV() {
        transactionsAdapter = TransactionsAdapter{ position: Int, transaction: Transaction ->

        }
        binding.transactionsRV.adapter = transactionsAdapter
    }

    private fun initUI() {
        transactionsAdapter.submitList(Mock.getTransaction())

        binding.transactionsCV.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToTransactionsFragment()
            findNavController().navigate(action)
        }

        binding.vehicleCV.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddVehicleFragment()
            findNavController().navigate(action)
        }
    }
}
package ng.gov.imostate.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.util.Mock
import ng.gov.imostate.model.domain.Transaction
import ng.gov.imostate.adapter.TransactionsAdapter
import ng.gov.imostate.databinding.FragmentTransactionsBinding

@AndroidEntryPoint
class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var transactionsAdapter: TransactionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchView.queryHint = "Search Transactions"

        initRV()

        initUI()
    }

    private fun initRV() {
        transactionsAdapter = TransactionsAdapter{ position: Int, transaction: Transaction ->

        }
        binding.transactionsRV.adapter = transactionsAdapter
    }

    private fun initUI() {
        transactionsAdapter.submitList(Mock.getTransactions())

        with(binding) {
            backArrowIV.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
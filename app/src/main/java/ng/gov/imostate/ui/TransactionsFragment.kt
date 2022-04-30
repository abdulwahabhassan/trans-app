package ng.gov.imostate.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.util.Mock
import ng.gov.imostate.model.domain.Transaction
import ng.gov.imostate.adapter.TransactionsAdapter
import ng.gov.imostate.databinding.FragmentTransactionsBinding
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import ng.gov.imostate.viewmodel.HomeFragmentViewModel
import ng.gov.imostate.viewmodel.TransactionsFragmentViewModel
import timber.log.Timber
import www.sanju.motiontoast.MotionToastStyle
import javax.inject.Inject

@AndroidEntryPoint
class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var transactionsAdapter: TransactionsAdapter
    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    //view model
    private lateinit var viewModel: TransactionsFragmentViewModel

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

        viewModel = ViewModelProvider(
            this,
            appViewModelFactory
        ).get(TransactionsFragmentViewModel::class.java)

        initRV()

        initUI()
    }

    private fun initRV() {
        transactionsAdapter = TransactionsAdapter{ position: Int, transaction: Transaction ->

        }
        binding.transactionsRV.adapter = transactionsAdapter
    }

    private fun initUI() {

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val result = viewModel.getInitialUserPreferences().token?.let { token ->
                    viewModel.getTransactions(token)
                }!!
                when (result) {
                    is ViewModelResult.Success -> {
                        Timber.d("${result.data?.transactions}")
                        transactionsAdapter.submitList(Mock.getTransactions())
                    }
                    is ViewModelResult.Error -> {
                        AppUtils.showToast(requireActivity(), result.errorMessage, MotionToastStyle.ERROR)
                    }
                }
            }
        }

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
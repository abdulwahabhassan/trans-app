package ng.gov.imostate.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.R
import ng.gov.imostate.adapter.FundWalletAccountDetailsAdapter
import ng.gov.imostate.databinding.FragmentFundWalletBinding
import ng.gov.imostate.model.domain.AccountDetails
import ng.gov.imostate.model.request.FundWalletAccountDetailsRequest
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.util.Mock
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import ng.gov.imostate.viewmodel.FundWalletFragmentViewModel
import timber.log.Timber
import www.sanju.motiontoast.MotionToastStyle
import javax.inject.Inject


@AndroidEntryPoint
class FundWalletFragment : Fragment() {

    private var _binding: FragmentFundWalletBinding? = null
    private val binding get() = _binding!!
    private lateinit var fundWalletAccountDetailsAdapter: FundWalletAccountDetailsAdapter
    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    //view model
    private lateinit var viewModel: FundWalletFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFundWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, appViewModelFactory)
            .get(FundWalletFragmentViewModel::class.java)

        initRV()

        initUI()
    }


    private fun initRV() {
        fundWalletAccountDetailsAdapter = FundWalletAccountDetailsAdapter{ position: Int, accountDetails: AccountDetails ->

        }
        binding.fundWalletAccountDetailsRV.adapter = fundWalletAccountDetailsAdapter
    }

    private fun initUI() {
        with(binding) {
            backArrowIV.setOnClickListener {
                findNavController().popBackStack()
            }
            madePaymentBTN.setOnClickListener {
                findNavController().popBackStack(R.id.homeFragment, false)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                AppUtils.showView(false, binding.fundWalletAccountDetailsRV)
                AppUtils.showProgressIndicator(true, binding.rvProgressIndicator)
                val result = viewModel.getInitialUserPreferences().token?.let { token ->
                    viewModel.getFundWalletAccountDetails(token, FundWalletAccountDetailsRequest(""))
                }!!
                when (result) {
                    is ViewModelResult.Success -> {
                        Timber.d("${result.data?.accounts}")
                        fundWalletAccountDetailsAdapter.submitList(Mock.getFundWalletAccountDetails())
                        AppUtils.showView(true, binding.fundWalletAccountDetailsRV)
                    }
                    is ViewModelResult.Error -> {
//                        AppUtils.showToast(requireActivity(), result.errorMessage, MotionToastStyle.ERROR)
//                        AppUtils.showView(false, binding.fundWalletAccountDetailsRV)

                        //debug use only
                        fundWalletAccountDetailsAdapter.submitList(Mock.getFundWalletAccountDetails())
                        AppUtils.showView(true, binding.fundWalletAccountDetailsRV)
                        //
                    }
                }
                AppUtils.showProgressIndicator(false, binding.rvProgressIndicator)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
package ng.gov.imostate.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ng.gov.imostate.R
import ng.gov.imostate.databinding.FragmentHomeBinding
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import ng.gov.imostate.viewmodel.HomeFragmentViewModel
import timber.log.Timber
import www.sanju.motiontoast.MotionToastStyle
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var connectionState: Boolean = false

    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    //view model
    private lateinit var viewModel: HomeFragmentViewModel

    private fun updateNetworkStatusUI(isConnected: Boolean?) {
        if (isConnected == true) {
            binding.networkStateTV.text = "Online"
            binding.networkStateTV.setTextColor(resources.getColor(R.color.white))
            binding.networkStateTV.setBackgroundResource(R.drawable.connectivity_online_bg)
        } else {
            binding.networkStateTV.text = "Offline"
            binding.networkStateTV.setTextColor(resources.getColor(R.color.black))
            binding.networkStateTV.setBackgroundResource(R.drawable.connectivity_offline_bg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //override on back pressed
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })
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

        Timber.d("Home fragment started")

        viewModel = ViewModelProvider(
            this,
            appViewModelFactory
        ).get(HomeFragmentViewModel::class.java)

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.connectionState.collect { viewModelResult ->
                updateNetworkStatusUI(viewModelResult.data)
            }
        }

        updateNetworkStatusUI(connectionState)

        initUI()
    }

    private fun initUI() {

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            when(val viewModelResult = viewModel.getUserPreferences().token?.let { viewModel.getDashBoardMetrics(it) }) {
                is ViewModelResult.Success -> {
                    val dashBoardMetrics = viewModelResult.data?.metrics
                    with(binding) {
                        walletBalanceTV.text = dashBoardMetrics?.currentBalance
                            ?.let { "₦${AppUtils.formatCurrency(it)}" }
                        totalCreditedTV.text = dashBoardMetrics?.totalAmountCredited
                            ?.let { "₦${AppUtils.formatCurrency(it)}" }
                        totalVendedTV.text = dashBoardMetrics?.totalAmountVended
                            ?.let { "₦${AppUtils.formatCurrency(it)}" }
                    }
                }
                is ViewModelResult.Error -> {
                    AppUtils.showToast(requireActivity(), viewModelResult.errorMessage, MotionToastStyle.ERROR)
                }
            }
        }

        with(binding) {
            scanVehicleBTN.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToVehicleDetailsDialogFragment()
                findNavController().navigate(action)
            }
            addVehicleBTN.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToAddVehicleFragment()
                findNavController().navigate(action)
            }
            transactionsBTN.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToTransactionsFragment()
                findNavController().navigate(action)
            }
            updatesBTN.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToUpdatesFragment()
                findNavController().navigate(action)
            }
            moreBTN.setOnClickListener {

            }
            paymentBTN.setOnClickListener {

            }
            dailyRatesTV.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToDailyRatesDialogFragment()
                findNavController().navigate(action)
            }
            userPhotoIV.setOnClickListener{
                val intent = Intent(requireContext(), ProfileActivity::class.java)
                startActivity(intent)
            }
        }


    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenResumed {
            if (viewModel.getUserPreferences().loggedIn == false) {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
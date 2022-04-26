package ng.gov.imostate.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ng.gov.imostate.R
import ng.gov.imostate.util.Mock
import ng.gov.imostate.model.Transaction
import ng.gov.imostate.adapter.TransactionsAdapter
import ng.gov.imostate.databinding.FragmentHomeBinding
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.util.NetworkConnectivityUtil
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import ng.gov.imostate.viewmodel.HomeFragmentViewModel
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var connectionState: Boolean = false

    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    //view model
    lateinit var viewModel: HomeFragmentViewModel

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
}
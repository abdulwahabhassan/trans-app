package ng.gov.imostate.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ng.gov.imostate.Mapper
import ng.gov.imostate.R
import ng.gov.imostate.databinding.FragmentHomeBinding
import ng.gov.imostate.model.domain.TransactionType
import ng.gov.imostate.model.request.CreateSyncTransactionsRequest
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

    private fun updateUI(isConnected: Boolean) {
        updateNetworkStatusUI(isConnected)
        if (isConnected) {
            getDashboardMetrics()
            syncDatabase()
            getCurrentUser()
        }
    }

    private fun syncDatabase() {
        getPreOnboardedVehicleRecords()
        syncTransactionRecords()
    }

    private fun getCurrentUser() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            val viewModelResult = viewModel.getInitialUserPreferences().token?.let { token ->
                viewModel.getCurrentUser(token)
            }!!
            when (viewModelResult) {
                is ViewModelResult.Success -> {}
                is ViewModelResult.Error -> {
                    Timber.d(viewModelResult.errorMessage)
                    AppUtils.showToast(requireActivity(), viewModelResult.errorMessage, MotionToastStyle.ERROR)
                }
            }
        }

    }

    private fun getPreOnboardedVehicleRecords() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            //get pre-registered vehicles from pre-existing data records
            when (val viewModelResult = viewModel.getInitialUserPreferences().token?.let { viewModel.getAllVehicles(it) }!!) {
                is ViewModelResult.Success -> {
                    val vehicles = viewModelResult.data?.vehicles
                    if (!vehicles.isNullOrEmpty()) {
                        Timber.d("vehicles from pre-existing database: ${viewModelResult.data}")
                        //insert pre-registered vehicles from pre-existing data records to app's database
                        viewModel.insertVehiclesToDatabase(Mapper.mapListOfVehicleToListOfVehicleEntity(vehicles))
                    }
                }
                is ViewModelResult.Error -> {
                    Timber.d(viewModelResult.errorMessage)
                    AppUtils.showToast(requireActivity(), viewModelResult.errorMessage, MotionToastStyle.ERROR)
                }
            }
        }
    }

    private fun syncTransactionRecords() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            //get all vehicle transactions saved to database and sync to remote database/server
            val transactions = Mapper.mapListOfTransactionEntityToListOfTransaction(
                viewModel.getAllTransactionsInDatabase()
            )

            Timber.d("transactions in database: $transactions")

            if (transactions.isNotEmpty()) {
                when (val viewModelResult = viewModel.getInitialUserPreferences().token?.let { token ->
                    viewModel.createSyncTransactions(token, CreateSyncTransactionsRequest(transactions))
                }!!) {
                    is ViewModelResult.Success -> {
                        Timber.d("synced transactions: ${viewModelResult.data}")
                        AppUtils.showToast(
                            requireActivity(),
                            "${viewModelResult.data?.status}",
                            MotionToastStyle.INFO
                        )
                        //update time last synced
                        val lastSyncTime = AppUtils.getCurrentFullDateTime()
                        viewModel.updateLastSyncTime(lastSyncTime)
                        //delete synced transactions from app's database
                        viewModel.deleteAllTransactionsInDatabase(
                            Mapper.mapListOfTransactionToListOfTransactionEntity(transactions)
                        )
                    }
                    is ViewModelResult.Error -> {
                        Timber.d("synced transactions error: ${viewModelResult.errorMessage}")
                        AppUtils.showToast(requireActivity(), viewModelResult.errorMessage, MotionToastStyle.ERROR)

                    }
                }
            }
        }
    }

    private fun updateNetworkStatusUI(isConnected: Boolean) {
        if (isConnected) {
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
                updateUI(viewModelResult.data)
            }
        }

        updateUI(connectionState)

        initUI()
    }

    private fun initUI() {

        observeLastSyncTime()

        getDashboardMetrics()

        with(binding) {
            scanVehicleBTN.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToFindVehicleDialogFragment()
                findNavController().navigate(action)
            }
            addVehicleBTN.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToAddVehicleFragment()
                findNavController().navigate(action)
            }
            transactionsBTN.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToTransactionsFragment(
                    TransactionType.AGENT_TRANSACTION.name,
                    null
                )
                findNavController().navigate(action)
            }
            updatesBTN.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToUpdatesFragment()
                findNavController().navigate(action)
            }
            dailyRatesBTN.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToDailyRatesDialogFragment()
                findNavController().navigate(action)
            }
            paymentBTN.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToPaymentFragment()
                findNavController().navigate(action)
            }
            userPhotoIV.setOnClickListener{
                val intent = Intent(requireContext(), ProfileActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun observeLastSyncTime() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.userPreferences.collect { userPreferences ->
                    when (userPreferences) {
                        is ViewModelResult.Success -> {
                            binding.lastSyncTV.text = "Last synced: ${userPreferences.data.lastSyncTime ?:
                            viewModel.getInitialUserPreferences().lastSyncTime ?: ""}"
                        }
                        is ViewModelResult.Error -> {
                            Timber.d("${userPreferences.errorMessage}")
                            AppUtils.showToast(
                                requireActivity(),
                                userPreferences.errorMessage,
                                MotionToastStyle.ERROR
                            )
                        }
                    }

                }
            }
        }
    }

    private fun getDashboardMetrics() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            when(val viewModelResult = viewModel.getInitialUserPreferences().token?.let { viewModel.getDashBoardMetrics(it) }!!) {
                is ViewModelResult.Success -> {
                    val dashBoardMetrics = viewModelResult.data?.metrics
                    with(binding) {
                        walletBalanceTV.text = dashBoardMetrics?.metrics?.currentBalance
                            ?.let { "₦${AppUtils.formatCurrency(it)}" }
                        totalCreditedTV.text = dashBoardMetrics?.metrics?.totalAmountCredited
                            ?.let { "₦${AppUtils.formatCurrency(it)}" }
                        totalVendedTV.text = dashBoardMetrics?.metrics?.totalAmountVended
                            ?.let { "₦${AppUtils.formatCurrency(it)}" }
                       paidOutTV.text = dashBoardMetrics?.metrics?.paidOut
                            ?.let { "₦${AppUtils.formatCurrency(it)}" }
                    }
                }
                is ViewModelResult.Error -> {
                    Timber.d(viewModelResult.errorMessage)
                    AppUtils.showToast(requireActivity(), viewModelResult.errorMessage, MotionToastStyle.ERROR)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenResumed {
            if (viewModel.getInitialUserPreferences().loggedIn == false) {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
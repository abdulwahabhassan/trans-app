package ng.gov.imostate.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
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
import ng.gov.imostate.ui.activity.ProfileActivity
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import ng.gov.imostate.viewmodel.HomeFragmentViewModel
import timber.log.Timber
import www.sanju.motiontoast.MotionToastStyle
import java.util.*
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
            syncDatabase()
            getCurrentUser()
            getDashboardMetrics()
        }
    }

    private fun syncDatabase() {
        getVehicleRecords()
        syncTransactionRecords()
    }

    private fun getCurrentUser() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.getInitialUserPreferences().token?.let { token ->
                //keep app up to date with latest rates, routes and agent's current wallet balance
                viewModel.getCurrentUser(token)
            }
        }

    }

    private fun getVehicleRecords() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            //get current vehicles from current enumeration data records
            when (val viewModelResult = viewModel.getInitialUserPreferences().token?.let {
                viewModel.getAllVehiclesFromCurrentEnumeration(it)
            }!!) {
                is ViewModelResult.Success -> {
                    val vehicles = viewModelResult.data?.vehicles
                    if (!vehicles.isNullOrEmpty()) {
                        Timber.d("vehicles from current enumeration database: ${viewModelResult.data}")
                        //insert vehicles from current enumeration data records to app's database
                        viewModel.insertVehiclesFromCurrentEnumerationToDatabase(
                            Mapper.mapListOfVehicleToListOfVehicleCurrentEntity(vehicles)
                        )

                        //using the records from current enumeration to populate the previous
                        //enumeration vehicle table database
                        //TO BE REMOVED LATER!!
                        viewModel.insertVehiclesFromPreviousEnumerationToDatabase(
                            Mapper.mapListOfVehicleToListOfVehiclePreviousEntity(vehicles)
                        )
                    }
                }
                is ViewModelResult.Error -> {
                    Timber.d(viewModelResult.errorMessage)
                    AppUtils.showToast(requireActivity(), viewModelResult.errorMessage, MotionToastStyle.ERROR)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            val lastVehicleId = viewModel.getLastVehicleIdFromPreviousEnumerationInDatabase()
            Timber.d("last vehicle id: $lastVehicleId")
            //get vehicles from previous enumeration data records
            when (val viewModelResult = viewModel.getInitialUserPreferences().token?.let {
                viewModel.getAllVehiclesFromPreviousEnumeration(it, lastVehicleId ?: 0)
            }!!) {
                is ViewModelResult.Success -> {
                    val vehicles = viewModelResult.data?.vehicles
                    if (!vehicles.isNullOrEmpty()) {
                        Timber.d("vehicles from previous enumeration database: ${viewModelResult.data}")
                        //insert vehicles from previous enumeration data records to app's database
                        viewModel.insertVehiclesFromPreviousEnumerationToDatabase(
                            Mapper.mapListOfVehicleToListOfVehiclePreviousEntity(vehicles)
                        )
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
                        //AppUtils.showToast(requireActivity(), viewModelResult.errorMessage, MotionToastStyle.ERROR)

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

        viewModel = ViewModelProvider(
            this,
            appViewModelFactory
        ).get(HomeFragmentViewModel::class.java)

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.connectionState.collect { viewModelResult ->
                updateUI(viewModelResult.data)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.serverTime.collect { serverTime ->
                Timber.d(serverTime)
                if (serverTime.isNotEmpty() && AppUtils.getCurrentDate() != serverTime.substring(0, 10)) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Incorrect date detected")
                        .setMessage(
                            "Adjust device current date in settings"
                        )
                        .setPositiveButton("Ok") { dialog, _ ->
                            dialog.dismiss()
                            startActivity(Intent(Settings.ACTION_DATE_SETTINGS))
                            activity?.finish()
                        }
                        .setCancelable(false)
                        .show()
                }
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
                val action =
                    HomeFragmentDirections.actionHomeFragmentToFindVehicleDialogFragment()
                findNavController().navigate(action)
            }
            addVehicleBTN.setOnClickListener {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToAddVehicleFragment()
                findNavController().navigate(action)
            }
            transactionsBTN.setOnClickListener {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToTransactionsFragment(
                        TransactionType.AGENT_TRANSACTION.name,
                        null
                    )
                findNavController().navigate(action)
            }
            updatesBTN.setOnClickListener {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToUpdatesFragment()
                findNavController().navigate(action)
            }
            dailyRatesBTN.setOnClickListener {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToDailyRatesFragment()
                findNavController().navigate(action)
            }
            paymentBTN.setOnClickListener {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToPaymentFragment()
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
                            when {
                                !userPreferences.data.lastSyncTime.isNullOrEmpty() -> {
                                    binding.lastSyncTV.text = "Last synced: ${userPreferences.data.lastSyncTime}"
                                    binding.lastSyncTV.visibility = VISIBLE
                                }
                                !viewModel.getInitialUserPreferences().lastSyncTime.isNullOrEmpty() -> {
                                    binding.lastSyncTV.text = "Last synced: ${viewModel.getInitialUserPreferences().lastSyncTime}"
                                    binding.lastSyncTV.visibility = VISIBLE
                                }
                                else -> {
                                    binding.lastSyncTV.visibility = GONE
                                }
                            }
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
                    Timber.d("${viewModelResult.data}")
                    val dashBoardMetrics = viewModelResult.data?.metrics
                    //display current wallet info to dashboard
                    with(binding) {
                        walletBalanceTV.text = dashBoardMetrics?.metrics?.currentBalance
                            ?.let { "₦${AppUtils.formatCurrency(it)}" }
                        totalCreditedTV.text = dashBoardMetrics?.metrics?.totalAmountCredited
                            ?.let { "₦${AppUtils.formatCurrency(it)}" }
                        totalVendedTV.text = dashBoardMetrics?.metrics?.totalAmountVended
                            ?.let { "₦${AppUtils.formatCurrency(it)}" }
                       currentPayableTV.text = dashBoardMetrics?.metrics?.currentPayable
                            ?.let { "₦${AppUtils.formatCurrency(it)}" }
                    }
                }
                is ViewModelResult.Error -> {
                    Timber.d(viewModelResult.errorMessage)
                    AppUtils.showToast(requireActivity(), viewModelResult.errorMessage, MotionToastStyle.ERROR)
                    //display previously cached wallet info from last sync to dashboard
                    val prefs = viewModel.getInitialUserPreferences()
                    with(binding) {
                        walletBalanceTV.text = prefs.currentWalletBalance
                            ?.let { "₦${AppUtils.formatCurrency(it)}" }
                        totalCreditedTV.text = prefs.currentTotalCredited
                            ?.let { "₦${AppUtils.formatCurrency(it)}" }
                        totalVendedTV.text = prefs.currentTotalVended
                            ?.let { "₦${AppUtils.formatCurrency(it)}" }
                        currentPayableTV.text = prefs.currentPayable
                            ?.let { "₦${AppUtils.formatCurrency(it)}" }
                    }
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
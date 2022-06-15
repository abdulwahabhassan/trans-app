package ng.gov.imostate.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ng.gov.imostate.R
import ng.gov.imostate.databinding.FragmentSuccessBinding
import ng.gov.imostate.model.domain.Data
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.*
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class SuccessFragment : Fragment() {

    //nav args
    private val args: SuccessFragmentArgs by navArgs()
    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!
    private val sharedNfcViewModel: SharedNfcViewModel by activityViewModels()
    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    //view model
    private lateinit var viewModel: SuccessFragmentViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //override on back pressed
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showConfirmExitDialog()
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeNfcMode()

        observeNfcSyncMode()

        viewModel = ViewModelProvider(
            this,
            appViewModelFactory
        ).get(SuccessFragmentViewModel::class.java)

        with(binding) {
            syncTagBTN.setOnClickListener {
                AppUtils.showProgressIndicator(true, binding.progressIndicator)
                AppUtils.showView(false, binding.syncTagBTN)

                val nfcMode = sharedNfcViewModel.getNfcMode()
                if (nfcMode == NfcMode.WRITE.name) {
                    sharedNfcViewModel.setNfcMode(NfcMode.READ)
                } else {
                    sharedNfcViewModel.setData(
                        Data(
                            args.vehicleId,
                            AppUtils.encryptLastPaidDate(args.lastPayDate.toString()),
                            args.vehicleCategory.toString(),
                            args.vehiclePlatesNumber.toString()
                        )
                    )
                    Timber.d("lastpaydatesuccessfragment: ${args.lastPayDate}")
                    sharedNfcViewModel.setNfcMode(NfcMode.WRITE)
                }
                AppUtils.showProgressIndicator(false, binding.progressIndicator)
                AppUtils.showView(true, binding.syncTagBTN)
            }
        }

    }

    private fun observeNfcMode() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                sharedNfcViewModel.nfcMode.collect { nfcMode ->
                    if (nfcMode == NfcMode.WRITE.name) {
                        binding.nfcWriteModeLAV.playAnimation()
                        binding.nfcWriteModeLAV.visibility = VISIBLE
                        binding.syncTagBTN.text = "STOP SYNC"
                        binding.successLAV.visibility = INVISIBLE
                    } else {
                        binding.nfcWriteModeLAV.pauseAnimation()
                        binding.nfcWriteModeLAV.visibility = INVISIBLE
                        binding.syncTagBTN.text =
                            when {
                                binding.successTV.text.toString() == "Successfully Synced Tag" -> {
                                    binding.successLAV.visibility = VISIBLE
                                    "RE-SYNC TAG"
                                }
                                binding.successTV.text.toString() == "Failed to Synced Tag" -> {
                                    binding.successLAV.visibility = INVISIBLE
                                    "SYNC TAG"
                                }
                                else -> {
                                    binding.successLAV.visibility = VISIBLE
                                    "SYNC TAG"
                                }
                            }

                    }
                }
            }
        }
    }

    private fun observeNfcSyncMode() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                sharedNfcViewModel.nfcSyncMode.collect { nfcSyncMode ->
                    if (nfcSyncMode == NfcSyncMode.SYNCED.name) {
                        binding.successTV.text = "Successfully Synced Tag"
                        binding.successTV.setTextColor(getResources().getColor(R.color.green))
                        binding.syncTagBTN.text = "RE-SYNC TAG"
                        binding.successLAV.visibility = VISIBLE
                        binding.successLAV.playAnimation()
                        Timber.d("Synced")
                    } else if (nfcSyncMode == NfcSyncMode.ERROR.name) {
                        binding.successTV.text = "Failed to Synced Tag"
                        binding.successTV.setTextColor(getResources().getColor(R.color.red))
                        binding.syncTagBTN.text = "RE-SYNC TAG"
                        binding.successLAV.visibility = INVISIBLE
                        Timber.d("syn Error")
                    } else if (nfcSyncMode == NfcSyncMode.UNSYNCED.name) {
                        Timber.d("Unsynced")
                    }
                    //reset sync mode to un-synced
                    sharedNfcViewModel.setNfcSyncMode(NfcSyncMode.UNSYNCED)
                }
            }
        }
    }

    fun showConfirmExitDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Info")
            .setMessage("Please confirm that you have successfully synced tag?")
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Yes") { dialog, _ ->
                sharedNfcViewModel.setNfcMode(NfcMode.READ)
                dialog.dismiss()
                findNavController().popBackStack()
            }.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
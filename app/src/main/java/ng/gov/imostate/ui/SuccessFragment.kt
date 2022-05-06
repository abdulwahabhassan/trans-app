package ng.gov.imostate.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ng.gov.imostate.databinding.FragmentSuccessBinding
import ng.gov.imostate.model.domain.Data
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.*
import www.sanju.motiontoast.MotionToastStyle
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

        viewModel = ViewModelProvider(
            this,
            appViewModelFactory
        ).get(SuccessFragmentViewModel::class.java)

        with(binding) {
            syncTagBTN.setOnClickListener {
                //do network call to retrieve last transaction for this vehicle id (navArgs.id)
                //if successfully retrieved, set sharedNfcViewModel data
                viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                    AppUtils.showProgressIndicator(true, binding.progressIndicator)
                    AppUtils.showView(false, binding.syncTagBTN)
                    val result = viewModel.getInitialUserPreferences().token?.let { token ->
                        viewModel.getRecentTransaction(
                            token, args.id)
                    }!!

                    //placeholder code
                    val nfcMode = sharedNfcViewModel.getNfcMode()
                    if (nfcMode == NfcMode.WRITE.name) {
                        sharedNfcViewModel.setNfcMode(NfcMode.READ)
                    } else {
                        //sharedNfcViewModel.setData(result.data.tagData)
                        sharedNfcViewModel.setData(
                            Data(args.driverName.toString(),
                                args.id, args.lastPayDate.toString(),
                                args.outstandingBalance?.toDouble() ?: 0.00
                            )
                        )
                        sharedNfcViewModel.setNfcMode(NfcMode.WRITE)
                    }
                    AppUtils.showProgressIndicator(false, binding.progressIndicator)
                    AppUtils.showView(true, binding.syncTagBTN)
                }

            }
        }

    }

    private fun observeNfcMode() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                sharedNfcViewModel.nfcMode.collect { nfcMode ->
                    if (nfcMode == NfcMode.WRITE.name) {
                        binding.nfcWriteModeLAV.playAnimation()
                        binding.nfcWriteModeLAV.visibility = View.VISIBLE
                        binding.syncTagBTN.text = "STOP SYNC"
                    } else {
                        binding.nfcWriteModeLAV.pauseAnimation()
                        binding.nfcWriteModeLAV.visibility = View.INVISIBLE
                        binding.syncTagBTN.text = "SYNC TAG"
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
package ng.gov.imostate.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.Mapper
import ng.gov.imostate.adapter.DailyRatesAdapter
import ng.gov.imostate.databinding.FragmentDailyRatesBinding
import ng.gov.imostate.model.domain.Rate
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import ng.gov.imostate.viewmodel.DailyRatesDialogFragmentViewModel
import timber.log.Timber
import www.sanju.motiontoast.MotionToastStyle
import javax.inject.Inject


@AndroidEntryPoint
class DailyRatesFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDailyRatesBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    private lateinit var dailyRatesAdapter: DailyRatesAdapter
    //view model
    lateinit var viewModel: DailyRatesDialogFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDailyRatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            appViewModelFactory
        ).get(DailyRatesDialogFragmentViewModel::class.java)

        with(binding) {
            dailyRatesTV.text = AppUtils.getCurrentFullDate()

            backArrowIV.setOnClickListener {
                findNavController().popBackStack()
            }

        }

        initRV()

        initUI()
    }

    private fun initRV() {
        dailyRatesAdapter = DailyRatesAdapter{ position: Int, rate: Rate ->

        }
        binding.dailyRatesRV.adapter = dailyRatesAdapter
    }

    private fun initUI() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                AppUtils.showView(false, binding.dailyRatesRV)
                AppUtils.showProgressIndicator(true, binding.progressIndicator)
                when (val result = viewModel.getRates()) {
                    is ViewModelResult.Success -> {
                        Timber.d("${Mapper.mapListOfRateEntityToListOfRate(result.data)}")
                        dailyRatesAdapter.submitList(
                            Mapper.mapListOfRateEntityToListOfRate(result.data).distinctBy { it.category }
                        )
                        AppUtils.showView(true, binding.dailyRatesRV)
                    }
                    is ViewModelResult.Error -> {
                        AppUtils.showToast(requireActivity(), result.errorMessage, MotionToastStyle.ERROR)
                        AppUtils.showView(false, binding.dailyRatesRV)
                    }
                }
                AppUtils.showProgressIndicator(false, binding.progressIndicator)
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
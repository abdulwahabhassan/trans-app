package ng.gov.imostate.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.Mapper
import ng.gov.imostate.adapter.UpdatesAdapter
import ng.gov.imostate.database.entity.UpdateEntity
import ng.gov.imostate.databinding.FragmentUpdatesBinding
import ng.gov.imostate.model.domain.Update
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.util.Mock
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import ng.gov.imostate.viewmodel.HomeFragmentViewModel
import ng.gov.imostate.viewmodel.NfcMode
import ng.gov.imostate.viewmodel.UpdatesFragmentViewModel
import timber.log.Timber
import www.sanju.motiontoast.MotionToastStyle
import javax.inject.Inject

@AndroidEntryPoint
class UpdatesFragment : Fragment() {

    private var _binding: FragmentUpdatesBinding? = null
    private val binding get() = _binding!!
    private lateinit var updatesAdapter: UpdatesAdapter
    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    //view model
    private lateinit var viewModel: UpdatesFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            appViewModelFactory
        ).get(UpdatesFragmentViewModel::class.java)

        initUI()

        initRV()

        Timber.d(
            "${arguments?.getBoolean(MainActivity.DATA_PUSH_NOTIFICATION_KEY)} " +
                    "${arguments?.getString(MainActivity.DATA_PAYLOAD_TITLE_KEY)} " +
                    "${arguments?.getString(MainActivity.DATA_PAYLOAD_BODY_KEY)} " +
                    "${arguments?.getString(MainActivity.DATA_PAYLOAD_TIME_KEY)}"
        )

        val title = arguments?.getString(MainActivity.DATA_PAYLOAD_TITLE_KEY)
        val body = arguments?.getString(MainActivity.DATA_PAYLOAD_BODY_KEY)
        val time = arguments?.getString(MainActivity.DATA_PAYLOAD_TIME_KEY)
        Timber.d("$title $body $time ")

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            if(arguments?.getBoolean(MainActivity.DATA_PUSH_NOTIFICATION_KEY) == true) {
                //add new notification to app database
                viewModel.insertUpdateToDatabase(UpdateEntity(title = title, body = body, time = time))
            }
            //set updates adapter
            val updates = Mapper.mapListOfUpdateEntityToListOfUpdate(viewModel.getAllUpdatesInDatabase())
            setUpdatesAdapter(updates)
        }

    }

    private fun initRV() {
        Timber.d("Rv init")
        updatesAdapter = UpdatesAdapter{ position: Int, itemAtPosition: Update ->

            AlertDialog.Builder(requireContext())
                .setTitle(itemAtPosition.title)
                .setMessage(itemAtPosition.body + "\n\n" + itemAtPosition.time)
                .setPositiveButton("Close") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        }
        binding.updatesRV.adapter = updatesAdapter
    }

    private fun initUI() {
        with(binding) {
            backArrowIV.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setUpdatesAdapter(updates: List<Update>) {
        if (updates.isNotEmpty()) {
            updatesAdapter.submitList(updates)
            AppUtils.showView(true, binding.updatesRV)
        } else {
            AppUtils.showToast(requireActivity(), "No updates available", MotionToastStyle.INFO)
        }
        AppUtils.showProgressIndicator(false, binding.progressIndicator)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
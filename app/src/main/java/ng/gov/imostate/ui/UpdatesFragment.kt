package ng.gov.imostate.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.adapter.UpdatesAdapter
import ng.gov.imostate.databinding.FragmentUpdatesBinding
import ng.gov.imostate.model.Update
import ng.gov.imostate.util.Mock
import timber.log.Timber

@AndroidEntryPoint
class UpdatesFragment : Fragment() {

    private var _binding: FragmentUpdatesBinding? = null
    private val binding get() = _binding!!
    private lateinit var updatesAdapter: UpdatesAdapter

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
        with(binding) {

            initRV()

            initUI()

        }
    }

    private fun initRV() {
        Timber.d("Rv init")
        updatesAdapter = UpdatesAdapter{ position: Int, itemAtPosition: Update ->
        }
        binding.updatesRV.adapter = updatesAdapter
    }

    private fun initUI() {
        updatesAdapter.submitList(Mock.getUpdates())

        with(binding) {
            backArrowIV.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }


}
package ng.gov.imostate.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.databinding.FragmentLoginBinding
import ng.gov.imostate.model.UserPreferences
import ng.gov.imostate.model.request.LoginRequest
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import ng.gov.imostate.viewmodel.LoginFragmentViewModel
import timber.log.Timber
import www.sanju.motiontoast.MotionToastStyle
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    //view model
    lateinit var viewModel: LoginFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            appViewModelFactory
        ).get(LoginFragmentViewModel::class.java)

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            if (viewModel.getUserPreferences().loggedIn == true) {
                val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                findNavController().navigate(action)
            }

        }

        with(binding) {

            if (AppUtils.isDebugBuild()) {
                userEmailET.setText("agent@gmail.com")
                passwordET.setText("3hKTqCCs")
            }

            loginBTN.setOnClickListener {
                val email = userEmailET.text.toString()
                val password = passwordET.text.toString()
                Timber.d("$email $password")
                LoginRequest(
                    email,
                    password
                )
                viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                    showProgressIndicator(true)
                    val viewModelResult = viewModel.loginAttendant(LoginRequest(email, password))
                    Timber.d("$viewModelResult")
                    when (viewModelResult) {
                        is ViewModelResult.Success -> {
                            AppUtils.showToast(requireActivity(), "Successfully logged in", MotionToastStyle.SUCCESS)

                            val token = viewModelResult.data?.token
                            val user = viewModelResult.data?.user
                            val userPreferences = UserPreferences(
                                token = "Bearer ${token ?: ""}",
                                agentName = user?.name ?: "",
                                agentId = user?.id ?: 0,
                                businessName = user?.businessName ?: "",
                                agentFirstName = user?.agentFirstName ?: "",
                                agentMiddleName = user?.agentLastName ?: "",
                                agentLastName = user?.agentLastName ?: "",
                                type = user?.type ?: "",
                                phone = user?.phone ?: "",
                                address = user?.address ?: "",
                                onboardingDate = user?.onboardingDate ?: "",
                                email = user?.email ?: "",
                                emailVerifiedAt = user?.emailVerifiedAt ?: "",
                                status = user?.status ?: "",
                                createdBy = user?.createdBy ?: 0,
                                createdAt = user?.createdAt ?: "",
                                updatedAt = user?.updatedAt ?: "",
                                bvn = user?.bvn ?: ""
                            )

                            viewModel.updateUserPreference(userPreferences)

                            val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                            findNavController().navigate(action)
                        }
                        is ViewModelResult.Error -> {
                            AppUtils.showToast(requireActivity(), viewModelResult.errorMessage, MotionToastStyle.ERROR)
                            showProgressIndicator(false)
                        }
                    }
                }

            }
        }
    }

    private fun showProgressIndicator(show: Boolean) {
        if (show) {
            binding.loginBTN.visibility = INVISIBLE
            binding.progressIndicator.visibility = VISIBLE
        } else {
            binding.loginBTN.visibility = VISIBLE
            binding.progressIndicator.visibility = INVISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
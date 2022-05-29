package ng.gov.imostate.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.databinding.FragmentLoginBinding
import ng.gov.imostate.model.request.LoginRequest
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.UserPreferencesRepository
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
            if (viewModel.getInitialUserPreferences().loggedIn == true) {
                val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                findNavController().navigate(action)
            } else {
                AppUtils.showView(true, binding.loginBTN)
                AppUtils.showProgressIndicator(false, binding.progressIndicator)
            }

        }

        with(binding) {

            if (AppUtils.isDebugBuild()) {
                userEmailET.setText("hassanabdulwahab@gmail.com")
                passwordET.setText("XpGjSzRM")
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
                    AppUtils.showProgressIndicator(true, binding.progressIndicator)
                    AppUtils.showView(false, binding.loginBTN)
                    val viewModelResult = viewModel.loginAttendant(LoginRequest(email, password))
                    Timber.d("$viewModelResult")
                    when (viewModelResult) {
                        is ViewModelResult.Success -> {
                            AppUtils.showToast(requireActivity(), "Successful login", MotionToastStyle.SUCCESS)

                            val token = viewModelResult.data?.token
                            val user = viewModelResult.data?.user
                            val userPreferences = UserPreferencesRepository.UserPreferences(
                                token = "Bearer ${token ?: ""}",
                                loggedIn = true,
                                agentName = user?.name,
                                agentId = user?.id,
                                photo = user?.profile?.photo,
                                agentFirstName = user?.profile?.agentFirstName,
                                agentMiddleName = user?.profile?.agentLastName,
                                agentLastName = user?.profile?.agentLastName,
                                type = user?.type,
                                phone = user?.phone,
                                address = user?.address,
                                onboardingDate = user?.onboardingDate,
                                email = user?.email,
                                emailVerifiedAt = user?.emailVerifiedAt,
                                status = user?.status,
                                bvn = user?.bvn
                            )

                            viewModel.updateUserPreference(userPreferences)

                            //keep user logged in until they log out
                            viewModel.updateAgentLogInStatus(true)

                            val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                            findNavController().navigate(action)
                        }
                        is ViewModelResult.Error -> {
                            AppUtils.showToast(requireActivity(), viewModelResult.errorMessage, MotionToastStyle.ERROR)
                            AppUtils.showProgressIndicator(false, binding.progressIndicator)
                            AppUtils.showView(true, binding.loginBTN)
                        }
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
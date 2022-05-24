package ng.gov.imostate.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ng.gov.imostate.contract.GalleryActivityContract
import ng.gov.imostate.databinding.ActivityProfileBinding
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import ng.gov.imostate.viewmodel.ProfileActivityViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import www.sanju.motiontoast.MotionToastStyle
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var image: String? = null
    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    private lateinit var activityViewModel: ProfileActivityViewModel
    private val openGallery = registerForActivityResult(GalleryActivityContract()) { imagePath ->
        if (imagePath != null) {
            AppUtils.showToast(this, "Selection confirmed, updating..", MotionToastStyle.INFO)
            image = imagePath
            displaySelectedPhoto(imagePath)
        } else {
            AppUtils.showToast(this, "No image selected", MotionToastStyle.INFO)
        }

    }

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> if (isGranted) { enableImageSelection() } else {
        val showRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        if (showRationale) {
            requestPermission()
        } else {
            goToSettings()
        }
    }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityViewModel = ViewModelProvider(
            this,
            appViewModelFactory
        ).get(ProfileActivityViewModel::class.java)

        enableImageSelection()

        with(binding) {
            backArrowIV.setOnClickListener {
                finish()
            }
            uploadIV.setOnClickListener {
                if (haveStoragePermission()) {
                    openGallery.launch(Unit)
                } else {
                    requestPermission()
                }
            }

            logoutTV.setOnClickListener {
               lifecycleScope.launchWhenResumed {
                   activityViewModel.updateAgentLogInStatus(false)
                   finish()
               }
            }

            lifecycleScope.launchWhenResumed {
                val user = activityViewModel.getInitialUserPreferences()
                //userPhotoIV.setImageResource()
                onBoardingDateTV.text = if (user.onboardingDate.isNullOrEmpty()) "_" else AppUtils.formatDateToFullDate(user.onboardingDate)
                emailTV.text = if (user.email.isNullOrEmpty()) "_" else "${user.email}"
                addressTV.text = if (user.address.isNullOrEmpty()) "_" else "${user.address}"
                nameTV.text = if (user.agentName.isNullOrEmpty()) "_" else "${user.agentName}"
                phonenumberTV.text = if (user.phone.isNullOrEmpty()) "_" else "${user.phone}"
                activityViewModel.getInitialUserPreferences().token?.let { token ->
                    when (val viewModelResult = activityViewModel.getDashBoardMetrics(token)) {
                        is ViewModelResult.Success -> {
                            paidOutDateTV.text = viewModelResult.data?.metrics?.metrics?.paidOut?.let {
                                "₦${AppUtils.formatCurrency(it)}"
                            }
                        }
                        is ViewModelResult.Error -> {
                            paidOutDateTV.text = "-"
                        }
                    }
                }

            }

        }

        if (!haveStoragePermission()) {
            requestPermission()
        }
    }

    private fun requestPermission() {
        if (!haveStoragePermission()) {
            requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun haveStoragePermission() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED


    private fun goToSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${packageName}")).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent ->
            startActivity(intent)
        }
    }

    private fun enableImageSelection() {
        if (haveStoragePermission()) {
            binding.uploadIV.setOnClickListener {
                openGallery.launch(Unit)
            }
        } else {
            requestPermission()
        }
    }

    private fun displaySelectedPhoto(imageUri: String) {
        Glide.with(this).load(imageUri).into(binding.userPhotoIV)
        uploadSelectedPhoto()
    }

    private fun uploadSelectedPhoto() {
        if (image != null) {
            val file = File(image!!)
            val requestFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData("image", file.name, requestFile)
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    //upload code
                }
            }
        } else AppUtils.showToast(
            this,
            "Select an image to upload",
            MotionToastStyle.INFO)
    }


}
package ng.gov.imostate.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ng.gov.imostate.Mapper
import ng.gov.imostate.adapter.RoutesAdapter
import ng.gov.imostate.contract.GalleryActivityContract
import ng.gov.imostate.databinding.ActivityProfileBinding
import ng.gov.imostate.databinding.LayoutSelectRoutesBinding
import ng.gov.imostate.model.domain.Route
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import ng.gov.imostate.viewmodel.ProfileActivityViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import www.sanju.motiontoast.MotionToastStyle
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var activityBinding: ActivityProfileBinding
    private var image: String? = null
    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    private lateinit var activityViewModel: ProfileActivityViewModel
    private lateinit var routesAdapter: RoutesAdapter
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
        activityBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        activityViewModel = ViewModelProvider(
            this,
            appViewModelFactory
        ).get(ProfileActivityViewModel::class.java)

        enableImageSelection()

        with(activityBinding) {
            backArrowIV.setOnClickListener {
                finish()
            }

            viewRoutesTV.setOnClickListener {
                AppUtils.showProgressIndicator(true, activityBinding.progressIndicator)
                AppUtils.showView(false, activityBinding.viewRoutesTV)
                showAgentRoutesDialog()
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
                val prefs = activityViewModel.getInitialUserPreferences()
                //userPhotoIV.setImageResource()
                onBoardingDateTV.text = if (prefs.onboardingDate.isNullOrEmpty()) "_" else AppUtils.formatDateToFullDate(prefs.onboardingDate)
                emailTV.text = if (prefs.email.isNullOrEmpty()) "_" else "${prefs.email}"
                addressTV.text = if (prefs.address.isNullOrEmpty()) "_" else "${prefs.address}"
                nameTV.text = if (prefs.agentName.isNullOrEmpty()) "_" else "${prefs.agentName}"
                phonenumberTV.text = if (prefs.phone.isNullOrEmpty()) "_" else "${prefs.phone}"
                activityViewModel.getInitialUserPreferences().token?.let { token ->
                    when (val viewModelResult = activityViewModel.getDashBoardMetrics(token)) {
                        is ViewModelResult.Success -> {
                            paidOutDateTV.text = viewModelResult.data?.metrics?.metrics?.paidOut?.let {
                                "₦${AppUtils.formatCurrency(it)}"
                            }
                        }
                        is ViewModelResult.Error -> {
                            //display previously cached wallet info from last sync to dashboard
                            paidOutDateTV.text = prefs.currentPaidOut?.let {
                                "₦${AppUtils.formatCurrency(it)}"
                            }
                        }
                    }
                }
            }
        }

        if (!haveStoragePermission()) {
            requestPermission()
        }
    }

    private fun showAgentRoutesDialog() {
        val binding = LayoutSelectRoutesBinding.inflate(
            LayoutInflater.from(this),
            activityBinding.root,
            false
        )
        binding.titleTV.text = "Agent's Routes"
        val selectRoutesSheet = BottomSheetDialog(this)
        selectRoutesSheet.setContentView(binding.root)
        selectRoutesSheet.dismissWithAnimation = true
        selectRoutesSheet.setCancelable(false)

        selectRoutesSheet.show()

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                AppUtils.showToast(this@ProfileActivity, newText, MotionToastStyle.INFO)
                return true
            }

        })

        binding.doneBTN.setOnClickListener {
            AppUtils.showProgressIndicator(false, activityBinding.progressIndicator)
            AppUtils.showView(true, activityBinding.viewRoutesTV)
            selectRoutesSheet.dismiss()
        }

        lifecycleScope.launchWhenResumed {
            Timber.d("Ok")
            AppUtils.showView(false, binding.routesRV)
            routesAdapter = RoutesAdapter { _, _ -> }
            binding.routesRV.adapter = routesAdapter
            val agentRoutes = Mapper.mapListOfAgentRouteEntityToListOfAgentRoute(activityViewModel.getAllAgentRoutesInDatabase())

            Timber.d("agent routes in database $agentRoutes")

            val mappedAgentRoutes = agentRoutes.map { agentRoute -> Route(
                agentRoute.routeId,
                "",
                "",
                agentRoute.status,
                "",
                ""
            ) }

            Timber.d("mappedAgentRoutes $mappedAgentRoutes")

            val routes = Mapper.mapListOfRouteEntityToListOfRoute(activityViewModel.getAllRoutesInDatabase())

            Timber.d("routes in database $routes")

            //map and set selected state of routes to null to hide check button
            //group routes by id and filter those with counts greater than 1 then return their first
            //element
            val resolvedAgentRoutes = routes.asSequence().map { route -> route.copy(selected = null) }
                .plus(mappedAgentRoutes).groupBy { it.id }.filter { entry ->
                    entry.value.count() > 1
                }.map { entry ->
                    entry.value.first()
                }.toList()

            Timber.d("resolved agent routes $resolvedAgentRoutes")

            routesAdapter.submitList(resolvedAgentRoutes)
            AppUtils.showView(true, binding.routesRV)
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
            activityBinding.uploadIV.setOnClickListener {
                openGallery.launch(Unit)
            }
        } else {
            requestPermission()
        }
    }

    private fun displaySelectedPhoto(imageUri: String) {
        Glide.with(this).load(imageUri).into(activityBinding.userPhotoIV)
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
package ng.gov.imostate.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ng.gov.imostate.adapter.GalleryAdapter
import ng.gov.imostate.databinding.ActivityGalleryBinding
import ng.gov.imostate.model.domain.MediaStoreImage
import ng.gov.imostate.viewmodel.GalleryActivityViewModel

class GalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGalleryBinding
    private val activityViewModel: GalleryActivityViewModel by viewModels()
    private lateinit var adapter: GalleryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        adapter = GalleryAdapter { image -> returnToMainScreen(image) }
        binding.galleryRecyclerView.adapter = adapter

        activityViewModel.loadImages()

        activityViewModel.images.observe(this) { images ->
            adapter.submitList(images)
        }
    }

    private fun returnToMainScreen(image: MediaStoreImage) {
        val intent = Intent().apply {
            putExtra(IMAGE_URI, image.data)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {
        const val IMAGE_URI = "imagePath"
    }
}
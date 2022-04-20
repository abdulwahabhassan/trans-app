package ng.gov.imostate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import ng.gov.imostate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.background = null

        val bottomNavView: BottomNavigationView = binding.bottomNavigationView

        val navController = findNavController(R.id.activity_main_nav_host_fragment)

        bottomNavView.setupWithNavController(navController)

        binding.scanFab.setOnClickListener {
            findNavController(R.id.activity_main_nav_host_fragment).navigate(R.id.scanFragment)
        }

    }
}
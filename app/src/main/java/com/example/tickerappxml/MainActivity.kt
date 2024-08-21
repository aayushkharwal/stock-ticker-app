package com.example.tickerappxml

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.plcoding.animatedsplashscreen.MainViewModel
import androidx.activity.viewModels
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContext = applicationContext
        enableEdgeToEdge()

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !viewModel.isReady.value
            }
        }

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        val titleFragment = TitleFragment()
//        val homeFragment = HomeFragment()
//
//        supportFragmentManager.beginTransaction().apply {
//            replace(R.id.flFragment, titleFragment)
//            commit()
//        }
//
//        supportFragmentManager.beginTransaction().apply {
//            replace(R.id.flFragment, homeFragment)
//            commit()
//        }

//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragmentContainerView, HomeImageFragment())
//                .commitNow()
//        }


    }
}
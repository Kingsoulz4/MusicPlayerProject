package com.example.musicplayerproject.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.musicplayerproject.R
import com.example.musicplayerproject.fragments.HomeFragment
import com.example.musicplayerproject.fragments.SearchFragment

//Main screen
class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var searchButton: Button
    private lateinit var homeButton: Button
    private lateinit var libraryButton: Button
    private lateinit var settingButton: Button
    private var fragManager: FragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101)

        //Initialize UI
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        setContentView(R.layout.mainhome)
        viewFinder()
        buttonSetup()

        //By default, visible fragment will be Home
        fragmentChange(HomeFragment())
    }

    private fun viewFinder() {
        searchButton = findViewById(R.id.search)
        homeButton = findViewById(R.id.home)
        libraryButton = findViewById(R.id.library)
        settingButton = findViewById(R.id.setting)
    }

    private fun buttonSetup() {
        searchButton.setOnClickListener(this)
        homeButton.setOnClickListener(this)
        libraryButton.setOnClickListener(this)
        settingButton.setOnClickListener(this)
    }

    //Handle bottom buttons click events
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.search -> {
                fragmentChange(SearchFragment())
            }
            R.id.home -> {
                fragmentChange(HomeFragment())
            }
            R.id.library -> {
                Toast.makeText(this, "Library", Toast.LENGTH_SHORT).show()
            }
            R.id.setting -> {
                Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fragmentChange(fragment: Fragment) {
        val ftTransactions: FragmentTransaction = fragManager.beginTransaction()
        ftTransactions.replace(R.id.fragment, fragment)
        ftTransactions.commit()
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }
}
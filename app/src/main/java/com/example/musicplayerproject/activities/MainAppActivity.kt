package com.example.musicplayerproject.activities


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.musicplayerproject.R
import com.example.musicplayerproject.databinding.ActivityMainAppBinding
import com.example.musicplayerproject.fragments.HomeFragment
import com.example.musicplayerproject.fragments.SearchFragment
import com.facebook.appevents.suggestedevents.ViewOnClickListener


class MainAppActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mainAppBinding: ActivityMainAppBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainAppBinding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(mainAppBinding.root)

        supportActionBar?.hide()

        var homeFragment = HomeFragment()

        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        transaction.replace(mainAppBinding.transactionLayout.id, homeFragment)
        transaction.commit()

        handleClickListener()

    }

    private fun handleClickListener() {
        mainAppBinding.buttonHome.setOnClickListener(this)
        mainAppBinding.buttonSearch.setOnClickListener(this)
        mainAppBinding.buttonLibrary.setOnClickListener(this)
        mainAppBinding.buttonSetting.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id)
        {
            R.id.button_home -> {
                var homeFragment = HomeFragment()
                replaceFragment(homeFragment)
            }
            R.id.button_search ->{
                var searchFragment = SearchFragment()
                replaceFragment(searchFragment)
            }
            R.id.button_library -> {}
            R.id.button_setting -> {}
        }

    }

    fun replaceFragment(fragment: Fragment)
    {
        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        transaction.replace(mainAppBinding.transactionLayout.id, fragment)
        transaction.commit()
    }
}
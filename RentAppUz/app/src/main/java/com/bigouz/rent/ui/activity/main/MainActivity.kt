package com.bigouz.rent.ui.activity.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.navigation.findNavController
import com.bigouz.rent.ui.fragments.rent.FavoriteFragment
import com.bigouz.rent.ui.fragments.rent.MyRentsFragment
import com.bigouz.rent.ui.fragments.rent.RootFragment
import com.bigouz.rent.ui.fragments.rent.SettingsFragment
import com.example.rent.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fm = supportFragmentManager
        val fragRoot = RootFragment()
        val fragMyRents = MyRentsFragment()
        val fragFavorite = FavoriteFragment()
        val fragSettings = SettingsFragment()
        fm.commit {
            setReorderingAllowed(true)
            add(R.id.navHostFragment, fragRoot, "root")
            add(R.id.navHostFragment, fragMyRents, "my")
            add(R.id.navHostFragment, fragFavorite, "favorite")
            add(R.id.navHostFragment, fragSettings, "settings")
            hide(fragMyRents)
            hide(fragFavorite)
            hide(fragSettings)
        }

        bottom_navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_find -> {
                    fm.commit {
                        show(fragRoot)
                        hide(fragMyRents)
                        hide(fragFavorite)
                        hide(fragSettings)
                    }
                    true
                }
                R.id.item_post -> {
                    fm.commit {
                        hide(fragRoot)
                        show(fragMyRents)
                        hide(fragFavorite)
                        hide(fragSettings)
                    }
                    true
                }
                R.id.item_favorite -> {
                    fm.commit {
                        hide(fragRoot)
                        hide(fragMyRents)
                        show(fragFavorite)
                        hide(fragSettings)
                    }
                    true
                }
                R.id.item_settings -> {
                    fm.commit {
                        hide(fragRoot)
                        hide(fragMyRents)
                        hide(fragFavorite)
                        show(fragSettings)
                    }
                    true
                }
                else -> false
            }
        }

        bottom_navigation.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.item_find -> {
                    //block
                }
                R.id.item_post -> {
                    //block
                }
                R.id.item_favorite -> {
                    //block
                }
                R.id.item_settings -> {
                    //block
                }
                else -> {
                }
            }
        }
    }
}
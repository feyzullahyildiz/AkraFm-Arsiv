package com.feyzullahefendi.akraarsiv

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_main.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.reactivex.disposables.Disposable
import android.content.pm.PackageManager
import android.Manifest.permission
import android.Manifest.permission.*
import android.content.*
import android.os.Build
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class MainActivity : AppCompatActivity() {
    lateinit var categoryDisposable: Disposable
    lateinit var childProgramDisposable: Disposable
    lateinit var streamModelDisposable: Disposable
    val REQUEST_CODE_FOREGROUND_SERVICE = 5
    val REQUEST_CODE_PHONE_STATE= 15
    var adapter: TabAdapter? = null
    var viewPager: ViewPager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        this.viewPager = findViewById<ViewPager>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout.addTab(tabLayout.newTab().setText("Kategoriler"))
        tabLayout.addTab(tabLayout.newTab().setText("Programlar"))
        tabLayout.addTab(tabLayout.newTab().setText("Bölümler"))
        this.adapter = TabAdapter(supportFragmentManager, 3)
        this.viewPager!!.adapter = this.adapter
        this.viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager!!.currentItem = tab!!.position
            }
        })
        if (!::categoryDisposable.isInitialized || categoryDisposable.isDisposed) {
            categoryDisposable = Utils.categoryModelSubject.subscribe {
                viewPager!!.currentItem = 1
            }
        }
        if (!::childProgramDisposable.isInitialized || childProgramDisposable.isDisposed) {
            childProgramDisposable = Utils.childProgramSubject.subscribe {
                viewPager!!.currentItem = 2
            }
        }

        streamModelDisposable = Utils.streamModelSubject.subscribe {
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()
            ft.replace(R.id.activity_main_frame_layout, MediaPlayerFragment.instance)

            ft.commitAllowingStateLoss()
        }
        askPhoneStatePermission()
        askForeGroundService()
    }

    private fun askForeGroundService() {
        //FixME Kontrol yapılmış ama üst version'a hitap etmiyor gibi duruyor
        if (Build.VERSION.SDK_INT >= 28) {
            if (ContextCompat.checkSelfPermission(this, FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
//                startService()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(FOREGROUND_SERVICE), REQUEST_CODE_FOREGROUND_SERVICE)
            }
//        } else {
//            startService()
        }
    }
    private fun askPhoneStatePermission() {
        val result = ContextCompat.checkSelfPermission(this, READ_PHONE_STATE)
        if(result != PackageManager.PERMISSION_GRANTED) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Telefon Çağrı İzni")
            builder.setMessage("Birazdan soracağımız izini kabul etmez iseniz birisi sizi aradığında radyoyu durduramayız.")
            builder.setPositiveButton("İzni Göster", object: DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(READ_PHONE_STATE), REQUEST_CODE_PHONE_STATE)
                }
            })
            builder.show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_FOREGROUND_SERVICE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                askForeGroundService()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::categoryDisposable.isInitialized) {
            categoryDisposable.dispose()
        }
    }


    fun startService() {
        val serviceIntent = Intent(this, PlayService::class.java)
        this.startService(serviceIntent)
    }

    inner class TabAdapter(val fm: FragmentManager, private val pageCount: Int) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int {
            return pageCount
        }

        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> return CategoryFragment.instance
                1 -> return ProgramFragment.instance
                2 -> return StreamListFragment.instance

                else -> null
            }
        }

    }

    override fun onBackPressed() {
        val position = this.viewPager!!.currentItem
        if (position == 0) {
            super.onBackPressed()
        } else {
            viewPager!!.currentItem = position - 1
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return super.onOptionsItemSelected(item)
        Utils.streamModelSubject.onNext(LiveRadioStreamModal())
        return  true
    }
}

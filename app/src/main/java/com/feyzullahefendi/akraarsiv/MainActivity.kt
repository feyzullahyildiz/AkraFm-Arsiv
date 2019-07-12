package com.feyzullahefendi.akraarsiv

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat

import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import android.content.pm.PackageManager
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.RemoteViews
import android.widget.TableLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.reactivex.disposables.Disposable
import org.reactivestreams.Subscription
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var categoryDisposable: Disposable
    lateinit var childProgramDisposable: Disposable
    lateinit var streamModelDisposable: Disposable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout.addTab(tabLayout.newTab().setText("Kategoriler"))
        tabLayout.addTab(tabLayout.newTab().setText("Programlar"))
        tabLayout.addTab(tabLayout.newTab().setText("Bölümler"))
        val adapter = TabAdapter(supportFragmentManager, 3)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }
        })
        if (!::categoryDisposable.isInitialized || categoryDisposable.isDisposed) {
            categoryDisposable = Utils.categoryModelSubject.subscribe {
                viewPager.currentItem = 1
            }
        }
        if (!::childProgramDisposable.isInitialized || childProgramDisposable.isDisposed) {
            childProgramDisposable = Utils.childProgramSubject.subscribe {
                viewPager.currentItem = 2
            }
        }
        streamModelDisposable = Utils.streamModelSubject.subscribe {
            //            val frameLayout = findViewById<FrameLayout>(R.id.activity_main_frame_layout)
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()
            ft.replace(R.id.activity_main_frame_layout, MediaPlayerFragment.instance)

            ft.commit()
        }

//        val notificationLayout = RemoteViews(packageName, R.layout.notification)
//        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.notification)
//
//        val notificationCompat = NotificationManagerCompat.from(applicationContext)
//        Log.i(Utils.TAG, "is notifcaitons enabled ${notificationCompat.areNotificationsEnabled()}")
//        val customNotification = NotificationCompat.Builder(this, "channel-id")
//            .setSmallIcon(R.drawable.ic_arsiv_rounded)
//            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
//            .setCustomContentView(notificationLayout)
//            .setCustomBigContentView(notificationLayoutExpanded)
//            .build()
//        notificationCompat.notify(101, customNotification)

        startService()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::categoryDisposable.isInitialized) {
            categoryDisposable.dispose()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
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

}

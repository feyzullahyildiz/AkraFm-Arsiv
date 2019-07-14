package com.feyzullahefendi.akraarsiv

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_main.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.reactivex.disposables.Disposable


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
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()
            ft.replace(R.id.activity_main_frame_layout, MediaPlayerFragment.instance)

            ft.commitAllowingStateLoss()
        }


        startService()
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

}

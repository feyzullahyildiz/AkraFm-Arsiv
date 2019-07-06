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
import android.widget.TableLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.reactivex.disposables.Disposable
import org.reactivestreams.Subscription


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

//        fab.setOnClickListener { view ->
//
//            val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//            if (writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED) {
//                val url =
//                    "http://cdn.akradyo.net/vods3cf/_definst_/mp4:amazons3/akra/programlar/eski/193/basmakale2/2008.09.13_13_Kadinlarin+onemli+gorevleri_Daha+cok+calismali+ve+ufkumuzu+genisletmeliyiz.mp4/playlist.m3u8"
//                val manager = M3U8Manager(this)
//                manager.download(url,
//                    "7159e243-7f3e-4772-9cce-355a570480d3",
//                    object : IM3u8StatusChangeListener {
//                        override fun onFinished() {
//                            var i = 0
//                        }
//
//                        override fun onError(e: Exception) {
//                            var i = 0
//                            var message = e.message
//                            if (message == null) {
//                                message = "Beklenmedik hata oluştu"
//                            }
//                            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
//                        }
//
//                        override fun onChunkFileDownloaded(index: Int, size: Int) {
//                            Log.i("onChunkFileDownloaded", "index: " + index + " / " + size)
//                        }
//                    })
//            } else {
//                Snackbar.make(view, "İzin verilmemiş durumda", Snackbar.LENGTH_LONG)
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                    ),
//                    1
//                )
//            }
//
//        }
//        val recyclerView = findViewById<RecyclerView>(R.id.category_list_recycler_view)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        RequestManager.getCategory(this, object : CategoryResponseInterface {
//            override fun success(categories: ArrayList<CategoryModel>) {
//                val adapter = CategoryRecyclerViewAdapter(categories)
//                recyclerView.adapter = adapter
//                recyclerView.adapter?.notifyDataSetChanged()
//            }
//
//            override fun error(error: Exception) {
//            }
//        })

        val serviceIntent = Intent(this, PlayService::class.java)
        startService(serviceIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::categoryDisposable.isInitialized) {
            categoryDisposable.dispose()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
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

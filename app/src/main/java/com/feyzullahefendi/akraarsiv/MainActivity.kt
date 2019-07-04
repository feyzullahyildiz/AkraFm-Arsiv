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
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feyzullahefendi.akraarsiv.Utils.categoryModelSubject


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->

            val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED) {
                val url =
                    "http://cdn.akradyo.net/vods3cf/_definst_/mp4:amazons3/akra/programlar/eski/193/basmakale2/2008.09.13_13_Kadinlarin+onemli+gorevleri_Daha+cok+calismali+ve+ufkumuzu+genisletmeliyiz.mp4/playlist.m3u8"
                val manager = M3U8Manager(this)
                manager.download(url,
                    "7159e243-7f3e-4772-9cce-355a570480d3",
                    object : IM3u8StatusChangeListener {
                        override fun onFinished() {
                            var i = 0
                        }

                        override fun onError(e: Exception) {
                            var i = 0
                            var message = e.message
                            if (message == null) {
                                message = "Beklenmedik hata oluştu"
                            }
                            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                        }

                        override fun onChunkFileDownloaded(index: Int, size: Int) {
                            Log.i("onChunkFileDownloaded", "index: " + index + " / " + size)
                        }
                    })
            } else {
                Snackbar.make(view, "İzin verilmemiş durumda", Snackbar.LENGTH_LONG)
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    1
                )
            }

        }
        val recyclerView = findViewById<RecyclerView>(R.id.category_list_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        RequestManager.getCategory(this, object : categoryResponseInterface {
            override fun success(categories: ArrayList<CategoryModel>) {
                val adapter = CategoryRecyclerViewAdapter(categories)
                recyclerView.adapter = adapter
                recyclerView.adapter?.notifyDataSetChanged()
            }

            override fun error(error: Exception) {
            }
        })

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


    inner class CategoryRecyclerViewAdapter(val categories: ArrayList<CategoryModel>) :
        RecyclerView.Adapter<CategoryRecyclerViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryRecyclerViewHolder {
//            return CategoryRecyclerViewHolder(LayoutInflater.from(R.layout.category_item))
            val itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.category_item, parent, false)
            return CategoryRecyclerViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return this.categories.size
        }

        override fun onBindViewHolder(holder: CategoryRecyclerViewHolder, position: Int) {
            holder.bindData(categories[position])
        }


    }

    inner class CategoryRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        lateinit var model: CategoryModel
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            categoryModelSubject.onNext(this.model)
            val intent = Intent(this@MainActivity, ChildProgramActivity::class.java)
            this@MainActivity.startActivity(intent)

        }

        private var textView: TextView = itemView.findViewById<TextView>(R.id.category_item_category_name_text_view)
        fun bindData(model: CategoryModel) {
            this.model = model
            textView.text = model.catName
        }
    }

}

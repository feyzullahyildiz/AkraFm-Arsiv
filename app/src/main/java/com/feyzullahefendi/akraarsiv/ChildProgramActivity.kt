package com.feyzullahefendi.akraarsiv

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feyzullahefendi.akraarsiv.Utils.categoryModelSubject
import com.feyzullahefendi.akraarsiv.Utils.childProgramSubject

class ChildProgramActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child_program)
        val recyclerView = findViewById<RecyclerView>(R.id.child_program_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val categoryModel = categoryModelSubject.value
        if(categoryModel != null) {
            recyclerView.adapter = ChildProgramRecyclerViewAdapter(categoryModel.progs)
        }
    }


    inner class ChildProgramRecyclerViewAdapter(val childPrograms: ArrayList<ChildProgram>) :
        RecyclerView.Adapter<ChildProgramViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildProgramViewHolder {
            val itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.child_program_item, parent, false)
            return ChildProgramViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return this.childPrograms.size
        }

        override fun onBindViewHolder(holder: ChildProgramViewHolder, position: Int) {
            holder.bindData(childPrograms[position])
        }


    }

    inner class ChildProgramViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var textView: TextView = itemView.findViewById(R.id.child_program_item_program_name_text_view)
        lateinit var model: ChildProgram

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            childProgramSubject.onNext(this.model)
            val intent = Intent(this@ChildProgramActivity, StreamListActivity::class.java)
            this@ChildProgramActivity.startActivity(intent)
        }
        fun bindData(model: ChildProgram) {
            this.model = model
            textView.text = model.name
        }
    }
}

package com.feyzullahefendi.akraarsiv

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.disposables.Disposable

class ProgramFragment : Fragment() {

    companion object {
        val instance = ProgramFragment()
    }

    lateinit var subscription: Disposable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as MainActivity).startService()
        Log.i(Utils.TAG, "ProgramFragment onCreateView")
        val view = inflater.inflate(R.layout.layout_recycler_view, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.layout_recycler_view_item)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.layout_swipe_refresh)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
        }
        if (!::subscription.isInitialized || subscription.isDisposed) {
            subscription = Utils.categoryModelSubject.subscribe { categoryModel: CategoryModel ->
                Log.i(Utils.TAG, "ProgramFragment ${categoryModel.catName}")
                recyclerView.adapter = ChildProgramRecyclerViewAdapter(categoryModel.progs)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::subscription.isInitialized) {
            subscription.dispose()
        }
    }

    inner class ChildProgramRecyclerViewAdapter(val childPrograms: ArrayList<ChildProgram>) :
        RecyclerView.Adapter<ChildProgramViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildProgramViewHolder {
            val itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.layout_item, parent, false)
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

        private var textView: TextView = itemView.findViewById(R.id.layout_item_text_view)
        lateinit var model: ChildProgram

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            Utils.childProgramSubject.onNext(this.model)
        }

        fun bindData(model: ChildProgram) {
            this.model = model
            textView.text = model.name
        }
    }

}
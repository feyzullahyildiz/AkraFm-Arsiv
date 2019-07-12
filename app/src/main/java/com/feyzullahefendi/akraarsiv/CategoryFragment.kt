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

class CategoryFragment : Fragment() {

    companion object {
        val instance = CategoryFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(Utils.TAG, "CategoryFragment onCreateView")
        (activity as MainActivity).startService()
        val view = inflater.inflate(R.layout.layout_recycler_view, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.layout_recycler_view_item)
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.layout_swipe_refresh)

        recyclerView.layoutManager = LinearLayoutManager(activity)
        RequestManager.getCategory(activity!!, object : CategoryResponseInterface {
            override fun success(categories: ArrayList<CategoryModel>) {
                val adapter = CategoryRecyclerViewAdapter(categories)
                recyclerView.adapter = adapter
                recyclerView.adapter?.notifyDataSetChanged()
            }

            override fun error(error: Exception) {
            }
        })
        swipeRefreshLayout.setOnRefreshListener {
            RequestManager.getCategory(activity!!, object : CategoryResponseInterface {
                override fun success(categories: ArrayList<CategoryModel>) {
                    val adapter = CategoryRecyclerViewAdapter(categories)
                    recyclerView.adapter = adapter
                    recyclerView.adapter?.notifyDataSetChanged()
                    swipeRefreshLayout.isRefreshing = false
                }

                override fun error(error: Exception) {
                    swipeRefreshLayout.isRefreshing = false
                }
            })
        }
        return view
    }

    inner class CategoryRecyclerViewAdapter(val categories: ArrayList<CategoryModel>) :
        RecyclerView.Adapter<CategoryRecyclerViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryRecyclerViewHolder {
            val itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.layout_item, parent, false)
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
            Utils.categoryModelSubject.onNext(this.model)

        }

        private var textView = itemView.findViewById<TextView>(R.id.layout_item_text_view)
        fun bindData(model: CategoryModel) {
            this.model = model
            textView.text = model.catName
        }
    }

}
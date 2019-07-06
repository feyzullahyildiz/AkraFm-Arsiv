package com.feyzullahefendi.akraarsiv

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.Disposable
import org.reactivestreams.Subscription
import java.lang.Exception

class StreamListFragment : Fragment() {
    companion object {
        val instance = StreamListFragment()
    }

    lateinit var subscription: Disposable
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_recycler_view, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.layout_recycler_view_item)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        if (!::subscription.isInitialized || subscription.isDisposed) {
            subscription = Utils.childProgramSubject.subscribe { childProgram: ChildProgram ->
                recyclerView.adapter = null
                RequestManager.getStreamListOfProgram(activity!!, childProgram!!.id, object : StreamResponseInterface {
                    override fun success(streamModels: ArrayList<StreamModel>) {
                        val adapter = StreamModelRecyclerViewAdapter(streamModels)
                        recyclerView.adapter = adapter
                    }

                    override fun error(error: Exception) {

                    }
                })
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


    inner class StreamModelRecyclerViewAdapter(val streamModels: ArrayList<StreamModel>) :
        RecyclerView.Adapter<StreamModelViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamModelViewHolder {
            val itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.stream_item, parent, false)
            return StreamModelViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return this.streamModels.size
        }

        override fun onBindViewHolder(holder: StreamModelViewHolder, position: Int) {
            holder.bindData(streamModels[position])
        }
    }

    inner class StreamModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textView: TextView = itemView.findViewById(R.id.stream_item_title_text_view)
        private val startButton: Button = itemView.findViewById(R.id.stream_item_start_button)
        private val stopButton: Button = itemView.findViewById(R.id.stream_item_stop_button)
        lateinit var model: StreamModel

        init {
            startButton.setOnClickListener {
                Utils.streamModelSubject.onNext(this.model)

            }
            stopButton.setOnClickListener {
            }
        }

        fun bindData(model: StreamModel) {
            this.model = model
            textView.text = model.name
        }
    }
}
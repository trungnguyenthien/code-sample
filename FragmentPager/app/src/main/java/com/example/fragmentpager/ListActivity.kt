package com.example.fragmentpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private var data = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scroller_actitivy)
        recyclerView = findViewById(R.id.recycler_view)
        data.addAll((0..100).map { "ROW #$it" })
        recyclerView.adapter = adapter
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(divider)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    class SampleVH(itemView: View): RecyclerView.ViewHolder(itemView)
    private var adapter = object :RecyclerView.Adapter<SampleVH>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleVH {
            val view = LayoutInflater.from(this@ListActivity).inflate(R.layout.sample_row, parent, false)
            return SampleVH(view)
        }
        override fun onBindViewHolder(holder: SampleVH, position: Int) {
            holder.itemView.findViewById<TextView>(R.id.text1).text = data[position]
        }
        override fun getItemCount() =
            data.size
    }
}
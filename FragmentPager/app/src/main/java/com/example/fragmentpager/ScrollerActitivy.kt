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

class ScrollerActitivy : AppCompatActivity() {
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
        val footer = findViewById<View>(R.id.float_footer)
        recyclerView.setOnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->

            PureValueCalculator.calculate(
                start = -200.0.dp2px(this@ScrollerActitivy).toDouble(),
                end = 0.0,
                velocity = -oldScrollY.toDouble(),
                current = {
                    val params = footer.layoutParams as? ViewGroup.MarginLayoutParams
                    return@calculate (params?.topMargin ?: 0.0).toDouble()
                }
            ).let {
                print("PureValueCalculator.calculate = $it")
                val params = footer.layoutParams as? ViewGroup.MarginLayoutParams
                params?.topMargin = it.toInt()
                footer.layoutParams = params
            }
        }
    }



    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    class SampleVH(itemView: View): RecyclerView.ViewHolder(itemView)
    private var adapter = object :RecyclerView.Adapter<SampleVH>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleVH {
            val view = LayoutInflater.from(this@ScrollerActitivy).inflate(R.layout.sample_row, parent, false)
            return SampleVH(view)
        }
        override fun onBindViewHolder(holder: SampleVH, position: Int) {
            holder.itemView.findViewById<TextView>(R.id.text1).text = data[position]
        }
        override fun getItemCount() =
            data.size
    }
}
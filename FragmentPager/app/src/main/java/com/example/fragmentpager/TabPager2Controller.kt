package com.example.fragmentpager

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.shape.*


interface FragmentsPagerAdapter<ITEM> {
    fun items(): List<ITEM>
    fun switchPageMethod(): TabPager2Controller.SwitchPageMethod
    fun item2Id(item: ITEM): String
    fun id2Item(id: String): ITEM

    /// BUTTON
    fun buttonLayoutId(): Int
    fun configButton(item: ITEM, view: View, isSelected: Boolean)
    /// CONTENT
    fun createFragment(item: ITEM): Fragment
}

interface FragmentsPagerListener<ITEM> {
    fun onReloadPageData(item: ITEM)
    fun onChangedPage(new: ITEM, old: ITEM?, action: TabPager2Controller.SwitchPageAction)
}

class TabPager2Controller<ITEM>(
    private val fragmentActivity: FragmentActivity,
    private val tabList: RecyclerView,
    private val contentPager: ViewPager2,
) {
    enum class SwitchPageAction { tap, drag, other }
    enum class SwitchPageMethod { tapOnly, dragOnly, both }
    private var listener: FragmentsPagerListener<ITEM>? = null
    private var adapter: FragmentsPagerAdapter<ITEM>? = null
    private var selectedId: String? = null
    private var isTapTab = false

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedIndex(selectedItem: ITEM) {
        val adapter = adapter ?: return
        val itemId = adapter.item2Id(selectedItem) ?: return
        selectedId = itemId
        val newIndex = adapter.items().indexOfFirst { itemId == adapter.item2Id(it) }
        tabList.adapter?.notifyDataSetChanged()
        tabList.scrollToPosition(newIndex)
        contentPager.currentItem = newIndex
    }

    init {
        tabList.layoutManager = LinearLayoutManager(
            tabList.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        tabList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                // Apply offset only to first item
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.left += 40
                }
                if (parent.getChildAdapterPosition(view) == (adapter?.items()?.size ?: 0) - 1) {
                    outRect.right += 40
                }
                val cornerSize = 16f
                ViewCompat.setBackground(view.findViewById(R.id.card), MaterialShapeDrawable(
                    ShapeAppearanceModel.builder()
                        .setTopLeftCornerSize(cornerSize)
                        .setTopRightCornerSize(cornerSize)
                        .setBottomLeftCornerSize(cornerSize)
                        .setBottomRightCornerSize(cornerSize)
                        .setBottomEdge(TriangleEdgeTreatment(15f, false))
                        .build()
                ))
            }
        })

        contentPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        contentPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            var isDraging = false
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val adapter = adapter ?: return
                val old = selectedItem()
                setSelectedIndex(adapter.items()[position])
                invalidate()
                val itemPosition = adapter.items().indexOfFirst { selectedId == adapter.item2Id(it) }
                tabList.scrollToPosition(itemPosition)
                val new = selectedItem() ?: return
                val action = when {
                    isTapTab -> SwitchPageAction.tap
                    isDraging -> SwitchPageAction.drag
                    else -> SwitchPageAction.other
                }
                listener?.onChangedPage(new, old, action)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                contentPager.isUserInputEnabled = allowDragToSwitch() &&
                        state == ViewPager2.SCROLL_STATE_DRAGGING
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                isDraging = positionOffsetPixels != 0
            }
        })
    }

    fun selectedItem() =
        adapter?.items()?.firstOrNull { selectedId == adapter?.item2Id(it) }

    fun reloadPage(
        selectedPageItem: ITEM? = null,
        listener: FragmentsPagerListener<ITEM>? = null,
        adapter: FragmentsPagerAdapter<ITEM>
    ) {
        if (adapter.items().isEmpty()) return

        selectedId = selectedPageItem?.let { adapter.item2Id(it) }
            ?: adapter.item2Id(adapter.items().first())

        this.listener = listener
        this.adapter = adapter

        tabList.adapter = tabPagerAdapter
        contentPager.adapter = contentPagerAdapter
        // ON/OFF Drag to switch
        contentPager.isNestedScrollingEnabled = allowDragToSwitch()
        contentPager.children.find { it is RecyclerView }?.let {
            (it as RecyclerView).isNestedScrollingEnabled = allowDragToSwitch()
        }

        notifyDataSetChanged()
        listener?.onReloadPageData(selectedItem() ?: return)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyDataSetChanged() {
        contentPager.adapter?.notifyDataSetChanged()
        tabList.adapter?.notifyDataSetChanged()
    }

    fun invalidate() {
        contentPager.invalidate()
        tabList.invalidate()
    }

    private class TemptViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val tabPagerAdapter = object : RecyclerView.Adapter<TemptViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemptViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(adapter?.buttonLayoutId() ?: -1, parent, false)
            return TemptViewHolder(itemView = view)
        }

        override fun onBindViewHolder(holder: TemptViewHolder, position: Int) {
            val adapter = adapter ?: return
            val item = adapter.items().getOrNull(position) ?: return
            val isSelected = adapter.item2Id(item) == this@TabPager2Controller.selectedId
            adapter.configButton(item, holder.itemView, isSelected)
            holder.itemView.setOnClickListener {
                if (!allowTapToSwitch()) return@setOnClickListener
                isTapTab = true
                setSelectedIndex(item)
                isTapTab = false
            }
        }

        override fun getItemCount() =
            adapter?.items()?.size ?: 0
    }

    private val contentPagerAdapter = object : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = adapter?.items()?.size ?: 0
        override fun createFragment(position: Int): Fragment {
            val item = adapter!!.items()[position]
            return adapter?.createFragment(item)!!
        }
    }

    private fun allowTapToSwitch() = when(adapter?.switchPageMethod()) {
        SwitchPageMethod.both, SwitchPageMethod.tapOnly -> true
        else -> false
    }

    private fun allowDragToSwitch() = when(adapter?.switchPageMethod()) {
        SwitchPageMethod.both, SwitchPageMethod.dragOnly -> true
        else -> false
    }
}
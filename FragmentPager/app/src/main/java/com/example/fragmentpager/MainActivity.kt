package com.example.fragmentpager

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.fragmentpager.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), FragmentsPagerListener<MainActivity.Page>,
    FragmentsPagerAdapter<MainActivity.Page> {


    private lateinit var binding: ActivityMainBinding

    data class Page(val title: String, val content: String)

    val pages = listOf(
        Page("TITLE 1", "CONTENT 1"),
        Page("TITLE 2", "CONTENT 2"),
        Page("TITLE 3", "CONTENT 3"),
        Page("TITLE 4", "CONTENT 4"),

        Page("TITLE 5", "CONTENT 5"),
        Page("TITLE 6", "CONTENT 6"),
        Page("TITLE 7", "CONTENT 7"),
        Page("TITLE 8", "CONTENT 8"),

    )

    private lateinit var controller: TabPager2Controller<Page>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val tabList: RecyclerView = binding.root.findViewById(R.id.tabs_pager)
        val contentPager: ViewPager2 = binding.root.findViewById(R.id.contents_pager)
        controller = TabPager2Controller(this, tabList, contentPager)
        controller.reloadPage(listener = this, adapter = this)
    }

    override fun onReloadPageData(item: Page) {

    }

    override fun onChangedPage(new: Page, old: Page?, action: TabPager2Controller.SwitchPageAction) {

    }

    override fun items(): List<Page> = pages
    override fun switchPageMethod(): TabPager2Controller.SwitchPageMethod = TabPager2Controller.SwitchPageMethod.tapOnly
    override fun item2Id(item: Page): String = item.title
    override fun id2Item(id: String): Page = pages.first { it.title == id }
    override fun buttonLayoutId() = R.layout.tab_button_layout
    override fun configButton(item: Page, view: View, isSelected: Boolean) {
        val text1: TextView = view.findViewById(R.id.text1)
        val text2: TextView = view.findViewById(R.id.text2)
        text1.text = "TAB ${item.title}"
        text2.text = ""
        view.setBackgroundResource(when(isSelected) {
            true -> R.color.purple_200
            false -> R.color.teal_700
        })
    }

    override fun createFragment(item: Page) = FirstFragment(item.content)

}

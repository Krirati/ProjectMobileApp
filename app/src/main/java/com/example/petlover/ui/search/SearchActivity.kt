package com.example.petlover.ui.search
//
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//
//class SearchAdapter1(private val mutableList: MutableList<SearchModel1>) :
//    DynamicSearchAdapter<SearchModel1>(mutableList) {
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val textView = TextView(parent.context)
//        return ViewHolder(textView)
//    }
//
//    override fun getItemCount(): Int {
//        return mutableList.count()
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val tv = holder.containerView as TextView
//        tv.text = mutableList[position].data
//
//    }
//
//
//}
//class SearchAdapter2(private val mutableList: MutableList<SearchModel2>) :
//    DynamicSearchAdapter<SearchModel2>(mutableList) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val textView = TextView(parent.context)
//        return ViewHolder(textView)
//    }
//
//    override fun getItemCount(): Int {
//        return mutableList.count()
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val tv = holder.containerView as TextView
//        tv.text = mutableList[position].data
//
//    }
//
//
//}
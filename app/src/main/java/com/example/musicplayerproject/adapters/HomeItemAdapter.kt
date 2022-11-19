package com.example.musicplayerproject.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerproject.R
import com.example.musicplayerproject.models.ui.ItemHome
import com.squareup.picasso.Picasso

class HomeItemAdapter(
    val context: Context,
    val layoutToInflater: Int,
    val listItemHome: List<ItemHome>



): RecyclerView.Adapter<HomeItemAdapter.ViewHolder>() {

    companion object
    {
        val MARGIN: Int = 10;
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var imgThumb = itemView.findViewById<ImageView>(R.id.item_home_poster_img_thumbnail)
        var title = itemView.findViewById<TextView>(R.id.item_home_poster_txt_title)
        var detail = itemView.findViewById<TextView>(R.id.item_home_poster_txt_detail)
        fun bind(position: Int)
        {
            Picasso.get().load(listItemHome[position].thumbnail).into(imgThumb)
            title.text = listItemHome[position].title

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layoutToInflater, parent, false)
        val layoutParams = view.findViewById<LinearLayout>(R.id.item_home_poster_layout).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(MARGIN, MARGIN, MARGIN, MARGIN)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = listItemHome.size

    interface ItemClickListener {
        fun onItemClicked()
        {

        }
    }
}
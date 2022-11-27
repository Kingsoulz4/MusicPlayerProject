package com.example.musicplayerproject.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerproject.R
import com.example.musicplayerproject.activities.PlayerActivity
import com.example.musicplayerproject.models.data.Song
import com.example.musicplayerproject.models.data.ZingAPI
import com.example.musicplayerproject.models.ui.ItemDisplayData
import com.squareup.picasso.Picasso
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException

class HomeItemAdapter(
    val context: Context,
    val layoutToInflater: Int,
    val listItemDisplayData: List<ItemDisplayData>,
    val itemClickListener: ItemClickListener


): RecyclerView.Adapter<HomeItemAdapter.ViewHolder>() {

    companion object
    {
        val MARGIN: Int = 5;
        fun createHomeItemAdapter(context: Context, layoutToInflater: Int, listItemDisplayData: List<ItemDisplayData>): HomeItemAdapter
        {

            var adapter = HomeItemAdapter(context, layoutToInflater, listItemDisplayData, object : ItemClickListener{
                override fun onItemClicked(position: Int) {
                    if (listItemDisplayData[position].type == ItemDisplayData.ITEM_TYPE.SONG)
                    {
                        var item = listItemDisplayData[position]
                        var switchToPlayerSceneIntent = Intent(context, PlayerActivity::class.java)
                        ZingAPI.getInstance(context).getSongByID(item.encodeId, object : ZingAPI.OnRequestCompleteListener{
                            override fun onSuccess(call: Call, response: String) {
                                var jsonResponseBody = JSONObject(response).getJSONObject("data")
                                var song = Song()
                                song.encodeId = item.encodeId
                                song.title = item.title
                                song.thumbnail = item.thumbnail
                                song.linkQuality128 = jsonResponseBody.getString("128")
                                song.linkQuality320 = jsonResponseBody.getString("320")
                                switchToPlayerSceneIntent.putExtra("SONG_EXTRA", song)
                                ContextCompat.startActivity(context, switchToPlayerSceneIntent, null)
                            }

                            override fun onError(call: Call, e: IOException) {

                            }
                        })
                    }
                    else if(listItemDisplayData[position].type == ItemDisplayData.ITEM_TYPE.VIDEO)
                    {

                    }
                    else if (listItemDisplayData[position].type == ItemDisplayData.ITEM_TYPE.PLAYLIST)
                    {

                    }
                }
            })
            return adapter
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var imgThumb = itemView.findViewById<ImageView>(R.id.item_home_poster_img_thumbnail)
        var title = itemView.findViewById<TextView>(R.id.item_home_poster_txt_title)
        var detail = itemView.findViewById<TextView>(R.id.item_home_poster_txt_detail)
        fun bind(position: Int)
        {
            Picasso.get().load(listItemDisplayData[position].thumbnail).fit().into(imgThumb)
            title.text = listItemDisplayData[position].title

            itemView.setOnClickListener {
                itemClickListener.onItemClicked(position)
            }
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

    override fun getItemCount() = listItemDisplayData.size

    interface ItemClickListener {
        fun onItemClicked(position: Int);
    }
}
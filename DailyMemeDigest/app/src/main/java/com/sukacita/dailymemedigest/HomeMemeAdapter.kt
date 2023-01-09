package com.sukacita.dailymemedigest

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class HomeMemeAdapter(val context: Context): RecyclerView.Adapter<HomeMemeAdapter.HomeMemeViewHolder>() {
    class HomeMemeViewHolder(val v: View): RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeMemeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        var v = inflater.inflate(R.layout.card_memes, parent, false)
        return HomeMemeViewHolder(v)
    }

    override fun onBindViewHolder(holder: HomeMemeViewHolder, position: Int) {
        with(Global.homeMemes[position]){
//            val time = time
//            val customers = nCustomers
//            holder.v.txtTimeCustomer.text = "0$time.00 - $customers"
            val img: ImageView = holder.v.findViewById(R.id.imgMeme)

            Picasso.get().load(this.imageurl).into(img)
        }
    }

    override fun getItemCount(): Int {
        return Global.homeMemes.size
    }
}
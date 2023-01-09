package com.sukacita.dailymemedigest

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MyCreationAdapter(val context: Context, val userMemes: ArrayList<Meme>): RecyclerView.Adapter<MyCreationAdapter.MyCreationViewHolder>() {
    class MyCreationViewHolder(val v: View): RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCreationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        var v = inflater.inflate(R.layout.card_memes, parent, false)
        return MyCreationViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyCreationViewHolder, position: Int) {
        with(userMemes[position]) {
            val img: ImageView = holder.v.findViewById(R.id.imgMeme)
            holder.v.findViewById<TextView>(R.id.toptext_cardmeme).text = toptext
            holder.v.findViewById<TextView>(R.id.bottomtext_cardmeme).text = bottomtext

            Picasso.get().load(imageurl).into(img)
        }
    }

    override fun getItemCount(): Int {
        return userMemes.size
    }
}
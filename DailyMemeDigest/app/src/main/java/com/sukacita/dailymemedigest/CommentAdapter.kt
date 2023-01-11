package com.sukacita.dailymemedigest

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class CommentAdapter(val context: Context, val comments: ArrayList<Comment>): RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    class CommentViewHolder(val v: View): RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        var v = inflater.inflate(R.layout.comment_card, parent, false)

        return CommentViewHolder(v)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val name: TextView = holder.v.findViewById(R.id.txtCommentator)
        val date: TextView = holder.v.findViewById(R.id.txtCommentDate)
        val content: TextView = holder.v.findViewById(R.id.txtCommentContent)

        var dfn = comments[position].firstname
        var dln = comments[position].lastname

        if (dfn == "" || dfn == "null") {
            dfn = "User"
        }

        if (dln == "null") {
            dln = ""
        }

        name.text = "$dfn $dln"
        date.text = comments[position].date
        content.text = comments[position].content

    }

    override fun getItemCount(): Int {
        return comments.size
    }
}
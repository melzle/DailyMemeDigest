package com.sukacita.dailymemedigest

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject

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

        var displayName = "$dfn $dln"
        if (comments[position].privacysetting == 1) {
            displayName = privateName(displayName)
        }
        name.text = displayName
        date.text = comments[position].date
        content.text = comments[position].content
        holder.v.findViewById<TextView>(R.id.txtLikesComment).text = "${ comments[position].likes } likes"

        val btnDelete: ImageButton = holder.v.findViewById(R.id.btnDeleteComment)
        if (comments[position].userid != Global.currentUser.id) {
            btnDelete.isVisible = false
        }

        btnDelete.setOnClickListener() {
            if (comments[position].userid == Global.currentUser.id) {
                Log.d("COMMENT_POS", "${comments[position].toString()}")
                deleteComment(comments[position].id, Global.currentUser.id, position)
            }
        }

        val btnComment: ImageButton = holder.v.findViewById(R.id.btnLikeComment)

        if (comments[position].userid == Global.currentUser.id) {
            btnComment.setImageResource(R.drawable.ic_baseline_favorite_grey_24)
            btnComment.isClickable = false
//            btnLike.isEnabled = false
        } else {
            if (comments[position].isLiked == 0) {
                btnComment.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            } else {
                btnComment.setImageResource(R.drawable.ic_baseline_favorite_24)
            }

            btnComment.setOnClickListener() {
                if (comments[position].isLiked == 0) {
                    likeComment(comments[position].id, Global.currentUser.id, position)
                    btnComment.setImageResource(R.drawable.ic_baseline_favorite_24)
                    comments[position].isLiked = 1
                } else {
                    unLikeComment(comments[position].id, Global.currentUser.id, position)
                    btnComment.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                    comments[position].isLiked = 0
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    private fun privateName(displayName: String): String {
        var newName = ""
        for (i in displayName.indices) {
            if (i<3) {
                newName += displayName[i]
            } else {
                newName += if (displayName[i] != " ".toCharArray()[0]) {
                    "*"
                } else {
                    " "
                }
            }
        }
        return newName
    }

    private fun deleteComment(commentId: Int, userId: Int, position: Int) {
        val q = Volley.newRequestQueue(this.context)
        val url = "https://scheday.site/nmp/delete_comment.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> {
                Log.d("apiresult", it)
                val obj = JSONObject(it)
                if(obj.getString("result") == "OK") {
                    Toast.makeText(context, "delete comment Success", Toast.LENGTH_SHORT).show()
                    comments.removeAt(position)
                    this.notifyItemRemoved(position)
                    this.notifyItemRangeChanged(position, comments.size)
                } else {
                    Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show()
                    Log.d("err", obj.getString("message"))
                }},
            Response.ErrorListener {
//                Log.d("cekparams", it.message.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = userId.toString()
                params["commentid"] = commentId.toString()
                return params
            }
        }
        q.add(stringRequest)
    }

    private fun likeComment(commentId: Int, userId: Int, position: Int) {
        val q = Volley.newRequestQueue(this.context)
        val url = "https://scheday.site/nmp/like_comment.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> {
                Log.d("apiresult", it)
                val obj = JSONObject(it)
                if(obj.getString("result") == "OK") {
                    Toast.makeText(context, "like comment Success", Toast.LENGTH_SHORT).show()
                    comments[position].likes += 1
                    this.notifyItemChanged(position)
                } else {
                    Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show()
                    Log.d("err", obj.getString("message"))
                }},
            Response.ErrorListener {
//                Log.d("cekparams", it.message.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = userId.toString()
                params["commentid"] = commentId.toString()
                return params
            }
        }
        q.add(stringRequest)
    }

    private fun unLikeComment(commentId: Int, userId: Int, position: Int) {
        val q = Volley.newRequestQueue(this.context)
        val url = "https://scheday.site/nmp/undo_like_comment.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> {
                Log.d("apiresult", it)
                val obj = JSONObject(it)
                if(obj.getString("result") == "OK") {
                    Toast.makeText(context, "unlike comment Success", Toast.LENGTH_SHORT).show()
                    comments[position].likes -= 1
                    this.notifyItemChanged(position)
                } else {
                    Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show()
                    Log.d("err", obj.getString("message"))
                }},
            Response.ErrorListener {
//                Log.d("cekparams", it.message.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = userId.toString()
                params["commentid"] = commentId.toString()
                return params
            }
        }
        q.add(stringRequest)
    }
}
package com.sukacita.dailymemedigest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MyCreationAdapter(val context: Context, val userMemes: ArrayList<Meme>, val idUser: Int): RecyclerView.Adapter<MyCreationAdapter.MyCreationViewHolder>() {
    class MyCreationViewHolder(val v: View): RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCreationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        var v = inflater.inflate(R.layout.card_memes, parent, false)
        return MyCreationViewHolder(v)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyCreationViewHolder, position: Int) {
        with(userMemes[position]) {
            val img: ImageView = holder.v.findViewById(R.id.imgMeme)
            holder.v.findViewById<TextView>(R.id.toptext_cardmeme).text = toptext
            holder.v.findViewById<TextView>(R.id.bottomtext_cardmeme).text = bottomtext

            var nLikes = numoflikes
            var dLikes = "likes"
            var nComments = comments
            var dComments = "comments"
            if (nLikes == 1) {
                dLikes = "like"
            }
            if (nComments == 1) {
                dComments = "comment"
            }

            holder.v.findViewById<TextView>(R.id.txtLikes).text = "${ numoflikes } $dLikes"
            holder.v.findViewById<TextView>(R.id.txtLikes2).text = "${ comments } $dComments"
            val date = getDate(date).toInstant().atZone(ZoneId.of("VST")).toLocalDate()
            holder.v.findViewById<TextView>(R.id.txtReleaseDate).text = "Posted on ${date.dayOfMonth} ${date.month.toString().lowercase().replaceFirstChar { it.uppercase() }} ${date.year}"

            Picasso.get().load(imageurl).into(img)

            val btnLike: AppCompatImageButton = holder.v.findViewById(R.id.btnLike)
            btnLike.setImageResource(R.drawable.ic_baseline_favorite_grey_24)

            val btnComment: AppCompatImageButton = holder.v.findViewById(R.id.btnComment)
            btnComment.setOnClickListener() {
                val meme = this
                val intent = Intent(context, DetailMemeActivity::class.java)
                intent.putExtra("meme_id", meme.id)
                intent.putExtra("user_id", idUser)
                intent.putExtra("top_meme", meme.toptext)
                intent.putExtra("bottom_meme", meme.bottomtext)
                intent.putExtra("url", meme.imageurl)
                intent.putExtra("likes", meme.numoflikes)
                intent.putExtra("comments", meme.comments)

                val REQUEST_UPDATE = 1
                val a: Activity  = context as Activity
                a.startActivityForResult(intent, REQUEST_UPDATE)

//                a.onactiv
            }
        }
    }

    override fun getItemCount(): Int {
        return userMemes.size
    }

    private fun like(memeId: Int, userId: Int, txtLikes: TextView, position: Int) {
        val q = Volley.newRequestQueue(this.context)
        val url = "https://scheday.site/nmp/like_meme.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> {
                Log.d("apiresult", it)
                val obj = JSONObject(it)
                if(obj.getString("result") == "OK") {
                    Toast.makeText(context, "Like Meme Success", Toast.LENGTH_SHORT).show()
                    val likesArr = txtLikes.text.split(' ')
                    val likes = likesArr[0].toString().toInt()
                    userMemes[position].numoflikes = userMemes[position].numoflikes+1
                    this.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show()
                }},
            Response.ErrorListener {
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = userId.toString()
                params["memeid"] = memeId.toString()
                return params
            }
        }
        q.add(stringRequest)
    }

    fun getDate(s: String): Date {
        val strArr = s.split(" ")
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.parse(strArr[0])
    }
}
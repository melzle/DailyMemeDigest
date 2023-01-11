package com.sukacita.dailymemedigest

import android.content.Context
import android.content.Intent
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomeMemeAdapter(val context: Context, val homeMemes: ArrayList<Meme>, val idUser: Int): RecyclerView.Adapter<HomeMemeAdapter.HomeMemeViewHolder>() {
    class HomeMemeViewHolder(val v: View): RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeMemeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        var v = inflater.inflate(R.layout.card_memes, parent, false)
//        getMemes()
//        Log.d("adaptermemecount", Global.homeMemes.size.toString())
        return HomeMemeViewHolder(v)
    }

    override fun onBindViewHolder(holder: HomeMemeViewHolder, position: Int) {
//        with(homeMemes[position]){
////            val time = time
////            val customers = nCustomers
////            holder.v.txtTimeCustomer.text = "0$time.00 - $customers"
//            val img: ImageView = holder.v.findViewById(R.id.imgMeme)
//            holder.v.findViewById<TextView>(R.id.toptext_cardmeme).text = toptext
//            holder.v.findViewById<TextView>(R.id.bottomtext_cSardmeme).text = bottomtext
//
//            Picasso.get().load(imageurl).into(img)
//        }
//        Log.d("cek", homeMemes.toString())
        val img: ImageView = holder.v.findViewById(R.id.imgMeme)
        holder.v.findViewById<TextView>(R.id.toptext_cardmeme).text = homeMemes[position].toptext
        holder.v.findViewById<TextView>(R.id.bottomtext_cardmeme).text = homeMemes[position].bottomtext
        holder.v.findViewById<TextView>(R.id.txtLikes).text = "${ homeMemes[position].numoflikes } likes"
        holder.v.findViewById<TextView>(R.id.txtLikes2).text = "${homeMemes[position].comments} comments"
//        holder.v.findViewById<TextView>(R.id.txtReleaseDate).text = homeMemes[position]

        Picasso.get().load(homeMemes[position].imageurl).into(img)

        val btnLike: AppCompatImageButton = holder.v.findViewById(R.id.btnLike)
        btnLike.setOnClickListener() {
            like(homeMemes[position].id, idUser, holder.v.findViewById(R.id.txtLikes), position)
        }

        val btnComment: AppCompatImageButton = holder.v.findViewById(R.id.btnComment)
        btnComment.setOnClickListener() {
            val meme = homeMemes[position]
            val intent = Intent(context, DetailMemeActivity::class.java)
            intent.putExtra("meme_id", meme.id)
            intent.putExtra("user_id", idUser)
            intent.putExtra("top_meme", meme.toptext)
            intent.putExtra("bottom_meme", meme.bottomtext)
            intent.putExtra("url", meme.imageurl)
            intent.putExtra("likes", meme.numoflikes)
            intent.putExtra("comments", meme.comments)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return homeMemes.size
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
//                    txtLikes.text = "${likes+1} likes"
//                    Log.d("globalmemelen", Global.homeMemes.size.toString())
                    homeMemes[position].numoflikes = homeMemes[position].numoflikes+1
                    this.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show()
                }},
            Response.ErrorListener {
//                Log.d("cekparams", it.message.toString())
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

    fun getMemes() {
        val q = Volley.newRequestQueue(this.context)
        val url = "https://scheday.site/nmp/get_home_memes.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> {
                Log.d("apiresult", it)
                val obj = JSONObject(it)
                if(obj.getString("result") == "OK") {
                    val data = obj.getJSONArray("data")
                    Log.d("datalen", data.length().toString())
                    for(i in 0 until data.length()) {
                        val memeObj = data.getJSONObject(i)
                        val meme = Meme(
                            memeObj.getInt("id"),
                            memeObj.getString("imageurl"),
                            memeObj.getString("toptext"),
                            memeObj.getString("bottomtext"),
                            memeObj.getInt("numoflikes"),
                            memeObj.getInt("users_id"),
                            memeObj.getInt("reportcount"),
                            memeObj.getInt("commentcount")
                        )
                        Log.d("dariadapter", memeObj.getString("toptext"))
                        Global.homeMemes.add(meme)
                    }
//                    Log.d("globalmemelen", Global.homeMemes.size.toString())
                } else {
//                    Toast.makeText(this, "Invalid credentials. Please check your username and password", Toast.LENGTH_SHORT).show()
                }},
            Response.ErrorListener {
//                Log.d("cekparams", it.message.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                return params
            }
        }
        q.add(stringRequest)
    }


}
package com.sukacita.dailymemedigest

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import org.w3c.dom.Text

class DetailMemeActivity : AppCompatActivity() {
//    private val comments: ArrayList<Comment> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_meme)

        val memeId = intent.getIntExtra("meme_id",0)
        val userId = intent.getIntExtra("user_id", 0)
        val top = intent.getStringExtra("top_meme")
        val bottom = intent.getStringExtra("bottom_meme")
        val url = intent.getStringExtra("url")
        val likes = intent.getIntExtra("likes", 0)
        val commentQty = intent.getIntExtra("comments", 0)


        getComments(memeId)
        Thread.sleep(1000)

        val lm: LinearLayoutManager = LinearLayoutManager(this)
        val recycler: RecyclerView = this.findViewById(R.id.recyclerComment)
        recycler.layoutManager = lm
        recycler.setHasFixedSize(true)
        recycler.adapter = CommentAdapter(this, Global.comments)

        val noComment: TextView = findViewById(R.id.txtNoComment)
        if (commentQty != 0) {
            noComment.text = ""
        } else {
            noComment.text = "No comments yet :("
        }

        val txtTop: TextView = findViewById(R.id.txtTop_detail)
        val txtBottom: TextView = findViewById(R.id.txtBottom_detail)
        val img: ImageView = findViewById(R.id.imgMeme_detail)
        val txtLikes: TextView = findViewById(R.id.txtLikes)

        txtTop.text = top
        txtBottom.text = bottom
        txtLikes.text = "${likes} likes"
        Picasso.get().load(url).into(img)

        val btnInsert: ImageButton = findViewById(R.id.btnComments)
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val txtComment: TextView = findViewById(R.id.txtComment)
        btnInsert.setOnClickListener() {
            if (txtComment.text.toString() != "") {
                insertComment(txtComment.text.toString(), userId, memeId)
                Thread.sleep(500)
                getComments(memeId)
                Thread.sleep(500)
                recycler.adapter!!.notifyDataSetChanged()
                if (commentQty == 0) {
                    noComment.text = ""
                }
                txtComment.text = ""
            } else {
                Toast.makeText(this, "Comment content can't be empty", Toast.LENGTH_SHORT).show()
            }
        }
        btnBack.setOnClickListener() {
            finish()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun getComments(idmeme: Int) {
        Global.comments.clear()
        val q = Volley.newRequestQueue(this)
        val url = "https://scheday.site/nmp/get_comments.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> {
                val obj = JSONObject(it)
                if(obj.getString("result") == "OK") {
                    val data = obj.getJSONArray("data")
                    for(i in 0 until data.length()) {
                        val commentObj = data.getJSONObject(i)
                        val comment = Comment(
                            commentObj.getInt("id"),
                            commentObj.getString("firstname"),
                            commentObj.getString("lastname"),
                            commentObj.getInt("users_id"),
                            commentObj.getString("content"),
                            commentObj.getString("date"),
                            commentObj.getInt("numlikes")
                        )
                        Global.comments.add(comment)
                    }
                }},
            Response.ErrorListener {
                Log.d("cekparams", it.message.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["memesid"] = idmeme.toString()
                return params
            }
        }
        q.add(stringRequest)
    }

    private fun insertComment(content: String, userId: Int, memeId: Int) {
        val q = Volley.newRequestQueue(this)
        val url = "https://scheday.site/nmp/add_comment.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> {
                val obj = JSONObject(it)
                if(obj.getString("result") == "OK") {
                    Toast.makeText(this, "Insert comment success", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, obj.getString("result"), Toast.LENGTH_SHORT).show()
                }},
            Response.ErrorListener {
                Log.d("cekparams", it.message.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = userId.toString()
                params["memeid"] = memeId.toString()
                params["content"] = content
                return params
            }
        }
        q.add(stringRequest)
    }
}
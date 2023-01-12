package com.sukacita.dailymemedigest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso

class LeaderboardAdapter(val context: Context): RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {
    class LeaderboardViewHolder(val v: View): RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        var v = inflater.inflate(R.layout.card_leader, parent, false)
        return LeaderboardViewHolder(v)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        with(Global.leaderboardArr[position]){
            val imgProfile: ImageView = holder.v.findViewById(R.id.imgProfilePic_drawerHeader)
            val txtName: TextView = holder.v.findViewById(R.id.txtName)
            val txtLikes: TextView = holder.v.findViewById(R.id.txtJumlahLike)

            if (avatarurl != "".toString()) {
                Picasso.get().load(avatarurl).into(imgProfile)
            } else {
                val defaultPfp = "https://www.personality-insights.com/wp-content/uploads/2017/12/default-profile-pic-e1513291410505.jpg"
                Picasso.get().load(defaultPfp).into(imgProfile)
            }

            var dfn = firstname
            var dln = lastname

            if (firstname == "" || firstname == "null" || firstname.isNullOrBlank() || firstname.isNullOrEmpty()) {
                dfn = "User"
            }

            if (lastname == "" || lastname == "null") {
                dln = ""
            }

            var displayName = "$dfn $dln"
            if (privacysetting == 1) {
                displayName = privateName(displayName)
            }
            txtName.text = displayName
            txtLikes.text = "${ likes.toString() } likes"
        }
    }

    override fun getItemCount(): Int {
        return Global.leaderboardArr.size
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
}
package com.sukacita.dailymemedigest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class LeaderboardAdapter(val context: Context): RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {
    class LeaderboardViewHolder(val v: View): RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        var v = inflater.inflate(R.layout.card_leader, parent, false)
        return LeaderboardViewHolder(v)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        with(Global.leaderboardArr[position]){
            val imgProfile: ImageView = holder.v.findViewById(R.id.imgProfile_leader)
            val txtName: TextView = holder.v.findViewById(R.id.txtName)
            val txtLikes: TextView = holder.v.findViewById(R.id.txtJumlahLike)

            Picasso.get().load(this.avatarurl).into(imgProfile)

            var displayName = username
            if (privacysetting == 1) {
                displayName = privateName(displayName)
            }
            txtName.text = displayName
            txtLikes.text = likes.toString()
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
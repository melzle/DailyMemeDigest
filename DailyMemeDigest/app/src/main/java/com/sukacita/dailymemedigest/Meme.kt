package com.sukacita.dailymemedigest

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Meme(val id: Int, val imageurl: String, val toptext: String,
                val bottomtext: String, var numoflikes: Int, val users_id: Int, val reportCount: Int, val comments: Int): Parcelable

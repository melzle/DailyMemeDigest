package com.sukacita.dailymemedigest

data class Comment(val id: Int, val firstname: String, val lastname: String,
                   val userid: Int, val content: String, val date: String, var likes: Int, val privacysetting: Int, var isLiked: Int)

package com.sukacita.dailymemedigest

data class Comment(val id: Int, val firstname: String, val lastname: String,
                   val userid: Int, val content: String, val date: String, val likes: Int)

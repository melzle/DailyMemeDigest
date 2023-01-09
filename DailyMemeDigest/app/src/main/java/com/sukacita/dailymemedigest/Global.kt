package com.sukacita.dailymemedigest

object Global {
    var homeMemes: ArrayList<Meme> = arrayListOf()
    var userMemes: ArrayList<Meme> = arrayListOf()
    var leaderboardArr: ArrayList<Leaderboard> = arrayListOf()

    var user: User = User(0,"", "", "", "", "", 0)
}
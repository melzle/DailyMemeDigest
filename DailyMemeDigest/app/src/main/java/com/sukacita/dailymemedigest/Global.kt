package com.sukacita.dailymemedigest

object Global {
    var homeMemes: ArrayList<Meme> = arrayListOf(
        Meme(0, "", "", "", 0,0,0)
    )

    var leaderboardArr: ArrayList<Leaderboard> = ArrayList()

    var user: User = User(0,"", "", "", "", "", 0)
}
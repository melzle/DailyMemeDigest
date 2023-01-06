package com.sukacita.dailymemedigest

data class User(val id: Int,
                val username: String,
                val firstname: String,
                val lastname: String,
                val regisDate: String,
                val avatarUrl: String,
                val privacySetting: Int)

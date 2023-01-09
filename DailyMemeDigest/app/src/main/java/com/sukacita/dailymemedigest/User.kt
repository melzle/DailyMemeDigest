package com.sukacita.dailymemedigest

data class User(val id: Int,
                val username: String,
                var firstname: String,
                var lastname: String,
                val regisDate: String,
                val avatarUrl: String,
                val privacySetting: Int)


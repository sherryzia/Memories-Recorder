package com.example.memoriesrecorder

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Memory(
    var id: String = "", // Firestore document ID
    val title: String = "",
    val description: String = "",
    val date: String = "" // You can store date as a formatted string
) : Parcelable


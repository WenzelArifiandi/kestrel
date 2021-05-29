package com.capstone.bangkit.kestrel.ui.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Alphabet(
    var name: String? = null,
    var image: Int = 0
) : Parcelable
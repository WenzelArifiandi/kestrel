package com.capstone.bangkit.kestrel.ui.home

import com.capstone.bangkit.kestrel.R

object AlphabetData {
    private val alphabetNames =
        arrayOf(
            "A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J",
            "K",
            "L",
            "M",
            "N",
            "O",
            "P",
            "Q",
            "R",
            "S",
            "T",
            "U",
            "V",
            "W",
            "X",
            "Y",
            "Z"
        )

    private val alphabetImages = intArrayOf(
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
        R.drawable.v_alpha,
    )

    val listData: ArrayList<Alphabet>
        get() {
            val list = arrayListOf<Alphabet>()
            for (position in alphabetNames.indices) {
                val alphabet = Alphabet()
                alphabet.name = alphabetNames[position]
                alphabet.image = alphabetImages[position]
                list.add(alphabet)

            }
            return list
        }
}
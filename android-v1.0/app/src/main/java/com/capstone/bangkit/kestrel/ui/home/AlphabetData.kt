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
        R.drawable.a_alpha,
        R.drawable.b_alpha,
        R.drawable.c_alpha,
        R.drawable.d_alpha,
        R.drawable.e_alpha,
        R.drawable.f_alpha,
        R.drawable.g_alpha,
        R.drawable.h_alpha,
        R.drawable.i_alpha,
        R.drawable.j_alpha,
        R.drawable.k_alpha,
        R.drawable.l_alpha,
        R.drawable.m_alpha,
        R.drawable.n_alpha,
        R.drawable.o_alpha,
        R.drawable.p_alpha,
        R.drawable.q_alpha,
        R.drawable.r_alpha,
        R.drawable.s_alpha,
        R.drawable.t_alpha,
        R.drawable.u_alpha,
        R.drawable.v_alpha,
        R.drawable.w_alpha,
        R.drawable.x_alpha,
        R.drawable.y_alpha,
        R.drawable.z_alpha,
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
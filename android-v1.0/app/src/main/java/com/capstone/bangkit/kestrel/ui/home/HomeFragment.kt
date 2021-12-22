package com.capstone.bangkit.kestrel.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.capstone.bangkit.kestrel.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val list: ArrayList<Alphabet> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.addAll(AlphabetData.listData)
        showRecyclerView()
    }

    private fun showRecyclerView() {
        binding.rvAlphabet.layoutManager = GridLayoutManager(context, 2)
        val listAlphabet = AlphabetAdapter(list)
        binding.rvAlphabet.adapter = listAlphabet
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
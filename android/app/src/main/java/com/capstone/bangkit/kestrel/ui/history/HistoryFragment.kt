package com.capstone.bangkit.kestrel.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.bangkit.kestrel.databinding.FragmentHistoryBinding
import com.google.firebase.firestore.*


class HistoryFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val list: ArrayList<History> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }


    private fun showRecyclerView() {
        binding.rvHistory.setHasFixedSize(true)
        binding.rvHistory.layoutManager = LinearLayoutManager(context)
        val adapter = HistoryAdapter(list)
        binding.rvHistory.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun loadData() {
        db.collection("histories").orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot?> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (value != null) {
                        for (doc in value.documentChanges) {
                            if (doc.type == DocumentChange.Type.ADDED) {
                                list.add(doc.document.toObject(History::class.java))
                            }
                        }
                        showRecyclerView()
                    }
                }
            })
    }

}
package com.example.crm_application.ui.leads

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crm_application.R

class LeadsFragment : Fragment() {
    private val viewModel: LeadsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_leads, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.leadsRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Get Token from Shared Preference
        val prefs = requireContext().getSharedPreferences("app_prefs", 0)
        val token = prefs.getString("access_token", null)
        val key = prefs.getString("key", null)

        if(!token.isNullOrEmpty()) {

            viewModel.loadLeads(token)

        } else if(!key.isNullOrEmpty()) {

            viewModel.loadLeadsToken(key)

        } else {

            Toast.makeText(requireContext(), "Missing Token", Toast.LENGTH_SHORT).show()

        }

        viewModel.leads.observe(viewLifecycleOwner, Observer { leads ->
            leads?.let {
                var adapter = LeadsAdapter(it)
                recyclerView.adapter = adapter
                Log.i("LeadsFragment", adapter.getItemCount().toString())
            }

        })

        viewModel.error.observe(viewLifecycleOwner, Observer { err -> {
                err?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
            }
        })

        return view
    }
}
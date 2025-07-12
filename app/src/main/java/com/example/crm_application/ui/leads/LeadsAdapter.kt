package com.example.crm_application.ui.leads

import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.crm_application.R

class LeadsAdapter(private val leads: List<LeadsData>) : RecyclerView.Adapter<LeadsAdapter.LeadViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LeadViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lead, parent, false)
        return LeadViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: LeadViewHolder,
        position: Int
    ) {
        Log.i("Adapter", "check me: $leads")
        val lead = leads[position]
        holder.nameText.text = lead.name
        holder.statusText.text = lead.phone
    }

    override fun getItemCount(): Int = leads.size

    class LeadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.leadNameTextView)
        val statusText: TextView = itemView.findViewById(R.id.leadStatusTextView)
    }
}
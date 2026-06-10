package com.halombg.mobile

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.halombg.mobile.data.MockData
import com.halombg.mobile.databinding.ActivityMainBinding
import com.halombg.mobile.model.School

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSearch()

        binding.btnMasuk.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.cardFeatureSppg.setOnClickListener {
            binding.etSearchSchool.requestFocus()
        }

        binding.cardFeatureMenu.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                putExtra("PRESELECT_ROLE", "Siswa")
            })
        }

        binding.cardFeatureDistribusi.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                putExtra("PRESELECT_ROLE", "SPPG (Dapur)")
            })
        }

        binding.cardFeatureAi.setOnClickListener {
            // Allows guest demo validation AI directly! Extremely premium for demoing.
            startActivity(Intent(this, DashboardActivity::class.java).apply {
                putExtra("ROLE", "Siswa")
                putExtra("GO_TO_AI", true)
            })
        }
    }

    private fun setupSearch() {
        binding.rvSearchResults.layoutManager = LinearLayoutManager(this)
        searchAdapter = SearchAdapter { school ->
            selectSchool(school)
        }
        binding.rvSearchResults.adapter = searchAdapter

        binding.etSearchSchool.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim() ?: ""
                if (query.length >= 2) {
                    val results = MockData.searchSchools(query)
                    searchAdapter.updateData(results)
                    binding.rvSearchResults.visibility = if (results.isNotEmpty()) View.VISIBLE else View.GONE
                } else {
                    binding.rvSearchResults.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun selectSchool(school: School) {
        binding.etSearchSchool.setText(school.name)
        binding.rvSearchResults.visibility = View.GONE

        binding.tvSelectedSchoolName.text = school.name
        binding.tvSelectedSchoolMeta.text = "${school.district}, ${school.province}"

        val sppg = school.sppgId?.let { MockData.getSppgProfile(it) }
        binding.tvSelectedSchoolSppg.text = sppg?.kitchenName ?: "Belum terhubung ke dapur SPPG"

        binding.cardSelectedSchool.visibility = View.VISIBLE
    }

    // Inner Search Adapter to avoid creating multiple files
    private class SearchAdapter(private val onItemClick: (School) -> Unit) :
        RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

        private val items = mutableListOf<School>()

        fun updateData(newItems: List<School>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_school_result, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val school = items[position]
            holder.tvName.text = school.name
            holder.tvLoc.text = "${school.district} · ${school.province}"
            
            val sppg = school.sppgId?.let { MockData.getSppgProfile(it) }
            holder.tvKitchen.text = sppg?.kitchenName ?: "Belum Terhubung"

            holder.itemView.setOnClickListener { onItemClick(school) }
        }

        override fun getItemCount(): Int = items.size

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tvSchoolName)
            val tvLoc: TextView = view.findViewById(R.id.tvSchoolLocation)
            val tvKitchen: TextView = view.findViewById(R.id.tvSchoolKitchenName)
        }
    }
}

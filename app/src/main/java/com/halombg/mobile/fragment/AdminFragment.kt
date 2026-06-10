package com.halombg.mobile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.halombg.mobile.R
import com.halombg.mobile.data.MockData
import com.halombg.mobile.databinding.FragmentAdminBinding

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMetrics()
        setupMappingsList()
    }

    private fun setupMetrics() {
        binding.tvAdminSchoolsCount.text = MockData.schools.size.toString()
        binding.tvAdminKitchensCount.text = MockData.sppgProfiles.size.toString()
        binding.tvAdminReviewsCount.text = MockData.reviews.size.toString()
    }

    private fun setupMappingsList() {
        binding.layoutAdminMappingList.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        for (school in MockData.schools) {
            val itemView = inflater.inflate(R.layout.item_school_result, binding.layoutAdminMappingList, false)
            
            val tvName = itemView.findViewById<TextView>(R.id.tvSchoolName)
            val tvLoc = itemView.findViewById<TextView>(R.id.tvSchoolLocation)
            val tvKitchen = itemView.findViewById<TextView>(R.id.tvSchoolKitchenName)

            tvName.text = school.name
            tvLoc.text = "${school.address}, ${school.district}"
            
            val sppg = school.sppgId?.let { MockData.getSppgProfile(it) }
            tvKitchen.text = sppg?.kitchenName ?: "Belum Terhubung"

            binding.layoutAdminMappingList.addView(itemView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.halombg.mobile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.halombg.mobile.data.MockData
import com.halombg.mobile.databinding.FragmentGuruBinding

class GuruFragment : Fragment() {

    private var _binding: FragmentGuruBinding? = null
    private val binding get() = _binding!!
    private lateinit var reviewAdapter: SiswaFragment.ReviewAdapter

    // Simulated teacher belongs to SD Negeri 1 Jaya
    private val teacherSchoolId = 1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGuruBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvGuruReviews.layoutManager = LinearLayoutManager(requireContext())
        reviewAdapter = SiswaFragment.ReviewAdapter(
            showDelete = false,
            onFlagClick = { review ->
                if (review.flagStatus == "none") {
                    review.flagStatus = "flagged"
                    Toast.makeText(context, "Ulasan berhasil dilaporkan (flagged)!", Toast.LENGTH_SHORT).show()
                } else {
                    review.flagStatus = "none"
                    Toast.makeText(context, "Ulasan dipulihkan.", Toast.LENGTH_SHORT).show()
                }
                refreshReviews()
            }
        )
        binding.rvGuruReviews.adapter = reviewAdapter

        refreshReviews()
    }

    private fun refreshReviews() {
        val list = MockData.getReviewsForSchool(teacherSchoolId)
        reviewAdapter.updateData(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

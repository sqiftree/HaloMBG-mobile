package com.halombg.mobile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.halombg.mobile.R
import com.halombg.mobile.data.MockData
import com.halombg.mobile.databinding.FragmentSiswaBinding
import com.halombg.mobile.model.Review
import java.text.SimpleDateFormat
import java.util.*

class SiswaFragment : Fragment() {

    private var _binding: FragmentSiswaBinding? = null
    private val binding get() = _binding!!
    private lateinit var reviewAdapter: ReviewAdapter

    // Simulated logged-in student info
    private val studentSchoolId = 1L // SD Negeri 1 Jaya
    private val studentName = "Ahmad Dani"
    private var simulatedPhotoName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSiswaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenuDetails()
        setupReviewsList()
        setupInputForm()
    }

    private fun setupMenuDetails() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val school = MockData.schools.find { it.id == studentSchoolId }
        val sppgId = school?.sppgId

        if (sppgId != null) {
            val menu = MockData.getDailyMenuForSppg(sppgId, today)
            val sppg = MockData.getSppgProfile(sppgId)

            if (menu != null) {
                binding.tvSiswaMenuName.text = menu.menuName
                binding.tvSiswaMenuComponents.text = "Komponen: ${menu.components ?: "—"}"
                binding.tvSiswaCalories.text = menu.calories?.toString() ?: "—"
                binding.tvSiswaProtein.text = "${menu.protein ?: "—"}g"
                binding.tvSiswaCarbs.text = "${menu.carbs ?: "—"}g"
                binding.tvSiswaFat.text = "${menu.fat ?: "—"}g"
            } else {
                binding.tvSiswaMenuName.text = "Belum Ada Menu Hari Ini"
                binding.tvSiswaMenuComponents.text = "Menu akan muncul setelah SPPG menginput data harian."
                binding.tvSiswaCalories.text = "—"
                binding.tvSiswaProtein.text = "—"
                binding.tvSiswaCarbs.text = "—"
                binding.tvSiswaFat.text = "—"
            }
            binding.tvSiswaKitchenLabel.text = "Disediakan oleh: ${sppg?.kitchenName ?: "Dapur SPPG"}"
        } else {
            binding.tvSiswaMenuName.text = "Belum Terhubung ke Dapur SPPG"
            binding.tvSiswaMenuComponents.text = "Sekolah Anda belum terhubung dengan dapur SPPG mana pun."
            binding.tvSiswaKitchenLabel.text = "Hubungi admin sekolah untuk bantuan."
        }
    }

    private fun setupReviewsList() {
        binding.rvSiswaReviews.layoutManager = LinearLayoutManager(requireContext())
        reviewAdapter = ReviewAdapter(onDeleteClick = { review ->
            MockData.reviews.remove(review)
            refreshReviews()
            Toast.makeText(context, "Ulasan berhasil dihapus", Toast.LENGTH_SHORT).show()
        }, showDelete = true)
        binding.rvSiswaReviews.adapter = reviewAdapter
        refreshReviews()
    }

    private fun refreshReviews() {
        val list = MockData.getReviewsForSchool(studentSchoolId)
        reviewAdapter.updateData(list)
    }

    private fun setupInputForm() {
        binding.btnSiswaSelectPhoto.setOnClickListener {
            // Simulate photo selection
            simulatedPhotoName = "makan_siswa_${System.currentTimeMillis() / 1000}.jpg"
            binding.tvSiswaPhotoStatus.text = "Foto: $simulatedPhotoName"
            Toast.makeText(context, "Foto makanan disimulasikan!", Toast.LENGTH_SHORT).show()
        }

        binding.btnSiswaSubmitReview.setOnClickListener {
            val content = binding.etReviewContent.text.toString().trim()
            if (content.length < 10) {
                binding.etReviewContent.error = "Ulasan minimal 10 karakter"
                return@setOnClickListener
            }

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val newReview = Review(
                id = (MockData.reviews.maxOfOrNull { it.id } ?: 0L) + 1,
                userId = "siswa_1",
                userName = studentName,
                schoolId = studentSchoolId,
                schoolName = MockData.schools.find { it.id == studentSchoolId }?.name ?: "Sekolah",
                reviewDate = today,
                content = content,
                photo = simulatedPhotoName
            )

            MockData.addReview(newReview)
            refreshReviews()

            // Reset Form
            binding.etReviewContent.text.clear()
            simulatedPhotoName = null
            binding.tvSiswaPhotoStatus.text = "Belum ada foto terpilih"

            Toast.makeText(context, "Ulasan berhasil dikirim!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Inner class for Review Adapter to keep project organized
    class ReviewAdapter(
        private val onDeleteClick: (Review) -> Unit = {},
        private val showDelete: Boolean = false,
        private val onFlagClick: (Review) -> Unit = {}
    ) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

        private val items = mutableListOf<Review>()

        fun updateData(newItems: List<Review>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_review, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val review = items[position]
            holder.tvSchool.text = review.schoolName
            holder.tvAuthorDate.text = "${review.userName} · ${review.reviewDate}"
            holder.tvContent.text = review.content

            if (review.flagStatus == "flagged") {
                holder.cardFlagBadge.visibility = View.VISIBLE
            } else {
                holder.cardFlagBadge.visibility = View.GONE
            }

            if (showDelete) {
                holder.btnAction.text = "Hapus"
                holder.btnAction.setOnClickListener { onDeleteClick(review) }
            } else {
                // Moderation mode for Teacher
                if (review.flagStatus == "flagged") {
                    holder.btnAction.text = "Kembalikan"
                } else {
                    holder.btnAction.text = "Flag"
                }
                holder.btnAction.setOnClickListener { onFlagClick(review) }
            }
        }

        override fun getItemCount(): Int = items.size

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvSchool: TextView = view.findViewById(R.id.tvReviewSchool)
            val tvAuthorDate: TextView = view.findViewById(R.id.tvReviewAuthorDate)
            val tvContent: TextView = view.findViewById(R.id.tvReviewContent)
            val cardFlagBadge: View = view.findViewById(R.id.cardReviewFlagBadge)
            val btnAction: TextView = view.findViewById(R.id.btnReviewAction)
        }
    }
}

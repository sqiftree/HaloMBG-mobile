package com.halombg.mobile.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.halombg.mobile.R
import com.halombg.mobile.databinding.FragmentValidasiAiBinding
import java.text.SimpleDateFormat
import java.util.*

class ValidasiAiFragment : Fragment() {

    private var _binding: FragmentValidasiAiBinding? = null
    private val binding get() = _binding!!

    // Preset menu results matching ValidationAI.jsx
    private val presets = listOf(
        PresetMenu(
            name = "Nasi Putih, Fillet Ayam Panggang, Cah Wortel & Buncis",
            calories = 640,
            protein = 25,
            carbs = 82,
            fat = 14,
            tags = "Nasi Putih · Fillet Ayam Panggang · Cah Wortel & Buncis · Susu Kotak UHT · Melon",
            summary = "Menu memenuhi standar kecukupan nutrisi program MBG dengan gizi makro lengkap dan serat seimbang."
        ),
        PresetMenu(
            name = "Nasi Merah, Ikan Kembung Goreng, Sayur Sop Bening",
            calories = 590,
            protein = 22,
            carbs = 75,
            fat = 12,
            tags = "Nasi Merah · Ikan Kembung Goreng · Sayur Sop Bening · Susu Kotak UHT · Buah Pisang",
            summary = "Menu makanan kaya akan protein hewani, omega-3, dan karbohidrat kompleks dari nasi merah."
        ),
        PresetMenu(
            name = "Nasi Putih, Tumis Daging Sapi Lada Hitam, Sayur Bayam",
            calories = 670,
            protein = 28,
            carbs = 85,
            fat = 15,
            tags = "Nasi Putih · Tumis Daging Sapi Lada Hitam · Sayur Bayam · Susu Kotak UHT · Irisan Pepaya",
            summary = "Menu gizi lengkap dengan kandungan zat besi tinggi dari daging sapi segar serta zat antioksidan dari bayam."
        )
    )

    private var activePreset: PresetMenu? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentValidasiAiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCameraSimulation()
        setupValidationActions()
        setupHistoryLog()
    }

    private fun setupCameraSimulation() {
        binding.btnAiOpenSimulate.setOnClickListener {
            binding.layoutAiCameraPlaceholder.visibility = View.GONE
            binding.layoutAiCameraActive.visibility = View.VISIBLE
            binding.btnAiCapture.visibility = View.VISIBLE
            binding.btnAiOpenSimulate.visibility = View.GONE
            binding.viewScanLine.visibility = View.VISIBLE
        }
    }

    private fun setupValidationActions() {
        binding.btnAiCapture.setOnClickListener {
            binding.layoutAiScanning.visibility = View.VISIBLE
            binding.btnAiCapture.isEnabled = false

            // Simulate AI scanning delay of 2.5 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                if (isAdded) {
                    binding.layoutAiScanning.visibility = View.GONE
                    binding.btnAiCapture.isEnabled = true
                    binding.layoutAiCameraActive.visibility = View.GONE
                    binding.btnAiCapture.visibility = View.GONE
                    binding.viewScanLine.visibility = View.GONE
                    binding.btnAiOpenSimulate.visibility = View.VISIBLE
                    binding.layoutAiCameraPlaceholder.visibility = View.VISIBLE

                    showAiResult()
                }
            }, 2500)
        }

        binding.btnAiSaveLog.setOnClickListener {
            val preset = activePreset
            if (preset != null) {
                val timeNow = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                addHistoryRecord("Hari ini, $timeNow", preset)
                Toast.makeText(context, "Hasil validasi disimpan ke log harian!", Toast.LENGTH_SHORT).show()
                resetState()
            }
        }

        binding.btnAiReset.setOnClickListener {
            resetState()
        }
    }

    private fun showAiResult() {
        // Randomly pick a preset
        val randomPreset = presets[Random().nextInt(presets.size)]
        activePreset = randomPreset

        binding.tvAiResultSummary.text = randomPreset.summary
        binding.tvAiValCalories.text = randomPreset.calories.toString()
        binding.tvAiValProtein.text = "${randomPreset.protein}g"
        binding.tvAiValCarbs.text = "${randomPreset.carbs}g"
        binding.tvAiValFat.text = "${randomPreset.fat}g"
        binding.tvAiResultFoodTags.text = randomPreset.tags

        binding.cardAiResult.visibility = View.VISIBLE
    }

    private fun resetState() {
        activePreset = null
        binding.cardAiResult.visibility = View.GONE
        binding.layoutAiCameraActive.visibility = View.GONE
        binding.btnAiCapture.visibility = View.GONE
        binding.viewScanLine.visibility = View.GONE
        binding.btnAiOpenSimulate.visibility = View.VISIBLE
        binding.layoutAiCameraPlaceholder.visibility = View.VISIBLE
    }

    private fun setupHistoryLog() {
        binding.layoutAiHistoryContainer.removeAllViews()

        // Seed with two default historical entries matching web
        addHistoryRecord("Hari ini, 12:15", presets[0])
        addHistoryRecord("Kemarin, 11:58", presets[1])
    }

    private fun addHistoryRecord(time: String, menu: PresetMenu) {
        val itemView = layoutInflater.inflate(R.layout.item_review, binding.layoutAiHistoryContainer, false)
        
        val tvTitle = itemView.findViewById<TextView>(R.id.tvReviewSchool)
        val tvSub = itemView.findViewById<TextView>(R.id.tvReviewAuthorDate)
        val tvDesc = itemView.findViewById<TextView>(R.id.tvReviewContent)
        val btnAction = itemView.findViewById<TextView>(R.id.btnReviewAction)

        tvTitle.text = menu.name
        tvSub.text = "$time · Akurasi 96%"
        tvDesc.text = "Gizi: Kalori ${menu.calories} kkal, Protein ${menu.protein}g, Karbo ${menu.carbs}g, Lemak ${menu.fat}g.\n\nBahan: ${menu.tags}"
        
        // Use flag badge for Verified status indicator
        val cardFlagBadge = itemView.findViewById<View>(R.id.cardReviewFlagBadge)
        cardFlagBadge.visibility = View.VISIBLE
        val tvFlagText = cardFlagBadge.findViewById<TextView>(R.id.cardReviewFlagBadge.parent.findViewById<ViewGroup>(R.id.cardReviewFlagBadge).getChildAt(0).id) 
        // Note: we can just find any child or use tag, but simpler:
        try {
            val tv = cardFlagBadge.findViewById<TextView>(android.R.id.text1) ?: cardFlagBadge.findViewById<TextView>(cardFlagBadge.resources.getIdentifier("text1", "id", requireContext().packageName))
            tv?.text = "✓ Tervalidasi"
        } catch (e: Exception) {
            // fallback
        }

        btnAction.visibility = View.GONE // hide action button on log

        // Add at index 0 to show latest on top
        binding.layoutAiHistoryContainer.addView(itemView, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Data class to represent preset results
    private data class PresetMenu(
        val name: String,
        val calories: Int,
        val protein: Int,
        val carbs: Int,
        val fat: Int,
        val tags: String,
        val summary: String
    )
}

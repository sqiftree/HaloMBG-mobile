package com.halombg.mobile.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.halombg.mobile.R
import com.halombg.mobile.data.MockData
import com.halombg.mobile.databinding.FragmentSppgBinding
import com.halombg.mobile.model.DailyMenu
import com.halombg.mobile.model.DistributionStatus
import java.text.SimpleDateFormat
import java.util.*

class SppgFragment : Fragment() {

    private var _binding: FragmentSppgBinding? = null
    private val binding get() = _binding!!
    private lateinit var distAdapter: DistAdapter

    private var mode: String = "menu" // menu or distribusi
    private val sppgId = 1L // Dapur Sehat Kartasura
    private var simulatedPhotoName: String? = null
    private var isAiValidated = false

    companion object {
        fun newInstance(mode: String): SppgFragment {
            val fragment = SppgFragment()
            val args = Bundle()
            args.putString("MODE", mode)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mode = arguments?.getString("MODE") ?: "menu"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSppgBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabs()
        setupMenuForm()
        setupDistList()

        // Sync view state based on mode
        updateViewMode()
    }

    private fun setupTabs() {
        binding.btnTabMenu.setOnClickListener {
            mode = "menu"
            updateViewMode()
        }
        binding.btnTabDistribusi.setOnClickListener {
            mode = "distribusi"
            updateViewMode()
        }
    }

    private fun updateViewMode() {
        if (mode == "menu") {
            binding.btnTabMenu.setTextColor(resources.getColor(R.color.primary_navy, null))
            binding.btnTabMenu.setTypeface(null, android.graphics.Typeface.BOLD)
            binding.btnTabDistribusi.setTextColor(resources.getColor(R.color.text_secondary, null))
            binding.btnTabDistribusi.setTypeface(null, android.graphics.Typeface.NORMAL)

            binding.layoutSppgMenu.visibility = View.VISIBLE
            binding.layoutSppgDistribusi.visibility = View.GONE
        } else {
            binding.btnTabDistribusi.setTextColor(resources.getColor(R.color.primary_navy, null))
            binding.btnTabDistribusi.setTypeface(null, android.graphics.Typeface.BOLD)
            binding.btnTabMenu.setTextColor(resources.getColor(R.color.text_secondary, null))
            binding.btnTabMenu.setTypeface(null, android.graphics.Typeface.NORMAL)

            binding.layoutSppgMenu.visibility = View.GONE
            binding.layoutSppgDistribusi.visibility = View.VISIBLE
            refreshDistributions()
        }
    }

    private fun setupMenuForm() {
        binding.btnSppgSelectPhoto.setOnClickListener {
            simulatedPhotoName = "menu_sppg_${System.currentTimeMillis() / 1000}.jpg"
            binding.tvSppgPhotoStatus.text = "Foto: $simulatedPhotoName"
            Toast.makeText(context, "Foto menu disimulasikan!", Toast.LENGTH_SHORT).show()
        }

        binding.btnSppgAiValidate.setOnClickListener {
            binding.tvSppgAiStatus.text = "Status AI: Menganalisis foto gizi..."
            binding.tvSppgAiStatus.setTextColor(resources.getColor(R.color.status_warning, null))
            binding.btnSppgAiValidate.isEnabled = false

            // 1.5s delay simulation
            Handler(Looper.getMainLooper()).postDelayed({
                if (isAdded) {
                    isAiValidated = true
                    binding.tvSppgAiStatus.text = "Status AI: ✦ TERVALIDASI (Akurasi Porsi 97%)"
                    binding.tvSppgAiStatus.setTextColor(resources.getColor(R.color.status_success, null))
                    binding.btnSppgAiValidate.isEnabled = true
                    Toast.makeText(context, "Validasi AI sukses!", Toast.LENGTH_SHORT).show()
                }
            }, 1500)
        }

        binding.btnSppgSaveMenu.setOnClickListener {
            val name = binding.etSppgMenuName.text.toString().trim()
            val components = binding.etSppgMenuComponents.text.toString().trim()
            val calStr = binding.etSppgCalories.text.toString().trim()
            val protStr = binding.etSppgProtein.text.toString().trim()
            val carbStr = binding.etSppgCarbs.text.toString().trim()
            val fatStr = binding.etSppgFat.text.toString().trim()

            if (name.isEmpty()) {
                binding.etSppgMenuName.error = "Nama menu tidak boleh kosong"
                return@setOnClickListener
            }

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val menu = DailyMenu(
                id = (MockData.dailyMenus.maxOfOrNull { it.id } ?: 0L) + 1,
                sppgId = sppgId,
                servedAt = today,
                menuName = name,
                components = components.ifEmpty { null },
                calories = calStr.toIntOrNull() ?: 600,
                protein = protStr.toIntOrNull() ?: 24,
                carbs = carbStr.toIntOrNull() ?: 80,
                fat = fatStr.toIntOrNull() ?: 12,
                photo = simulatedPhotoName,
                isAiValidated = isAiValidated
            )

            MockData.addDailyMenu(menu)

            Toast.makeText(context, "Menu Harian berhasil disimpan!", Toast.LENGTH_SHORT).show()

            // Reset
            binding.etSppgMenuName.text.clear()
            binding.etSppgMenuComponents.text.clear()
            binding.etSppgCalories.text.clear()
            binding.etSppgProtein.text.clear()
            binding.etSppgCarbs.text.clear()
            binding.etSppgFat.text.clear()
            simulatedPhotoName = null
            binding.tvSppgPhotoStatus.text = "Belum ada foto"
            isAiValidated = false
            binding.tvSppgAiStatus.text = "Status AI: Belum divalidasi"
            binding.tvSppgAiStatus.setTextColor(resources.getColor(R.color.text_tertiary, null))
        }
    }

    private fun setupDistList() {
        binding.rvSppgSchools.layoutManager = LinearLayoutManager(requireContext())
        distAdapter = DistAdapter { schoolId, status ->
            MockData.updateDistributionStatus(sppgId, schoolId, status)
            refreshDistributions()
            Toast.makeText(context, "Status pengiriman diperbarui", Toast.LENGTH_SHORT).show()
        }
        binding.rvSppgSchools.adapter = distAdapter
    }

    private fun refreshDistributions() {
        val list = MockData.getDistributionStatusesForSppg(sppgId)
        distAdapter.updateData(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Inner class for Distribution RecyclerView Adapter
    class DistAdapter(private val onStatusChanged: (Long, String) -> Unit) :
        RecyclerView.Adapter<DistAdapter.ViewHolder>() {

        private val items = mutableListOf<DistributionStatus>()
        private val statusOptions = listOf(
            "Belum Diantar" to "belum_diantar",
            "Siap Diantar" to "siap_diantar",
            "Sudah Diantar" to "sudah_diantar",
            "Batal" to "batal"
        )

        fun updateData(newItems: List<DistributionStatus>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_distribution, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val dist = items[position]
            holder.tvSchoolName.text = dist.schoolName
            holder.tvUpdateTime.text = "Diperbarui: ${dist.statusUpdatedAt}"

            val labelList = statusOptions.map { it.first }
            val adapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, labelList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            holder.spinnerStatus.adapter = adapter

            val activeIndex = statusOptions.indexOfFirst { it.second == dist.status }
            if (activeIndex >= 0) {
                holder.spinnerStatus.setSelection(activeIndex, false)
            }

            // Spinner Selection Listener
            holder.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                private var isFirstSelection = true

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    if (isFirstSelection) {
                        isFirstSelection = false
                        return
                    }
                    val selectedValue = statusOptions[pos].second
                    if (selectedValue != dist.status) {
                        onStatusChanged(dist.schoolId, selectedValue)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        override fun getItemCount(): Int = items.size

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvSchoolName: TextView = view.findViewById(R.id.tvDistSchoolName)
            val tvUpdateTime: TextView = view.findViewById(R.id.tvDistUpdateTime)
            val spinnerStatus: Spinner = view.findViewById(R.id.spinnerDistStatus)
        }
    }
}

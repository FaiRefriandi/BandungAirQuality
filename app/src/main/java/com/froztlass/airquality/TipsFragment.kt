package com.froztlass.airquality

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TipsFragment : Fragment() {
    // Fa'i Refriandi - 10121079
    // PemAndro3 - 08/08/2024
    // Tugas Besar UAS
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tips, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_tips)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val tipsList = listOf(
            TipsItem("Gunakan Transportasi Umum", "Berpindah dari kendaraan pribadi ke transportasi umum dapat mengurangi jumlah emisi karbon yang Anda hasilkan. Cobalah naik bus atau kereta api dan bantu mengurangi kemacetan serta polusi udara."),
            TipsItem("Hemat Energi di Rumah", "Kurangi penggunaan energi dengan mematikan lampu dan perangkat elektronik saat tidak digunakan. Gunakan lampu LED dan peralatan rumah tangga yang hemat energi untuk mengurangi jejak karbon Anda."),
            TipsItem("Dukung Energi Terbarukan", "Berpindahlah ke sumber energi terbarukan seperti tenaga surya atau angin. Ini membantu mengurangi ketergantungan pada bahan bakar fosil yang menyumbang polusi udara."),
            TipsItem("Tanam Pohon di Sekitar Anda", "Menanam pohon di halaman rumah atau lingkungan sekitar tidak hanya mempercantik area tetapi juga membantu menyerap polutan dan meningkatkan kualitas udara."),
            TipsItem("Kurangi Penggunaan Plastik", "Hindari penggunaan plastik sekali pakai dan beralihlah ke alternatif ramah lingkungan seperti tas kain atau botol air stainless steel. Plastik yang tidak terurai berkontribusi pada polusi tanah dan laut.")
        )

        recyclerView.adapter = TipsAdapter(tipsList)

        return view
    }
}

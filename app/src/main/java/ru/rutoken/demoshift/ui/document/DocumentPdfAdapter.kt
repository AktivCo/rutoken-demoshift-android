package ru.rutoken.demoshift.ui.document

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.rutoken.demoshift.databinding.ItemPdfPageBinding

class DocumentPdfAdapter : RecyclerView.Adapter<DocumentPdfAdapter.PdfPageViewHolder>() {
    private val pageBitmaps: MutableList<Bitmap> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfPageViewHolder {
        val binding = ItemPdfPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PdfPageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PdfPageViewHolder, position: Int) = holder.bind(pageBitmaps[position])

    override fun getItemCount(): Int = pageBitmaps.size

    fun addPdfPageBitmap(bitmap: Bitmap) {
        pageBitmaps.add(bitmap)
        notifyItemInserted(pageBitmaps.lastIndex)
    }

    inner class PdfPageViewHolder(private val binding: ItemPdfPageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bitmap: Bitmap) = binding.imageViewPdfPage.setImageBitmap(bitmap)
    }
}

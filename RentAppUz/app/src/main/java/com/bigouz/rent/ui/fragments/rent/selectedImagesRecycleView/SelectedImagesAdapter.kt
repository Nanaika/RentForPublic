package com.bigouz.rent.ui.fragments.rent.selectedImagesRecycleView

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bigouz.rent.ui.fragments.rent.OnSelectedImageClicked
import com.bumptech.glide.Glide
import com.example.rent.R

class SelectedImagesAdapter(
    var context: Context, var images: List<Uri>,
    private val itemClickListener: OnSelectedImageClicked
) : RecyclerView.Adapter<SelectedImagesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImagesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.selected_image, parent, false)
        return SelectedImagesViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelectedImagesViewHolder, position: Int) {
        val glideApp = Glide.with(context)
        glideApp.load(images[position]).into(holder.image)
        holder.bind(images[position], itemClickListener)
    }

    override fun getItemCount(): Int {
        return images.size
    }
}
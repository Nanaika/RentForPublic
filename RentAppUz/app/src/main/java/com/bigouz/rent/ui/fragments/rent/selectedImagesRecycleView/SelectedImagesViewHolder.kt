package com.bigouz.rent.ui.fragments.rent.selectedImagesRecycleView

import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bigouz.rent.ui.fragments.rent.OnSelectedImageClicked
import kotlinx.android.synthetic.main.selected_image.view.*

class SelectedImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var image: ImageView = itemView.iv_selected_image

    fun bind(uri: Uri, clickListener: OnSelectedImageClicked) {
        itemView.setOnClickListener {
            clickListener.selectedImageClicked(uri)
        }
    }
}
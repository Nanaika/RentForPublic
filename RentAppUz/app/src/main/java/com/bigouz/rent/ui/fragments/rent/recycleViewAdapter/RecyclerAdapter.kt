package com.bigouz.rent.ui.fragments.rent.recycleViewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bigouz.rent.domain.models.Rent
import com.bigouz.rent.ui.fragments.rent.recycleViewAdapter.multiSelect.ClickHandler
import com.bigouz.rent.ui.fragments.rent.recycleViewAdapter.multiSelect.OnEventListener
import com.bigouz.rent.utils.favoriteButton
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rent.BR
import com.example.rent.R
import com.example.rent.databinding.MyRentsBinding
import kotlinx.android.synthetic.main.fragment_card.view.*

class RecyclerAdapter(
    private val onEventListener: OnEventListener
) : RecyclerView.Adapter<RecyclerAdapter.RentViewHolder>() {

    private var isSelectModeEnabled = false

    inner class RentViewHolder(
        private val dataBinding: ViewDataBinding,
        private val onEventListener: OnEventListener
    ) : RecyclerView.ViewHolder(dataBinding.root) {
        fun bind(rent: Rent, position: Int) {
            val context = dataBinding.root.context
            val options: RequestOptions = RequestOptions()
                .error(R.color.blue_new)
            if (rent.listImages.isNotEmpty()) {
                Glide.with(context).load(rent.listImages.first()).apply(options)
                    .into(dataBinding.root.rootView.iv_card_rent_main_photo)
            }

            dataBinding.root.rootView.tv_card_owner_name.text = rent.ownerName
            dataBinding.root.rootView.tv_card_address.text = "${rent.city}, ${rent.region}"
            dataBinding.root.rootView.tv_card_price.text = rent.price.toString()
            dataBinding.root.rootView.tv_card_rooms.text = rent.rooms.toString()
            dataBinding.root.rootView.tv_card_floor.text = rent.floor.toString()
            dataBinding.root.rootView.tv_card_area.text = rent.area.toString()
            dataBinding.root.rootView.iv_card_rent_main_photo.isFocusable = false
            favoriteButton(dataBinding.root.rootView.btn_add_to_favorite)

            if (rent.isSelected) {
                dataBinding.root
                    .rootView
                    .iv_selected.visibility = View.VISIBLE
            } else {
                dataBinding.root
                    .rootView
                    .iv_selected.visibility = View.INVISIBLE
            }

            dataBinding.setVariable(BR.rent, rent)
            dataBinding.setVariable(BR.position, position)
            dataBinding.setVariable(BR.clickHandler, ClickHandler(onEventListener))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val dataBinding = MyRentsBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return RentViewHolder(dataBinding, onEventListener)
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Rent>() {
        override fun areItemsTheSame(oldItem: Rent, newItem: Rent): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Rent, newItem: Rent): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    fun getSelectedItems() = differ.currentList.filter { rent -> rent.isSelected }

    override fun onBindViewHolder(holder: RentViewHolder, position: Int) {
        val rent = differ.currentList[position]
        holder.bind(rent, position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onViewRecycled(holder: RentViewHolder) {
        holder.itemView.rootView.iv_card_rent_main_photo.setImageDrawable(null)
        holder.itemView.rootView.btn_add_to_favorite.icon = ResourcesCompat.getDrawable(
            holder.itemView.rootView.context.resources,
            R.drawable.ic_apple_favorite_24dp_empty, null
        )

    }

    fun setSelectMode(bool: Boolean) {
        isSelectModeEnabled = bool
    }
}
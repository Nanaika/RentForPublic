package com.bigouz.rent.ui.fragments.rent.recycleViewAdapter.multiSelect

import com.bigouz.rent.domain.models.Rent

class ClickHandler(
    private val onEventListener: OnEventListener
) {
    fun onClick(position: Int, rent: Rent): Boolean {
        onEventListener.onClick(position, rent)
        return true
    }

    fun onLongClick(position: Int): Boolean {
        onEventListener.onLongClick(position)
        return true
    }
}

interface OnEventListener {
    fun onClick(position: Int, rent: Rent)

    fun onLongClick(position: Int) {}
}
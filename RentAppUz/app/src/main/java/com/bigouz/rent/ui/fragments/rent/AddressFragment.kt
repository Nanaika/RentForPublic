package com.bigouz.rent.ui.fragments.rent

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.rent.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_address.*

class AddressFragment : DialogFragment() {

    private val viewModel: RentViewModel by activityViewModels()
    private val tashkent = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemsRegions = requireActivity().resources.getStringArray(R.array.tashkent_regions)
        val itemsCities = requireActivity().resources.getStringArray(R.array.uz_city)

        btn_back.setOnClickListener {
            dismiss()
        }

        btn_location.setOnClickListener {
            val mapDialog = MapFragment()
            val fm = requireActivity().supportFragmentManager
            mapDialog.setStyle(STYLE_NORMAL, R.style.CustomDialog)
            mapDialog.show(fm, "map_dialog")
        }

        et_city.isFocusableInTouchMode = false
        et_city.isLongClickable = false
        et_city.setOnClickListener {
            MaterialAlertDialogBuilder(requireActivity(), R.style.MyDialogStyle)
                .setTitle("Select preferred city ...")
                .setNegativeButton("Clear") { dialog, which ->
                    val editable: Editable = SpannableStringBuilder("")
                    et_city.text = editable
                    tv_district.visibility = View.GONE
                    et_district.visibility = View.GONE
                    viewModel.city = null
                    viewModel.region = null

                }
                .setPositiveButton("Ok") { dialog, which ->
                    dialog.dismiss()
                }
                .setSingleChoiceItems(itemsCities, -1) { dialog, which ->
                    val editable: Editable = SpannableStringBuilder(itemsCities[which])
                    et_city.text = editable
                    viewModel.city = et_city.text.toString()
                    if (which == tashkent) {
                        tv_district.visibility = View.VISIBLE
                        et_district.visibility = View.VISIBLE
                    } else {
                        tv_district.visibility = View.GONE
                        et_district.visibility = View.GONE
                    }
                }
                .show()
        }

        et_district.isFocusableInTouchMode = false
        et_district.isLongClickable = false
        et_district.setOnClickListener {
            MaterialAlertDialogBuilder(requireActivity(), R.style.MyDialogStyle)
                .setTitle("Select preferred region ...")
                .setNegativeButton("Clear") { dialog, which ->
                    val editable: Editable = SpannableStringBuilder("")
                    et_district.text = editable
                    viewModel.region = null
                }
                .setPositiveButton("Ok") { dialog, which ->
                    dialog.dismiss()
                }
                .setSingleChoiceItems(itemsRegions, -1) { dialog, which ->
                    val editable: Editable = SpannableStringBuilder(itemsRegions[which])
                    et_district.text = editable
                    viewModel.region = et_district.text.toString()
                }
                .show()
        }

    }// end of on view created

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation;
    }

}
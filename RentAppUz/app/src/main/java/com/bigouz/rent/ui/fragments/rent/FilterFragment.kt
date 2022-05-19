package com.bigouz.rent.ui.fragments.rent

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bigouz.rent.domain.models.Filter
import com.bigouz.rent.domain.models.PropertyType
import com.bigouz.rent.domain.models.RentType
import com.bigouz.rent.utils.*
import com.example.rent.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_filter.*


class FilterFragment : DialogFragment() {

    private val viewModel: RentViewModel by activityViewModels()
    private var priceValues: List<Float> = listOf(0.0f, 100000.0f)
    private var numRoomsValue: Float = 1.0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_filter, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lifecycleOwner = viewLifecycleOwner
        viewModel.rentType.observe(lifecycleOwner) {
            when (it) {
                0 -> {
                    colorFirstButton(
                        btn_sell, btn_rent, btn_rent_room,
                        requireActivity()
                    )
                }
                1 -> {
                    colorSecondButton(
                        btn_sell, btn_rent, btn_rent_room,
                        requireActivity()
                    )
                }
                2 -> {
                    colorThirdButton(
                        btn_sell, btn_rent, btn_rent_room,
                        requireActivity()
                    )
                }
            }
        }

        btn_back.setOnClickListener {
            viewModel.isFilterClicked = false
            dismiss()
        }

        threeFilterButtons(
            btn_sell,
            btn_rent,
            btn_rent_room,
            requireContext(),
            viewModel.rentType
        )
        twoFilterButtons(btn_house, btn_flat, requireContext(), viewModel.propertyType)

        var checkedItem = 0
        val itemsRegions = requireActivity().resources.getStringArray(R.array.tashkent_regions)
        val itemsCities = requireActivity().resources.getStringArray(R.array.uz_city)

//        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
//        et_select_district.setAdapter(adapter)
//        et_select_district.setText("All", false)

//        sli_price.setLabelFormatter { value: Float ->
//            return@setLabelFormatter "${value.toInt()}"
//        }
//        changeSliderTypeFace(sli_price, R.style.CustomTooltipTextAppearance)
//        changeSliderTypeFace(sli_num_rooms, R.style.CustomTooltipTextAppearance)

        sli_price.addOnChangeListener { _, _, fromUser ->
            priceValues = sli_price.values
            println("Start value: ${priceValues[0]}, End value: ${priceValues[1]}")
            tv_price_range.text = "${priceValues[0].toInt()} - ${priceValues[1].toInt()}"
        }

        sli_num_rooms.addOnChangeListener { val1, val2, fromUser ->
            println("Start value: ${val1}, End value: ${val2}")
            numRoomsValue = val2
            tv_number_of_rooms.text = "${val2.toInt()}"
        }


        et_district.isFocusableInTouchMode = false
        et_district.isLongClickable = false
        et_district.setOnClickListener {
            MaterialAlertDialogBuilder(requireActivity(), R.style.MyDialogStyle)
                .setTitle("Select preferred region ...")
                .setNegativeButton("Clear") { dialog, which ->
                    val editable: Editable = SpannableStringBuilder("")
                    et_district.text = editable
                }
                .setPositiveButton("Select") { dialog, which ->
                    dialog.dismiss()
                }
                .setSingleChoiceItems(itemsRegions, -1) { dialog, which ->
                    val editable: Editable = SpannableStringBuilder(itemsRegions[which])
                    et_district.text = editable
                }
                .show()
        }

        et_city.isFocusableInTouchMode = false
        et_city.isLongClickable = false
        et_city.setOnClickListener {
            MaterialAlertDialogBuilder(requireActivity(), R.style.MyDialogStyle)
                .setTitle("Select preferred city ...")
                .setNegativeButton("Clear") { dialog, which ->
                    val editable: Editable = SpannableStringBuilder("")
                    et_city.text = editable
                }
                .setPositiveButton("Select") { dialog, which ->
                    dialog.dismiss()
                }
                .setSingleChoiceItems(itemsCities, -1) { dialog, which ->
                    val editable: Editable = SpannableStringBuilder(itemsCities[which])
                    et_city.text = editable
                }
                .show()
        }

        btn_apply.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isNeedShow", true)
            viewModel.filter =
                Filter(
                    RentType.valueOf(viewModel.rentType.value!!),
                    priceValues[0].toInt(),
                    priceValues[1].toInt(),
                    "Nukus str 2A 71",
                    numRoomsValue.toInt(),
                    PropertyType.valueOf(viewModel.propertyType.value!!)
                )
            viewModel.isFilterClicked = false
            dismiss()
        }

    }//end of on view created

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        viewModel.isFilterClicked = false
    }

    //    override fun onResume() {
    // Get existing layout params for the window
//        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
    // Assign window properties to fill the parent
//        params.width = WindowManager.LayoutParams.MATCH_PARENT
//        params.height = WindowManager.LayoutParams.MATCH_PARENT
//        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    // Call super onResume after sizing
//        super.onResume()
//    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

}
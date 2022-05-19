package com.bigouz.rent.ui.fragments.rent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigouz.rent.domain.models.Rent
import com.bigouz.rent.ui.fragments.rent.recycleViewAdapter.RecyclerAdapter
import com.bigouz.rent.ui.fragments.rent.recycleViewAdapter.multiSelect.OnEventListener
import com.bigouz.rent.utils.MarginItemDecoration
import com.example.rent.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_my_rents.*

class MyRentsFragment : Fragment(), OnEventListener {

    private var selectionModeEnabled: Boolean = false

    lateinit var adapter: RecyclerAdapter
    private val viewModel: RentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_my_rents, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getMyRents(Firebase.auth.currentUser!!.uid)
        rv_my_rents.layoutManager =
            LinearLayoutManager(requireContext())
        adapter = RecyclerAdapter(this)
        viewModel.myRents.observe(viewLifecycleOwner, Observer {
            adapter.differ.submitList(it)
            Log.i("AAD", "getMyRents: size ----  ${it.size}")
        })
        rv_my_rents.setItemViewCacheSize(0)
        rv_my_rents.adapter = adapter
        rv_my_rents.addItemDecoration(
            MarginItemDecoration(25)
        )

        btn_select.setOnClickListener {
            if (btn_select.text == "Back") {
                deselectAllProducts()
            } else {
                setDeleteToolBar()
                selectionModeEnabled = true
            }
        }

        btn_add_rent.setOnClickListener {
                val addRentDialog = AddRentFragment()
                val fm = requireActivity().supportFragmentManager
                addRentDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog)
                addRentDialog.show(fm, "add_rent_dialog")
        }

        btn_delete.setOnClickListener {
            Toast.makeText(requireActivity(), "Deleted", Toast.LENGTH_SHORT).show()
        }

    }// on view created end

    override fun onClick(position: Int, rent: Rent) {
        if (selectionModeEnabled) {
            startSelection(position)
        } else {
            var intent = Intent(requireContext(), DetailsFragment::class.java)
            intent.putExtra("Rent", rent)
            startActivity(intent)
        }
    }

    override fun onLongClick(position: Int) {
        selectionModeEnabled = true
        startSelection(position)
    }

    private fun startSelection(position: Int) {
        if (!adapter.differ.currentList[position].isSelected) {
            selectProductAt(position)
        } else {
            deselectProductAt(position)
        }
        if (adapter.differ.currentList.none { p -> p.isSelected }) {
//            setDefaultToolbar()
//            selectionModeEnabled = false
        } else {
            setDeleteToolBar()
        }
    }

    private fun selectProductAt(position: Int) {
        adapter.differ.currentList[position].isSelected = true
        adapter.notifyItemChanged(position)
    }

    private fun deselectProductAt(position: Int) {
        adapter.differ.currentList[position].isSelected = false
        adapter.notifyItemChanged(position)
    }

    private fun deselectAllProducts() {
        adapter.differ.currentList.forEachIndexed { position, product ->
            if (product.isSelected) {
                deselectProductAt(position)
            }
        }
        setDefaultToolbar()
        selectionModeEnabled = false
    }

    private fun setDefaultToolbar() {
        btn_select.text = resources.getText(R.string.select)
        btn_delete.visibility = View.GONE
        btn_add_rent.visibility =View.VISIBLE
        tv_title.text = resources.getText(R.string.my_rentals_title)
    }

    private fun setDeleteToolBar() {
        btn_select.text = resources.getText(R.string.back)
        btn_delete.visibility = View.VISIBLE
        btn_add_rent.visibility =View.GONE
        tv_title.text = "Selected : ${adapter.getSelectedItems().size}"
    }

}
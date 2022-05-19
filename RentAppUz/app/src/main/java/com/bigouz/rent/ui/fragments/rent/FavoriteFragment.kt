package com.bigouz.rent.ui.fragments.rent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigouz.rent.domain.models.Rent
import com.bigouz.rent.ui.activity.main.MainActivity
import com.bigouz.rent.ui.fragments.rent.recycleViewAdapter.RecyclerAdapter
import com.bigouz.rent.utils.MarginItemDecoration
import com.example.rent.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_favorite.*


class FavoriteFragment : Fragment(), OnItemClickListener {

    private val viewModel: RentViewModel by activityViewModels()

//    val rents = listOf(
//        Rent(
//            id= 11.toString(),
//            price = 1200,
//            address = "Express Lono, Sarasota, FL,  34238 USA",
//            date = "01.02.2021",
//            R.drawable.appartment_1
//        ),
//        Rent(
//            id= 11.toString(),
//            price = 1000,
//            address = "Express Lono, Sarasota, FL,  34238 USA",
//            date = "21.03.2021",
//            R.drawable.appartment_2
//        ),
//        Rent(
//            id= 11.toString(),
//            price = 600,
//            address = "Express Lono, Sarasota, FL,  34238 USA",
//            date = "15.04.2021",
//            R.drawable.appartment_3
//        ),
//        Rent(
//            id= 11.toString(),
//            price = 1200,
//            address = "Express Lono, Sarasota, FL,  34238 USA",
//            date = "21.03.2021",
//            R.drawable.appartment_4
//        ),
//        Rent(
//            id= 11.toString(),
//            price = 2000,
//            address = "Express Lono, Sarasota, FL,  34238 USA",
//            date = "21.03.2021",
//            R.drawable.appartment_5
//        ),
//        Rent(
//            id= 11.toString(),
//            price = 5000,
//            address = "Express Lono, Sarasota, FL,  34238 USA",
//            date = "21.03.2021",
//            R.drawable.appartment_6
//        ),
//        Rent(
//            id= 11.toString(),
//            price = 1500,
//            address = "Express Lono, Sarasota, FL,  34238 USA",
//            date = "21.03.2021",
//            R.drawable.appartment_7
//        ),
//        Rent(
//            id= 11.toString(),
//            price = 10000,
//            address = "Express Lono, Sarasota, FL,  34238 USA",
//            date = "21.03.2021",
//            R.drawable.appartment_8
//        ),
//        Rent(
//            id= 11.toString(),
//            price = 7000,
//            address = "Express Lono, Sarasota, FL,  34238 USA",
//            date = "21.03.2021",
//            R.drawable.appartment_9
//        ),
//        Rent(
//            id= 11.toString(),
//            price = 9000,
//            address = "Express Lono, Sarasota, FL,  34238 USA",
//            date = "21.03.2021",
//            R.drawable.appartment_10
//        ),
//        Rent(
//            id= 11.toString(),
//            price = 22000,
//            address = "Express Lono, Sarasota, FL,  34238 USA",
//            date = "21.03.2021",
//            R.drawable.appartment_1
//        ),
//    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_favorite, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(requireActivity() as MainActivity) {
            bottom_navigation.visibility = View.VISIBLE
        }

        rv_my_favorites.layoutManager = LinearLayoutManager(activity)
//        val adapter = activity?.let { RecyclerAdapter(it.applicationContext, rents, this) }
//        rv_my_favorites.adapter = adapter
        rv_my_favorites.addItemDecoration(
            MarginItemDecoration(20)
        )

    }//end of on view created

    override fun onClick(rent: Rent) {
        viewModel.destFrom.value = 2
        findNavController().navigate(
            FavoriteFragmentDirections.actionFavoriteFragmentToDetailsFragment(
                rent
            )
        )
    }


}
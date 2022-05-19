package com.bigouz.rent.ui.fragments.rent

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigouz.rent.domain.models.CollectionTypes
import com.bigouz.rent.domain.models.Rent
import com.bigouz.rent.ui.fragments.rent.recycleViewAdapter.RecyclerAdapter
import com.bigouz.rent.ui.fragments.rent.recycleViewAdapter.multiSelect.OnEventListener
import com.bigouz.rent.utils.*
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.rent.R
import com.example.rent.databinding.FragmentRootBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_root.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class RootFragment : Fragment(), OnEventListener {

    private lateinit var adapterSell: RecyclerAdapter
    private lateinit var adapterRent: RecyclerAdapter
    private lateinit var adapterRoom: RecyclerAdapter
    private val viewModel: RentViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = DataBindingUtil.inflate<FragmentRootBinding>(
        inflater, R.layout.fragment_root, container, false
    ).root

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        initRecyclers()


        viewModel.sellRents.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    hideProgressBar()
                    resource.data?.let {
                        adapterSell.differ.submitList(it)
                        adapterSell.notifyItemInserted(adapterSell.differ.currentList.size)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    Toast.makeText(this.context, resource.message!!, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        viewModel.rentRents.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    hideProgressBar()
                    resource.data?.let {
                        adapterRent.differ.submitList(it)
                        adapterRent.notifyItemInserted(adapterRent.differ.currentList.size)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    Toast.makeText(this.context, resource.message!!, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        viewModel.roomRents.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    hideProgressBar()
                    resource.data?.let {
                        adapterRoom.differ.submitList(it)
                        adapterRoom.notifyItemInserted(adapterRoom.differ.currentList.size)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    Toast.makeText(this.context, resource.message!!, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        if (Firebase.auth.currentUser!!.displayName != null) {
            tv_name.text = "Hello ${Firebase.auth.currentUser?.displayName}!"
        }

        val glide = GlideApp.with(requireActivity())

        if (!getUserPhotoFromDisk()) {
            if (Firebase.auth.currentUser!!.photoUrl != null) {
                glide.load(Firebase.auth.currentUser!!.photoUrl).into(iv_card_owner_photo)
            } else {
                val userUid = Firebase.auth.currentUser!!.uid
                Firebase.firestore.collection(CollectionTypes.USERS.toString()).document(userUid)
                    .get().addOnCompleteListener {
                        if (it.result.exists()) {
                            val photoUrl: String = it.result.get(PHOTO_URL).toString()
                            if (photoUrl != "null") {
                                glide.load(photoUrl.toUri())
                                    .listener(object : RequestListener<Drawable> {
                                        override fun onResourceReady(
                                            resource: Drawable?,
                                            model: Any?,
                                            target: Target<Drawable>?,
                                            dataSource: DataSource?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            val drawableBitmap = resource!!.toBitmap()
                                            val file = convertBmpToFile(drawableBitmap)
                                            val outputStream: FileOutputStream

                                            try {
                                                outputStream = requireActivity().openFileOutput(
                                                    Firebase.auth.currentUser!!.uid,
                                                    Context.MODE_PRIVATE
                                                )
                                                outputStream.write(file.readBytes())
                                                outputStream.close()
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                            return true
                                        }

                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any?,
                                            target: Target<Drawable>?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            return false
                                        }
                                    })
                                    .into(iv_card_owner_photo)
                            }
                        }
                    }
            }
        }

        btn_filter.setOnClickListener()
        {
            if (!viewModel.isFilterClicked) {
                val filterDialog = FilterFragment()
                val fm = requireActivity().supportFragmentManager
                filterDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog)
                filterDialog.show(fm, "filter_dialog")
            }
            viewModel.isFilterClicked = true
        }

        rv_rents_sell.addItemDecoration(
            MarginItemDecoration(25)
        )
        rv_rents_rent.addItemDecoration(
            MarginItemDecoration(25)
        )
        rv_rents_room.addItemDecoration(
            MarginItemDecoration(25)
        )

        threeRentTypeButtons(
            btn_buy,
            btn_rent,
            btn_rent_room,
            requireActivity(),
            viewModel.rentType
        )

        val lifecycleOwner = viewLifecycleOwner
        viewModel.rentType.observe(lifecycleOwner) {
            when (it) {
                0 -> {
                    colorFirstButton(
                        btn_buy,
                        btn_rent,
                        btn_rent_room,
                        requireActivity()
                    )
                    rv_rents_sell.visibility = View.VISIBLE
                    rv_rents_rent.visibility = View.GONE
                    rv_rents_room.visibility = View.GONE
                }
                1 -> {
                    colorSecondButton(
                        btn_buy,
                        btn_rent,
                        btn_rent_room,
                        requireActivity()
                    )
                    rv_rents_sell.visibility = View.GONE
                    rv_rents_rent.visibility = View.VISIBLE
                    rv_rents_room.visibility = View.GONE
                    if (!isLastRentReached) {
                        viewModel.getRents {
                            if (it < LIMIT) {
                                isLastRentReached = true
                            }
                        }
                    }
                }
                2 -> {
                    colorThirdButton(
                        btn_buy,
                        btn_rent,
                        btn_rent_room,
                        requireActivity()
                    )
                    rv_rents_sell.visibility = View.GONE
                    rv_rents_rent.visibility = View.GONE
                    rv_rents_room.visibility = View.VISIBLE
                    if (!isLastRoomReached) {
                        viewModel.getRents {
                            if (it < LIMIT) {
                                isLastRoomReached = true
                            }
                        }
                    }
                }
            }
        }
    }// end of view created


    private fun getUserPhotoFromDisk(): Boolean {
        val fis: FileInputStream

        try {
            fis =
                requireActivity().openFileInput(Firebase.auth.currentUser!!.uid)
            val bitmap = BitmapFactory.decodeStream(fis)
            fis.close()
            iv_card_owner_photo.setImageBitmap(bitmap)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isWaitingData = false
    var isScrolling = false
    var isLastSellReached = false
    var isLastRentReached = false
    var isLastRoomReached = false
    var isLoading = false


    private val sellScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                val firstVisibleItemPosition =
                    linearLayoutManager!!.findFirstVisibleItemPosition()
                val visibleItemCount = linearLayoutManager.childCount
                val totalItemCount = linearLayoutManager.itemCount

                if (isScrolling
                    && firstVisibleItemPosition + visibleItemCount == totalItemCount
                    && !isLastSellReached && !isWaitingData
                ) {
                    isScrolling = false
                    isWaitingData = true
                    viewModel.getRents {
                        isWaitingData = false
                        if (it < LIMIT) {
                            isLastSellReached = true
                        }
                    }
                }
            }
        }
    private val rentScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                val firstVisibleItemPosition =
                    linearLayoutManager!!.findFirstVisibleItemPosition()
                val visibleItemCount = linearLayoutManager.childCount
                val totalItemCount = linearLayoutManager.itemCount

                if (isScrolling
                    && firstVisibleItemPosition + visibleItemCount == totalItemCount
                    && !isLastRentReached && !isWaitingData
                ) {
                    isScrolling = false
                    isWaitingData = true
                    viewModel.getRents {
                        isWaitingData = false
                        if (it < LIMIT) {
                            isLastRentReached = true
                        }
                    }
                }
            }
        }
    private val roomScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                val firstVisibleItemPosition =
                    linearLayoutManager!!.findFirstVisibleItemPosition()
                val visibleItemCount = linearLayoutManager.childCount
                val totalItemCount = linearLayoutManager.itemCount

                if (isScrolling
                    && firstVisibleItemPosition + visibleItemCount == totalItemCount
                    && !isLastRoomReached && !isWaitingData
                ) {
                    isScrolling = false
                    isWaitingData = true
                    viewModel.getRents {
                        isWaitingData = false
                        if (it < LIMIT) {
                            isLastRoomReached = true
                        }
                    }
                }
            }
        }


    private fun convertBmpToFile(image: Bitmap): File {
        val file = File(requireActivity().cacheDir, Firebase.auth.currentUser!!.uid)
        file.createNewFile()
        val bos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, bos)
        val bitMapData = bos.toByteArray()
        val fos = FileOutputStream(file)
        fos.write(bitMapData)
        fos.flush()
        fos.close()
        return file
    }

    private fun initRecyclers() {
        rv_rents_sell.apply {
            adapterSell = RecyclerAdapter(this@RootFragment)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterSell
            addOnScrollListener(sellScrollListener)
        }

        rv_rents_rent.apply {
            adapterRent = RecyclerAdapter(this@RootFragment)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterRent
            addOnScrollListener(rentScrollListener)
        }

        rv_rents_room.apply {
            adapterRoom = RecyclerAdapter(this@RootFragment)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterRoom
            addOnScrollListener(roomScrollListener)
        }
    }

    override fun onClick(position: Int, rent: Rent) {
        var intent = Intent(requireContext(), DetailsFragment::class.java)
        intent.putExtra("Rent", rent)
        startActivity(intent)
    }


}




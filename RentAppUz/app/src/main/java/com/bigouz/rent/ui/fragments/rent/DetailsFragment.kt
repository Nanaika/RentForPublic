package com.bigouz.rent.ui.fragments.rent

import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.bigouz.rent.domain.models.CollectionTypes
import com.bigouz.rent.domain.models.Rent
import com.bigouz.rent.domain.models.User
import com.bigouz.rent.ui.fragments.rent.viewPager.ImageViewPagerAdapter
import com.bigouz.rent.utils.GlideApp
import com.example.rent.R
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_details.*

class DetailsFragment : AppCompatActivity(R.layout.fragment_details), OnImageItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = intent.extras

        if (bundle != null) {
            var rent: Rent = bundle.getParcelable("Rent")!!
            with(rent) {
                tv_rent_address.text = "$city, $region"
                tv_number_rooms.text = rooms.toString()
                tv_number_floor.text = floor.toString()
                tv_number_area.text = area.toString()
                tv_info.text = info.toString()
                val formattedText =
                    resources.getString(R.string.price_holder_details, price.toString())
                tv_price.text = Html.fromHtml(formattedText)
                tv_owner_name.text = ownerName

                getUser(rent.ownerUid!!) {
                    GlideApp.with(this@DetailsFragment)
                        .load(it.photoUrl)
                        .error(R.drawable.ic_profile_icon)
                        .into(iv_owner_photo)
                }
            }

            val adapter = ImageViewPagerAdapter(
                this.applicationContext,
                rent.listImages,
                this@DetailsFragment
            )
            vp_details_images.offscreenPageLimit = 5
            vp_details_images.adapter = adapter

            TabLayoutMediator(tl_dots, vp_details_images) { tab, position ->
                //Some implementation
            }.attach()
        }

        btn_close.setOnClickListener {
            this@DetailsFragment.finish()
        }

    }// end of on view created

    override fun onClick(image: String) {

    }

    private fun getUser(uid: String, successFun: (User) -> Unit) {
        Firebase.firestore.collection(CollectionTypes.USERS.toString())
            .document(uid)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    successFun.invoke(it.result.toObject(User::class.java)!!)
                }
            }
    }
}
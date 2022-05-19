package com.bigouz.rent.ui.fragments.rent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.rent.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : DialogFragment() {

    private val viewModel: RentViewModel by activityViewModels()

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        if (viewModel.location.longitude != 0.0) {
            val destination = LatLng(viewModel.location.latitude, viewModel.location.longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 13f))
        } else {
            val destination = LatLng(41.31,69.24)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 13f))
        }



        googleMap.setOnCameraIdleListener {
            println("-------center -----     ${googleMap.cameraPosition.target.longitude}, ${googleMap.cameraPosition.target.latitude}")
            viewModel.location.latitude = googleMap.cameraPosition.target.latitude
            viewModel.location.longitude = googleMap.cameraPosition.target.longitude
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        btn_back.setOnClickListener {
            dismiss()
        }

        btn_save.setOnClickListener {
            dismiss()
        }


    }//end of on view created

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation;
    }

}
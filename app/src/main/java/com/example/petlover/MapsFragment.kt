package com.example.petlover

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapsFragment : Fragment(), OnMapReadyCallback{
    private lateinit var mMap: GoogleMap

    private var latitude: Double=0.toDouble()
    private var longitude: Double=0.toDouble()

    private lateinit var mLastLocation: Location
    private var mMarker: Marker?=null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    companion object {
        private val MY_PERMISSION_CODE: Int = 1000
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_maps2, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           if (checkLocationPermission()) {
               buildLocationRequest()
               buildLocationCallBack()

               fusedLocationProviderClient =
                   LocationServices.getFusedLocationProviderClient(activity!!.parent)
               fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,
                   Looper.myLooper()
               )
           }
        } else {
            buildLocationRequest()
            buildLocationCallBack()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!.parent)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,Looper.myLooper()
            )
        }
        return view
    }

    private fun checkLocationPermission (): Boolean {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), MY_PERMISSION_CODE)
            }
                return false
        } else {
            return false

        }
        return true
    }
    private fun buildLocationRequest () {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }
    private fun buildLocationCallBack () {
        locationCallback = object :LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                mLastLocation = p0!!.locations.get(p0.locations.size-1) //get last location

                if (mMarker != null) {
                    mMarker!!.remove()
                }
                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                val  latLng = LatLng(latitude,longitude)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("Your position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                mMarker = mMap.addMarker(markerOptions)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkLocationPermission()) {
                        buildLocationRequest()
                        view?.let { buildLocationCallBack() }

                        fusedLocationProviderClient =
                            LocationServices.getFusedLocationProviderClient(activity!!.parent)
                        fusedLocationProviderClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.myLooper()
                        )
                        mMap.isMyLocationEnabled = true
                    }
                } else {
                    Toast.makeText(view?.context, "Permission Denied", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        val btnGetLocation = view.findViewById<Button>(R.id.getLocation)
        btnGetLocation.setOnClickListener {
            val args = MapsFragmentArgs.fromBundle(arguments!!)
            var uid: String
            Toast.makeText(view.context,"${args.events} $latitude : $longitude", Toast.LENGTH_SHORT).show()
            uid = if (args.events == "ADD") {
                ""
            } else {
                args.uidPet
            }
            Navigation.findNavController(it).navigate(MapsFragmentDirections.actionMapsFragmentToAddFragment(args.events, uid ,latitude.toString(),longitude.toString()))
        }
//        val args = MapsFragmentArgs.fromBundle(arguments!!)
//        if (args.latitude !== null) {
//            val latlng = LatLng(args.latitude!!.toDouble(),args.longitude!!.toDouble())
//            val markerOptions = MarkerOptions()
//            markerOptions.position(latlng)
//            markerOptions.title("${args.latitude} : ${args.longitude}")
//            latitude = latlng.latitude
//            longitude = latlng.longitude
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))
//            mMap.addMarker(markerOptions)
//        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val mMap = googleMap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap!!.isMyLocationEnabled = true
            }
        } else {
            mMap!!.isMyLocationEnabled = true
        }
        mMap?.uiSettings!!.isZoomControlsEnabled = true
        mMap.setOnMapClickListener {
            val markerOptions = MarkerOptions()
            markerOptions.position(it)
            markerOptions.title("${it.latitude} : ${it.longitude}")
            latitude = it.latitude
            longitude = it.longitude
            mMap.clear()
            mMap.moveCamera(CameraUpdateFactory.newLatLng(it))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))
            mMap.addMarker(markerOptions)
        }
    }
}
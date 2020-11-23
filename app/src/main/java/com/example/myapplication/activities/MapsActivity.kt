package com.example.myapplication.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.mapapplicationkotlin.adapters.SectionsPagerAdapter
import com.example.mapapplicationkotlin.data.place.PlaceEntity
import com.example.mapapplicationkotlin.data.place.PlaceViewModel
import com.example.mapapplicationkotlin.data.road_diraction.RoadDiraction
import com.example.myapplication.R
import com.example.myapplication.fragments.MessageFormFragment
import com.example.myapplication.retrofit.Api
import com.example.myapplication.retrofit.ApiInterface
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MapsActivity() : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var REQUEST_CODE = 101
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    private var position: LatLng? = null
    private var current_Marker: Marker? = null

    private var add_icon: FloatingActionButton? = null
    private var check_icon: FloatingActionButton? = null

    private var move_marker = false
    private var un_updated = false

    private var transaction: FragmentTransaction? = null
    private var fragment: Fragment? = null

    private var drawer: DrawerLayout? = null
    private var toggle: ActionBarDrawerToggle? = null

    private var mPlaceViewModel: PlaceViewModel? = null

    private var loadingBar: ProgressDialog? = null

    private var list_roadDiractions: ArrayList<RoadDiraction>? = null
    private var nearest_Place: RoadDiraction? = null

    private var current_placeEntity: PlaceEntity? = null
    private var polylines: List<Polyline>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Get Current Position
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLastLocation()

        add_icon = findViewById(R.id.add_icon)
        check_icon = findViewById(R.id.check_icon)

        loadingBar = ProgressDialog(this)

        // Add the swipe menu
        drawer = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close)
        drawer?.addDrawerListener(toggle!!)
        toggle!!.syncState()

        // Add the fragments of menu Gorever and Adres Arar
        val sectionsPagerAdapter = SectionsPagerAdapter(
            this,
            supportFragmentManager
        )
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        // Get ViewMode of PlaceViewModel where used to insert new place
        mPlaceViewModel = ViewModelProvider(this).get(PlaceViewModel::class.java)

        // FloatingActionButton when want to add new place + button
        add_icon?.setOnClickListener(View.OnClickListener {
            add_icon?.visibility = View.GONE
            check_icon?.setVisibility(View.VISIBLE)

            Clear_All()

            position = mMap.cameraPosition.target
            current_Marker = mMap.addMarker(
                MarkerOptions().draggable(true).position(position!!).title(
                    "New Place"
                )
            )
        })

        // FloatingActionButton when want to completely add the new place
        check_icon?.setOnClickListener(View.OnClickListener {
            Open_PlaceFragment(null, null)
            un_updated = false
        })

        // If can't get current position, at that time will set the Ankara position
        if (position == null) {
            position = LatLng(39.9286, 32.8547)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Check the Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
            return
        }

        mMap.isMyLocationEnabled = true

        // Move the camera to position
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))

        // Move Maker Simultaneously with screen movement
        mMap.setOnCameraMoveListener {
            if (current_Marker != null && !move_marker) {
                current_Marker!!.position = mMap.cameraPosition.target
            }
        }

        // Process marker when clicking if clicked on marker or new marker or moving marker
        mMap.setOnMarkerClickListener { marker ->
            if (un_updated) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                move_marker = true
                if (current_Marker == null) {
                    current_Marker = marker
                } else {
                    if (current_Marker!!.id != marker.id) {
                        current_Marker!!.setIcon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN
                            )
                        )
                        current_Marker = marker
                    }
                }
            }
            false
        }

        // Process marker while dragging marker
        mMap.setOnMarkerDragListener(object : OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {
                if (current_Marker != null && current_Marker!!.id != marker.id) {
                    current_Marker!!.setIcon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN
                        )
                    )
                }
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            }

            override fun onMarkerDrag(marker: Marker) {}
            override fun onMarkerDragEnd(marker: Marker) {
                current_Marker = marker
            }
        })
    }

    // Using to get the diration beteen tow points use "Open route service"
    fun getRoadDirection() {
        loadingBar!!.setTitle("Hazırlınıyor")
        loadingBar!!.setMessage("Bekle Lüften... Rota Oluşuyor")
        loadingBar!!.setCanceledOnTouchOutside(false)
        loadingBar!!.show()

        list_roadDiractions = ArrayList<RoadDiraction>()
        nearest_Place = RoadDiraction(null, null, null)

        var _places: ArrayList<PlaceEntity> = (mPlaceViewModel?.getUserWithPlacesRoad(MainActivity.user_id)?.get(0)?.placesEntities as ArrayList<PlaceEntity>?)!!

        if (_places.size == 0) {
            loadingBar!!.dismiss()
            return
        }

        val pos1 = "" + position!!.longitude + "," + position!!.latitude

        for (i in _places.indices) {
            val pos2 = "" + _places[i].position2 + "," + _places[i].position1

            val request = Api.buildService(ApiInterface::class.java)
            val call: Call<JsonObject> = request.getRoadDuration(Api.Api_key(), pos1, pos2)

            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val jsonObject = response.body()
                        val features = jsonObject["features"] as JsonArray

                        var _distance =
                            (((((features[0] as JsonObject)["properties"] as JsonObject)["segments"] as JsonArray)[0] as JsonObject).getAsJsonPrimitive(
                                "distance"
                            ).toString().toFloat()) // ['features'][0]['geometry']['coordinates']
                        var _road_points =
                            ((features[0] as JsonObject)["geometry"] as JsonObject)["coordinates"] as JsonArray
                        val roadDiraction = RoadDiraction(
                            Road_placeEntity = _places[0],
                            distance = _distance,
                            road_Points = _road_points
                        )

                        _places.removeAt(0)

                        list_roadDiractions?.add(roadDiraction)
                        if (nearest_Place?.Road_placeEntity == null || nearest_Place?.distance!! > list_roadDiractions?.get(
                                list_roadDiractions?.size!! - 1
                            )?.distance!!
                        ) {
                            nearest_Place =
                                list_roadDiractions?.get(list_roadDiractions?.size!! - 1)
                        }
                    } else {
                        // Check url address
                        Toast.makeText(applicationContext, "Teknik Hatasi 121", Toast.LENGTH_SHORT)
                            .show()
                    }
                    loadingBar!!.dismiss()
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    // Check the coming data or ssl
                    Toast.makeText(applicationContext, "Road bulunamadı 102 ", Toast.LENGTH_LONG)
                        .show()
                    call.cancel()
                }
            })
        }
    }

    fun RoadDirection_Button(view: View){
        loadingBar!!.setTitle("Çiziyor")
        loadingBar!!.setMessage("Bekle Lüften... Rotayı çiziyor")
        loadingBar!!.setCanceledOnTouchOutside(false)
        loadingBar!!.show()

        if (polylines != null) {
            for (line in polylines!!) {
                line.remove()
            }
            polylines = null
            mMap.clear()
        }

        if (list_roadDiractions?.isEmpty()!!) {
            Toast.makeText(applicationContext, "No Address for route", Toast.LENGTH_LONG)
            loadingBar!!.dismiss()
            return
        }

        //polyline object
        polylines = null
        val polyOptions = PolylineOptions()
        var polylineStartLatLng: LatLng? = null
        var polylineEndLatLng: LatLng? = null

        polylines = ArrayList()

        //add route(s) to the map using polyline

        //add route(s) to the map using polyline
        for (i in 0 until nearest_Place?.road_Points?.size()!!) {
            polyOptions.color(resources.getColor(R.color.colorPrimary))
            polyOptions.width(7f)
            val json: JsonArray = nearest_Place?.road_Points?.get(i) as JsonArray
            val l = LatLng(json[1].asDouble, json[0].asDouble)
            polyOptions.addAll(setOf(l))
            val polyline = mMap.addPolyline(polyOptions)
            polylineStartLatLng = polyline.points[0]
            val k = polyline.points.size
            polylineEndLatLng = polyline.points[k - 1]
            (polylines as ArrayList<Polyline>).add(polyline)
        }

        //Add Marker on route starting position
        val startMarker = MarkerOptions()
        startMarker.position(polylineStartLatLng!!)
        startMarker.title("My Location")
        mMap.addMarker(startMarker)

        //Add Marker on route ending position
        val endMarker = MarkerOptions()
        endMarker.position(polylineEndLatLng!!)
        endMarker.title(nearest_Place?.Road_placeEntity?.place_name)
        mMap.addMarker(endMarker)

        Close_Drawer()

        loadingBar!!.dismiss()

        mMap.moveCamera(CameraUpdateFactory.newLatLng(position))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
    }

    // Get local Position
    fun fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
            return
        }

        val task: Task<Location> = fusedLocationProviderClient?.lastLocation!!
        task.addOnSuccessListener { location ->
            if (location != null) {
                position = LatLng(location.latitude, location.longitude)
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment?
                mapFragment!!.getMapAsync(this@MapsActivity)
                getRoadDirection()
            }
        }
    }

    // Open the place fragment to add new place or to show place's details
    fun Open_PlaceFragment(place_name: String?, place_description: String?){
        fragment = MessageFormFragment.newInstance(place_name, place_description)
        transaction = supportFragmentManager.beginTransaction()
        transaction!!.setCustomAnimations(
            R.anim.enter_from_left,
            R.anim.exit_to_right,
            R.anim.enter_from_right,
            R.anim.exit_to_left
        )
        transaction!!.replace(R.id.fragment_form, fragment!!)
        transaction!!.commit()

        if (current_Marker != null) {
            current_Marker!!.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        }
        move_marker = true

        Close_Drawer()
    }

    // Close menu navication where will check phone language
    fun Close_Drawer() {
        if (Locale.getDefault().language.contentEquals("ar")) {
            drawer!!.closeDrawer(Gravity.RIGHT)
        } else {
            drawer!!.closeDrawer(Gravity.LEFT)
        }
    }

    // Add new place when click on kaydet button in place fragment
    fun AddNewPlace_KaydetButton(view: View){
        var t1 = findViewById<EditText>(R.id.is_adi_edittext)
        var t2 = findViewById<EditText>(R.id.is_aciklama_edittext)

        var placeEntity: PlaceEntity = PlaceEntity(
            id = 0,
            user_id = MainActivity.user_id,
            place_name = t1.text.toString(),
            place_description = t2.text.toString(),
            position1 = current_Marker?.position?.latitude.toString(),
            position2 = current_Marker?.position?.longitude.toString()
        )

        mPlaceViewModel?.insert(placeEntity)

        CloseThePlaceFragment_IptalButton(view)

        drawer!!.openDrawer(GravityCompat.START)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLastLocation()
    }

    // Delete the place used with sil button
    fun DeletePlace_DeleteButton(view: View){
        mPlaceViewModel!!.delete(current_placeEntity!!)

        val s: RecyclerView = findViewById(R.id.place_recyclerview_list)
        s.removeAllViews()

        CloseThePlaceFragment_IptalButton(view)

        drawer!!.openDrawer(GravityCompat.START)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLastLocation()
    }

    // Close the Place Fragment when click on Iptal button
    fun CloseThePlaceFragment_IptalButton(view: View?) {
        add_icon!!.visibility = View.VISIBLE
        check_icon!!.visibility = View.GONE
        Clear_All()
    }

    private fun Clear_All() {
        if (current_Marker != null) {
            current_Marker!!.remove()
        }
        current_Marker = null
        move_marker = false
        if (polylines != null) {
            for (line in polylines!!) {
                line.remove()
            }
            polylines = null
            mMap.clear()
        }
        if (fragment != null && fragment!!.fragmentManager != null) {
            fragment?.fragmentManager?.beginTransaction()?.remove(fragment!!)?.commit()
        }
    }

    // When click on Detayi Goster button for show place detail and place position as marker in list places menu
    fun Detayi_ButtonOnClick(placeEntity: PlaceEntity) {
        Konum_ButtonOnClick(placeEntity)
        Open_PlaceFragment(placeEntity.place_name, placeEntity.place_description)
        current_placeEntity = placeEntity
    }

    // When click on Konum Goster button for place detail in list places menu
    fun Konum_ButtonOnClick(placeEntity: PlaceEntity) {
        Clear_All()
        position = LatLng(placeEntity.position1.toDouble(), placeEntity.position2.toDouble())
        current_Marker =
            mMap.addMarker(MarkerOptions().position(position!!).title(placeEntity.place_name))
        current_Marker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        move_marker = true
        un_updated = false
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
        if (Locale.getDefault().language.contentEquals("ar")) {
            drawer!!.closeDrawer(Gravity.RIGHT)
        } else {
            drawer!!.closeDrawer(Gravity.LEFT)
        }
    }

    // Close the keyboard
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}
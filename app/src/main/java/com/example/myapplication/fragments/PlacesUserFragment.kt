package com.example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapapplicationkotlin.adapters.PlaceItemAdapter
import com.example.mapapplicationkotlin.data.place.PlaceEntity
import com.example.mapapplicationkotlin.data.place.PlaceViewModel
import com.example.mapapplicationkotlin.data.relation_ships.UserWithPlaces
import com.example.myapplication.R
import com.example.myapplication.activities.MainActivity
import com.example.myapplication.activities.MapsActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlacesUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlacesUserFragment : Fragment() {

    private lateinit var mplaceViewModel: PlaceViewModel
    private lateinit var mplaceItemAdapter: PlaceItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mplaceItemAdapter = PlaceItemAdapter()
        mplaceViewModel = ViewModelProvider(this).get(PlaceViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_places_user, container, false)

        val mrelativeLayout: RecyclerView = view.findViewById(R.id.place_recyclerview_list)
        mrelativeLayout.adapter = mplaceItemAdapter
        mrelativeLayout.layoutManager = LinearLayoutManager(requireContext())

        val activity: MapsActivity = activity as MapsActivity

        // To get places of user and working as livedata
        mplaceViewModel.getUserWithPlaces(MainActivity.user_id).observe(
            viewLifecycleOwner,
            Observer { userWithPlaces: List<UserWithPlaces> ->
                mplaceItemAdapter.PlaceItemAdapter(userWithPlaces[0].placesEntities)
//                mplaceItemAdapter.notifyDataSetChanged()
            })


        // When add to any item in list where will show to "Detayi Goster and Konum Gster" buttons
        mplaceItemAdapter.setOnItemClickListener(object: PlaceItemAdapter.OnItemClickListener{
            override fun onItemClick(placeEntity: PlaceEntity, view: View) {
                val linearLayout = view.findViewById<LinearLayout>(R.id.linearlayout_detayikonum_buttons)
                val down_icon = view.findViewById<ImageView>(R.id.down_icon)
                val up_icon = view.findViewById<ImageView>(R.id.up_icon)
                val detayi_button = view.findViewById<Button>(R.id.detayi_button)
                val konum_button = view.findViewById<Button>(R.id.konum_button)

                linearLayout.visibility = if (linearLayout.visibility == View.GONE) View.VISIBLE else View.GONE
                down_icon.visibility = if (down_icon.visibility == View.GONE) View.VISIBLE else View.GONE
                up_icon.visibility = if (up_icon.visibility == View.GONE) View.VISIBLE else View.GONE

                detayi_button.setOnClickListener { activity.Detayi_ButtonOnClick(placeEntity!!) }
                konum_button.setOnClickListener { activity.Konum_ButtonOnClick(placeEntity!!) }
            }
        })
        return view
    }
}
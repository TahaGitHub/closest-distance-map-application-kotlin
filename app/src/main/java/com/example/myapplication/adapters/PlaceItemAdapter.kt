package com.example.mapapplicationkotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mapapplicationkotlin.data.place.PlaceEntity
import com.example.myapplication.R

class PlaceItemAdapter(): RecyclerView.Adapter<PlaceItemAdapter.ViewHolder>() {

    private var mValues = emptyList<PlaceEntity>()
    private lateinit var onItemClickListener: OnItemClickListener

    fun PlaceItemAdapter(items: List<PlaceEntity>) {
        this.mValues = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemview = LayoutInflater.from(parent.context)
                .inflate(R.layout.place_item, parent, false)
        return ViewHolder(itemview)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentPlace = mValues[position]
        holder.place_name_item.setText(currentPlace.place_name)

        holder.itemView.setOnClickListener(View.OnClickListener {view ->
            onItemClickListener.onItemClick( currentPlace, view = view )
            notifyDataSetChanged()
        })
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(itemview: View): RecyclerView.ViewHolder(itemview) {
        val place_name_item: TextView = itemview.findViewById(R.id.place_name_item)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(placeEntity: PlaceEntity, view: View)
    }
}
package com.example.gps.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gps.data.entities.Route
import com.example.gps.data.entities.RouteWithPoints
import com.example.gps.databinding.ItemRouteBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RouteAdapter(
    private val onItemClicked: (RouteWithPoints) -> Unit
) : ListAdapter<RouteWithPoints, RouteAdapter.RouteViewHolder>(RouteDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val binding = ItemRouteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RouteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RouteViewHolder(private val binding: ItemRouteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RouteWithPoints) {
            // 1. Izena jarri (Route objektutik)
            binding.tvRouteName.text = item.route.name

            // 2. Koordenatuak kudeatu (Points zerrendatik)
            if (item.points.isNotEmpty()) {
                // Azken puntua hartuko dugu (azken kokapena)
                val lastPoint = item.points.last()
                val lat = String.format("%.4f", lastPoint.latitude) // 4 hamartarrekin
                val lon = String.format("%.4f", lastPoint.longitude)

                binding.tvCoordinates.text = "Lat: $lat, Lon: $lon"
            } else {
                binding.tvCoordinates.text = "Punturik gabe"
            }

            // Klik egitean, Route objektua pasatzen dugu (lehen bezala)
            binding.root.setOnClickListener {
                onItemClicked(item)
            }
        }
    }

    object RouteDiffCallback : DiffUtil.ItemCallback<RouteWithPoints>() {
        override fun areItemsTheSame(oldItem: RouteWithPoints, newItem: RouteWithPoints): Boolean {
            return oldItem.route.routeId == newItem.route.routeId
        }

        override fun areContentsTheSame(oldItem: RouteWithPoints, newItem: RouteWithPoints): Boolean {
            // Datu klaseek (Data Classes) automatikoki konparatzen dituzte barruko eremu guztiak (puntuak barne)
            return oldItem == newItem
        }
    }
}
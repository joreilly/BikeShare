package com.surrus.bikeshare.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.surrus.bikeshare.R
import com.surrus.bikeshare.ext.inflate
import com.surrus.common.remote.Station
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.station_list_item.view.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var stationListAdapter: BikeStationListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup service list recycler view
        stationList.run {
            setHasFixedSize(true)
            stationListAdapter = BikeStationListAdapter()
            layoutManager = LinearLayoutManager(context)
            adapter = stationListAdapter
        }


        homeViewModel.stations.observe(viewLifecycleOwner, Observer {
            stationListAdapter.submitList(it)
        })

    }

    inner class BikeStationListAdapter : ListAdapter<Station, BikeStationListAdapter.ViewHolder>(TaskDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.station_list_item))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(station: Station) = with(itemView) {
                stationName.text = station.name
                emptySlots.text = station.emptySlots.toString()
                freeBikes.text = station.freeBikes.toString()
            }
        }
    }

    inner class TaskDiffCallback : DiffUtil.ItemCallback<Station>() {
        override fun areItemsTheSame(oldItem: Station, newItem: Station): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Station, newItem: Station): Boolean {
            return oldItem == newItem
        }
    }

}
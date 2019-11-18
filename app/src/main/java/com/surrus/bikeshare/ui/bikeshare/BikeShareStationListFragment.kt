package com.surrus.bikeshare.ui.bikeshare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.surrus.bikeshare.R
import com.surrus.bikeshare.ext.inflate
import com.surrus.common.remote.Station
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.station_list_item.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class BikeShareStationListFragment : Fragment() {

    private val bikeShareViewModel by sharedViewModel<BikeShareViewModel>()

    private lateinit var stationListAdapter: BikeStationListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

            val dividerItemDecoration = DividerItemDecoration(requireContext(),  LinearLayout.VERTICAL)
            addItemDecoration(dividerItemDecoration)
        }


        bikeShareViewModel.stations.observe(viewLifecycleOwner, Observer {
            stationListAdapter.submitList(it)
        })

    }

    inner class BikeStationListAdapter : ListAdapter<Station, BikeStationListAdapter.ViewHolder>(TaskDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.station_list_item))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(station: Station) = with(itemView) {
                stationName.text = station.name
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
package com.untitled.multimeter.experiments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.untitled.multimeter.MyData
import com.untitled.multimeter.databinding.FragmentExperimentsBinding
import java.text.DateFormatSymbols
import java.util.*


/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for the data type.
 */
class MyItemRecyclerViewAdapter(
    private var list: List<MyData> = emptyList<MyData>()
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentExperimentsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]

        //Format data into string
        var collaboratorString = ""
        for (currentCollaborator in currentItem.collaborators) {
            if (currentCollaborator != currentItem.collaborators.last() ) {
                collaboratorString = collaboratorString + currentCollaborator + ", "
            }
            else {
                collaboratorString += currentCollaborator
            }
        }

        var titleString = "Title: " + currentItem.title

        var dateString = ""
        val currentDate = currentItem.date
        val time = currentDate[Calendar.HOUR_OF_DAY].toString() +":"+ currentDate[Calendar.MINUTE] +":"+ currentDate[Calendar.SECOND]
        val month = DateFormatSymbols().months[currentDate.get(Calendar.MONTH)]
        var date = ""
        if (currentDate[Calendar.DATE] < 10) {
            date = "0"+currentDate[Calendar.DATE].toString()
        }
        else {
            date = currentDate[Calendar.DATE].toString()
        }
        val year = currentDate[Calendar.YEAR].toString()
        val fullDate = month +" "+ date +" "+ year
        dateString = "$dateString$fullDate, $time"

        //Put values into the textviews
        holder.titleView.text = titleString
        holder.timeView.text = time
        holder.monthView.text = month
        holder.dateView.text = date
        holder.yearView.text = year
        //Set up onClickListener to navigate to ExperimentEntry
        val data = Bundle()
        val dataValues = currentItem.dataValues
        data.putSerializable("values", dataValues)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ExperimentEntry::class.java)
            intent.putExtra("title", currentItem.title)
            intent.putExtra("collaborators", collaboratorString)
            intent.putExtra("dateTime", dateString)
            intent.putExtra("data", data)
            intent.putExtra("comment", currentItem.comment)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(binding: FragmentExperimentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val titleView: TextView = binding.itemTitle
        val timeView: TextView = binding.itemTime
        val dateView: TextView = binding.itemDate
        val monthView: TextView = binding.itemMonth
        val yearView: TextView = binding.itemYear

        override fun toString(): String {
            return super.toString()
        }
    }

}
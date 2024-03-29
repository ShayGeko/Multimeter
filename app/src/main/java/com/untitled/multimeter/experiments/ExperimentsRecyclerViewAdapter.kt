package com.untitled.multimeter.experiments

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.untitled.multimeter.data.model.Experiment
import com.untitled.multimeter.data.model.ExperimentModel
import com.untitled.multimeter.databinding.FragmentExperimentsBinding
import com.untitled.multimeter.experimentdetails.ExperimentDetailsActivity
import com.untitled.multimeter.invitations.InvitationsViewModel
import java.text.DateFormatSymbols
import java.util.*


/**
 * Adapter for the list of [Experiment]
 */
class ExperimentsRecyclerViewAdapter(
    private var list: List<ExperimentModel> = emptyList()
) : RecyclerView.Adapter<ExperimentsRecyclerViewAdapter.ViewHolder>() {
    private lateinit var viewModel: ExperimentViewModel

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

        //Put values into the textviews
        holder.titleView.text = titleString
        holder.timeView.text = time
        holder.monthView.text = month
        holder.dateView.text = date
        holder.yearView.text = year

        //Set up onClickListener to navigate to ExperimentEntry
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ExperimentDetailsActivity::class.java)
            intent.putExtra("id",currentItem.id.toString())
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
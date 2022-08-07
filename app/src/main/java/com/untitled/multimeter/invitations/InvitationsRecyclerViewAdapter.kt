package com.untitled.multimeter.invitations

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.untitled.multimeter.data.model.CollaborationInvite
import com.untitled.multimeter.experimentdetails.ExperimentDetailsActivity
import com.untitled.multimeter.databinding.FragmentInvitationsBinding
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.types.RealmList
import java.text.DateFormatSymbols
import java.util.*

class InvitationsRecyclerViewAdapter(
    private val viewModel: InvitationsViewModel,
    private var list: RealmList<CollaborationInvite> = realmListOf()
) : RecyclerView.Adapter<InvitationsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentInvitationsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]

        //Get buttons from current item
        val acceptButton = holder.acceptButton
        val declineButton = holder.declineButton

        val experiment = currentItem.experiment ?: return
        //Format data into string
        var collaboratorString = ""
        for (currentCollaborator in experiment.collaborators) {
            if (currentCollaborator != experiment.collaborators.last() ) {
                collaboratorString = collaboratorString + currentCollaborator + ", "
            }
            else {
                collaboratorString += currentCollaborator
            }
        }

        //get experiment details from current item
        var titleString = experiment.title

        //Format date

        val foundDate = Date(experiment.date.epochSeconds * 1000)
        val currentDate: Calendar = Calendar.getInstance()
        currentDate.time = foundDate

        var dateString = "Created: "
        var seconds: String = currentDate[Calendar.SECOND].toString()
        if (currentDate[Calendar.SECOND] < 10) {
            seconds = "0"+currentDate[Calendar.SECOND].toString()
        }
        val time = currentDate[Calendar.HOUR_OF_DAY].toString() +":"+ currentDate[Calendar.MINUTE] +":"+ seconds
        val date = currentDate.get(Calendar.DATE).toString()
        val month = DateFormatSymbols().months[currentDate.get(Calendar.MONTH)]
        val year = currentDate[Calendar.YEAR].toString()
        val fullDate = "$month $date $year"
        dateString += "$fullDate, $time"

        //Put values into the textviews
        holder.titleView.text = titleString
        holder.dateView.text = dateString

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ExperimentDetailsActivity::class.java)
            intent.putExtra("id", experiment._id.toString())
            holder.itemView.context.startActivity(intent)
        }

        //On accept button clicked, add experiment to the current users list of participating experiments,
        // then remove invitation from the users invatation list in the database
        acceptButton.setOnClickListener {
            acceptInvitation(currentItem)
        }

        //On decline button clicked, remove invitation from the users invitation list in the database
        declineButton.setOnClickListener {
            declineInvitation(currentItem)
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(binding: FragmentInvitationsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val titleView: TextView = binding.itemTitle
        val dateView: TextView = binding.itemDate
        val acceptButton: Button = binding.buttonAccept
        val declineButton: Button = binding.buttonDecline

        override fun toString(): String {
            return super.toString()
        }
    }

    fun updateData(updates: UpdatedResults<CollaborationInvite>){
        notifyItemChanged(0)
    }

    fun setInitialData(list : RealmList<CollaborationInvite>){
        this.list = list
        notifyDataSetChanged()
    }
    //TODO
    private fun acceptInvitation(invite: CollaborationInvite) {
        viewModel.accept(invite)
    }

    //TODO
    private fun declineInvitation(invite: CollaborationInvite) {
        viewModel.decline(invite)
    }
}
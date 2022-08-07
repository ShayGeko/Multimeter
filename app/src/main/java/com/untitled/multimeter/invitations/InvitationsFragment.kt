package com.untitled.multimeter.invitations

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.MultimeterApp.Companion.APPLICATION_TAG
import com.untitled.multimeter.data.model.Experiment
import com.untitled.multimeter.R
import com.untitled.multimeter.RealmViewModelFactory
import com.untitled.multimeter.data.model.ExperimentModel
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedResults

/**
 * A fragment representing a list of Items.
 */
class InvitationsFragment : Fragment() {
    private lateinit var viewModel: InvitationsViewModel

    private lateinit var invitationsAdapter : InvitationsRecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var noDataTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get the application user
        val user = MultimeterApp.realmApp.currentUser

        //get viewmodel
        val viewModelFactory = RealmViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(InvitationsViewModel::class.java)


        viewModel.changesToUsersInvitations().observe(this) {
            //On success, store the invitations into invitations list
            when(it){
                is InitialResults -> {
                    if(it.list.isEmpty()){
                        recyclerView.visibility = View.GONE
                        noDataTextView.visibility = View.VISIBLE
                    }
                    else {
                        recyclerView.visibility = View.VISIBLE
                        noDataTextView.visibility = View.GONE
                    }
                    invitationsAdapter.setInitialData(it.list.toRealmList())
                }
                is UpdatedResults -> {
                    Log.d(APPLICATION_TAG, "invitations collection update noticed")
                    Log.d(APPLICATION_TAG, it.toString())
                    if(it.list.isEmpty()){
                        recyclerView.visibility = View.GONE;
                        noDataTextView.visibility = View.VISIBLE;
                    }
                    else {
                        recyclerView.visibility = View.VISIBLE;
                        noDataTextView.visibility = View.GONE;
                    }
                    invitationsAdapter.updateData(it)
                }
            }
        }

        invitationsAdapter = InvitationsRecyclerViewAdapter(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_invitations_list_holder, container, false)

        recyclerView = view.findViewById<RecyclerView>(R.id.list)
        noDataTextView = view.findViewById(R.id.no_data_textview)
        // Set the adapter
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = invitationsAdapter
        }

        return view
    }
}
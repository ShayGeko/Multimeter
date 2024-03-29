package com.untitled.multimeter.experiments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.untitled.multimeter.R
import com.untitled.multimeter.RealmViewModelFactory
import com.untitled.multimeter.data.model.ExperimentModel
import java.util.*


/**
 * Fragment to hold the recycler view for the Experiment Fragment
 * Needed to add a floating action button on top of the recycler view
 */
class ExperimentListHolderFragment : Fragment() {
    private var dataList = ArrayList<ExperimentModel>()
    private lateinit var viewModel: ExperimentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialize Viewmodel
        val viewModelFactory = RealmViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ExperimentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_experiment_list_holder, container, false)
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = ExperimentsRecyclerViewAdapter(dataList)

                //get experiments, on success update RecyclerView , on failure do nothing
                updateRecyclerView()

            }
        }
        return view
    }

    override fun onResume() {
        //get experiments, on success override adapter, on failure do nothing
        updateRecyclerView()
        super.onResume()
    }

    //get experiments, on success update RecyclerView, on failure do nothing
    private fun updateRecyclerView() {
        viewModel.getAllExperimentsForUser().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                dataList = result.getOrDefault(ArrayList<ExperimentModel>())
                if (this.view != null) {
                    val experimentsRecyclerView = this.requireView().findViewById<RecyclerView>(R.id.list)
                    experimentsRecyclerView.adapter = ExperimentsRecyclerViewAdapter(dataList)
                }
                else {
                    Log.e("ExperimentListHolderFragment","view is null")
                }
            }
            result.onFailure { error ->
                Log.e("ExperimentListHolderFragment", "Error in getAllExperimentsForUser() : $error")
            }
        }
    }
}
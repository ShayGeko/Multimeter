package com.untitled.multimeter.experiments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.untitled.multimeter.data.model.Experiment
import com.untitled.multimeter.R
import com.untitled.multimeter.createexperiment.CreateExperimentActivity
import com.untitled.multimeter.experimentdetails.ExperimentDetailsActivity
import kotlin.collections.ArrayList

/**
 * A fragment representing a list of Items.
 */
class ExperimentsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_experiments_list, container, false)

        //Launch Create Experiment Activity On FloatingButtonClick
        val createExperimentButton = view.findViewById<FloatingActionButton>(R.id.create_experiment_button)
        createExperimentButton.setOnClickListener  {
            val intent = Intent(this.context, CreateExperimentActivity::class.java)
            startActivity(intent)
        }

        return view
    }

}
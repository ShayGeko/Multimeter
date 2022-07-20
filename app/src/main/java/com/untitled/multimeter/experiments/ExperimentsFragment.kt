package com.untitled.multimeter.experiments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.untitled.multimeter.data.model.Experiment
import com.untitled.multimeter.R
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

        return view
    }

}
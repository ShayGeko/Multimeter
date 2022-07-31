package com.untitled.multimeter.experiments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.jjoe64.graphview.series.DataPoint
import com.untitled.multimeter.data.model.Experiment
import com.untitled.multimeter.R
import com.untitled.multimeter.UserViewModelFactory
import com.untitled.multimeter.createaccount.CreateAccountViewModel
import io.realm.kotlin.types.ObjectId
import java.util.*
import kotlin.collections.ArrayList

/**
 * Fragment to hold the recycler view for the Experiment Fragment
 * Needed to add a floating action button on top of the recycler view
 */
class ExperimentListHolderFragment : Fragment() {
    private var dataList = ArrayList<Experiment>()
    private lateinit var viewModel: ExperimentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelFactory = UserViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ExperimentViewModel::class.java)
        var x: ObjectId = ObjectId.create()
        viewModel.getExperiment(x)

        addMockData()
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
            }
        }
        return view
    }


    // TW: lots of mock data below
    private fun addMockData(){
        //Fake data
        val mockCommentString = mockComment
        val fakeDataOne = mockExperimentData1
        val fakeDataTwo = mockExperimentData2
        val fakeDataThree = mockExperimentData3

        dataList.add(Experiment("Experiment Number One", Calendar.getInstance(),"Needs more data", arrayListOf("Whiliam Smith","Micheal Smith", "Timmy Jones"), arrayListOf(fakeDataOne)))
        dataList.add(Experiment("Experiment Number two", Calendar.getInstance(),mockCommentString, arrayListOf("jack","joe"), arrayListOf(fakeDataOne, fakeDataTwo)))
        dataList.add(Experiment("Test three", Calendar.getInstance(),"commentThree", arrayListOf("saint","joe"), arrayListOf(fakeDataThree)))
        dataList.add(Experiment("Experiment Number four", Calendar.getInstance(),"comment4", arrayListOf("bob","tim"), arrayListOf(fakeDataTwo)))
        dataList.add(Experiment("Experiment Number five", Calendar.getInstance(),"commentFive", arrayListOf("smith","joe"), arrayListOf(fakeDataTwo)))
        dataList.add(Experiment("Test 6", Calendar.getInstance(),"commentFive", arrayListOf("smith","joe"), arrayListOf(fakeDataTwo)))
        dataList.add(Experiment("Test 7", Calendar.getInstance(),"commentFive", arrayListOf("smith","joe"), arrayListOf(fakeDataTwo)))
        dataList.add(Experiment("Test 8", Calendar.getInstance(),"commentFive", arrayListOf("smith","joe"), arrayListOf(fakeDataTwo)))
        dataList.add(Experiment("Test 9", Calendar.getInstance(),"commentFive", arrayListOf("smith","joe"), arrayListOf(fakeDataTwo)))
        dataList.add(Experiment("Test 10", Calendar.getInstance(),"commentFive", arrayListOf("smith","joe"), arrayListOf(fakeDataTwo)))
        dataList.add(Experiment("Test 11", Calendar.getInstance(),"commentFive", arrayListOf("smith","joe"), arrayListOf(fakeDataTwo)))
        dataList.add(Experiment("Test 12", Calendar.getInstance(),"commentFive", arrayListOf("smith","joe"), arrayListOf(fakeDataTwo)))
        dataList.add(Experiment("Test 13", Calendar.getInstance(),"commentFive", arrayListOf("smith","joe"), arrayListOf(fakeDataTwo)))
        //Fake data
    }

    private val mockExperimentData1 = arrayListOf(
        DataPoint(1.0,6.2),
        DataPoint(2.0,4.3),
        DataPoint(3.0,5.0),
        DataPoint(4.0,6.2),
        DataPoint(5.0,10.0),
        DataPoint(6.0,1.4),
        DataPoint(7.0,4.5),
        DataPoint(8.0,3.3),
        DataPoint(9.0, 12.0),
        DataPoint(10.0, 5.4),
        DataPoint(11.0,4.3),
        DataPoint(12.0,7.3),
        DataPoint(13.0,5.0),
        DataPoint(14.0,6.2),
        DataPoint(15.0,17.0),
        DataPoint(16.0,1.4),
        DataPoint(17.0,4.5),
        DataPoint(18.0,3.3),
        DataPoint(19.0, 7.0),
        DataPoint(20.0, 5.4)
    )
    private val mockExperimentData2 = arrayListOf(
        DataPoint(1.0,3.2),
        DataPoint(2.0,5.3),
        DataPoint(3.0,2.0),
        DataPoint(4.0,8.2),
        DataPoint(5.0,2.0),
        DataPoint(6.0,2.4),
        DataPoint(7.0,8.5),
        DataPoint(8.0,5.3),
        DataPoint(9.0, 0.0),
        DataPoint(10.0, 5.9),
        DataPoint(11.0,4.2),
        DataPoint(12.0,9.8),
        DataPoint(13.0,8.9),
        DataPoint(14.0,6.1),
        DataPoint(15.0,6.9),
        DataPoint(16.0,1.3),
        DataPoint(17.0,4.4),
        DataPoint(18.0,3.2),
        DataPoint(19.0, 9.0),
        DataPoint(20.0, 5.3)
    )
    private val mockExperimentData3 = arrayListOf(
        DataPoint(1.0,6.2),
        DataPoint(2.0,4.3),
        DataPoint(3.0,5.0),
        DataPoint(4.0,6.2),
        DataPoint(5.0,64.0),
        DataPoint(6.0,1.4),
        DataPoint(7.0,4.5),
        DataPoint(8.0,3.3),
        DataPoint(9.0, 90.0),
        DataPoint(10.0, 5.4),
        DataPoint(11.0,4.3),
        DataPoint(12.0,7.3),
        DataPoint(13.0,5.0),
        DataPoint(14.0,6.2),
        DataPoint(15.0,64.0),
        DataPoint(16.0,1.4),
        DataPoint(17.0,4.5),
        DataPoint(18.0,3.3),
        DataPoint(19.0, 90.0),
        DataPoint(20.0, 5.4)
    )
    private val mockComment =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Donec lobortis elit non aliquam imperdiet. Fusce pulvinar lectus ligula, " +
                "non commodo diam fermentum sagittis. Proin at augue in metus eleifend feugiat " +
                "nec at odio. In tincidunt iaculis luctus. Sed a sollicitudin."
}
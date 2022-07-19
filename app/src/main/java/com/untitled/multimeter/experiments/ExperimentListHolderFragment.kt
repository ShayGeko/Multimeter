package com.untitled.multimeter.experiments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jjoe64.graphview.series.DataPoint
import com.untitled.multimeter.MyData
import com.untitled.multimeter.R
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [ExperimentListHolderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExperimentListHolderFragment : Fragment() {

    private var columnCount = 1

    //Fake data
    private var dataList = ArrayList<MyData>()
    //Fake data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Fake data
        val copypastaString = "I have been programming (not coding, how awfully, you kids these days, say) powerful DOS programs in assembly since 1983 and I dont know any of your hipster node.js bullshit that makes you look cool. Seriously, get that shit outta here. Youre just making yourself look like clowns typing some incomprehensible sugarified as fuck bullshit, half of which, in fact, is written by your brand new and shiny Electron-based text editor you enjoy every second of, and the other half of which, is copied from fucking StackOverflow. Look, we didnt even fucking have that and we did shit, and you suckers cant even spend a single second without it. You spend hours looking for a gorgeous and flawless color scheme for it so your so-called coding is always a pleasure for you. We didnt even fucking have IntelliSense or linters, you fucking spoiled little brats. Stop making programming look like a joke and go back to your parents basements, where you are truly who you are."
        val fakeDataOne = arrayListOf(
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
        val fakeDataTwo = arrayListOf(
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
        val fakeDataThree = arrayListOf(
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
        dataList.add(MyData("Experiment Number One", Calendar.getInstance(),"Needs more data", arrayListOf("Whiliam Smith","Micheal Smith", "Timmy Jones"), arrayListOf(fakeDataOne)))
        dataList.add(MyData("Experiment Number two", Calendar.getInstance(),copypastaString, arrayListOf("jack","joe"), arrayListOf(fakeDataOne, fakeDataTwo)))
        dataList.add(MyData("Test three", Calendar.getInstance(),"commentThree", arrayListOf("saint","joe"), arrayListOf(fakeDataThree)))
        dataList.add(MyData("Experiment Number four", Calendar.getInstance(),"comment4", arrayListOf("bob","tim"), arrayListOf(fakeDataTwo)))
        dataList.add(MyData("Experiment Number five", Calendar.getInstance(),"commentFive", arrayListOf("smith","joe"), arrayListOf(fakeDataTwo)))
        dataList.add(MyData("Test 6", Calendar.getInstance(),"commentFive", arrayListOf("smith","joe"), arrayListOf(fakeDataTwo)))
        dataList.add(MyData("Test 7", Calendar.getInstance(),"commentFive", arrayListOf("smith","joe"), arrayListOf(fakeDataTwo)))
        dataList.add(MyData("Test 8", Calendar.getInstance(),"commentFive", arrayListOf("smith","joe"), arrayListOf(fakeDataTwo)))
        dataList.add(MyData("Test 9", Calendar.getInstance(),"commentFive", arrayListOf("smith","joe"), arrayListOf(fakeDataTwo)))
        dataList.add(MyData("Test 10", Calendar.getInstance(),"commentFive", arrayListOf("smith","joe"), arrayListOf(fakeDataTwo)))
        dataList.add(MyData("Test 11", Calendar.getInstance(),"commentFive", arrayListOf("smith","joe"), arrayListOf(fakeDataTwo)))
        dataList.add(MyData("Test 12", Calendar.getInstance(),"commentFive", arrayListOf("smith","joe"), arrayListOf(fakeDataTwo)))
        dataList.add(MyData("Test 13", Calendar.getInstance(),"commentFive", arrayListOf("smith","joe"), arrayListOf(fakeDataTwo)))
        //Fake data

        arguments?.let {
            columnCount = it.getInt(Experiments.ARG_COLUMN_COUNT)
        }
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
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyItemRecyclerViewAdapter(dataList)
            }
        }
        return view
    }

}
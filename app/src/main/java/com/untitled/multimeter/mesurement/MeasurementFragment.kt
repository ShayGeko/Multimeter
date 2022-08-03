package com.untitled.multimeter.mesurement

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.untitled.multimeter.R
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.data.model.*
import com.untitled.multimeter.experiments.ExperimentsRecyclerViewAdapter
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmList
import java.util.*


/**
 * Measurements display. Currently the input data is random
 */
class MeasurementFragment : Fragment() {
    private var x_values:Float = 0.0F
    lateinit var lineGraphView: GraphView
    private lateinit var values:ArrayList<DataPoint>
    private lateinit var viewModel: MeasurementViewModel

    private lateinit var collectBtn : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val t = inflater.inflate(R.layout.measure, container, false)

        // get the view model
        viewModel = ViewModelProvider(requireActivity()).get(MeasurementViewModel::class.java)

        // mock the connection to the hardware
        viewModel.mockConnection()


        val voltageTextView = t.findViewById<TextView>(R.id.voltage_value)
        // display the text with new data whenever it is received
        viewModel.measurementInput.observe(requireActivity()) {
            voltage -> voltageTextView.text = "${voltage} V";
        }

        // sett up the graph and add mock data
        setUpLineGraph(t)
        mockLineGraphData()


        // checks for button click and changes color and text
        collectBtn = t.findViewById(R.id.collect_button)
        collectBtn.setOnClickListener {
            // keep the state in viewModel so that state is persevered on rotations
            viewModel.changeCollectingStatus()
        }
        // change the button based on the collecting state
        viewModel.collectionStatus.observe(requireActivity()){ isCollecting ->
            if(isCollecting){
                collectBtn.text = "STOP collecting"
                collectBtn.setBackgroundColor(Color.RED)
            }
            else{
                collectBtn.text = "START collecting"
                collectBtn.setBackgroundColor(Color.GREEN )
            }
        }

        //Button Adds Dummy Data To A Preset Experiment
        //There should be a screen or something to pick a specific Experiment which would be used as input in this function
        //For now the experiment to add to is hardcoded for testing

        val addMeasurementButton = t.findViewById<Button>(R.id.dummy_measurement_button)
        addMeasurementButton.setOnClickListener {
            val newMeasurement = Measurement().apply {
                this._id = ObjectId.create()
                this.user = ObjectId.from(MultimeterApp.realmApp.currentUser!!.identity)
                this.dataPoints = realmListOf()
            }
            //viewModel.addMeasurementToExperiment(newMeasurement, )
        }


        return t
    }

    /**
     * finds the lineGraph view and configures it
     */
    private fun setUpLineGraph(view: View){
        // draws a line graph
        lineGraphView = view.findViewById(R.id.voltage_graph)
        lineGraphView.viewport.isScrollable = true
        lineGraphView.viewport.isScalable = true
        lineGraphView.viewport.setScalableY(true)
        lineGraphView.viewport.setScrollableY(true)
    }

    /**
     * Adds mock up data to the [lineGraphView]
     */
    private fun mockLineGraphData(){
        val series: LineGraphSeries<DataPoint> = LineGraphSeries(arrayOf(
            // on below line we are adding
            // each point on our x and y axis.
            DataPoint(0.0, 1.0),
            DataPoint(0.5, 2.0),
            DataPoint(1.0, 1.0),
            DataPoint(1.5, 3.0),
            DataPoint(2.0, 0.5),
            DataPoint(2.5, 0.8),
            DataPoint(3.0, 2.0),
            DataPoint(3.5, 1.0),
            DataPoint(4.0, 2.0)
        ))
        lineGraphView.animate()
        lineGraphView.addSeries(series)
    }

    /**
     * Created mock data for testing adding a measurement to an experiment
     */
    private fun measurementsDummyData(): RealmList<Measurement> {
        //TODO: delete this
        var result: RealmList<Measurement> = realmListOf()
        var dummyMeasurementDataPoint = realmListOf<MeasurementDataPoint>()
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 1.0; this.y = 6.2 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 2.0; this.y = 4.3 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 3.0; this.y = 5.0 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 4.0; this.y = 6.2 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 5.0; this.y = 10.0 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 6.0; this.y = 3.0 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 7.0; this.y = 9.4 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 8.0; this.y = 3.6 })
        var dummyMeasurement1 = Measurement().apply {
            this.user = ObjectId.from(MultimeterApp.realmApp.currentUser!!.identity)
            this.dataPoints = dummyMeasurementDataPoint
        }
        dummyMeasurementDataPoint = realmListOf<MeasurementDataPoint>()
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 6.0; this.y = 4.2 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 2.0; this.y = 5.3 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 4.0; this.y = 1.2 })
        dummyMeasurementDataPoint.add(MeasurementDataPoint().apply { this.x = 5.0; this.y = 4.0 })
        var dummyMeasurement2 = Measurement().apply {
            this.user = ObjectId.from(MultimeterApp.realmApp.currentUser!!.identity)
            this.dataPoints = dummyMeasurementDataPoint
        }
        result.add(dummyMeasurement1)
        result.add(dummyMeasurement2)
        return result
    }
}
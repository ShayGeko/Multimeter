package com.untitled.multimeter.mesurement

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.untitled.multimeter.R
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.untitled.multimeter.MainFragment
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.RealmViewModelFactory
import com.untitled.multimeter.connection.ConnectionFragment
import com.untitled.multimeter.createexperiment.CreateExperimentViewModel
import com.untitled.multimeter.data.model.*
import com.untitled.multimeter.data.source.realm.RealmObjectNotFoundException
import com.untitled.multimeter.experiments.ExperimentsRecyclerViewAdapter
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmList
import java.util.*
import kotlin.collections.ArrayList


/**
 * Measurements display. Currently the input data is random
 */
class MeasurementFragment : Fragment() {
    private var x_values:Float = 0.0F
    lateinit var lineGraphView: GraphView
    private lateinit var values:ArrayList<DataPoint>
    private lateinit var viewModel: MeasurementViewModel
    val Max_Datapoints = 1000000000
    private lateinit var collectBtn : Button

    //List of user experiments
    private var dataList = ArrayList<ExperimentModel>()
    val series: LineGraphSeries<DataPoint> = LineGraphSeries(arrayOf())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val t = inflater.inflate(R.layout.measure, container, false)

        // get the view model
        val viewModelFactory = RealmViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MeasurementViewModel::class.java)

        // mock the connection to the hardware
        viewModel.realConnection()


        val voltageTextView = t.findViewById<TextView>(R.id.voltage_value)
        // display the text with new data whenever it is received
        viewModel.measurementInput.observe(requireActivity()) { datapoint ->
            voltageTextView.text = "${datapoint.y} V";
            if(viewModel.isCollecting){
                series.appendData(datapoint,true,Max_Datapoints)
            }
        }
        viewModel.connection_stat.observe(requireActivity()){
            if(it == false){
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.fragment_container, ConnectionFragment())
                transaction?.commit()
                MainFragment.state = MainFragment.CONNECT
            }
        }


        // sett up the graph and add mock data
        setUpLineGraph(t)
        mockLineGraphData()


        // checks for button click and changes color and text
        collectBtn = t.findViewById(R.id.collect_button)
        val addMeasurementButton = t.findViewById<Button>(R.id.dummy_measurement_button)
        collectBtn.setOnClickListener {
            // keep the state in viewModel so that state is persevered on rotations
            viewModel.changeCollectingStatus()
        }
        // change the button based on the collecting state
        viewModel.collectionStatus.observe(requireActivity()){ isCollecting ->
            if(isCollecting){
                addMeasurementButton.setBackgroundColor(Color.GRAY)
                addMeasurementButton.isClickable = false
                addMeasurementButton.isEnabled = false
                collectBtn.text = "STOP collecting"
                collectBtn.setBackgroundColor(Color.RED)
            }
            else{
                addMeasurementButton.setBackgroundColor(Color.BLUE)
                addMeasurementButton.isClickable = true
                addMeasurementButton.isEnabled = true
                collectBtn.text = "START collecting"
                collectBtn.setBackgroundColor(Color.GREEN )
            }
        }

        //Button Adds Measurement To An Experiment
        //When measurement works store Measurements in collectedData as a ArrayList<DataPoints>
        //For now I have a private function to Create dummy data, delete later, put ur data in collectedData
        //This is the only thing you have to change, the AlertDialog stuff is done
        val collectedData = measurementDummyData()

        //When the Add Measurement button is clicked, opens an alertDialog to choose an experiment to add to
        addMeasurementButton.setOnClickListener {
            openAlertDialog(viewModel.arraylist)
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
        lineGraphView.onDataChanged(true,true)
    }

    /**
     * Adds mock up data to the [lineGraphView]
     */


    private fun mockLineGraphData(){//array:ArrayList<DataPoint>
        lineGraphView.animate()
        lineGraphView.addSeries(series)
    }

    /**
     * Creates the AlertDialog for choosing an experiment
     */
    private fun openAlertDialog(collectedData: ArrayList<DataPoint>) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this.context)

        // set title
        builder.setTitle("Select Experiment")

        // Get all the experiments for the user
        viewModel.getAllExperimentsForUser().observe(viewLifecycleOwner) { result ->
            //On Success, Build and Show the AlertDialog
            result.onSuccess {
                var choice = -1

                //dataList now has the users experiments
                dataList = result.getOrDefault(ArrayList<ExperimentModel>())

                //If user has no experiments
                if(dataList.isEmpty()) {
                    Toast.makeText(this.activity?.applicationContext,"User Has No Experiments",Toast.LENGTH_LONG).show()
                    return@observe
                }

                //get the titles for the experiments (this is what we display in he alertDialog)
                val titles = ArrayList<String>()
                for (experiment in dataList) {
                    titles.add(experiment.title)
                }
                Log.e("titles",titles.toString())

                // set choices
                builder.setSingleChoiceItems(titles.toTypedArray(), -1) { dialog, chosenVal ->
                    Log.e("Choice",chosenVal.toString())
                    Log.e("Dialog",dialog.toString())
                    choice = chosenVal
                }

                //Set Positive button
                builder.setPositiveButton("OK") { dialog, whichButton ->

                    //If the user made a choice
                    if (choice >= 0) {

                        //Get chosen experiment
                        val chosenExperiment = dataList[choice]
                        Log.e("chosenId-Title",chosenExperiment.title)
                        val chosenId = chosenExperiment.id
                        viewModel.getExperiment(chosenId).observe(viewLifecycleOwner) { result ->

                            //On get experiment success attempt to add measurement to experiment
                            result.onSuccess {
                                val experiment: Experiment =
                                    result.getOrElse { throw RealmObjectNotFoundException("Experiment not found!") }
                                viewModel.addMeasurementToExperiment(collectedData, experiment._id).observe(viewLifecycleOwner) { result ->

                                    //On add measurement success, make toast
                                    result.onSuccess {
                                        Toast.makeText(this.activity?.applicationContext,"Measurement Successfully Added",Toast.LENGTH_LONG).show()
                                        Log.e("MeasurementFragment","Measurement Successfully Added")
                                    }

                                    //On add measurement Failure, make toast, throw error
                                    result.onFailure { error->
                                        Toast.makeText(this.activity?.applicationContext,"Measurement Adding Failed",Toast.LENGTH_LONG).show()
                                        Log.e("MeasurementFragment","Measurement Adding Failed")
                                        throw error
                                    }
                                }
                            }
                            result.onFailure { error ->
                                Toast.makeText(this.activity?.applicationContext,"Get Experiment Failed",Toast.LENGTH_LONG).show()
                                Log.e("MeasurementFragment","Get Experiment Failed")
                                throw error
                            }
                        }
                    dialog.dismiss()
                    }
                }

                //Set Negative button
                builder.setNegativeButton("Cancel") {dialog, _ ->
                    dialog.dismiss()
                }

                builder.show()

            }
            //On Failure throw error
            result.onFailure { error ->
                Toast.makeText(this.activity?.applicationContext,"Get User Experiments Failed",Toast.LENGTH_LONG).show()
                throw error
            }
        }
    }

    /**
     * Created mock data for testing adding a measurement to an experiment
     * Use this to learn how to create a RealmList<MeasurementDataPoint> for viewmodel input
     */
    private fun measurementDummyData(): ArrayList<DataPoint> {
        //TODO: delete this
        var dummyMeasurementDataPoint = ArrayList<DataPoint>()
        dummyMeasurementDataPoint.add(DataPoint(1.0, 1.5 ))
        dummyMeasurementDataPoint.add(DataPoint(2.0, 1.1 ))
        dummyMeasurementDataPoint.add(DataPoint(3.0, 6.2 ))
        dummyMeasurementDataPoint.add(DataPoint(4.0,3.6 ))
        dummyMeasurementDataPoint.add(DataPoint(5.0,1.6 ))
        dummyMeasurementDataPoint.add(DataPoint(6.0,8.3 ))
        dummyMeasurementDataPoint.add(DataPoint(7.0,5.9 ))
        dummyMeasurementDataPoint.add(DataPoint(8.0,3.3 ))
        dummyMeasurementDataPoint.add(DataPoint(9.0,6.7 ))
        dummyMeasurementDataPoint.add(DataPoint(10.0, 9.3 ))
        dummyMeasurementDataPoint.add(DataPoint(12.0, 4.2 ))
        dummyMeasurementDataPoint.add(DataPoint(13.0, 6.3 ))
        dummyMeasurementDataPoint.add(DataPoint(14.0, 9.2 ))
        return dummyMeasurementDataPoint
    }
}
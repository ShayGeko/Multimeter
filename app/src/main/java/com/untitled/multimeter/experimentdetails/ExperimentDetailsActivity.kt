package com.untitled.multimeter.experimentdetails

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.generateViewId
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.untitled.multimeter.R
import com.untitled.multimeter.RealmViewModelFactory
import io.realm.kotlin.types.ObjectId
import com.untitled.multimeter.data.model.Experiment
import com.untitled.multimeter.data.source.realm.RealmObjectNotFoundException
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class ExperimentDetailsActivity : AppCompatActivity() {
    private lateinit var viewModel:ExperimentDetailsViewModel
    private lateinit var id: ObjectId
    private lateinit var validIds: ArrayList<Int>
    private lateinit var titleView: TextView
    private lateinit var collaboratorsView: TextView
    private lateinit var dateTimeView: TextView
    private lateinit var commentView: TextView
    private val colors = arrayListOf(Color.RED, Color.BLUE, Color.CYAN, Color.GREEN, Color.LTGRAY, Color.MAGENTA, Color.YELLOW, Color.WHITE, Color.GRAY, Color.DKGRAY, Color.BLACK)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.experiment_entry)

        //Initialize Viewmodel
        val viewModelFactory = RealmViewModelFactory(this.application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ExperimentDetailsViewModel::class.java)

        titleView = findViewById(R.id.experiment_title)
        collaboratorsView = findViewById(R.id.experiment_collaborators)
        dateTimeView = findViewById(R.id.experiment_dateTime)
        commentView  = findViewById(R.id.experiment_comment)

        //get Experiment Id
        id = ObjectId.from(intent.extras?.getString("id")!!)
        viewModel.getExperiment(id).observe(this) { result ->
            result.onSuccess {
                val experiment: Experiment = result.getOrElse { throw RealmObjectNotFoundException("Experiment not found!") }

                //Format Collaborator String
                var collaboratorString = ""
                for (currentCollaborator in experiment.collaborators) {
                    if (currentCollaborator != experiment.collaborators.last() ) {
                        collaboratorString = collaboratorString + currentCollaborator + ", "
                    }
                    else {
                        collaboratorString += currentCollaborator
                    }
                }

                //Format Title
                var titleString = "Title: " + experiment.title

                //Get RealmInstant and convert to a Calendar Object
                val realmInstantDate = experiment.date
                val foundDate = Date(realmInstantDate.epochSeconds * 1000)
                val currentDate: Calendar = Calendar.getInstance()
                currentDate.time = foundDate

                //Format Date
                var dateString = ""
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
                val fullDate = month +" "+ date +" "+ year
                dateString = "$dateString$fullDate, $time"

                //Assign text
                titleView.text = experiment.title
                collaboratorsView.text = collaboratorString
                dateTimeView.text = dateString
                commentView.text = experiment.comment

                //Get arraylist of userIds and Measurements
                val dataValuesUserNames = ArrayList<String>()
                val dataValuesDataPoints = ArrayList<ArrayList<DataPoint>>()
                for (measurement in experiment.measurements) {
                    dataValuesUserNames.add(measurement.user.toString())

                    val dataPoints = ArrayList<DataPoint>()
                    for (dataPoint in measurement.dataPoints) {
                        dataPoints.add(DataPoint(dataPoint.x,dataPoint.y))
                    }
                    dataValuesDataPoints.add(dataPoints)
                }

                //For the users get the usernames to display in the table
                val usernames: ArrayList<String> = ArrayList()
                validIds = ArrayList()
                for (index in 0 until dataValuesUserNames.size) {
                    validIds.add(generateViewId())
                    viewModel.getUserName(ObjectId.Companion.from(dataValuesUserNames[index])).observe(this) { result ->
                        result.onSuccess {
                            usernames.add(result.getOrElse { "" })

                            //When all usernames are gotten, display the table
                            if (usernames.size == dataValuesUserNames.size) { displayDataInTheTable(dataValuesDataPoints, usernames) }
                        }
                        result.onFailure { error ->
                            Log.e("ExperimentDetaillsActivity", "Error getting userName")
                            throw error
                        }
                    }
                }

                // display the data
                displayDataOnGraph(dataValuesDataPoints)
            }
            result.onFailure {
                Log.e("ExperimentDetailsActivity","FAIL")
            }
        }
    }

    /**
     * Displayes the data in the graph
     *
     * @param allData data to be displayed
     */
    private fun displayDataOnGraph(allData : ArrayList<ArrayList<DataPoint>>){
        val graph = findViewById<View>(com.untitled.multimeter.R.id.graph) as GraphView
        //Graph Display Settings + add data
        var maxY = 0.0
        var maxX = 0.0
        var minY = Double.MAX_VALUE
        var minX = Double.MAX_VALUE
        var pos = 0
        for (data in allData) {
            val series: LineGraphSeries<DataPoint> = LineGraphSeries<DataPoint>()
            series.dataPointsRadius = 15F
            series.isDrawDataPoints = true
            series.color = colors[pos]
            data.sortWith(compareBy({it.x}, {it.y}))
            for (dataPoint in data) {
                series.appendData(dataPoint,true,9999999)
                if (maxY < dataPoint.y) { maxY = dataPoint.y }
                if (maxX < dataPoint.x) { maxX = dataPoint.x }
                if (minY > dataPoint.y) { minY = dataPoint.y }
                if (minX > dataPoint.x) { minX = dataPoint.x }
            }
            graph.addSeries(series)
            if (pos+1 >= colors.size) { pos = 0 }
            else { pos++ }
        }
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(minX)
        graph.viewport.setMaxX(maxX)
        graph.viewport.isYAxisBoundsManual = true
        graph.viewport.setMinY(minY)
        graph.viewport.setMaxY(maxY)
    }


    /**
     * Displays the data in the table
     *
     * @param allData data to be displayed
     */
    private fun displayDataInTheTable(allData : ArrayList<ArrayList<DataPoint>>, usernames: ArrayList<String>){
        //Add entries to table
        val tableLayoutView = findViewById<TableLayout>(R.id.table_layout)

        displayTableHeader(tableLayoutView, usernames, usernames.size-1)
        displayTableRows(tableLayoutView, allData)
    }

    /**
     * Displays the header of the table (time column, column for each series)
     *
     * @param tableLayoutView table to add the header to
     * @param numSeries number of series to add the columns for
     */
    private fun displayTableHeader(tableLayoutView : TableLayout,usernames: ArrayList<String>, numSeries: Int){
        //header row
        val row = TableRow(this)
        val timeHeader = TextView(this)
        timeHeader.text = "time"
        timeHeader.gravity = Gravity.CENTER
        row.addView(timeHeader)
        for (i in 0..numSeries) {
            val text = TextView(this)
            text.text = usernames[i]
            text.gravity = Gravity.CENTER
            row.addView(text)
        }
        tableLayoutView.addView(
            row,
            TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }

    /**
     * Displays the rows of the table based on the data
     *
     * @param tableLayoutView table to add the header to
     * @param allData data to be displayed
     */
    private fun displayTableRows(tableLayoutView : TableLayout, allData: ArrayList<ArrayList<DataPoint>>){
        val usedXCoordinates = HashSet<Double>()

        for (data in allData) {
            for (dataPoint in data) {
                //if already used x coordinate skip
                if (dataPoint.x in usedXCoordinates) {
                    break
                }
                usedXCoordinates.add(dataPoint.x)

                val row = TableRow(this)
                val time = TextView(this)
                val y = TextView(this)

                //Add entry for this datapoint
                time.text = dataPoint.x.toString()
                time.gravity = Gravity.CENTER
                y.text = dataPoint.y.toString()
                y.gravity = Gravity.CENTER
                row.addView(time)
                row.addView(y)

                //find other datapoints with the same x value and put in same row
                for (nestedData in allData.subList(allData.indexOf(data)+1,allData.size)) {
                    var foundEntry = false
                    for (nestedDataPoint in nestedData) {
                        if (nestedDataPoint.x == dataPoint.x) {
                            var newVal = TextView(this)
                            newVal.text = nestedDataPoint.y.toString()
                            newVal.gravity = Gravity.CENTER
                            row.addView(newVal)
                            foundEntry = true
                        }
                    }
                    if (!foundEntry) {
                        var newVal = TextView(this)
                        newVal.text = "N/A"
                        newVal.gravity = Gravity.CENTER
                        row.addView(newVal)
                    }
                }
                tableLayoutView.addView(
                    row,
                    TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT
                    )
                )
            }
        }
    }

    /**
     * Adds a delete option in the menu
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (intent.extras?.getInt("ReadOnly") == 0) {
            menuInflater.inflate(R.menu.delete_menu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * On the delete option being selected, insert data into the database, and redirect back to MainMenuActivity
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_delete) {

            viewModel.removeExperimentFromUser(id)
            viewModel.deleteExperiment(id)

            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
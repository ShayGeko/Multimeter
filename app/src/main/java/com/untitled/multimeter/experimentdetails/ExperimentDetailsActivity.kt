package com.untitled.multimeter.experimentdetails

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.untitled.multimeter.MainMenuActivity
import com.untitled.multimeter.R
import com.untitled.multimeter.UserViewModelFactory
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.runBlocking


class ExperimentDetailsActivity : AppCompatActivity() {
    private lateinit var viewModel:ExperimentDetailsViewModel
    private lateinit var id: ObjectId
    private val colors = arrayListOf(Color.RED, Color.BLUE, Color.CYAN, Color.GREEN, Color.LTGRAY, Color.MAGENTA, Color.YELLOW, Color.WHITE, Color.GRAY, Color.DKGRAY, Color.BLACK)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.experiment_entry)

        //Initialize Viewmodel
        val viewModelFactory = UserViewModelFactory(this.application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ExperimentDetailsViewModel::class.java)

        val titleView : TextView = findViewById(R.id.experiment_title)
        val collaboratorsView : TextView = findViewById(R.id.experiment_collaborators)
        val dateTimeView : TextView = findViewById(R.id.experiment_dateTime)
        val commentView : TextView = findViewById(R.id.experiment_comment)

        titleView.text = intent.extras?.getString("title")!!
        collaboratorsView.text = intent.extras?.getString("collaborators")!!
        dateTimeView.text = intent.extras?.getString("dateTime")!!
        commentView.text = intent.extras?.getString("comment")!!
        id = ObjectId.Companion.from(intent.extras?.getString("id")!!)

        //Get the data
        val dataValues = intent.getBundleExtra("data")!!
        val allData: ArrayList<ArrayList<DataPoint>> = dataValues.getSerializable("values") as ArrayList<ArrayList<DataPoint>>

        // display the data
        displayDataOnGraph(allData)
        displayDataInTheTable(allData)
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
                if (maxX < data.size) { maxX = data.size.toDouble() }
            }
            graph.addSeries(series)
            if (pos+1 >= colors.size) { pos = 0 }
            else { pos++ }
        }
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(0.0)
        graph.viewport.setMaxX(maxX+1.0)
        graph.viewport.isYAxisBoundsManual = true
        graph.viewport.setMinY(0.0)
        graph.viewport.setMaxY(maxY)
    }


    /**
     * Displays the data in the table
     *
     * @param allData data to be displayed
     */
    private fun displayDataInTheTable(allData : ArrayList<ArrayList<DataPoint>>){
        //Add entries to table
        val tableLayoutView = findViewById<TableLayout>(R.id.table_layout)

        displayTableHeader(tableLayoutView, allData.size)
        displayTableRows(tableLayoutView, allData)
    }

    /**
     * Displays the header of the table (time column, column for each series)
     *
     * @param tableLayoutView table to add the header to
     * @param numSeries number of series to add the columns for
     */
    private fun displayTableHeader(tableLayoutView : TableLayout, numSeries: Int){
        //header row
        val row = TableRow(this)
        val timeHeader = TextView(this)
        timeHeader.text = "time"
        timeHeader.gravity = Gravity.CENTER
        row.addView(timeHeader)
        for (i in 1..numSeries) {
            val text = TextView(this)
            text.text = "series "+i.toString()
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

            //viewModel.removeExperimentFromUser(id)
            //viewModel.deleteExperiment(id)

            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}
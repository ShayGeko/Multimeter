package com.untitled.multimeter.mesurements

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.untitled.multimeter.R
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.io.File
import java.io.FileOutputStream
import java.util.ArrayList

/**
 * random measurements display.
 */

// global values
private var volt:Float = 0.0F
private var x_values:Float = 0.0F
lateinit var lineGraphView: GraphView
private lateinit var values:ArrayList<DataPoint>

class Measure : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val t = inflater.inflate(R.layout.measure, container, false)

        // starts the update function
        val handler = Handler()
        handler.postDelayed({
            update(t)
        }, 500)

        // draws a line graph
        lineGraphView = t.findViewById(R.id.voltage_graph)
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
        lineGraphView.viewport.isScrollable = true
        lineGraphView.viewport.isScalable = true
        lineGraphView.viewport.setScalableY(true)
        lineGraphView.viewport.setScrollableY(true)
        lineGraphView.addSeries(series)


        // checks for button click and changes color and text
        val btn_click_me = t.findViewById(R.id.collect_button) as Button
        btn_click_me.setOnClickListener {
            if(btn_click_me.text.equals("START collecting")){
                btn_click_me.text = "STOP collecting"
                btn_click_me.setBackgroundColor(Color.RED)
            }
            else{
                btn_click_me.text = "START collecting"
                btn_click_me.setBackgroundColor(Color.GREEN )
            }
        }



        return t
    }

    // update method used for random voltage values.
    private fun update(view: View){
        volt = (((1..10).random().toFloat())/(11.0).toFloat())*10
        val voltage: TextView = view.findViewById(R.id.voltage_value)
        voltage.text = (volt.toString())+"  V"
//        val dataPoint:DataPoint
//        dataPoint = DataPoint(x_values.toDouble(),volt.toDouble())
//        values.add(dataPoint)
//        x_values += 0.5F
        val handler =Handler()
        handler.postDelayed({
            update2(view)
        },500)
    }
    // update2 method used for random voltage values.
    private fun update2(view:View){
        volt = (((1..10).random().toFloat())/(11.0).toFloat())*10
        val voltage: TextView = view.findViewById(R.id.voltage_value)
        voltage.text = (volt.toString())+"  V"
//        val dataPoint:DataPoint
//        dataPoint = DataPoint(x_values.toDouble(),volt.toDouble())
//        values.add(dataPoint)
//        x_values += 0.5F
        val handler =Handler()

        handler.postDelayed({
            update(view)
        },500)
    }

}
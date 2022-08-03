package com.untitled.multimeter.createexperiment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.untitled.multimeter.MainMenuActivity
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.R
import com.untitled.multimeter.UserViewModelFactory
import com.untitled.multimeter.createaccount.CreateAccountViewModel
import com.untitled.multimeter.data.model.*
import com.untitled.multimeter.data.source.realm.RealmObjectNotFoundException
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.ObjectId
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.ArrayList


class CreateExperimentActivity : AppCompatActivity() {
    private lateinit var viewModel: CreateExperimentViewModel
    private lateinit var titleEditText: EditText
    private lateinit var collaboratorsEditText: EditText
    private lateinit var cancelButton : Button
    private lateinit var saveButton : Button
    private lateinit var currentTimeTextView: TextView
    private lateinit var friendsArray: ArrayList<String>
    private lateinit var collaboratorsChoices: ArrayList<Int>
    private lateinit var experimentCollaborators: ArrayList<String>
    private lateinit var dateTimeTimer: Timer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_experiment)

        // get the viewmodel
        val viewModelFactory = UserViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CreateExperimentViewModel::class.java)

        //Initialize lateinit variables
        collaboratorsChoices = ArrayList()
        experimentCollaborators = ArrayList<String>()

        //Get all the views
        titleEditText = findViewById(R.id.title_edittext)
        collaboratorsEditText = findViewById(R.id.collaborators_edittext)
        cancelButton = findViewById(R.id.button_cancel)
        saveButton = findViewById(R.id.button_save)
        currentTimeTextView = findViewById(R.id.current_time)

        //Starts a timer subroutine that updates the Date and Time display every 1000 milliseconds (1 second)
        dateTimeTimer = Timer()
        startDateTimeUpdator(dateTimeTimer)

        //Get Friends List to create options for choosing collaborators
        friendsArray = ArrayList()

        //########################
        // Get list of user friends from the database
        // This is dummy data
        val friendsList = ArrayList(listOf("Ben Smith", "Jim Smith", "Frank Smith","Howie Smith", "Joe Smith", "Timmy","Smith Smith", "Frojo", "Finn", "Simon", "Christopher", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))
        //########################

        for (x in friendsList) {
            friendsArray.add(x)
        }

        //Create an alertDialog when choose collaborators is picked
        collaboratorsEditText.isFocusable = false
        collaboratorsEditText.setOnClickListener { collaboratorAlertDialog() }
        cancelButton.setOnClickListener { cancelFunctions() }
        saveButton.setOnClickListener { saveFunctions() }
    }

    /**
     * Get the current date and time as a string (Month Date Year HH:MM:SS)
     */
    private fun getDateTimeAsString(): String {
        val currentDate = Calendar.getInstance()
        var seconds: String = currentDate[Calendar.SECOND].toString()
        if (currentDate[Calendar.SECOND] < 10) {
            seconds = "0"+currentDate[Calendar.SECOND].toString()
        }
        val time = currentDate[Calendar.HOUR_OF_DAY].toString() +":"+ currentDate[Calendar.MINUTE] +":"+ seconds
        val date = currentDate.get(Calendar.DATE).toString()
        val month = DateFormatSymbols().months[currentDate.get(Calendar.MONTH)]
        val year = currentDate[Calendar.YEAR].toString()
        val fullDate = "$month $date $year"
        val dateString = "$fullDate, $time"
        return dateString
    }

    /**
     * Simply redirects to the Experiments Fragment
     */
    private fun cancelFunctions() {
        finish()
    }

    /**
     * Store the input into the database
     */
    private fun saveFunctions() {

        Log.d(MultimeterApp.APPLICATION_TAG, "saving the experiment")
        //Get input from edit text
        val title = titleEditText.text.toString()
        val dateTime = Calendar.getInstance()

        //Create New Experiment Entry
        val newExperiment = Experiment().apply {
            this.title = title
            this.date = Calendar.getInstance()
            this.collaborators = RealmListString(experimentCollaborators)
            this.comment = ""
            this.measurements = realmListOf()
        }

        //Insert into database
        //Observers are to ensure that finish() is called after all DB operations
        viewModel.insertExperiment(newExperiment).observe(this) { result ->
            result.onSuccess {
                viewModel.addExperimentToUser(newExperiment).observe(this) { result ->
                    result.onSuccess {
                        finish()
                    }
                    result.onFailure { error ->
                        Log.e("CreateExperimentActivity", "addExperimentToUser: " + error.toString())
                        finish()
                    }
                }
            }
            result.onFailure { error ->
                Log.e("CreateExperimentActivity", "Insert Failed: " + error.toString())
                finish()
            }
        }
    }

    /**
     * Creates an AlertDialog for choosing collaborators from user's list of friends
     */
    private fun collaboratorAlertDialog() {

        //Convert FriendsArray to Charsequence[]
        val friendsCharSequence = friendsArray.toTypedArray<CharSequence>()

        //Create a boolean array to signify choices, initialized to all false,
        val checkedItems = BooleanArray(friendsCharSequence.size) { false }

        //Initialize Alert Builder
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        // set title
        builder.setTitle("Invite collaborator")



        val editText = EditText(this)
        editText.inputType = EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        builder.setView(editText)

//        // set up options
//        builder.setMultiChoiceItems(friendsCharSequence, checkedItems) { dialog, chosenVal, chosen ->
//            if (chosen) {
//                //If chosen, add choice (as an int) to choices
//                collaboratorsChoices.add(chosenVal)
//            } else {
//                //If unchosen, remove the int from list
//                collaboratorsChoices.remove(Integer.valueOf(chosenVal))
//            }
//        }
        //On save, read the choices by the user and add them to the collaborators ArrayList
        builder.setPositiveButton("OK") { dialog, whichButton ->

              val receiverEmail = editText.text.toString()

              viewModel.findUser(receiverEmail).observe(this){ result : Result<UserInfo> ->
                  with(result){
                      val toast = Toast.makeText(this@CreateExperimentActivity,"", Toast.LENGTH_LONG)
                      onSuccess {
                          viewModel.invitationReceivers.add(it)
                          toast.setText("Added ${it.userName} to receivers")
                          toast.show()
                          dialog.dismiss()
                      }
                      onFailure { exception ->
                          when(exception){
                              is RealmObjectNotFoundException -> {
                                  toast.setText("User not found! Try again")
                              }
                              else -> {
                                  toast.setText("Oops! Something went wrong")
                              }
                          }
                          toast.show()

                      }
                  }
              }
//            //Clear previous choices
//            experimentCollaborators.clear()
//
//            //Prepare edittext string
            var collaboratorString = ""

            for (x in viewModel.invitationReceivers) {
                collaboratorString += x.userName
                if (x != viewModel.invitationReceivers.last()) {
                    collaboratorString += ", "
                }
            }

            //Set Collaborators Edittext to a string of the values
            collaboratorsEditText.setText(collaboratorString)
        }
        //On cancel, close dialog
        builder.setNegativeButton("Cancel") {dialog, _ ->
            dialog.dismiss()
        }

        // show dialog
        builder.show()
    }

    /**
     * Starts a Timer that updates the currentTimeTextView.text every second
     *
     * @param dateTimeUpdator - A Timer object that has a schedule set to update currentTimeTextView.text every second
     *
     * @returns
     * A Unit object that represents the timer (Used to stop the Timer onStop)
     */
    private fun startDateTimeUpdator(dateTimeUpdator: Timer) {
        dateTimeTimer.schedule(object : TimerTask() {
            override fun run() {

                //Allows the thread to make changes as if it was on the UI thread
                this@CreateExperimentActivity.runOnUiThread(java.lang.Runnable {
                    this.run {
                        currentTimeTextView.text = getDateTimeAsString()
                    }
                })
            }
        }, 1, 1000)
    }

    override fun onStop() {
        dateTimeTimer.cancel()
        super.onStop()
    }
}
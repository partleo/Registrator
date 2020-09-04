package com.example.workhourregistrator

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.START_TIME_WORK
import kotlinx.android.synthetic.main.fragment_start_duty.*
import java.text.SimpleDateFormat
import java.util.*


class StartDutyFragment: Fragment() {

    private lateinit var v: View
    private lateinit var c: Context

    private var viewGroup: ViewGroup? = null

    private val sdf = SimpleDateFormat( "dd.MM.yyyy", Locale.getDefault())
    private val stf = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val swf = SimpleDateFormat("EE", Locale.getDefault())

    private val cal = Calendar.getInstance()
    private lateinit var timeListener: TimePickerDialog.OnTimeSetListener

    private val m = MainActivity()
    private var spe = SharedPreferencesEditor()
    private val dp = DateProvider()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_start_duty, container, false)
        c = v.context
        spe.setupSharedPreferencesEditor(c)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = v.rootView.findViewById<Toolbar>(R.id.main_toolbar)
        //toolbar.setTitle(R.string.card_text_3)

        start_duty_button.setOnClickListener {
            spe.setStatus(START_TIME_WORK, stf.format(cal.time))
            end_duty_button.isEnabled = true
            end_day_button.isEnabled = true
            start_duty_button.isEnabled = false
        }
        end_duty_button.setOnClickListener {
            registerDuty()
        }
        end_day_button.setOnClickListener {
            registerDuty()
            start_duty_button.isEnabled = true
            end_duty_button.isEnabled = false
            end_day_button.isEnabled = false
        }
    }

    private fun registerDuty() {
        val date = cal.time
        val weekday = swf.format(date)
        val project = if (work_number_checkbox.isChecked) {
            work_number_input.text.toString()
        } else {
            if (work_number_spinner.selectedItem == null) {
                ""
            } else {
                work_number_spinner.selectedItem.toString()
            }
        }
        val description = edit_text_description.text.toString()
        val startTime = spe.getStatus(START_TIME_WORK, "")
        val endTime = stf.format(cal.time)
        val totalTime = dp.getTimeDifference(startTime, endTime)



        if (validateForm(project, description)) {
            val list = spe.getWorkNumberList()
            list.add(project)
            spe.setWorkNumberList(list)
            m.writeIntoExcelFile(c, weekday, date, project, description, startTime, endTime, totalTime)
            spe.setStatus(START_TIME_WORK, endTime)
        }
    }

    private fun validateForm(project: String, description: String): Boolean {
        return when {
            TextUtils.isEmpty(project) -> {
                Toast.makeText(c, c.getText(R.string.field_empty), Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(description) -> {
                Toast.makeText(c, c.getText(R.string.field_empty), Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }
}
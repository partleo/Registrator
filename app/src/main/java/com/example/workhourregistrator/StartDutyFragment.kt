package com.example.workhourregistrator

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.START_TIME_WORK
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.WORK_IN_PROGRESS
import kotlinx.android.synthetic.main.fragment_register_duty.*
import kotlinx.android.synthetic.main.fragment_start_duty.*
import kotlinx.android.synthetic.main.fragment_start_duty.edit_text_description
import kotlinx.android.synthetic.main.fragment_start_duty.spinner_background_layout
import kotlinx.android.synthetic.main.fragment_start_duty.work_number_checkbox
import kotlinx.android.synthetic.main.fragment_start_duty.work_number_input
import kotlinx.android.synthetic.main.fragment_start_duty.work_number_spinner
import java.text.SimpleDateFormat
import java.util.*


class StartDutyFragment: androidx.fragment.app.Fragment() {

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
        //val toolbar = v.rootView.findViewById<Toolbar>(R.id.main_toolbar)
        //toolbar.setTitle(R.string.card_text_3)

        setAdapter()
        setOnItemSelectedListener()

        if (spe.getStatus(WORK_IN_PROGRESS, false)) {
            enableDisableButtons()
        }

        start_duty_button.setOnClickListener {
            spe.setStatus(START_TIME_WORK, stf.format(cal.time))
            enableDisableButtons()
            spe.setStatus(WORK_IN_PROGRESS, true)
        }
        end_duty_button.setOnClickListener {
            registerDuty(true)
        }
        end_day_button.setOnClickListener {
            registerDuty(false)
        }

        work_number_checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                spinner_background_layout.visibility = RelativeLayout.GONE
                work_number_input.visibility = EditText.VISIBLE
            }
            else {
                work_number_input.visibility = EditText.GONE
                spinner_background_layout.visibility = RelativeLayout.VISIBLE
            }
        }
    }

    private fun enableDisableButtons() {
        start_duty_button.isEnabled = !start_duty_button.isEnabled
        end_duty_button.isEnabled = !end_duty_button.isEnabled
        end_day_button.isEnabled = !end_day_button.isEnabled

        start_duty_button.visibility = if (start_duty_button.visibility == Button.GONE) {
            Button.VISIBLE
        } else {
            Button.GONE
        }
        end_duty_button.visibility = if (end_duty_button.visibility == Button.GONE) {
            Button.VISIBLE
        } else {
            Button.GONE
        }
        end_day_button.visibility = if (end_day_button.visibility == Button.GONE) {
            Button.VISIBLE
        } else {
            Button.GONE
        }
    }

    private fun registerDuty(workInProgress: Boolean) {
        val date = cal.time
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
        val endTime = stf.format(Date(System.currentTimeMillis()))

        if (validateForm(project, description)) {
            val list = spe.getWorkNumberList()
            list.add(project)
            spe.setWorkNumberList(list)

            spe.setStatus(START_TIME_WORK, endTime)
            m.writeIntoExcelFile(c, date, project, description, startTime, endTime)


            if (!workInProgress) {
                spe.setStatus(WORK_IN_PROGRESS, workInProgress)
                enableDisableButtons()
            }
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

    private fun setAdapter() {
        val adapter = ArrayAdapter(c, android.R.layout.simple_spinner_item, spe.getWorkNumberList())
        work_number_spinner.adapter = adapter
    }

    private fun setOnItemSelectedListener() {
        work_number_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position >= 0) {
                    //password_input.setText(spe.getPasswordOfDevice(adapterView.getItemAtPosition(position).toString()))
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
                return
            }
        }
    }
}
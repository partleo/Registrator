package com.example.workhourregistrator

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.fragment_register_duty.*
import java.text.SimpleDateFormat
import java.util.*


class RegisterDutyFragment: Fragment() {

    private lateinit var v: View
    private lateinit var c: Context
    private var message = ""

    private var viewGroup: ViewGroup? = null

    private val sdf = SimpleDateFormat( "dd.MM.yyyy", Locale.getDefault())
    private val stf = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val swf = SimpleDateFormat("EE", Locale.getDefault())

    private val cal = Calendar.getInstance()
    private lateinit var dateListener: DatePickerDialog.OnDateSetListener
    private lateinit var timeFromListener: TimePickerDialog.OnTimeSetListener
    private lateinit var timeToListener: TimePickerDialog.OnTimeSetListener

    private val m = MainActivity()
    private var spe = SharedPreferencesEditor()
    private val dp = DateProvider()

    private var weekday = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_register_duty, container, false)
        c = v.context
        spe.setupSharedPreferencesEditor(c)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnSetListeners()
        setAdapter()
        setOnItemSelectedListener()

        date_picker_text_view.text = sdf.format(System.currentTimeMillis())
        time_picker_from_text_view.text = stf.format(System.currentTimeMillis())
        time_picker_to_text_view.text = stf.format(System.currentTimeMillis())

        date_picker_text_view.setOnClickListener {
            showDatePicker(dateListener)
        }
        time_picker_from_text_view.setOnClickListener {
            showTimePicker(timeFromListener)
        }
        time_picker_to_text_view.setOnClickListener {
            showTimePicker(timeToListener)
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
        register_duty_button.setOnClickListener {
            val date = sdf.parse(date_picker_text_view.text.toString())
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
            val startTime = time_picker_from_text_view.text.toString()
            val endTime = time_picker_to_text_view.text.toString()
            val totalTime = dp.getTimeDifference(startTime, endTime)

            if (validateForm(project, description)) {
                val list = spe.getWorkNumberList()
                list.add(project)
                spe.setWorkNumberList(list)
                m.writeIntoExcelFile(c, weekday, date, project, description, startTime, endTime, totalTime)
            }
        }
    }

    private fun setupOnSetListeners() {
        weekday = swf.format(cal.time)
        dateListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            date_picker_text_view.text = sdf.format(cal.time)
            weekday = swf.format(cal.time)
        }
        timeFromListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            time_picker_from_text_view.text = stf.format(cal.time)

            if (stf.parse(time_picker_from_text_view.text.toString()).after(stf.parse(time_picker_to_text_view.text.toString()))) {
                time_picker_to_text_view.text = stf.format(cal.time)
            }
        }
        timeToListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            time_picker_to_text_view.text = stf.format(cal.time)
            if (stf.parse(time_picker_from_text_view.text.toString()).after(stf.parse(time_picker_to_text_view.text.toString()))) {
                time_picker_from_text_view.text = stf.format(cal.time)
            }
        }
    }

    private fun showDatePicker(dateSetListener: DatePickerDialog.OnDateSetListener) {
        val dialog = DatePickerDialog(c, R.style.PickerDialogTheme, dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH))
        //dialog.datePicker.minDate = System.currentTimeMillis() - 1000
        dialog.show()
    }

    private fun showTimePicker(timeSetListener: TimePickerDialog.OnTimeSetListener) {
        TimePickerDialog(c, R.style.PickerDialogTheme, timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true).show()
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
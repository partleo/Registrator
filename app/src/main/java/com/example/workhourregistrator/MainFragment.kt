package com.example.workhourregistrator

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_start_duty.*
import java.text.SimpleDateFormat
import java.util.*


class MainFragment: androidx.fragment.app.Fragment() {

    private lateinit var v: View
    private lateinit var c: Context
    private var message = ""

    private var viewGroup: ViewGroup? = null

    private val sdf = SimpleDateFormat( "dd.MM.yyyy", Locale.getDefault())
    private val stf = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val ssf = SimpleDateFormat("ss", Locale.getDefault())

    private val cal = Calendar.getInstance()
    private lateinit var dateListener: DatePickerDialog.OnDateSetListener
    private lateinit var timeListener: TimePickerDialog.OnTimeSetListener

    private val m = MainActivity()
    private var sp = SharedPreferencesEditor()

    private var weekDay = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_main, container, false)
        c = v.context
        sp.setupSharedPreferencesEditor(c)
        return v
    }

    /*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = v.rootView.findViewById<Toolbar>(R.id.main_toolbar)
        //toolbar.setTitle(R.string.card_text_3)
        //m.toolbar(toolbar, 18f)

        setupDateSetListeners()
        setupTimeSetListeners()

        date_picker_text_view.text = sdf.format(System.currentTimeMillis())
        time_picker_text_view.text = stf.format(System.currentTimeMillis())

        date_picker_text_view.setOnClickListener {
            showDatePicker(dateListener)
        }
        time_picker_text_view.setOnClickListener {
            showTimePicker(timeListener)
        }
        set_rtu_time_button.setOnClickListener {

        }
    }

    private fun setupDateSetListeners() {
        weekDay = getWeekDay()
        dateListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            date_picker_text_view.text = sdf.format(cal.time)

            weekDay = getWeekDay()
        }
    }

    private fun getWeekDay(): String {
        return if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
            "W07"
        } else {
            "W0"+(cal.get(Calendar.DAY_OF_WEEK)-1)
        }
    }

    private fun setupTimeSetListeners() {
        timeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            time_picker_text_view.text = stf.format(cal.time)
        }
    }

    private fun showDatePicker(dateSetListener: DatePickerDialog.OnDateSetListener) {
        val dialog = DatePickerDialog(c, R.style.PickerDialogTheme, dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH))
        dialog.datePicker.minDate = System.currentTimeMillis() - 1000
        dialog.show()
    }

    private fun showTimePicker(timeSetListener: TimePickerDialog.OnTimeSetListener) {
        TimePickerDialog(c, R.style.PickerDialogTheme, timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true).show()
    }

    private fun getDate(d: TextView): String {
        val dateParts = d.text.split(".")
        return "${dateParts[2]}-${dateParts[1]}-${dateParts[0]}"
    }
    */

}
package com.example.workhourregistrator

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_start_duty.*
import java.text.SimpleDateFormat
import java.util.*


class StartDutyFragment: Fragment() {

    private lateinit var v: View
    private lateinit var c: Context

    private var viewGroup: ViewGroup? = null

    private val sdf = SimpleDateFormat( "dd.MM.yyyy", Locale.getDefault())
    private val stf = SimpleDateFormat("HH:mm", Locale.getDefault())

    private val cal = Calendar.getInstance()
    private lateinit var timeListener: TimePickerDialog.OnTimeSetListener

    private val m = MainActivity()
    private var sp = SharedPreferencesEditor()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_start_duty, container, false)
        c = v.context
        sp.setupSharedPreferencesEditor(c)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = v.rootView.findViewById<Toolbar>(R.id.main_toolbar)
        //toolbar.setTitle(R.string.card_text_3)
        //m.toolbar(toolbar, 18f)

        setupOnTimeSetListener()

        time_picker_text_view.text = stf.format(System.currentTimeMillis())

        time_picker_text_view.setOnClickListener {
            showTimePicker(timeListener)
        }
        set_rtu_time_button.setOnClickListener {

        }
    }

    private fun setupOnTimeSetListener() {
        timeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            time_picker_text_view.text = stf.format(cal.time)
        }
    }

    private fun showTimePicker(timeSetListener: TimePickerDialog.OnTimeSetListener) {
        TimePickerDialog(c, R.style.PickerDialogTheme, timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true).show()
    }
}
package com.example.workhourregistrator

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import android.text.Editable
import android.text.TextUtils.*
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.CURRENT_WORK_NUMBER
import kotlinx.android.synthetic.main.fragment_register_duty.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.widget.LinearLayout
import android.widget.EditText
import android.view.WindowManager
import android.widget.FrameLayout
import android.view.Gravity
import android.R.attr.gravity
import android.content.res.Resources
import android.text.AlteredCharSequence
import android.text.InputFilter
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat


class RegisterDutyFragment: androidx.fragment.app.Fragment() {

    private lateinit var v: View
    private lateinit var c: Context
    private var message = ""

    private var viewGroup: ViewGroup? = null

    private val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val stf = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val swf = SimpleDateFormat("EE", Locale.getDefault())

    private val smdf = SimpleDateFormat("dd.", Locale.getDefault())
    private val smyf = SimpleDateFormat("yyyy", Locale.getDefault())
    private val smmf = SimpleDateFormat("MMMM", Locale.getDefault())

    private val m = MainActivity()
    private var spe = SharedPreferencesEditor()
    private val adp = AlertDialogProvider()

    private lateinit var date: Date


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_register_duty, container, false)
        c = v.context
        spe.setupSharedPreferencesEditor(c)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = v.rootView.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.register_duty)


        val p = adp.setupWorkNumberList(c)
        val l = p.first
        val currentIndex = p.second
        if (l[currentIndex] != getString(R.string.new_work_number)) {
            work_number_input.text = l[currentIndex]
        }

        date = Date(System.currentTimeMillis())

        date_text_view.text = smdf.format(System.currentTimeMillis())
        month_text_view.text = smmf.format(System.currentTimeMillis())
        year_text_view.text = smyf.format(System.currentTimeMillis())
        time_from_text_view.text = stf.format(System.currentTimeMillis())
        time_to_text_view.text = stf.format(System.currentTimeMillis())

        date_picker_linear_layout.setOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            val constraints = CalendarConstraints.Builder()
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, -2)
            val previousMonth = calendar.timeInMillis
            calendar.add(Calendar.MONTH, 4)
            val nextMonth = calendar.timeInMillis
            constraints.setStart(previousMonth).setEnd(nextMonth)
            builder.setCalendarConstraints(constraints.build())
            val picker = builder.build()
            picker.addOnPositiveButtonClickListener {
                date = sdf.parse(sdf.format(Date(it)))
                date_text_view.text = smdf.format(it)
                month_text_view.text = smmf.format(it)
                year_text_view.text = smyf.format(it)
            }
            picker.show(fragmentManager!!, picker.toString())
        }
        time_from_picker_linear_layout.setOnClickListener {
            showTimePicker(time_from_text_view)
        }
        time_to_picker_linear_layout.setOnClickListener {
            showTimePicker(time_to_text_view)
        }
        work_number_input.setOnClickListener {
            adp.showAlertDialog(c, work_number_input)
        }
        register_duty_button.setOnClickListener {
            val project = work_number_input.text.toString()
            val description = edit_text_description.text.toString()
            val startTime = time_from_text_view.text.toString()
            val endTime = time_to_text_view.text.toString()

            if (validateForm(project, description)) {
                val list = spe.getWorkNumberList()
                list.add(project)
                spe.setWorkNumberList(list)
                m.writeIntoExcelFile(c, date, project, description, startTime, endTime)
            }
        }
    }

    private fun Int.intToString(): String {
        return if (this < 10) {
            "0${this}"
        } else {
            "${this}"
        }
    }

    private fun validateForm(project: String, description: String): Boolean {
        return when {
            isEmpty(project) -> {
                Toast.makeText(c, c.getText(R.string.field_empty), Toast.LENGTH_SHORT).show()
                false
            }
            isEmpty(description) -> {
                Toast.makeText(c, c.getText(R.string.field_empty), Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    /*
    private fun setupWorkNumberList(): Pair<ArrayList<String>, Int> {
        val list = spe.getWorkNumberList()
        /*
        list.clear()
        spe.setWorkNumberList(list)
        spe.setStatus(CURRENT_WORK_NUMBER, 0)
        */

        list.add(getString(R.string.new_work_number))

        val currentIndex = spe.getStatus(CURRENT_WORK_NUMBER, 0)
        //work_number_input.text = list[currentIndex]
        return Pair(list, currentIndex)
    }

    private fun addNewWorkNumber(workNumber: String) {
        val list = spe.getWorkNumberList()
        list.add(workNumber)
        spe.setWorkNumberList(list)
        spe.setStatus(CURRENT_WORK_NUMBER, spe.getWorkNumberList().indexOf(workNumber))

    }

     */

    private fun showTimePicker(textView: TextView) {
        val builder = MaterialTimePicker.Builder()
        val picker = builder.setTimeFormat(TimeFormat.CLOCK_24H).build()
        //picker.setStyle(DialogFragment.STYLE_NORMAL, R.style.Custom_TimePickerDialogStyle) //NOT WORKING!!!!!
        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour.intToString()
            val minute = picker.minute.intToString()
            textView.text = c.getString(R.string.time_display_format, hour, minute)//"${picker.hour.intToString()}:${picker.hour.intToString()}"
        }
        picker.show(fragmentManager!!, picker.toString())
    }

    /*
    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(c)
        builder.setTitle(getString(R.string.choose_work_number))

        val input = EditText(c)
        val container = FrameLayout(c)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        container.layoutParams = params

        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = InputFilter.LengthFilter(10)
        input.filters = filterArray
        input.gravity = Gravity.CENTER
        input.setSingleLine()
        input.setTextColor(ContextCompat.getColor(c, R.color.colorWhite))
        params.setMargins(250, 0, 250, 0)
        input.background = c.getDrawable(R.drawable.rounded_edit_text)
        input.layoutParams = params
        container.addView(input)

        val pair = setupWorkNumberList()
        val list = pair.first
        val checkedItem = pair.second

        builder.setSingleChoiceItems(list.toTypedArray(), checkedItem) { _, index ->
            if (list[index] == getString(R.string.new_work_number)) {
                input.visibility = EditText.VISIBLE
                if (input.text.isNullOrEmpty()) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                }
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).isEnabled = false
            } else {
                input.visibility = EditText.GONE
                work_number_input.text = list[index]
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).isEnabled = true
            }
        }
        builder.setView(container)
        builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
            if (input.visibility == EditText.VISIBLE) {
                work_number_input.text = input.text
                addNewWorkNumber(input.text.toString())
            } else {
                spe.setStatus(CURRENT_WORK_NUMBER, list.indexOf(work_number_input.text.toString()))
            }

        }
        builder.setNegativeButton(getString(R.string.cancel), null)
        builder.setNeutralButton(getString(R.string.delete)) { _, _ ->
            spe.deleteWorkNumberFromList(work_number_input.text.toString())
        }
        input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (input.visibility == EditText.VISIBLE) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = !isEmpty(s)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        dialog = builder.create()
        dialog.setOnDismissListener {
            if (spe.getWorkNumberList().isNotEmpty()) {
                val wnl = spe.getWorkNumberList()
                val cwn = spe.getStatus(CURRENT_WORK_NUMBER, 0)
                if (wnl.size > cwn) {
                    work_number_input.text = wnl[cwn]
                } else {
                    spe.setStatus(CURRENT_WORK_NUMBER, 0)
                    work_number_input.text = wnl[0]
                }
            } else {
                work_number_input.text = ""
            }
        }
        dialog.show()

        if (spe.getWorkNumberList().isEmpty()) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).isEnabled = false
            input.visibility = EditText.VISIBLE
        } else {
            input.visibility = EditText.GONE
        }
        if (setupWorkNumberList().first.size >= 9) {
            dialog.listView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 1152)
        }
    }


     */


}
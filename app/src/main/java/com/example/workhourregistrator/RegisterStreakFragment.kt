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
import kotlinx.android.synthetic.main.fragment_register_streak.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.appcompat.widget.Toolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker


class RegisterStreakFragment: androidx.fragment.app.Fragment() {

    private lateinit var v: View
    private lateinit var c: Context

    private val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val stf = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val swf = SimpleDateFormat("EE", Locale.getDefault())

    private val smdf = SimpleDateFormat("dd.", Locale.getDefault())
    private val smyf = SimpleDateFormat("yyyy", Locale.getDefault())
    private val smmf = SimpleDateFormat("MMMM", Locale.getDefault())

    private lateinit var startDate: Date
    private lateinit var endDate: Date

    private val m = MainActivity()
    private var spe = SharedPreferencesEditor()
    private val adp = AlertDialogProvider()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_register_streak, container, false)
        c = v.context
        spe.setupSharedPreferencesEditor(c)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = v.rootView.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.register_day_off)

        val p = adp.setupWorkNumberList(c)
        val l = p.first
        val currentIndex = p.second
        if (l[currentIndex] != getString(R.string.new_work_number)) {
            work_number_input.text = l[currentIndex]
        }

        startDate = Date(System.currentTimeMillis())
        endDate = Date(System.currentTimeMillis())

        date_from_text_view.text = smdf.format(System.currentTimeMillis())
        month_from_text_view.text = smmf.format(System.currentTimeMillis())
        year_from_text_view.text = smyf.format(System.currentTimeMillis())

        date_to_text_view.text = smdf.format(System.currentTimeMillis())
        month_to_text_view.text = smmf.format(System.currentTimeMillis())
        year_to_text_view.text = smyf.format(System.currentTimeMillis())

        date_picker_linear_layout.setOnClickListener {
            val builder = MaterialDatePicker.Builder.dateRangePicker()


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
                startDate = sdf.parse(sdf.format(Date(it.first!!)))
                endDate = sdf.parse(sdf.format(Date(it.second!!)))
                date_from_text_view.text = smdf.format(it.first)
                month_from_text_view.text = smmf.format(it.first)
                year_from_text_view.text = smyf.format(it.first)
                date_to_text_view.text = smdf.format(it.second)
                month_to_text_view.text = smmf.format(it.second)
                year_to_text_view.text = smyf.format(it.second)
            }
            picker.show(fragmentManager!!, picker.toString())
        }
        work_number_input.setOnClickListener {
            adp.showAlertDialog(c, work_number_input)
        }
        register_duty_button.setOnClickListener {

            val project = work_number_input.text.toString()
            val description = edit_text_description.text.toString()
            val startTime = "8:00"//time_from_text_view.text.toString()
            val endTime = "16:00"//time_to_text_view.text.toString()

            if (validateForm(project, description)) {
                val list = spe.getWorkNumberList()
                list.add(project)
                spe.setWorkNumberList(list)


                if (m.writeStreakIntoExcelFile(c, startDate, endDate, project, description, startTime, endTime)) {
                    Log.d("tää", "ok")
                }
            }
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
        list.add(getString(R.string.new_work_number))
        val currentIndex = spe.getStatus(CURRENT_WORK_NUMBER, 0)
        return Pair(list, currentIndex)
    }

    private fun addNewWorkNumber(workNumber: String) {
        val list = spe.getWorkNumberList()
        list.add(workNumber)
        spe.setWorkNumberList(list)
        spe.setStatus(CURRENT_WORK_NUMBER, spe.getWorkNumberList().indexOf(workNumber))

    }

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
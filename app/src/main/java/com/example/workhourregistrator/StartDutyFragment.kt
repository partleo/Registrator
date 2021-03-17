package com.example.workhourregistrator

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import androidx.appcompat.widget.Toolbar
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.DESCRIPTION
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.END_WORKING_DAY
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.START_TIME_WORK
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.WORK_IN_PROGRESS
import kotlinx.android.synthetic.main.fragment_start_duty.*
import kotlinx.android.synthetic.main.fragment_start_duty.edit_text_description
import kotlinx.android.synthetic.main.fragment_start_duty.work_number_input
import java.text.SimpleDateFormat
import java.util.*


class StartDutyFragment: Fragment(), AlertDialogInterface {

    override fun onAlertDialogRespond(continues: Boolean?, project: String, description: String, endTime: String) {
        if (continues != null) registerDuty(continues, project, description, endTime)
    }

    private lateinit var v: View
    private lateinit var c: Context

    private var viewGroup: ViewGroup? = null

    private val sdf = SimpleDateFormat( "dd.MM.yyyy", Locale.getDefault())
    private val stf = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val swf = SimpleDateFormat("EE", Locale.getDefault())
    private val sdtf = SimpleDateFormat( "dd.MM.yyyy HH:mm:ss", Locale.getDefault())

    private val cal = Calendar.getInstance()
    private lateinit var timeListener: TimePickerDialog.OnTimeSetListener

    private val m = MainActivity()
    private var spe = SharedPreferencesEditor()
    private val adp = AlertDialogProvider()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_start_duty, container, false)
        c = v.context
        spe.setupSharedPreferencesEditor(c)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = v.rootView.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.start_duty)

        edit_text_description.addListener()

        val pair = adp.setupWorkNumberList(c)
        val list = pair.first
        val currentIndex = pair.second
        if (list[currentIndex] != getString(R.string.new_work_number)) {
            work_number_input.text = list[currentIndex]
        }

        if (spe.getStatus(WORK_IN_PROGRESS, false)) {
            start_duty_button.text = getText(R.string.quit)
            input_layout.visibility = LinearLayout.VISIBLE
        }
        setClickListeners()
    }

    private fun EditText.addListener() {
        setText(spe.getStatus(DESCRIPTION, ""))
        setSelection(text.length)
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                spe.setStatus(DESCRIPTION, s.toString())
            }
        })
    }

    private fun SharedPreferencesEditor.clearText() {
        setStatus(DESCRIPTION, "")
        edit_text_description.text.clear()
    }

    private fun setClickListeners() {
        work_number_input.setOnClickListener {
            adp.showAlertDialog(c, work_number_input)
        }
        start_duty_button.setOnClickListener {
            if (!spe.getStatus(WORK_IN_PROGRESS, false)) {
                spe.setStatus(START_TIME_WORK, stf.format(cal.time))
                start_duty_button.text = getText(R.string.quit)
                spe.setStatus(WORK_IN_PROGRESS, true)
                input_layout.visibility = LinearLayout.VISIBLE

                val alarmTime = sdtf.parse("${sdf.format(cal.time)} 15:45:00").time

                if (cal.time.before(Date(alarmTime))) {
                    m.setAlarm(c, alarmTime, false)
                }

            } else {
                adp.showRegisterDutyDialog(c, layoutInflater, this, fragmentManager!!, work_number_input.text, edit_text_description.text, spe.getStatus(START_TIME_WORK, ""))
            }
        }
        /*
        end_duty_button.setOnClickListener {
            adp.showRegisterDutyDialog(c, layoutInflater, this, work_number_input.text, edit_text_description.text, spe.getStatus(START_TIME_WORK, ""))
            //registerDuty(true)
        }
        end_day_button.setOnClickListener {
            //registerDuty(false)
        }
        */
    }


    /*
    private fun enableDisableButtons() {
        start_duty_button.setEnableDisable()
        end_duty_button.setEnableDisable()
        end_day_button.setEnableDisable()
    }

    private fun Button.setEnableDisable() {
        isEnabled = !isEnabled
        visibility = if (visibility == Button.GONE) {
            Button.VISIBLE
        } else {
            Button.GONE
        }
    }
    */

    private fun registerDuty(workInProgress: Boolean, project: String, description: String, endTime: String) {

        val date = cal.time
        val startTime = spe.getStatus(START_TIME_WORK, "")
        //val endTime = stf.format(Date(System.currentTimeMillis()))

        if (validateForm(project, description)) {
            val list = spe.getWorkNumberList()
            list.add(project)
            spe.setWorkNumberList(list)

            spe.setStatus(START_TIME_WORK, endTime)
            m.writeIntoExcelFile(c, date, project, description, startTime, endTime)

            if (!workInProgress) {
                m.setAlarm(c, 0, true)
                spe.setStatus(WORK_IN_PROGRESS, workInProgress)
                spe.setStatus(END_WORKING_DAY, false)
                start_duty_button.text = getText(R.string.start)
                input_layout.visibility = LinearLayout.GONE
            }
            spe.clearText()
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

    /*
    private fun setupWorkNumberList(): Pair<ArrayList<String>, Int> {
        val list = spe.getWorkNumberList()
        list.add(getString(R.string.new_work_number))
        val currentIndex = spe.getStatus(SharedPreferencesEditor.CURRENT_WORK_NUMBER, 0)
        return Pair(list, currentIndex)
    }

    private fun addNewWorkNumber(workNumber: String) {
        val list = spe.getWorkNumberList()
        list.add(workNumber)
        spe.setWorkNumberList(list)
        spe.setStatus(SharedPreferencesEditor.CURRENT_WORK_NUMBER, spe.getWorkNumberList().indexOf(workNumber))

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
                spe.setStatus(SharedPreferencesEditor.CURRENT_WORK_NUMBER, list.indexOf(work_number_input.text.toString()))
            }

        }
        builder.setNegativeButton(getString(R.string.cancel), null)
        builder.setNeutralButton(getString(R.string.delete)) { _, _ ->
            spe.deleteWorkNumberFromList(work_number_input.text.toString())
        }
        input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (input.visibility == EditText.VISIBLE) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = !TextUtils.isEmpty(s)
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
                val cwn = spe.getStatus(SharedPreferencesEditor.CURRENT_WORK_NUMBER, 0)
                if (wnl.size > cwn) {
                    work_number_input.text = wnl[cwn]
                } else {
                    spe.setStatus(SharedPreferencesEditor.CURRENT_WORK_NUMBER, 0)
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
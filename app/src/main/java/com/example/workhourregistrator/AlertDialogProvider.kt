package com.example.workhourregistrator

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*

class AlertDialogProvider {

    private lateinit var dialog: AlertDialog
    private var spe = SharedPreferencesEditor()


    private val stf = SimpleDateFormat("HH:mm", Locale.getDefault())


    fun setupWorkNumberList(c: Context): Pair<ArrayList<String>, Int> {
        spe.setupSharedPreferencesEditor(c)
        val list = spe.getWorkNumberList()
        list.add(c.getString(R.string.new_work_number))
        val currentIndex = spe.getStatus(SharedPreferencesEditor.CURRENT_WORK_NUMBER, 0)
        return Pair(list, currentIndex)
    }

    private fun addNewWorkNumber(workNumber: String) {
        val list = spe.getWorkNumberList()
        list.add(workNumber)
        spe.setWorkNumberList(list)
        spe.setStatus(SharedPreferencesEditor.CURRENT_WORK_NUMBER, spe.getWorkNumberList().indexOf(workNumber))

    }

    fun showAlertDialog(c: Context, tv: TextView) {
        spe.setupSharedPreferencesEditor(c)

        val builder = AlertDialog.Builder(c)
        builder.setTitle(c.getString(R.string.choose_work_number))

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

        val pair = setupWorkNumberList(c)
        val list = pair.first
        val checkedItem = pair.second

        builder.setSingleChoiceItems(list.toTypedArray(), checkedItem) { _, index ->
            if (list[index] == c.getString(R.string.new_work_number)) {
                input.visibility = EditText.VISIBLE
                if (input.text.isNullOrEmpty()) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                }
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).isEnabled = false
            } else {
                input.visibility = EditText.GONE
                tv.text = list[index]
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).isEnabled = true
            }
        }
        builder.setView(container)
        builder.setPositiveButton(c.getString(R.string.ok)) { _, _ ->
            if (input.visibility == EditText.VISIBLE) {
                tv.text = input.text
                addNewWorkNumber(input.text.toString())
            } else {
                spe.setStatus(SharedPreferencesEditor.CURRENT_WORK_NUMBER, list.indexOf(tv.text.toString()))
            }

        }
        builder.setNegativeButton(c.getString(R.string.cancel), null)
        builder.setNeutralButton(c.getString(R.string.delete)) { _, _ ->
            spe.deleteWorkNumberFromList(tv.text.toString())
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
                    tv.text = wnl[cwn]
                } else {
                    spe.setStatus(SharedPreferencesEditor.CURRENT_WORK_NUMBER, 0)
                    tv.text = wnl[0]
                }
            } else {
                tv.text = ""
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
        if (setupWorkNumberList(c).first.size >= 9) {
            dialog.listView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 1152)
        }
    }

    private var viewGroup: ViewGroup? = null

    fun showRegisterDutyDialog(c: Context, layoutInflater: LayoutInflater, listener: AlertDialogInterface, fragmentManager: FragmentManager, workNumber: CharSequence, description: Editable, startTime: String) {
        val builder = AlertDialog.Builder(c, R.style.Custom_DialogTheme)
        val dialogView = layoutInflater.inflate(R.layout.dialog_register_duty, viewGroup)
        val dialogText: TextView = dialogView.findViewById(R.id.dialog_text)
        val input: TextView = dialogView.findViewById(R.id.work_number_input)
        val editText: EditText = dialogView.findViewById(R.id.edit_text_description)
        dialogText.text = c.getString(R.string.dialog_current_work_started, startTime)
        input.text = workNumber
        input.setOnClickListener { showAlertDialog(c, input) }
        editText.text = description

        val endTime = stf.format(Date(System.currentTimeMillis()))

        builder.setView(dialogView)
            .setPositiveButton(R.string.register) { _, _ ->
                showContinueWorkDialog(c, layoutInflater, listener, fragmentManager, input.text.toString(), editText.text.toString(), startTime, endTime)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                listener.onAlertDialogRespond(null, input.text.toString(), editText.text.toString(), endTime)
            }.setOnCancelListener {
                listener.onAlertDialogRespond(null, input.text.toString(), editText.text.toString(), endTime)
            }.show()
    }

    private fun showContinueWorkDialog(c: Context, layoutInflater: LayoutInflater, listener: AlertDialogInterface, fragmentManager: FragmentManager, project: String, description: String, startTime: String, endTime: String) {
        val builder = AlertDialog.Builder(c)
        builder.setTitle(c.getString(R.string.finish_working_day))

        /*
        val container = FrameLayout(c)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            500
        )
        container.layoutParams = params

        val endTimePicker = TextView(c)
        endTimePicker.text = endTime
        endTimePicker.setOnClickListener {
            showTimePicker(c, fragmentManager, endTimePicker)
        }
        container.addView(endTimePicker)
        */

        val container = layoutInflater.inflate(R.layout.time_picker_layout, viewGroup)

        val startTimePicker: TextView = container.findViewById(R.id.time_from_text_view)
        startTimePicker.text = startTime
        startTimePicker.setOnClickListener {
            showTimePicker(c, fragmentManager, startTimePicker)
        }
        val endTimePicker: TextView = container.findViewById(R.id.time_to_text_view)
        endTimePicker.text = endTime
        endTimePicker.setOnClickListener {
            showTimePicker(c, fragmentManager, endTimePicker)
        }


        builder.setView(container)

        builder.setPositiveButton(c.getString(R.string.continue_working)) { _, _ ->
            listener.onAlertDialogRespond(true, project, description, endTimePicker.text.toString())
        }.setNegativeButton(c.getString(R.string.quit_the_day)) { _, _ ->
            listener.onAlertDialogRespond(false, project, description, endTimePicker.text.toString())
        }.setNeutralButton(c.getString(R.string.cancel)) { _, _ ->
            listener.onAlertDialogRespond(null, project, description, endTimePicker.text.toString())
        }


        dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }






    private fun showTimePicker(context: Context, fragmentManager: FragmentManager, textView: TextView) {

        val hours = textView.text.split(":")[0].toInt()
        val minutes = textView.text.split(":")[1].toInt()

        val builder = MaterialTimePicker.Builder()
        val picker = builder.setTimeFormat(TimeFormat.CLOCK_24H).setHour(hours).setMinute(minutes).build()
        //picker.setStyle(DialogFragment.STYLE_NORMAL, R.style.Custom_TimePickerDialogStyle) //NOT WORKING!!!!!
        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour.intToString()
            val minute = picker.minute.intToString()
            textView.text = context.getString(R.string.time_display_format, hour, minute)//"${picker.hour.intToString()}:${picker.hour.intToString()}"
        }
        picker.show(fragmentManager, picker.toString())
    }

    private fun Int.intToString(): String {
        return if (this < 10) {
            "0${this}"
        } else {
            "${this}"
        }
    }


}
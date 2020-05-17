package com.example.workhourregistrator

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.apache.poi.hssf.util.HSSFColor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.widget.Toast
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import java.io.FileInputStream
import android.content.Intent
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.CURRENT_MONTH
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.CURRENT_WORK_NUMBER
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.LAST_ROW
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.START_TIME_WORK
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.poi.hssf.usermodel.*
import org.apache.poi.hssf.util.CellRangeAddress
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.usermodel.CellStyle.ALIGN_CENTER
import org.apache.poi.ss.usermodel.CellStyle.VERTICAL_CENTER


class MainActivity : AppCompatActivity() {

    companion object {
        private const val MAIN_FOLDER = "WorkHourRegistrator"
        private const val EXCEL_FOLDER = "excel_files"
        val s = File.separator!!
        //val folder = Environment.getExternalStorageDirectory().toString() + s + MAIN_FOLDER + s + EXCEL_FOLDER
        //val path = Environment.getExternalStorageDirectory().toString() + s + MAIN_FOLDER + s + EXCEL_FOLDER + s
    }

    private val dp = DateProvider()
    private var spe = SharedPreferencesEditor()

    //private var listOfRows = arrayListOf<Row>()
    private var wb = HSSFWorkbook()
    private lateinit var sheet: Sheet

    private lateinit var row: Row

    private var finalWorkNumber = ""
    private var finalDescription = ""

    private val viewGroup: ViewGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        spe.setupSharedPreferencesEditor(this)

        //spe.setStatus(LAST_ROW, 0) //remove this later!!

        initializeExcelFile()

        //wb = HSSFWorkbook()

        //New Sheet
        //sheet = wb.createSheet("Work hours")

        //val row = sheet.createRow(0)
        //listOfRows.add(row)
        //spe.setStatus(LAST_ROW, spe.getStatus(LAST_ROW, 0)+1)


        //createTaskRow(wb, sheet, row)

        /*
        fillRow(wb, sheet, 1, "TEST", "tehtävän kuvaus", "8.00", "9.15", "1:15")
        fillRow(wb, sheet, 2, "TEST2", "tässä esimerkkinä vähän pidempi tehtävän kuvaus", "9.15", "10.45", "1:30")
        fillRow(wb, sheet, 3, "TEST3", "lounastauko", "10.45", "11.15", "0:30")
        fillRow(wb, sheet, 4, "TEST", "tehtävän kuvaus", "11.15", "16.00", "4:45")
        fillRow(wb, sheet, 5, "TUNNIT YHT.", "päivän työtunnit mukaanlukien lounastauko", "8.00", "16.00", "8:00")
        */

        //saveExcelFile(this,"test.xls")

        send_email.setOnClickListener {

            /*
            val row3 = sheet.createRow(13)

            fillRow(wb, sheet, 13, "TEST", "tehtävän kuvaus", "8.00", "9.15", "1:15")
            fillRow(wb, sheet, 14, "TEST2", "tässä esimerkkinä vähän pidempi tehtävän kuvaus", "9.15", "10.45", "1:30")
            fillRow(wb, sheet, 15, "TEST3", "lounastauko", "10.45", "11.15", "0:30")
            fillRow(wb, sheet, 16, "TEST", "tehtävän kuvaus", "11.15", "16.00", "4:45")
            fillRow(wb, sheet, 17, "TUNNIT YHT.", "päivän työtunnit mukaanlukien lounastauko", "8.00", "16.00", "8:00")


            createDateCells(wb, sheet, row3,"perjantai", "24.1.2020")
            saveExcelFile(this, "test.xls")
            */



            //createDateCells(wb, sheet, row,"keskiviikko", "22.1.2020")
            //saveExcelFile(this, "test.xls")
            //readExcelFile(this, "test.xls")

            //sendEmail(this, "test.xls")
            /*
            try {
                val sender = GMailSender("username@gmail.com", "password")
                sender.sendMail(
                    "This is Subject",
                    "This is Body",
                    "user@gmail.com",
                    "leo.partanen@kolumbus.fi"
                )
            } catch (e: Exception) {
                Log.e("SendMail", e.message, e)
            }
            */


        }

        start_work.setOnClickListener {
            row = sheet.createRow(spe.getStatus(LAST_ROW, 0))

            val workNumber =
                if (select_saved_number_checkbox.isChecked) {
                    if (spinner.selectedItem == null) {
                        ""
                    } else {
                        spinner.selectedItem.toString()
                    }
                } else {
                    work_number_input.text.toString()
                }
            if (workNumber != "") {
                workInProgress(true)
                spe.setStatus(START_TIME_WORK, dp.getCurrentTime())
                spe.setStatus(CURRENT_WORK_NUMBER, workNumber)
            }
            else {
                Toast.makeText(this, "field empty"//getString(R.string.field_empty)
                    , Toast.LENGTH_SHORT).show()
            }



        }
        change_work.setOnClickListener {
            openDialog(edit_text_description.text, false)
        }
        start_lunch.setOnClickListener {
            openDialog(edit_text_description.text, true)
        }
        quit_work.setOnClickListener {
            openDialog(edit_text_description.text, null)
        }
    }

    private fun workInProgress(inProgress: Boolean) {
        if (inProgress) {
            start_work.isEnabled = false
            action_buttons.visibility = View.VISIBLE
            work_number_select.visibility = View.GONE
        }
        else {
            start_work.isEnabled = true
            action_buttons.visibility = View.GONE
            work_number_select.visibility = View.VISIBLE
        }
    }

    private fun initializeExcelFile() {
        if (dp.getCurrentMonth() != spe.getStatus(CURRENT_MONTH, "")) {
            wb = HSSFWorkbook()
            sheet = wb.createSheet(dp.getCurrentMonth())

            spe.setStatus(LAST_ROW, 0)

            val row = sheet.createRow(0)
            //listOfRows.add(row)
            spe.setStatus(LAST_ROW, spe.getStatus(LAST_ROW, 0)+1)
            createTaskRow(wb, sheet, row)


            val fileName = dp.getCurrentMonthAndYear()
            saveExcelFile(this,"$fileName.xls")

        } else {
            /*
            val row = sheet.createRow(spe.getStatus(LAST_ROW, 0))
            //listOfRows.add(row)
            spe.setStatus(LAST_ROW, spe.getStatus(LAST_ROW, 0)+1)


            createTaskRow(wb, sheet, row)

            saveExcelFile(this,"test.xls")
            */
        }
    }

    private fun saveExcelFile(context: Context, fileName: String): Boolean {

        var success = false

        //New Workbook

        /*
        sheet1.setColumnWidth(0, 15 * 150)
        sheet1.setColumnWidth(1, 15 * 150)
        sheet1.setColumnWidth(2, 15 * 150)
        */


        // Create a path where we will place our List of objects on external storage
        val file = File(context.getExternalFilesDir(null), fileName)
        //val file = File(path, fileName)
        var os: FileOutputStream? = null

        try {
            os = FileOutputStream(file)
            wb.write(os)
            Log.d("FileUtils", "Writing file$file")
            success = true
        } catch (e: IOException) {
            Log.d("FileUtils", "Error writing $file", e)
        } catch (e: Exception) {
            Log.d("FileUtils", "Failed to save file", e)
        } finally {
            try {
                os?.close()
            } catch (ex: Exception) {
            }

        }
        return success
    }

    private fun createDateCells(wb: Workbook, sheet: Sheet, row: Row, weekDay: String, date: String) {
        sheet.setColumnWidth(0, 15 * 50)
        sheet.setColumnWidth(1, 15 * 50)

        //val row = listOfRows[0]
        val firstRow = row.rowNum

        //val lastRow = listOfRows[listOfRows.size-1].rowNum
        //spe.setStatus(LAST_ROW, lastRow)

        val lastRow = spe.getStatus(LAST_ROW, 0)-1

        //listOfRows.size

        val cs = wb.createCellStyle()
        cs.rotation = 90
        cs.alignment = ALIGN_CENTER
        cs.verticalAlignment = VERTICAL_CENTER

        val font = wb.createFont()
        font.boldweight = HSSFFont.BOLDWEIGHT_BOLD
        cs.setFont(font)

        var c = row.createCell(0)
        c!!.setCellValue(weekDay)
        c.cellStyle = cs

        c = row.createCell(1)
        c!!.setCellValue(date)
        c.cellStyle = cs

        sheet.addMergedRegion(CellRangeAddress(firstRow,lastRow,0,0))
        sheet.addMergedRegion(CellRangeAddress(firstRow,lastRow,1,1))

        spe.setStatus(LAST_ROW, spe.getStatus(LAST_ROW, 0)+1)
    }

    private fun createTaskRow(wb: Workbook, sheet: Sheet, row: Row) {
        //Cell style for header row
        val cs = wb.createCellStyle()
        cs.fillForegroundColor = HSSFColor.GREEN.index
        cs.fillPattern = HSSFCellStyle.SOLID_FOREGROUND
        cs.alignment = ALIGN_CENTER
        cs.verticalAlignment = VERTICAL_CENTER

        

        val font = wb.createFont()
        font.color = HSSFColor.WHITE.index
        font.boldweight = HSSFFont.BOLDWEIGHT_BOLD
        cs.setFont(font)
        cs.wrapText = true

        /*
        sheet.addMergedRegion(CellRangeAddress(0,0,0,1))
        var c = row.createCell(0)
        c.setCellValue("PVM")
        c.cellStyle = cs
        */

        var c = row.createCell(2)
        c.setCellValue("projekti")
        c.cellStyle = cs

        sheet.setColumnWidth(3, 15 * 500)
        c = row.createCell(3)
        c.setCellValue("kuvaus")
        c.cellStyle = cs

        c = row.createCell(4)
        c.setCellValue("alkaen")
        c.cellStyle = cs

        c = row.createCell(5)
        c.setCellValue("päättyen")
        c.cellStyle = cs

        c = row.createCell(6)
        c.setCellValue("h/min")
        c.cellStyle = cs
    }

    private fun fillRow(wb: Workbook, sheet: Sheet, project: String, description: String, start: String, end: String, time: String) {
        val cs = wb.createCellStyle()
        cs.alignment = ALIGN_CENTER
        cs.verticalAlignment = VERTICAL_CENTER
        cs.wrapText = true

        val row = sheet.createRow(spe.getStatus(LAST_ROW, 0))
        //listOfRows.add(row)
        spe.setStatus(LAST_ROW, spe.getStatus(LAST_ROW, 0)+1)

        var c = row.createCell(2)
        c.setCellValue(project)
        c.cellStyle = cs

        c = row.createCell(3)
        c.setCellValue(description)
        c.cellStyle = cs

        c = row.createCell(4)
        c.setCellValue(start)
        c.cellStyle = cs

        c = row.createCell(5)
        c.setCellValue(end)
        c.cellStyle = cs

        c = row.createCell(6)
        c.setCellValue(time)
        c.cellStyle = cs
    }

    private fun readExcelFile(context: Context, filename: String) {

        try {
            // Creating Input Stream
            //val file = File(context.getExternalFilesDir(null), filename)
            val file = File(context.getExternalFilesDir(null), filename)
            val myInput = FileInputStream(file)

            // Create a POIFSFileSystem object
            val myFileSystem = POIFSFileSystem(myInput)

            // Create a workbook using the File System
            val myWorkBook = HSSFWorkbook(myFileSystem)

            // Get the first sheet from workbook
            val mySheet = myWorkBook.getSheetAt(0)

            /** We now need something to iterate through the cells. */
            val rowIter = mySheet.rowIterator()

            while (rowIter.hasNext()) {
                val myRow = rowIter.next() as HSSFRow
                val cellIter = myRow.cellIterator()
                while (cellIter.hasNext()) {
                    val myCell = cellIter.next() as HSSFCell
                    Log.d("FragmentActivity.TAG", "Cell Value: $myCell")
                    Toast.makeText(context, "cell Value: $myCell", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return
    }

    private fun sendEmail(context: Context, fileName: String) {
        val file = File(context.getExternalFilesDir(null), fileName)
        //val fileLocation = File(Environment.getExternalStorageDirectory().absolutePath, fileName)
        //val path = Uri.fromFile(file)

        val path = FileProvider.getUriForFile(context, "com.example.workhourregistrator.fileprovider", file);


        val emailIntent = Intent(Intent.ACTION_SEND)
        // set the type to 'email'
        emailIntent.type = "vnd.android.cursor.dir/email"
        val to = arrayOf("leo.partanen@kolumbus.fi")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        // the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, path)
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }




    /*
    private fun openDialog(id: Int) {
        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, viewGroup)
        val dialogText: TextView = dialogView.findViewById(R.id.dialog_text)
        val dialogEditText: EditText = dialogView.findViewById(R.id.dialog_edit_text)
        dialogText.text = this.getText(id)
        builder.setView(dialogView)
            .setPositiveButton(R.string.ok) { _, _ ->

            }
            .setNegativeButton(R.string.cancel) { _, _ ->
            }.show()
    }
    */

    private fun openDialog(description: Editable, addLunch: Boolean?) {
        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, viewGroup)
        val dialogText: EditText = dialogView.findViewById(R.id.dialog_text)
        val dialogEditText: EditText = dialogView.findViewById(R.id.dialog_edit_text)
        dialogText.setText(spe.getStatus(CURRENT_WORK_NUMBER, ""))
        dialogEditText.text = description
        builder.setView(dialogView)
            .setPositiveButton(R.string.ok) { _, _ ->
                if (dialogText.text.isNotEmpty() && dialogEditText.text.isNotEmpty()) {
                    spe.setStatus(CURRENT_WORK_NUMBER, dialogText.text.toString())
                    register(dialogText.text.toString(), dialogEditText.text.toString(), false)
                    when {
                        addLunch == null -> {
                            workInProgress(false)
                            createDateCells(wb, sheet, row, dp.getCurrentWeekday(), dp.getCurrentDate())
                            //saveExcelFile(this, "test.xls")
                            val fileName = dp.getCurrentMonthAndYear()
                            saveExcelFile(this,"$fileName.xls")
                        }
                        addLunch -> register("LOU", "lounastauko", true)
                    }
                } else {
                    openDialog(description, addLunch)
                }
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
            }.show()
    }

    private fun register(workNumber: String, description: String, addLunch: Boolean) {
        val startTime = spe.getStatus(START_TIME_WORK, dp.getCurrentTime())
        val endTime = when {
            addLunch -> dp.getEndTimeFromStartTime(startTime)
            startTime.toFloat() > dp.getCurrentTime().toFloat() -> startTime
            else -> dp.getCurrentTime()
        }
        val totalTime = dp.getTimeDifference(startTime, endTime)
        fillRow(wb, sheet, workNumber, description, startTime, endTime, totalTime)
        spe.setStatus(START_TIME_WORK, endTime)
    }

}

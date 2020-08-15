package com.example.workhourregistrator

import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.apache.poi.hssf.util.HSSFColor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import java.io.FileInputStream
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.ViewGroup
import android.widget.Toast
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.CURRENT_MONTH_AND_YEAR
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.CURRENT_WEEK
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.LAST_COLUMN
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.LAST_ROW
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.apache.poi.hssf.usermodel.*
import org.apache.poi.hssf.util.CellRangeAddress
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.usermodel.CellStyle.ALIGN_CENTER
import org.apache.poi.ss.usermodel.CellStyle.VERTICAL_CENTER
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.ss.usermodel.CellStyle
import java.util.*
import org.apache.poi.ss.usermodel.Workbook





class MainActivity : AppCompatActivity() {

    companion object {
        private const val MAIN_FOLDER = "WorkHourRegistrator"
        private const val EXCEL_FOLDER = "excel_files"
        val s = File.separator!!
        //val folder = Environment.getExternalStorageDirectory().toString() + s + MAIN_FOLDER + s + EXCEL_FOLDER
        //val path = Environment.getExternalStorageDirectory().toString() + s + MAIN_FOLDER + s + EXCEL_FOLDER + s
        private const val ACTION = "Action"
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
        //spe.setupSharedPreferencesEditor(this)
        setupFragment(MainFragment(), "tag")

        //spe.setStatus(LAST_ROW, 0) //remove this later!!

        //initializeExcelFile()

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

        /*-------------------main fragment
        send_email.setOnClickListener {


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
            row = if (sheet.getRow(spe.getStatus(LAST_ROW, 0)) == null) {
                sheet.createRow(spe.getStatus(LAST_ROW, 0))
            } else {
                sheet.getRow(spe.getStatus(LAST_ROW, 0))
            }
            row = sheet.createRow(spe.getStatus(LAST_ROW, 0))
            workInProgress(true)
            spe.setStatus(START_TIME_WORK, dp.getCurrentTime())
            /*
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
            */

        }

        change_work.setOnClickListener {
            openDialog(work_number_input.text, edit_text_description.text, false)
        }
        start_lunch.setOnClickListener {
            openDialog(work_number_input.text, edit_text_description.text, true)
        }
        quit_work.setOnClickListener {
            openDialog(work_number_input.text, edit_text_description.text, null)
            spe.setStatus(CURRENT_WEEK, 32)
        }
        main fragment */

        //-------------------------------------------


        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    setupFragment(MainFragment(), "tag")
                }
                R.id.nav_start -> {
                    setupFragment(StartDutyFragment(), "tag")
                }
                R.id.nav_register -> {
                    setupFragment(RegisterDutyFragment(), "tag")
                }
                R.id.nav_manage -> {

                }
                R.id.nav_share -> {

                }
                R.id.nav_send -> {

                }
            }
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }
        //nav_view.setNavigationItemSelectedListener(this)

        //-------------------------------------------
    }

    //-------------------------------------------

    private fun setupFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, tag).commit()
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    //-------------------------------------------

    /*main fragement
    private fun workInProgress(inProgress: Boolean) {
        if (inProgress) {
            start_work.isEnabled = false
            action_buttons.visibility = View.VISIBLE
            action_button_layout.visibility = View.GONE
        }
        else {
            start_work.isEnabled = true
            action_buttons.visibility = View.GONE
            action_button_layout.visibility = View.VISIBLE
        }
    }
    main fragment*/

    private fun initializeExcelFile() {
        val currentM = dp.getCurrentMonthAndYear()
        val currentW = dp.getCurrentWeekNumber()
        Log.d("tää", "initialize")
        when {
            currentM != spe.getStatus(CURRENT_MONTH_AND_YEAR, "") -> {
                Log.d("tää", "eka")
                wb = HSSFWorkbook()
                sheet = wb.createSheet(currentM)

                spe.setStatus(CURRENT_MONTH_AND_YEAR, currentM)
                spe.setStatus(CURRENT_WEEK, currentW)

                spe.setStatus(LAST_ROW, 0)
                spe.setStatus(LAST_COLUMN, 0)

                row = if (sheet.getRow(spe.getStatus(LAST_ROW, 0)) == null) {
                    sheet.createRow(spe.getStatus(LAST_ROW, 0))
                } else {
                    sheet.getRow(spe.getStatus(LAST_ROW, 0))
                }

                //listOfRows.add(row)
                spe.setStatus(LAST_ROW, spe.getStatus(LAST_ROW, 0)+1)
                createTaskRow(wb, sheet, row)

                saveExcelFile(this,"$currentM.xls")

            }
            /*currentW != spe.getStatus(CURRENT_WEEK, 0) -> {
                Log.d("tää", "toka")
                readExcelFile(this, "$currentM.xls")
                spe.setStatus(CURRENT_WEEK, currentW)
                spe.setStatus(LAST_ROW, 0)
                spe.setStatus(LAST_COLUMN, spe.getStatus(LAST_COLUMN, 0)+7)
                val row = if (sheet.getRow(spe.getStatus(LAST_ROW, 0)) == null) {
                    Toast.makeText(this, "eka", Toast.LENGTH_SHORT).show()
                    sheet.createRow(spe.getStatus(LAST_ROW, 0))
                } else {
                    Toast.makeText(this, "toka", Toast.LENGTH_SHORT).show()
                    sheet.getRow(spe.getStatus(LAST_ROW, 0))
                }
                createTaskRow(wb, sheet, row)

                /*
                val row = sheet.createRow(spe.getStatus(LAST_ROW, 0))
                //listOfRows.add(row)
                spe.setStatus(LAST_ROW, spe.getStatus(LAST_ROW, 0)+1)


                createTaskRow(wb, sheet, row)

                saveExcelFile(this,"test.xls")
                */
            }*/
            else -> {
                Log.d("tää", "kolmas")
                readExcelFile(this, "$currentM.xls")
            }
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
            Toast.makeText(context, getText(R.string.excel_file_saved_successfully), Toast.LENGTH_SHORT).show()
            Snackbar.make(window.decorView, getText(R.string.excel_file_saved_successfully), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()
        } catch (e: IOException) {
            Log.d("FileUtils", "Error writing $file", e)
            Toast.makeText(context, context.getText(R.string.excel_file_saved_successfully), Toast.LENGTH_SHORT).show()
            Snackbar.make((context as Activity).window.decorView, context.getText(R.string.excel_file_saved_successfully), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()
            //Snackbar.make(window.decorView, getText(R.string.excel_file_saved_successfully), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()
        } catch (e: Exception) {
            Log.d("FileUtils", "Failed to save file", e)
            Toast.makeText(context, context.getText(R.string.excel_file_saved_successfully), Toast.LENGTH_SHORT).show()
            Snackbar.make((context as Activity).window.decorView, context.getText(R.string.excel_file_saved_successfully), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()
            //Snackbar.make(window.decorView, getText(R.string.excel_file_saved_successfully), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()
        } finally {
            try {
                os?.close()
            } catch (e: Exception) {
                Log.d("FileUtils", "Failed to close output stream", e)
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

        val column = spe.getStatus(LAST_COLUMN, 0)

        var c = row.createCell(column + 0)
        c!!.setCellValue(weekDay)
        c.cellStyle = cs

        c = row.createCell(column + 1)
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

        val column = spe.getStatus(LAST_COLUMN, 0)

        sheet.setColumnWidth(0, 15 * 200)
        var c = row.createCell(column + 0)
        c.setCellValue("viikonpäivä")
        c.cellStyle = cs

        sheet.setColumnWidth(1, 15 * 200)
        c = row.createCell(column + 1)
        c.setCellValue("päivämäärä")
        c.cellStyle = cs

        c = row.createCell(column + 2)
        c.setCellValue("projekti")
        c.cellStyle = cs

        sheet.setColumnWidth(3, 15 * 500)
        c = row.createCell(column + 3)
        c.setCellValue("kuvaus")
        c.cellStyle = cs

        c = row.createCell(column + 4)
        c.setCellValue("alkaen")
        c.cellStyle = cs

        c = row.createCell(column + 5)
        c.setCellValue("päättyen")
        c.cellStyle = cs

        c = row.createCell(column + 6)
        c.setCellValue("h/min")
        c.cellStyle = cs
    }

    private fun fillRow(wb: Workbook, sheet: Sheet, project: String, description: String, start: String, end: String, time: String) {
        val cs = wb.createCellStyle()
        cs.alignment = ALIGN_CENTER
        cs.verticalAlignment = VERTICAL_CENTER
        cs.wrapText = true

        val row = if (sheet.getRow(spe.getStatus(LAST_ROW, 0)) == null) {
            sheet.createRow(spe.getStatus(LAST_ROW, 0))
        } else {
            sheet.getRow(spe.getStatus(LAST_ROW, 0))
        }
        //listOfRows.add(row)
        spe.setStatus(LAST_ROW, spe.getStatus(LAST_ROW, 0)+1)

        val column = spe.getStatus(LAST_COLUMN, 0)

        var c = row.createCell(column + 2)
        c.setCellValue(project)
        c.cellStyle = cs

        c = row.createCell(column + 3)
        c.setCellValue(description)
        c.cellStyle = cs

        c = row.createCell(column + 4)
        c.setCellValue(start)
        c.cellStyle = cs

        c = row.createCell(column + 5)
        c.setCellValue(end)
        c.cellStyle = cs

        c = row.createCell(column + 6)
        c.setCellValue(time)
        c.cellStyle = cs
    }

    private fun readExcelFile(context: Context, filename: String) {
        try {
            Log.d("tää", "try")
            val file = File(context.getExternalFilesDir(null), filename)
            val myInput = FileInputStream(file)
            val myFileSystem = POIFSFileSystem(myInput)
            wb = HSSFWorkbook(myFileSystem)
            sheet = wb.getSheetAt(0)
            Log.d("tää", "wb and sheet are initialized here !!?")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("tää", "wb and sheet are NOT initialized here !!?")
        }
    }

    /*
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
    */

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

    /* main fragement

    private fun openDialog(workNumber: Editable, description: Editable, addLunch: Boolean?) {
        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, viewGroup)
        val dialogText: EditText = dialogView.findViewById(R.id.dialog_text)
        val dialogEditText: EditText = dialogView.findViewById(R.id.dialog_edit_text)
        dialogText.text = workNumber
        dialogEditText.text = description
        builder.setView(dialogView)
            .setPositiveButton(R.string.ok) { _, _ ->
                if (dialogText.text.isNotEmpty() && dialogEditText.text.isNotEmpty()) {
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
                    openDialog(workNumber, description, addLunch)
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

    main fragment */


    ////----------------NEW------EXCEL------COMMANDS---------------->>>>

    private fun fillRowNEW(wb: Workbook, row: Row, weekday: String, date: Date, project: String, description: String, start: String, end: String, time: String) {
        val cs = wb.createCellStyle()
        cs.alignment = ALIGN_CENTER
        cs.verticalAlignment = VERTICAL_CENTER
        cs.wrapText = true

        var cell = row.createCell(0)
        cell.setCellValue(weekday)
        cell.cellStyle = cs


        //-----------------------------------------------------

        val cellStyle = wb.createCellStyle()
        val createHelper = wb.creationHelper
        cellStyle.dataFormat = createHelper.createDataFormat().getFormat("m/d/yy")
        cellStyle.alignment = ALIGN_CENTER
        cellStyle.verticalAlignment = VERTICAL_CENTER
        cellStyle.wrapText = true
        cell = row.createCell(1)
        cell.setCellValue(date)
        cell.cellStyle = cellStyle

        //-----------------------------------------------------

        cell = row.createCell(2)
        cell.setCellValue(project)
        cell.cellStyle = cs

        cell = row.createCell(3)
        cell.setCellValue(description)
        cell.cellStyle = cs

        cell = row.createCell(4)
        cell.setCellValue(start)
        cell.cellStyle = cs

        cell = row.createCell(5)
        cell.setCellValue(end)
        cell.cellStyle = cs

        cell = row.createCell(6)
        cell.setCellValue(time)
        cell.cellStyle = cs
    }

    fun writeIntoExcelFile(c: Context, weekday: String, date: Date, project: String, description: String, start: String, end: String, time: String) {
        val triple = initializeExcelFileNEW(c)
        wb = triple.first
        sheet = triple.second
        val filename = triple.third
        val row = sheet.createRow(spe.getStatus(LAST_ROW, 0))
        spe.setStatus(LAST_ROW, spe.getStatus(LAST_ROW, 0)+1)
        fillRowNEW(wb, row, weekday, date, project, description, start, end, time)



        //sortSheet(wb, sheet)
        saveExcelFile(c, filename)
    }

    fun initializeExcelFileNEW(context: Context): Triple<HSSFWorkbook, Sheet, String> {
        spe.setupSharedPreferencesEditor(context)
        val currentM = dp.getCurrentMonthAndYear()
        when {
            currentM != spe.getStatus(CURRENT_MONTH_AND_YEAR, "") -> {
                wb = HSSFWorkbook()
                sheet = wb.createSheet(currentM)

                spe.setStatus(CURRENT_MONTH_AND_YEAR, currentM)
                spe.setStatus(LAST_ROW, 0)

                row = sheet.createRow(spe.getStatus(LAST_ROW, 0))

                spe.setStatus(LAST_ROW, spe.getStatus(LAST_ROW, 0)+1)

                createTaskRow(wb, sheet, row)

                val cs = wb.createCellStyle()
                cs.alignment = ALIGN_CENTER
                cs.verticalAlignment = VERTICAL_CENTER
                cs.wrapText = true

                val filename = "$currentM.xls"
                saveExcelFile(context,filename)
                return Triple(wb, sheet, filename)
            }
            else -> {
                val filename = "$currentM.xls"
                return readExcelFileNEW(context, filename)
            }
        }
    }

    fun readExcelFileNEW(context: Context, filename: String): Triple<HSSFWorkbook, Sheet, String> {
        try {
            val file = File(context.getExternalFilesDir(null), filename)
            val myInput = FileInputStream(file)
            val myFileSystem = POIFSFileSystem(myInput)
            wb = HSSFWorkbook(myFileSystem)
            sheet = wb.getSheetAt(0)
            return Triple(wb, sheet, filename)
        } catch (e: Exception) {
            e.printStackTrace()
            return Triple(wb, sheet, filename)
        }
    }

    //--------------------->>>>


    /*
    fun sortSheet(workbook: Workbook, sheet: Sheet) {
        //copy all rows to temp
        val rows = Lists.newArrayList(sheet.rowIterator())
        //sort rows in the temp
        rows.sort(Comparator.comparing { cells -> cells.getCell(0).getStringCellValue() })
        //remove all rows from sheet
        removeAllRows(sheet)
        //create new rows with values of sorted rows from temp
        for (i in rows.indices) {
            val newRow = sheet.createRow(i)
            val sourceRow = rows.get(i)
            // Loop through source columns to add to new row
            for (j in 0 until sourceRow.getLastCellNum()) {
                // Grab a copy of the old/new cell
                val oldCell = sourceRow.getCell(j)
                var newCell = newRow.createCell(j)

                // If the old cell is null jump to next cell
                if (oldCell == null) {
                    newCell = null
                    continue
                }

                // Copy style from old cell and apply to new cell
                val newCellStyle = workbook.createCellStyle()
                newCellStyle.cloneStyleFrom(oldCell!!.getCellStyle())
                newCell!!.cellStyle = newCellStyle

                // If there is a cell comment, copy
                if (oldCell!!.getCellComment() != null) {
                    newCell.cellComment = oldCell!!.getCellComment()
                }

                // If there is a cell hyperlink, copy
                if (oldCell!!.getHyperlink() != null) {
                    newCell.hyperlink = oldCell!!.getHyperlink()
                }

                // Set the cell data type
                newCell.cellType = oldCell!!.getCellType()

                // Set the cell data value
                when (oldCell!!.getCellType()) {
                    BLANK -> newCell.setCellValue(oldCell!!.getStringCellValue())
                    BOOLEAN -> newCell.setCellValue(oldCell!!.getBooleanCellValue())
                    ERROR -> newCell.setCellErrorValue(oldCell!!.getErrorCellValue())
                    FORMULA -> newCell.cellFormula = oldCell!!.getCellFormula()
                    NUMERIC -> newCell.setCellValue(oldCell!!.getNumericCellValue())
                    STRING -> newCell.setCellValue(oldCell!!.getRichStringCellValue())
                }
            }

            // If there are are any merged regions in the source row, copy to new row
            for (j in 0 until sheet.numMergedRegions) {
                val cellRangeAddress = sheet.getMergedRegion(j)
                if (cellRangeAddress.firstRow == sourceRow.getRowNum()) {
                    val newCellRangeAddress = CellRangeAddress(
                        newRow.rowNum,
                        newRow.rowNum + (cellRangeAddress.lastRow - cellRangeAddress.firstRow),
                        cellRangeAddress.firstColumn,
                        cellRangeAddress.lastColumn
                    )
                    sheet.addMergedRegion(newCellRangeAddress)
                }
            }
        }

    }

    private fun removeAllRows(sheet: Sheet) {
        for (i in 0 until sheet.lastRowNum) {
            sheet.removeRow(sheet.getRow(i))
        }
    }
    */

    /*
    private fun sortSheet(sheet: Sheet, column: Int, rowStart: Int) {
        var sorting = true
        val lastRow = sheet.lastRowNum
        while (sorting) {
            sorting = false
            var n = 0
            for (row in sheet) {

                Log.d("tää", "$n")
                n++
                if (row.rowNum < rowStart) continue
                if (lastRow == row.rowNum) break
                val nextRow = sheet.getRow(row.rowNum + 1) ?: continue
                val firstValue = row.getCell(column).dateCellValue
                val secondValue = nextRow.getCell(column).dateCellValue
                if (secondValue.before(firstValue)) {
                    sheet.shiftRows(nextRow.rowNum, nextRow.rowNum, -1)
                    sheet.shiftRows(row.rowNum, row.rowNum, 1)
                    sorting = true
                }
            }
        }
    }
    */
}

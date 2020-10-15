package com.example.workhourregistrator

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.apache.poi.hssf.util.HSSFColor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import java.io.FileInputStream
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.view.LayoutInflater
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.CURRENT_WEEK
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.EMAIL_ADDRESS
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.LAST_COLUMN
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.LAST_ROW
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.MONTH_AND_YEAR
/*
import com.microsoft.onedrivesdk.picker.IPicker
import com.microsoft.onedrivesdk.picker.IPickerResult
import com.microsoft.onedrivesdk.picker.LinkType
import com.microsoft.onedrivesdk.picker.Picker
import com.microsoft.onedrivesdk.saver.ISaver
import com.microsoft.onedrivesdk.saver.Saver
import com.microsoft.onedrivesdk.saver.SaverException
import com.onedrive.sdk.authentication.ADALAuthenticator
import com.onedrive.sdk.authentication.MSAAuthenticator
import com.onedrive.sdk.concurrency.ICallback
import com.onedrive.sdk.core.ClientException
import com.onedrive.sdk.core.DefaultClientConfig
import com.onedrive.sdk.core.IClientConfig
import com.onedrive.sdk.extensions.Drive
import com.onedrive.sdk.extensions.IOneDriveClient
import com.onedrive.sdk.extensions.OneDriveClient
*/
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_start_duty.*
import org.apache.poi.hssf.usermodel.*
import org.apache.poi.hssf.util.CellRangeAddress
import org.apache.poi.ss.format.CellFormatType
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.usermodel.CellStyle.ALIGN_CENTER
import org.apache.poi.ss.usermodel.CellStyle.VERTICAL_CENTER
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.ss.usermodel.CellStyle
import java.util.*
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellReference
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {

    companion object {
        private const val MAIN_FOLDER = "WorkHourRegistrator"
        private const val EXCEL_FOLDER = "excel_files"
        val s = File.separator!!
        //val folder = Environment.getExternalStorageDirectory().toString() + s + MAIN_FOLDER + s + EXCEL_FOLDER
        //val path = Environment.getExternalStorageDirectory().toString() + s + MAIN_FOLDER + s + EXCEL_FOLDER + s
        const val ACTION = "Action"
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

    private val swf = SimpleDateFormat("EE", Locale.getDefault())


    //-------------------------------------------------------------------------------------------------------------

    //var oneDriveClient: IOneDriveClient? = null

    //-------------------------------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //spe.setupSharedPreferencesEditor(this)
        setupFragment(HomeFragment(), "tag")

        //-------------------------------------------------------------------------------------------------------------

        /*
        val msaAuthenticator: MSAAuthenticator = object: MSAAuthenticator() {
            override fun getClientId(): String {
                return "" //"<msa-client-id>";
            }

            override fun getScopes(): Array<String> {
                return arrayOf("onedrive.appfolder")
            }
        }

        val adalAuthenticator: ADALAuthenticator = object: ADALAuthenticator() {
            override fun getClientId(): String {
                return getString(R.string.app_id) //"<adal-client-id>";
            }

            override fun getRedirectUrl(): String {
                return "https://localhost";
            }
        }

        val oneDriveConfig: IClientConfig = DefaultClientConfig.createWithAuthenticators(
            msaAuthenticator,
            adalAuthenticator)

        val callback: ICallback<IOneDriveClient> = object: ICallback<IOneDriveClient> {
            override fun success(result: IOneDriveClient?) {

            }

            override fun failure(ex: ClientException?) {

            }
        }


        OneDriveClient.Builder()
            .fromConfig(DefaultClientConfig.createWithAuthenticator(msaAuthenticator))
            .loginAndBuildClient(this, object: ICallback<IOneDriveClient> {
                override fun success(result: IOneDriveClient?) {
                    oneDriveClient = result
                }

                override fun failure(ex: ClientException?) {
                    oneDriveClient = null
                }
            });


        //val oneDriveClient: IOneDriveClient = OneDriveClient.Builder().fromConfig(oneDriveConfig).loginAndBuildClient(this, callback)

        oneDriveClient!!
            .drive
            .buildRequest()
            .get(object: ICallback<Drive> {
                override fun success(result: Drive) {
                    Log.d("tää", "success")
                }
                override fun failure(ex: ClientException?) {
                    Log.d("tää", "failure")
                }
            });

        //-------------------------------------------------------------------------------------------------------------*/

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
                    setupFragment(HomeFragment(), "tag")
                }
                R.id.nav_start -> {
                    setupFragment(StartDutyFragment(), "tag")
                }
                R.id.nav_register_1 -> {
                    setupFragment(RegisterDutyFragment(), "tag")
                }
                R.id.nav_register_2 -> {
                    setupFragment(RegisterStreakFragment(), "tag")
                }
                R.id.nav_share -> {
                    val currentM = dp.getCurrentMonthAndYear()
                    val filename = "$currentM.xls"
                    sendEmail(this, filename)

                }
            }
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }
        //nav_view.setNavigationItemSelectedListener(this)

        //-------------------------------------------


        /*
        test_button.setOnClickListener {
            mPicker = Picker.createPicker(ONEDRIVE_APP_ID)
            mPicker.startPicking(this, LinkType.DownloadLink) //LinkType.DownloadLink
        }

        test_button2.setOnClickListener {
            // create example file to save to OneDrive
            val currentM = dp.getCurrentMonthAndYear()
            val fileName = "$currentM.xls"
            //val f: File = File(this.filesDir, filename)

            val file = File(this.getExternalFilesDir(null), fileName)

            // create and launch the saver
            mSaver = Saver.createSaver(ONEDRIVE_APP_ID)
            mSaver.startSaving(this, fileName, Uri.fromFile(file))
        }
        */
    }

    /*
    private lateinit var mPicker: IPicker
    private val ONEDRIVE_APP_ID = "96141ee0-fb7f-4b27-89c2-ec53f3dd469d"

    // Within the activity's class definition
    private lateinit var mSaver: ISaver


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("tää", "$requestCode")
        when (requestCode) {
            61680 -> {
                val result: IPickerResult? = mPicker.getPickerResult(requestCode, resultCode, data)
                if (result != null) {
                    Log.d("tää", "Link to file '" + result.name + ": " + result.link)
                    return
                }
                super.onActivityResult(requestCode, resultCode, data)
            }
            61937 -> {
                // check that the file was successfully saved to OneDrive
                try {
                    mSaver.handleSave(requestCode, resultCode, data)
                } catch (e: SaverException) {
                    // Log error information
                    Log.e("tää", e.errorType.toString()); // Provides one of the SaverError enum
                    //Log.e("tää", e.debugErrorInfo); // Detailed debug error message
                }
            }
            2 -> {

            }
        }
    }
    */



    //-------------------------------------------

    private fun setupFragment(fragment: androidx.fragment.app.Fragment, tag: String) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, tag).commit()
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

    /*
    private fun initializeExcelFile() {
        val currentM = dp.getCurrentMonthAndYear()
        val currentW = dp.getCurrentWeekNumber()
        when {
            currentM != spe.getStatus(CURRENT_MONTH_AND_YEAR, "") -> {
                wb = HSSFWorkbook()
                sheet = wb.createSheet(currentM)

                spe.setStatus(CURRENT_MONTH_AND_YEAR, currentM)
                spe.setStatus(CURRENT_WEEK, currentW)

                spe.setStatus(LAST_ROW, 9)
                spe.setStatus(LAST_COLUMN, 7)

                row = if (sheet.getRow(spe.getStatus(LAST_ROW, 9)) == null) {
                    sheet.createRow(spe.getStatus(LAST_ROW, 9))
                } else {
                    sheet.getRow(spe.getStatus(LAST_ROW, 9))
                }

                //listOfRows.add(row)
                spe.setStatus(LAST_ROW, spe.getStatus(LAST_ROW, 9)+1)
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
                readExcelFile(this, "$currentM.xls")
            }
        }
    }
    */

    private fun saveExcelFile(context: Context, workbook: Workbook, fileName: String): Boolean {
        var success = false

        val file = File(context.getExternalFilesDir(null), fileName)
        var os: FileOutputStream? = null

        try {
            os = FileOutputStream(file)
            workbook.write(os)
            Log.d("FileUtils", "Writing file$file")
            //Toast.makeText(context, getText(R.string.excel_file_saved_successfully), Toast.LENGTH_SHORT).show()
            Snackbar.make((context as Activity).window.decorView, getText(R.string.excel_file_saved), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()

        } catch (e: IOException) {
            Log.d("FileUtils", "Error writing $file", e)
            //Toast.makeText(context, context.getText(R.string.excel_file_writing_error), Toast.LENGTH_SHORT).show()
            Snackbar.make((context as Activity).window.decorView, context.getText(R.string.excel_file_saved), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()
            //Snackbar.make(window.decorView, getText(R.string.excel_file_saved_successfully), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()
        } catch (e: Exception) {
            Log.d("FileUtils", "Failed to save file", e)
            //Toast.makeText(context, context.getText(R.string.excel_file_writing_error), Toast.LENGTH_SHORT).show()
            Snackbar.make((context as Activity).window.decorView, context.getText(R.string.excel_file_saved), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()
            //Snackbar.make(window.decorView, getText(R.string.excel_file_saved_successfully), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()

        } finally {
            try {
                os?.close()
                success = true

            } catch (e: Exception) {
                Log.d("FileUtils", "Failed to close output stream", e)
            }
        }
        return success
    }

    /*
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
    */

    private fun createTaskRow(wb: Workbook, sheet: Sheet, row: Row) {
        //Cell style for header row
        /*
        val cs = wb.createCellStyle() //-------------------------------------------------------------------!!!
        cs.fillForegroundColor = HSSFColor.GREEN.index
        cs.fillPattern = HSSFCellStyle.SOLID_FOREGROUND
        cs.alignment = ALIGN_CENTER
        cs.verticalAlignment = VERTICAL_CENTER
        val font = wb.createFont()
        font.color = HSSFColor.WHITE.index
        font.boldweight = HSSFFont.BOLDWEIGHT_BOLD
        cs.setFont(font)
        cs.wrapText = true
        */
        cellStyleTask = wb.createCellStyle()
        cellStyleTask.setupCellStyleTask(wb)

        /*
        sheet.addMergedRegion(CellRangeAddress(0,0,0,1))
        var c = row.createCell(0)
        c.setCellValue("PVM")
        c.cellStyle = cs
        */

        //val column = spe.getStatus(LAST_COLUMN, 0)

        sheet.setColumnWidth(0, 15 * 200)
        var c = row.createCell( 0)
        c.setCellValue("viikonpäivä")
        c.cellStyle = cellStyleTask

        sheet.setColumnWidth(1, 15 * 200)
        c = row.createCell(1)
        c.setCellValue("päivämäärä")
        c.cellStyle = cellStyleTask

        c = row.createCell(2)
        c.setCellValue("projekti")
        c.cellStyle = cellStyleTask

        sheet.setColumnWidth(3, 15 * 500)
        c = row.createCell( 3)
        c.setCellValue("kuvaus")
        c.cellStyle = cellStyleTask

        c = row.createCell(4)
        c.setCellValue("alkaen")
        c.cellStyle = cellStyleTask

        c = row.createCell( 5)
        c.setCellValue("päättyen")
        c.cellStyle = cellStyleTask

        c = row.createCell(6)
        c.setCellValue("h/min")
        c.cellStyle = cellStyleTask
    }

    /*
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
    */

    /*
    private fun readExcelFile(context: Context, filename: String) {
        try {
            val file = File(context.getExternalFilesDir(null), filename)
            val myInput = FileInputStream(file)
            val myFileSystem = POIFSFileSystem(myInput)
            wb = HSSFWorkbook(myFileSystem)
            sheet = wb.getSheetAt(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    */

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

    fun sendEmail(context: Context, fileName: String) {
        val file = File(context.getExternalFilesDir(null), fileName)
        //val fileLocation = File(Environment.getExternalStorageDirectory().absolutePath, fileName)
        //val path = Uri.fromFile(file)

        val path = FileProvider.getUriForFile(context, "com.example.workhourregistrator.fileprovider", file);


        val emailIntent = Intent(Intent.ACTION_SEND)
        // set the type to 'email'
        emailIntent.type = "vnd.android.cursor.dir/email"

        val to = arrayOf("leo.partanen@kolumbus.fi") //get email from spe !!! //OPTIONAL

        if (to.isNotEmpty()) {
            //emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
            emailIntent.putExtra(Intent.EXTRA_STREAM, path)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "")
            (context as Activity).startActivity(Intent.createChooser(emailIntent, "Share file..."))
        }
        else {

        }

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

    private lateinit var cs: CellStyle
    private lateinit var cellStyle: CellStyle
    private lateinit var cellStyleTask: CellStyle

    private fun CellStyle.setupCellStyle() {
        alignment = ALIGN_CENTER
        verticalAlignment = VERTICAL_CENTER
        wrapText = true
    }

    private fun CellStyle.setupCellStyle(wb: Workbook) {
        val createHelper = wb.creationHelper
        dataFormat = createHelper.createDataFormat().getFormat("m/d/yy")
        alignment = ALIGN_CENTER
        verticalAlignment = VERTICAL_CENTER
        wrapText = true
    }

    private fun CellStyle.setupCellStyleTask(wb: Workbook) {
        fillForegroundColor = HSSFColor.GREEN.index
        fillPattern = HSSFCellStyle.SOLID_FOREGROUND
        alignment = ALIGN_CENTER
        verticalAlignment = VERTICAL_CENTER
        val font = wb.createFont()
        font.color = HSSFColor.WHITE.index
        font.boldweight = HSSFFont.BOLDWEIGHT_BOLD
        setFont(font)
        wrapText = true
    }



    private fun fillRowNEW(wb: Workbook, row: Row, weekday: String, date: Date, project: String, description: String, start: String, end: String, currentM: String) {
        //val cs = wb.createCellStyle() //-------------------------------------------------------------------!!!
        cs.alignment = ALIGN_CENTER
        cs.verticalAlignment = VERTICAL_CENTER
        cs.wrapText = true

        var cell = row.createCell(0)
        cell.setCellValue(weekday)
        cell.cellStyle = cs


        //-----------------------------------------------------

        //val cellStyle = wb.createCellStyle() //-------------------------------------------------------------------!!!
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

        //-------------

        cell = row.createCell(6)
        //cell.setCellValue(time)
        cs.dataFormat = createHelper.createDataFormat().getFormat("[h]:mm")
        val formula = "F${row.rowNum+1}-E${row.rowNum+1}"
        cell.cellFormula = formula
        cell.cellStyle = cs

        fillOtherCells(wb, row, project, currentM)



    }

    fun fillOtherCells(wb: Workbook, row: Row, project: String, currentM: String) {
        val createHelper = wb.creationHelper
        //val cs = wb.createCellStyle() //-------------------------------------------------------------------!!!
        cs.alignment = ALIGN_CENTER
        cs.verticalAlignment = VERTICAL_CENTER
        cs.wrapText = true

        val csf = cs
        csf.dataFormat = createHelper.createDataFormat().getFormat("[h]:mm")

        //wb.getSheetAt(0).createRow(8)
        var cell = wb.getSheetAt(0).createRow(8).createCell(6)
        cell.cellFormula = "SUM(G11:G${row.rowNum+1})"
        cell.cellStyle = cs


        var column = spe.getStatus(LAST_COLUMN+currentM, 7)

        if (column == 7) {
            cell = wb.getSheetAt(0).getRow(9).createCell(7)
            cell.setCellValue(project)
            cell.cellStyle = cs

            //



            cell = wb.getSheetAt(0).getRow(8).createCell(7)
            val time = wb.getSheetAt(0).getRow(10).getCell(6).numericCellValue
            cell.cellFormula = "SUM(H11:H${row.rowNum+1})" ///
            cell.cellStyle = csf

            //

            cell = wb.getSheetAt(0).getRow(10).createCell(7)
            val x = CellReference.convertNumToColString(cell.columnIndex)
            val s = "$"
            cell.cellFormula = "SUMIF(${s}C${row.rowNum+1},$x$10,${s}G${row.rowNum+1})"
            cell.cellStyle = cs

            spe.setStatus(LAST_COLUMN+currentM, column+1)
        }
        else {
            var isNewWorkNumber = true
            for (i in 0..spe.getStatus(LAST_COLUMN+currentM, 7)-6) {
                cell = wb.getSheetAt(0).getRow(9).getCell(7+i)
                if (cell == null) {
                    break
                }
                if (project == cell.stringCellValue) {
                    isNewWorkNumber = false

                    cell = row.createCell(7+i)



                    val x = CellReference.convertNumToColString(cell.columnIndex)
                    val s = "$"
                    //cell.setCellValue(row.createCell(6).stringCellValue)
                    cell.cellFormula = "SUMIF(${s}C${row.rowNum+1},$x$10,${s}G${row.rowNum+1})"//"SUM(G11:G${row.rowNum+1})"
                    //"SUMIF(\$C11;H\$10;\$G11)"

                    //"SUMIF($C12;H\$10;$G12)"
                    cell.cellStyle = cs
                    break
                }
            }
            if (isNewWorkNumber) {
                //write here to the excel file


                column = spe.getStatus(LAST_COLUMN+currentM, 7)

                cell = wb.getSheetAt(0).getRow(9).createCell(column)
                cell.setCellValue(project)
                cell.cellStyle = cs

                cell = row.createCell(column)
                //cell.setCellValue(row.getCell(6).stringCellValue)
                cell.cellFormula = "G${row.rowNum+1}"
                cell.cellStyle = cs




                val x = CellReference.convertNumToColString(column)

                cell = wb.getSheetAt(0).getRow(8).createCell(column)
                //cell.cellFormula = "SUM(${x}11:${x})"
                cell.cellFormula = "SUM(${x}11:INDEX($x:$x,MATCH(9.99E+307,$x:$x),1))"
                cell.cellStyle = csf

                spe.setStatus(LAST_COLUMN+currentM, column+1)

            }

            for (i in 0..spe.getStatus(LAST_COLUMN+currentM, 7)-8) {
                cell = wb.getSheetAt(0).getRow(8).createCell(7 + i)
                if (cell != null) {


                    val x = CellReference.convertNumToColString(cell.columnIndex)

                    cell.cellFormula = "SUM(${x}11:$x${row.rowNum+1})"
                    cell.cellStyle = cs
                }
            }
        }


    }

    fun writeIntoExcelFile(c: Context, date: Date, project: String, description: String, start: String, end: String): Boolean {
        val triple = initializeExcelFile(c, dp.getMonthAndYearFromDate(date))
        wb = triple.first
        sheet = triple.second
        val currentM = triple.third

        cs = wb.createCellStyle()
        cellStyle = wb.createCellStyle()
        cs.setupCellStyle()
        cellStyle.setupCellStyle(wb)


        val row = sheet.createRow(spe.getStatus(LAST_ROW+currentM, 0))
        spe.setStatus(LAST_ROW+currentM, spe.getStatus(LAST_ROW+currentM, 0)+1)
        val weekday = swf.format(date)
        fillRowNEW(wb, row, weekday, date, project, description, start, end, currentM)



        //sortSheet(wb, sheet)

        val filename = "$currentM.xls"
        return saveExcelFile(c, wb, filename)
    }

    /*
    private fun initializeExcelFileNEW(context: Context): Triple<HSSFWorkbook, Sheet, String> {
        spe.setupSharedPreferencesEditor(context)
        val currentM = dp.getCurrentMonthAndYear()
        when {
            currentM != spe.getStatus(CURRENT_MONTH_AND_YEAR, "") -> {
                wb = HSSFWorkbook()
                sheet = wb.createSheet(currentM)

                spe.setStatus(CURRENT_MONTH_AND_YEAR, currentM)
                spe.setStatus(LAST_ROW+currentM, 9)

                row = sheet.createRow(spe.getStatus(LAST_ROW+currentM, 9))

                spe.setStatus(LAST_ROW+currentM, spe.getStatus(LAST_ROW+currentM, 9)+1)

                createTaskRow(wb, sheet, row)

                val filename = "$currentM.xls"
                saveExcelFile(context,filename)
                return Triple(wb, sheet, currentM)
            }
            else -> {
                return readExcelFileNEW(context, currentM)
            }
        }
    }
    */

    private fun readExcelFile(context: Context, currentM: String): Triple<HSSFWorkbook, Sheet, String> {
        val filename = "$currentM.xls"
        try {
            val file = File(context.getExternalFilesDir(null), filename)
            val myInput = FileInputStream(file)
            val myFileSystem = POIFSFileSystem(myInput)
            wb = HSSFWorkbook(myFileSystem)
            sheet = wb.getSheetAt(0)
            return Triple(wb, sheet, currentM)
        } catch (e: Exception) {
            e.printStackTrace()
            return Triple(wb, sheet, currentM)
        }
    }

    private fun initializeExcelFile(context: Context, currentM: String): Triple<HSSFWorkbook, Sheet, String> {
        spe.setupSharedPreferencesEditor(context)

        when {
            currentM != spe.getStatus(MONTH_AND_YEAR+currentM, "") -> {
                wb = HSSFWorkbook()
                sheet = wb.createSheet(currentM)

                spe.setStatus(MONTH_AND_YEAR+currentM, currentM)
                spe.setStatus(LAST_ROW+currentM, 9)

                row = sheet.createRow(spe.getStatus(LAST_ROW+currentM, 9))

                spe.setStatus(LAST_ROW+currentM, spe.getStatus(LAST_ROW+currentM, 9)+1)

                createTaskRow(wb, sheet, row)

                val filename = "$currentM.xls"
                saveExcelFile(context, wb, filename)
                val workbookList = spe.getWorkbookList()
                workbookList.add(currentM)
                spe.setWorkbookList(workbookList)
                return Triple(wb, sheet, currentM)
            }
            else -> {
                return readExcelFile(context, currentM)
            }
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


    fun writeStreakIntoExcelFile(c: Context, startDate: Date, endDate: Date, project: String, description: String, start: String, end: String): Boolean {
        val triple = initializeExcelFile(c, dp.getMonthAndYearFromDate(startDate))
        wb = triple.first
        sheet = triple.second
        var currentM = triple.third

        cs = wb.createCellStyle()
        cellStyle = wb.createCellStyle()
        cs.setupCellStyle()
        cellStyle.setupCellStyle(wb)


        var date = startDate
        while (date.before(endDate) || date == endDate) {

            val cal = Calendar.getInstance()
            cal.time = date

            if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {

                if (currentM != dp.getMonthAndYearFromDate(date)) {
                    val filename = "$currentM.xls"
                    saveExcelFile(c, wb, filename)
                    val tripleNew = initializeExcelFile(c, dp.getMonthAndYearFromDate(date))
                    wb = tripleNew.first
                    sheet = tripleNew.second
                    currentM = tripleNew.third
                    cs = wb.createCellStyle()
                    cellStyle = wb.createCellStyle()
                    cs.setupCellStyle()
                    cellStyle.setupCellStyle(wb)
                }
                val row = sheet.createRow(spe.getStatus(LAST_ROW+currentM, 0))
                spe.setStatus(LAST_ROW+currentM, spe.getStatus(LAST_ROW+currentM, 0)+1)
                val weekday = swf.format(date)

                fillRowNEW(wb, row, weekday, date, project, description, start, end, currentM)
            }

            cal.add(Calendar.DATE, 1)
            date = cal.time
        }



        //sortSheet(wb, sheet)
        val filename = "$currentM.xls"
        return saveExcelFile(c, wb, filename)
    }





}

package com.example.workhourregistrator

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.workhourregistrator.cardrecyclerview.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.workhourregistrator.MainActivity.Companion.ACTION
import com.google.android.material.snackbar.Snackbar
import java.io.File
import androidx.constraintlayout.widget.ConstraintAttribute.setAttributes
import android.R.attr.y
import android.R.attr.x
import android.view.WindowManager
import android.R.id.edit
import android.content.SharedPreferences
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.LAST_COLUMN
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.LAST_ROW
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.MONTH_AND_YEAR
import android.content.DialogInterface
import android.widget.*


class HomeFragment: Fragment(), CardClickListener {

    private lateinit var v: View
    private lateinit var c: Context
    private lateinit var cardList: ArrayList<Card>

    private val m = MainActivity()
    private var spe = SharedPreferencesEditor()

    private lateinit var autoFitRecyclerView: CardRecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_home, container, false)
        c = v.context
        spe.setupSharedPreferencesEditor(c)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inflateCardList(c)
        autoFitRecyclerView = v.findViewById(R.id.auto_fit_recycler_view)
        autoFitRecyclerView.adapter = RecyclerViewAdapter(cardList, this)

    }

    private fun inflateCardList(c: Context) {
        val workbookList = spe.getWorkbookList()
        Collections.sort(workbookList, String.CASE_INSENSITIVE_ORDER)
        cardList = arrayListOf()
        for (workbook in workbookList) {
            cardList.add(Card(workbook))
        }
    }

    override fun onCardClicked(holder: CardViewHolder, position: Int) {
        showAlertDialog(cardList[position].title, position)
        /*
        when (position) {
            0 -> showAlertDialog(cardList[0].title)
            1 -> Toast.makeText(c, "$position", Toast.LENGTH_SHORT).show()
            2 -> Toast.makeText(c, "$position", Toast.LENGTH_SHORT).show()
            3 -> Toast.makeText(c, "$position", Toast.LENGTH_SHORT).show()
            else -> Log.d("CardView", "Exception: Too many positions!!")
        }
         */
    }

    private lateinit var dialog: AlertDialog


    private fun showAlertDialog(workbook: String, position: Int) {
        val builder = AlertDialog.Builder(c)
        builder.setTitle(getString(R.string.choose_option))


        /*
        val linearLayout: LinearLayout = v.findViewById(R.id.list_item_layout)

        val imageView: ImageView = linearLayout.findViewById(R.id.list_item_imageView)
        val textView: TextView = linearLayout.findViewById(R.id.list_item_textView)
        textView.text = workbook
        builder.setView(linearLayout)
        */
        /*
        val container = FrameLayout(c)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            500
        )
        container.layoutParams = params
        builder.setView(container)
        */




        val filename = "$workbook.xls"
        val file = File(c.getExternalFilesDir(null), filename)
        val path = FileProvider.getUriForFile(c, "com.example.workhourregistrator.fileprovider", file);

        builder.setItems(arrayOf<CharSequence>("Open file", "Share file", "Delete file")) { dialog, which ->
            when (which) {
                0 -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, path).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        //tel the user to install viewer to perform this action
                    }
                }
                1 -> {
                    m.sendEmail(c, filename)
                }
                2 -> {
                    showAlertDialog(workbook, file, position)
                }
            }
        }

        /*
        builder.setPositiveButton(getString(R.string.open)) { _, _ ->
            try {
                val intent = Intent(Intent.ACTION_VIEW, path).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                //tel the user to install viewer to perform this action
            }

        }
        builder.setNegativeButton(getString(R.string.share)) { _, _ ->
            m.sendEmail(c, filename)
        }
        builder.setNeutralButton(getString(R.string.delete)) { _, _ ->
            showAlertDialog(workbook, file, position)
        }
        */
        builder.setPositiveButton(getString(R.string.cancel), null)

        dialog = builder.create()
        dialog.show()

    }

    private fun showAlertDialog(workbook: String, file: File, position: Int) {
        val builder = AlertDialog.Builder(c)
        builder.setTitle(getString(R.string.are_you_sure))

        val container = FrameLayout(c)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            500
        )
        container.layoutParams = params
        builder.setView(container)

        builder.setPositiveButton(getString(R.string.delete)) { _, _ ->
            if (file.exists()) {
                if (file.delete()) {
                    spe.deleteWorkbookFromList(workbook)

                    spe.sharedPreferences().edit().remove(LAST_ROW+workbook).apply()
                    spe.sharedPreferences().edit().remove(LAST_COLUMN+workbook).apply()
                    spe.sharedPreferences().edit().remove(MONTH_AND_YEAR+workbook).apply()

                    cardList.removeAt(position)
                    autoFitRecyclerView.removeViewAt(0)
                    autoFitRecyclerView.adapter!!.notifyItemRemoved(position)
                    autoFitRecyclerView.adapter!!.notifyItemRangeChanged(position, cardList.size)
                    autoFitRecyclerView.adapter!!.notifyDataSetChanged()

                    Snackbar.make((c as Activity).window.decorView, getText(R.string.excel_file_deleted), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()
                } else {
                    Snackbar.make((c as Activity).window.decorView, getText(R.string.excel_file_delete_error), Snackbar.LENGTH_LONG).setAction(ACTION, null).show()
                }
            }

        }
        builder.setNegativeButton(getString(R.string.cancel), null)

        dialog = builder.create()
        dialog.show()



    }
}
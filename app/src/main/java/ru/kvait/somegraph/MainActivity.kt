package ru.kvait.somegraph

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.ext.isFloat
import org.pbreakers.mobile.androidtest.udacity.utils.LoadingState
import ru.kvait.somegraph.data.app.di.viewModelModule
import ru.kvait.somegraph.data.model.Point
import ru.kvait.somegraph.data.model.Point.Companion.toEntry
import ru.kvait.somegraph.viewmodel.PointViewModel
import java.lang.Exception
import java.lang.NumberFormatException
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private lateinit var series: MutableList<Entry>
    private lateinit var valueList: MutableList<String>
    private var value: Int = 0
    private var dataSet = LineDataSet(null, null)
    private lateinit var data: LineData
    private lateinit var listPoint: MutableList<Point>
    private var msg = true
    private val msgTag = "MSG"

    private val pointViewModel by viewModel<PointViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupObserver()
        changePoint()
        edit_point.afterTextChanged {
            isNumeric(it)
        }
    }


    private fun initGraph(list: MutableList<Point>) {
        series = list.map { it.toEntry() } as MutableList<Entry>
        valueList = list.map { it.serialNumber.toString() } as MutableList<String>
        dataSet = LineDataSet(series, getString(R.string.first_data))

        data = LineData(valueList, dataSet)
        dataSet.color = resources.getColor(R.color.red)
        dataSet.valueTextSize = 15F
        dataSet.circleRadius = 10F
        graph.data = data
        graph.invalidate()
    }

    private fun addNewPoint(list: MutableList<Point>) {
        btn_ok.setOnClickListener {
            val point = Point(
                list.last().id + 1,
                edit_point.text.toString().toFloat(),
                list.last().serialNumber + 1
            )
            list.add(point)
            pointViewModel.updateData(list)
            pointViewModel.fetchData()
        }
    }

    private fun changePoint() {
        graph.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, dataSetIndex: Int, h: Highlight?) {
                btn_ok.visibility = View.GONE
                btn_change.visibility = View.VISIBLE
                edit_point.text = e?.`val`.toString().toEditable()
                btn_change.setOnClickListener {
                    listPoint[e?.xIndex!!].point = edit_point.text.toString().toFloat()
                    pointViewModel.updateData(listPoint)
                    pointViewModel.fetchData()
                }
            }

            override fun onNothingSelected() {
                btn_ok.visibility = View.VISIBLE
                btn_change.visibility = View.GONE
                edit_point.text = null
                Toast.makeText(this@MainActivity, getString(R.string.cancel), Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

    private fun isNumeric(editString: String) {
        if (isFloatNumeric(editString)) {
            btn_ok.isEnabled = true
            btn_ok.isClickable = true
            btn_change.isEnabled = true
            btn_change.isClickable = true
        } else {
            btn_ok.isEnabled = false
            btn_ok.isClickable = false
            btn_change.isEnabled = false
            btn_change.isClickable = false
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun isFloatNumeric(edit: String) =
        Pattern.compile("[-+]?[0-9]*(\\.[0-9]+)").matcher(edit).matches()

    private fun saveToSP(boolean: Boolean) {
        getSharedPreferences(msgTag, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(msgTag, boolean)
            .apply()
    }

    private fun setupObserver() {
        msg = getSharedPreferences(msgTag, MODE_PRIVATE).getBoolean(msgTag, true)

        pointViewModel.data.observe(this, Observer {
            try {
                initGraph(it)
                listPoint = it.map { it } as MutableList<Point>
                addNewPoint(listPoint)
                if (listPoint.size == 11 && msg) {
                    msg = false
                    Toast.makeText(this, getString(R.string.msg_10), Toast.LENGTH_SHORT).show()
                    saveToSP(msg)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        pointViewModel.loadingState.observe(this, Observer {
            when (it.status) {
                LoadingState.Status.FAILED ->
                    Toast.makeText(
                        baseContext,
                        it.msg,
                        Toast.LENGTH_SHORT
                    ).show()
            }
        })
    }
}
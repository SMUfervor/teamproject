package com.example.schedulemanagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import com.example.schedulemanagement.databinding.ActivityWriteBinding

class WriteActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityWriteBinding
    private var Alram : Boolean = false
    private var DL : Boolean = false
    private var Pri : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val numberpickerPri : NumberPicker = viewBinding.numPri
        val numberpickerY : NumberPicker = viewBinding.numY
        val numberpickerM : NumberPicker = viewBinding.numM
        val numberpickerD : NumberPicker = viewBinding.numD
        val numberpickerH : NumberPicker = viewBinding.numH
        val numberpickeralramY : NumberPicker = viewBinding.alramY
        val numberpickeralramM : NumberPicker = viewBinding.alramM
        val numberpickeralramD : NumberPicker = viewBinding.alramD
        val numberpickeralramH : NumberPicker = viewBinding.alramH

        val formatter = NumberPicker.Formatter { value -> String.format("%02d", value) }
        numberpickerPri.setFormatter(formatter)
        numberpickerM.setFormatter(formatter)
        numberpickerD.setFormatter(formatter)
        numberpickerH.setFormatter(formatter)
        numberpickeralramM.setFormatter(formatter)
        numberpickeralramD.setFormatter(formatter)
        numberpickeralramH.setFormatter(formatter)

        numberpickerPri.minValue = 1
        numberpickerPri.maxValue = 20

        numberpickerY.minValue = 2023
        numberpickerY.maxValue = 2100
        numberpickeralramY.minValue = 2023
        numberpickeralramY.maxValue = 2100

        numberpickerM.minValue = 1
        numberpickerM.maxValue = 12
        numberpickeralramM.minValue = 1
        numberpickeralramM.maxValue = 12

        numberpickerD.minValue = 1
        numberpickerD.maxValue = 31
        numberpickeralramD.minValue = 1
        numberpickeralramD.maxValue = 31

        numberpickerH.minValue = 0
        numberpickerH.maxValue = 23
        numberpickeralramH.minValue = 0
        numberpickeralramH.maxValue = 23

        viewBinding.btnPri.setOnClickListener{
            Pri = true
            viewBinding.numPri.visibility = View.VISIBLE
            viewBinding.btnPriX.visibility = View.VISIBLE
            viewBinding.btnPri.alpha = 1f
        }

        viewBinding.btnAlram.setOnClickListener {
            Alram = true
            DeadlineGone()
            AlramVisible()
            textVisible()
            viewBinding.btnAlram.alpha = 1f
            viewBinding.btnAlramX.visibility = View.VISIBLE
            viewBinding.btnAlramOK.visibility = View.VISIBLE
            viewBinding.btnDeadlineX.visibility = View.GONE
            viewBinding.btnDeadlineOK.visibility = View.GONE
            viewBinding.btnDeadline.alpha = 0.5f
        }

        viewBinding.btnDeadline.setOnClickListener {
            DL = true
            AlramGone()
            DeadLineVisible()
            textVisible()
            viewBinding.btnDeadline.alpha = 1f
            viewBinding.btnDeadlineX.visibility = View.VISIBLE
            viewBinding.btnDeadlineOK.visibility = View.VISIBLE
            viewBinding.btnAlramX.visibility = View.GONE
            viewBinding.btnAlramOK.visibility = View.GONE
            viewBinding.btnAlram.alpha = 0.5f
        }

        viewBinding.btnPriX.setOnClickListener {
            Pri = false
            viewBinding.numPri.visibility = View.GONE
            viewBinding.btnPri.alpha = 0.5f
            viewBinding.btnPriX.visibility = View.GONE
        }

        viewBinding.btnDeadlineX.setOnClickListener {
            DL = false
            DeadlineGone()
            textInvisible()
            viewBinding.btnDeadline.alpha = 0.5f
            viewBinding.btnDeadlineX.visibility = View.GONE
            viewBinding.btnDeadlineOK.visibility = View.GONE
        }

        viewBinding.btnAlramX.setOnClickListener {
            Alram = false
            AlramGone()
            textInvisible()
            viewBinding.btnAlram.alpha = 0.5f
            viewBinding.btnAlramX.visibility = View.GONE
            viewBinding.btnAlramOK.visibility = View.GONE
        }


    }
    private fun AlramGone() {
        viewBinding.alramY.visibility = View.GONE
        viewBinding.alramM.visibility = View.GONE
        viewBinding.alramD.visibility = View.GONE
        viewBinding.alramH.visibility = View.GONE
    }
    private fun DeadlineGone() {
        viewBinding.numY.visibility = View.GONE
        viewBinding.numD.visibility = View.GONE
        viewBinding.numM.visibility = View.GONE
        viewBinding.numH.visibility = View.GONE
    }

    private fun AlramVisible() {
        viewBinding.alramY.visibility = View.VISIBLE
        viewBinding.alramM.visibility = View.VISIBLE
        viewBinding.alramD.visibility = View.VISIBLE
        viewBinding.alramH.visibility = View.VISIBLE
    }

    private fun DeadLineVisible() {
        viewBinding.numY.visibility = View.VISIBLE
        viewBinding.numD.visibility = View.VISIBLE
        viewBinding.numM.visibility = View.VISIBLE
        viewBinding.numH.visibility = View.VISIBLE
    }

    private fun textVisible() {
        viewBinding.textY.visibility = View.VISIBLE
        viewBinding.textM.visibility = View.VISIBLE
        viewBinding.textD.visibility = View.VISIBLE
        viewBinding.textH.visibility = View.VISIBLE
    }

    private fun textInvisible() {
        viewBinding.textY.visibility = View.INVISIBLE
        viewBinding.textM.visibility = View.INVISIBLE
        viewBinding.textD.visibility = View.INVISIBLE
        viewBinding.textH.visibility = View.INVISIBLE
    }
}
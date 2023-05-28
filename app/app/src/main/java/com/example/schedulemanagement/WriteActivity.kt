package com.example.schedulemanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import android.widget.Toast
import com.example.schedulemanagement.databinding.ActivityWriteBinding
import java.text.SimpleDateFormat

class WriteActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityWriteBinding
    private var Alram : Boolean = false
    private var DL : Boolean = false
    private var Pri : Boolean = false
    private var Dy = 2023
    private var Dm = 1
    private var Dd = 1
    private var Dh = 0
    private var P = 1
    private var Ay = 2023
    private var Am = 1
    private var Ad = 1
    private var Ah = 0
    private var title = ""
    private var detail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val numberpickerPri: NumberPicker = viewBinding.numPri
        val numberpickerY: NumberPicker = viewBinding.numY
        val numberpickerM: NumberPicker = viewBinding.numM
        val numberpickerD: NumberPicker = viewBinding.numD
        val numberpickerH: NumberPicker = viewBinding.numH
        val numberpickeralramY: NumberPicker = viewBinding.alarmY
        val numberpickeralramM: NumberPicker = viewBinding.alarmM
        val numberpickeralramD: NumberPicker = viewBinding.alarmD
        val numberpickeralramH: NumberPicker = viewBinding.alarmH

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

        numberpickerPri.setOnValueChangedListener { picker, _, newVal ->
            P = newVal
        }
        numberpickerY.setOnValueChangedListener { picker, _, newVal ->
            Dy = newVal
        }
        numberpickerM.setOnValueChangedListener { picker, _, newVal ->
            Dm = newVal
        }
        numberpickerD.setOnValueChangedListener { picker, _, newVal ->
            Dd = newVal
        }
        numberpickerH.setOnValueChangedListener { picker, _, newVal ->
            Dh = newVal
        }
        numberpickeralramY.setOnValueChangedListener { picker, _, newVal ->
            Ay = newVal
        }
        numberpickeralramM.setOnValueChangedListener { picker, _, newVal ->
            Am = newVal
        }
        numberpickeralramD.setOnValueChangedListener { picker, _, newVal ->
            Ad = newVal
        }
        numberpickeralramH.setOnValueChangedListener { picker, _, newVal ->
            Ah = newVal
        }

        viewBinding.btnPri.setOnClickListener{
            viewBinding.numPri.visibility = View.VISIBLE
            viewBinding.btnPriX.visibility = View.VISIBLE
            viewBinding.btnPriOK.visibility = View.VISIBLE
            viewBinding.btnPri.alpha = 1f
        }

        viewBinding.btnAlarm.setOnClickListener {
            DeadlineGone()
            AlramVisible()
            textVisible()
            viewBinding.btnAlarm.alpha = 1f
            viewBinding.btnAlarmX.visibility = View.VISIBLE
            viewBinding.btnAlarmOK.visibility = View.VISIBLE
            viewBinding.btnDeadlineX.visibility = View.GONE
            viewBinding.btnDeadlineOK.visibility = View.GONE
            viewBinding.btnDeadline.alpha = 0.5f
        }

        viewBinding.btnDeadline.setOnClickListener {
            AlramGone()
            DeadLineVisible()
            textVisible()
            viewBinding.btnDeadline.alpha = 1f
            viewBinding.btnDeadlineX.visibility = View.VISIBLE
            viewBinding.btnDeadlineOK.visibility = View.VISIBLE
            viewBinding.btnAlarmX.visibility = View.GONE
            viewBinding.btnAlarmOK.visibility = View.GONE
            viewBinding.btnAlarm.alpha = 0.5f
        }

        viewBinding.btnPriX.setOnClickListener {
            Pri = false
            viewBinding.numPri.visibility = View.GONE
            viewBinding.btnPriX.visibility = View.GONE
            viewBinding.btnPriOK.visibility = View.GONE
            viewBinding.btnPri.alpha = 0.5f
        }

        viewBinding.btnPriOK.setOnClickListener {
            Pri = true
            viewBinding.numPri.visibility = View.GONE
            viewBinding.btnPriX.visibility = View.GONE
            viewBinding.btnPriOK.visibility = View.GONE
            viewBinding.btnPri.alpha = 0.5f
        }

        viewBinding.btnDeadlineX.setOnClickListener {
            DL = false
            DeadlineGone()
            textInvisible()
            viewBinding.btnDeadline.alpha = 0.5f
            viewBinding.btnDeadlineX.visibility = View.GONE
            viewBinding.btnDeadlineOK.visibility = View.GONE
        }

        viewBinding.btnAlarmX.setOnClickListener {
            Alram = false
            AlramGone()
            textInvisible()
            viewBinding.btnAlarm.alpha = 0.5f
            viewBinding.btnAlarmX.visibility = View.GONE
            viewBinding.btnAlarmOK.visibility = View.GONE
        }

        viewBinding.btnAlarmOK.setOnClickListener {
            Alram = true
            AlramGone()
            textInvisible()
            viewBinding.btnAlarm.alpha = 0.5f
            viewBinding.btnAlarmX.visibility = View.GONE
            viewBinding.btnAlarmOK.visibility = View.GONE
        }

        viewBinding.btnDeadlineOK.setOnClickListener {
            DL = true
            DeadlineGone()
            textInvisible()
            viewBinding.btnDeadline.alpha = 0.5f
            viewBinding.btnDeadlineX.visibility = View.GONE
            viewBinding.btnDeadlineOK.visibility = View.GONE
        }

        viewBinding.btnPluse.setOnClickListener {
            if (!viewBinding.tasktitle.text.toString().isNullOrBlank()) {
                val intent = Intent(this, ListActivity::class.java)
                if(DL) {
                    val deadlineString = "${Dy}.${Dm}.${Dd}.${Dh}"
                    intent.putExtra("deadlineString", deadlineString)
                }
                if(Alram){
                    val alarmString = "${Ay}.${Am}.${Ad}.${Ah}"
                    intent.putExtra("alarmString", alarmString)
                }
                if(Pri){
                    intent.putExtra("Pri", P)
                }
                title = viewBinding.tasktitle.text.toString()
                detail = viewBinding.editDetail.text.toString()
                intent.putExtra("title", title)
                intent.putExtra("detail", detail)
                intent.putExtra("Alarm", Alram)
                intent.putExtra("DeadLine", DL)
                intent.putExtra("Priority", Pri)
                setResult(RESULT_OK, intent)
                finish()
            }
            else{
                Toast.makeText(this, "제목은 필수 입력란 입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun AlramGone() { //알람 설정에 필요한 넘버피커를 사라지게함
        viewBinding.alarmY.visibility = View.GONE
        viewBinding.alarmM.visibility = View.GONE
        viewBinding.alarmD.visibility = View.GONE
        viewBinding.alarmH.visibility = View.GONE
    }
    private fun DeadlineGone() { //마감일 설정에 필요한 넘버피커를 사라지게함
        viewBinding.numY.visibility = View.GONE
        viewBinding.numD.visibility = View.GONE
        viewBinding.numM.visibility = View.GONE
        viewBinding.numH.visibility = View.GONE
    }

    private fun AlramVisible() { //알림 설정에 필요한 넘버피커를 보이게함
        viewBinding.alarmY.visibility = View.VISIBLE
        viewBinding.alarmM.visibility = View.VISIBLE
        viewBinding.alarmD.visibility = View.VISIBLE
        viewBinding.alarmH.visibility = View.VISIBLE
    }

    private fun DeadLineVisible() { //마감일 설정에 필요한 넘버피커를 보이게함
        viewBinding.numY.visibility = View.VISIBLE
        viewBinding.numD.visibility = View.VISIBLE
        viewBinding.numM.visibility = View.VISIBLE
        viewBinding.numH.visibility = View.VISIBLE
    }

    private fun textVisible() { //년 월 일 시간 을 나타내는 text를 나타냄
        viewBinding.textY.visibility = View.VISIBLE
        viewBinding.textM.visibility = View.VISIBLE
        viewBinding.textD.visibility = View.VISIBLE
        viewBinding.textH.visibility = View.VISIBLE
    }

    private fun textInvisible() { //년 월 일 시간 을 나타내는 text를 없앰
        viewBinding.textY.visibility = View.INVISIBLE
        viewBinding.textM.visibility = View.INVISIBLE
        viewBinding.textD.visibility = View.INVISIBLE
        viewBinding.textH.visibility = View.INVISIBLE
    }
}
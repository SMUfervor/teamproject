package com.example.schedulemanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import android.widget.Toast
import com.example.schedulemanagement.databinding.ActivityWriteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class WriteActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityWriteBinding
    private lateinit var numberpickerPri: NumberPicker
    private lateinit var numberpickerY: NumberPicker
    private lateinit var numberpickerM: NumberPicker
    private lateinit var numberpickerD: NumberPicker
    private lateinit var numberpickerH: NumberPicker
    private lateinit var numberpickeralramY: NumberPicker
    private lateinit var numberpickeralramM: NumberPicker
    private lateinit var numberpickeralramD: NumberPicker
    private lateinit var numberpickeralramH: NumberPicker
    private var taskparentId = ""
    private var taskmyId = ""
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private val dateFormat = SimpleDateFormat("yyyy.MM.dd.HH")
    private var alarmString = ""
    private var deadlineString = ""
    private var Alarm : Boolean = false
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

        if(intent.getBooleanExtra("retask", false)){
            viewBinding.btnPluse.visibility = View.GONE
            viewBinding.btnRetask.visibility= View.VISIBLE

            taskparentId = intent.getStringExtra("parentId").toString()
            taskmyId = intent.getStringExtra("myId").toString()
            if(user != null){
                db.collection(user.uid).document(taskparentId).collection("task list")
                    .document(taskmyId)
                    .get()
                    .addOnSuccessListener { document ->
                        if(document != null){
                            viewBinding.tasktitle.setText(document.getString("title"))
                            viewBinding.editDetail.setText(document.getString("detail"))
                            val deadline = document.getTimestamp("deadline")?.toDate()
                            val alarm = document.getTimestamp("alarm")?.toDate()
                            P = document.getLong("priority")?.toInt()!!
                            deadlineString = dateFormat.format(deadline)
                            if(deadlineString != "2200.12.31.00") {
                                val dyear = deadlineString.substring(0, 4).toInt()
                                val dmonth = deadlineString.substring(5, 7).toInt()
                                val dday = deadlineString.substring(8, 10).toInt()
                                val dhour = deadlineString.substring(11, 13).toInt()
                                Dy = dyear
                                Dm = dmonth
                                Dd = dday
                                Dh = dhour
                                numberpickerY.value = dyear
                                numberpickerM.value = dmonth
                                numberpickerD.value = dday
                                numberpickerH.value = dhour
                            }
                            alarmString = dateFormat.format(alarm)
                            if(alarmString != "2200.12.31.00") {
                                Alarm = true
                                val ayear = alarmString.substring(0, 4).toInt()
                                val amonth = alarmString.substring(5, 7).toInt()
                                val aday = alarmString.substring(8, 10).toInt()
                                val ahour = alarmString.substring(11, 13).toInt()
                                Ay = ayear
                                Am = amonth
                                Ad = aday
                                Ah = ahour
                                numberpickeralramY.value = ayear
                                numberpickeralramM.value = amonth
                                numberpickeralramD.value = aday
                                numberpickeralramH.value = ahour
                            }
                            if(P != 100){
                                numberpickerPri.value = P
                            }
                        }
                    }
            }
        }

        numberpickerPri = viewBinding.numPri
        numberpickerY = viewBinding.numY
        numberpickerM = viewBinding.numM
        numberpickerD = viewBinding.numD
        numberpickerH = viewBinding.numH
        numberpickeralramY = viewBinding.alarmY
        numberpickeralramM = viewBinding.alarmM
        numberpickeralramD = viewBinding.alarmD
        numberpickeralramH = viewBinding.alarmH

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
            deadlineString = "2200.12.31.00"
            DL = false
            DeadlineGone()
            textInvisible()
            viewBinding.btnDeadline.alpha = 0.5f
            viewBinding.btnDeadlineX.visibility = View.GONE
            viewBinding.btnDeadlineOK.visibility = View.GONE
        }

        viewBinding.btnAlarmX.setOnClickListener {
            Alarm = false
            AlramGone()
            textInvisible()
            viewBinding.btnAlarm.alpha = 0.5f
            viewBinding.btnAlarmX.visibility = View.GONE
            viewBinding.btnAlarmOK.visibility = View.GONE
        }

        viewBinding.btnAlarmOK.setOnClickListener {
            alarmString = "${Ay}.${Am}.${Ad}.${Ah}"
            val alarmday = dateFormat.parse(alarmString)
            if(alarmday < Date()){
                Toast.makeText(this, "올바르지 않은 날짜입니다.\n날짜를 다시 설정해주세요.", Toast.LENGTH_SHORT).show()
            }else{
                Alarm = true
                AlramGone()
                textInvisible()
                viewBinding.btnAlarm.alpha = 0.5f
                viewBinding.btnAlarmX.visibility = View.GONE
                viewBinding.btnAlarmOK.visibility = View.GONE
            }
        }

        viewBinding.btnDeadlineOK.setOnClickListener {
            deadlineString = "${Dy}.${Dm}.${Dd}.${Dh}"
            val deadlineday = dateFormat.parse(deadlineString)
            if(deadlineday < Date()){
                Toast.makeText(this, "올바르지 않은 날짜입니다.\n날짜를 다시 설정해주세요.", Toast.LENGTH_SHORT).show()
            }else{
                DL = true
                DeadlineGone()
                textInvisible()
                viewBinding.btnDeadline.alpha = 0.5f
                viewBinding.btnDeadlineX.visibility = View.GONE
                viewBinding.btnDeadlineOK.visibility = View.GONE
            }
        }

        viewBinding.btnPluse.setOnClickListener {
            if (!viewBinding.tasktitle.text.toString().isNullOrBlank()) {
                val intent = Intent(this, ListActivity::class.java)
                if(DL) {
                    intent.putExtra("deadlineString", deadlineString)
                }
                if(Alarm){
                    intent.putExtra("alarmString", alarmString)
                }
                if(Pri){
                    intent.putExtra("Pri", P)
                }
                title = viewBinding.tasktitle.text.toString()
                detail = viewBinding.editDetail.text.toString()
                intent.putExtra("title", title)
                intent.putExtra("detail", detail)
                intent.putExtra("Alarm", Alarm)
                intent.putExtra("DeadLine", DL)
                intent.putExtra("Priority", Pri)
                setResult(RESULT_OK, intent)
                finish()
            }
            else{
                Toast.makeText(this, "제목은 필수 입력란 입니다.", Toast.LENGTH_SHORT).show()
            }
        }

        viewBinding.btnRetask.setOnClickListener {
            if (!viewBinding.tasktitle.text.toString().isNullOrBlank()) {
                val intent = Intent(this, TaskActivity::class.java)
                intent.putExtra("deadlineString", deadlineString)
                intent.putExtra("alarmString", alarmString)
                intent.putExtra("Pri", P)
                intent.putExtra("Alarm", Alarm)
                intent.putExtra("parentId", taskparentId)
                intent.putExtra("myId", taskmyId)

                title = viewBinding.tasktitle.text.toString()
                detail = viewBinding.editDetail.text.toString()
                intent.putExtra("title", title)
                intent.putExtra("detail", detail)
                setResult(RESULT_OK, intent)
                finish()
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
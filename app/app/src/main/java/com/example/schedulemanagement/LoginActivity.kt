package com.example.schedulemanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.schedulemanagement.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit  var checkbox : CheckBox
    private val startMainActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK){
            val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("email")
            editor.remove("password")
            editor.apply()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        auth = FirebaseAuth.getInstance()
        checkbox = viewBinding.checkboxKeep

        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("email", null)
        val savedPassword = sharedPreferences.getString("password", null)
        if (savedEmail != null && savedPassword != null) {
            signInWithEmailAndPassword(savedEmail, savedPassword)
        }

        viewBinding.signinBtn.setOnClickListener {
            val email = viewBinding.editEmail.text.toString()
            val password = viewBinding.editPwd.text.toString()

            signInWithEmailAndPassword(email, password)
        }
        viewBinding.signupBtn.setOnClickListener {
            val email = viewBinding.editEmail.text.toString()
            val password = viewBinding.editPwd.text.toString()

            signUpWithEmailAndPassword(email, password)
        }
    }

    private fun signUpWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    sendEmailVerification()
                } else {
                    // 사용자 계정 생성 실패
                }
            }
    }
    private fun sendEmailVerification() {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "이메일이 발송 되었습니다. 이메일 인증 후 로그인 해주시기 바랍니다.", Toast.LENGTH_SHORT).show()
                    // 이메일 인증 요청 성공
                } else {
                    // 이메일 인증 요청 실패
                }
            }
    }
    private fun isEmailVerified(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.isEmailVerified ?: false
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && isEmailVerified()) {
                    val intent = Intent(this, MainActivity::class.java)
                    startMainActivityForResult.launch(intent)

                    if (checkbox.isChecked) {
                        // 로그인 정보 저장
                        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("email", email)
                        editor.putString("password", password)
                        editor.apply()
                    }
                } else {
                    Toast.makeText(this, "이메일과 비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
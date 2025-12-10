package com.zjgsu.test

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var edAccount: EditText
    private lateinit var edPassword: EditText
    private lateinit var edConfirmPassword: EditText
    private lateinit var edEmail: EditText
    private lateinit var edPhone: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initViews()
    }

    private fun initViews() {
        edAccount = findViewById(R.id.ed_Name)
        edPassword = findViewById(R.id.ed_Password)
        edConfirmPassword = findViewById(R.id.ed_ConfirmPassword)
        edEmail = findViewById(R.id.ed_Email)
        edPhone = findViewById(R.id.ed_PhoneNumber)

        findViewById<Button>(R.id.btn_register).setOnClickListener { registerUser() }
        findViewById<Button>(R.id.btn_back).setOnClickListener { finish() }
    }

    private fun registerUser() {
        val account = edAccount.text.toString().trim()
        val password = edPassword.text.toString().trim()
        val confirmPassword = edConfirmPassword.text.toString().trim()
        val email = edEmail.text.toString().trim()
        val phone = edPhone.text.toString().trim()

        if (!validateInputs(account, password, confirmPassword, email, phone)) return

        Thread {
            if (checkExistingCredentials(account, email, phone)) return@Thread

            val success = DatabaseManager.executeUpdate(
                "INSERT INTO user (account, password, email, phone) VALUES (?, ?, ?, ?)",
                account, password, email, phone
            ) > 0

            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "注册失败，请重试", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun validateInputs(
        account: String,
        password: String,
        confirmPassword: String,
        email: String,
        phone: String
    ): Boolean {
        when {
            account.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                showToast("请填写账号、密码及确认密码")
                return false
            }
            password != confirmPassword -> {
                showToast("两次密码不一致")
                return false
            }
            email.isEmpty() && phone.isEmpty() -> {
                showToast("请至少填写邮箱或手机号")
                return false
            }
            !phone.isEmpty() && !Patterns.PHONE.matcher(phone).matches() -> {
                showToast("请输入有效的手机号")
                return false
            }
            !email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("请输入有效的邮箱")
                return false
            }
        }
        return true
    }

    private fun checkExistingCredentials(account: String, email: String, phone: String): Boolean {
        fun isFieldExists(field: String, value: String, errorMsg: String): Boolean {
            if (value.isEmpty()) return false
            val exists = DatabaseManager.executeQuery(
                "SELECT id FROM user WHERE $field = ?", value
            )?.use { it.next() } ?: false
            if (exists) showToast(errorMsg)
            return exists
        }

        return isFieldExists("account", account, "账号已存在") ||
                isFieldExists("email", email, "邮箱已注册") ||
                isFieldExists("phone", phone, "手机号已注册")
    }

    private fun showToast(message: String) {
        runOnUiThread { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
    }
}
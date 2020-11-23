package com.example.myapplication.activities

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mapapplicationkotlin.data.user.UserEntity
import com.example.mapapplicationkotlin.data.user.UserViewModel
import com.example.myapplication.R
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {

    private lateinit var ErrorDrawable: Drawable

    private var textInputEmail: TextInputLayout? = null
    private var textInputUsername: TextInputLayout? = null
    private var textInputPassword: TextInputLayout? = null

    private var userViewModel: UserViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        ErrorDrawable = resources.getDrawable(R.drawable.icon_error)
        ErrorDrawable.setBounds(
            0,
            0,
            ErrorDrawable.getIntrinsicWidth(),
            ErrorDrawable.getIntrinsicHeight()
        )

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        textInputEmail = findViewById(R.id.text_singup_email)
        textInputUsername = findViewById(R.id.text_singup_username)
        textInputPassword = findViewById(R.id.text_singup_password)

        val singUpButton = findViewById<Button>(R.id.singup_button)
        // Clicking on singUp button when register
        singUpButton.setOnClickListener {
            var emailInput = textInputEmail?.editText?.text.toString().trim()
            var usernameInput = textInputUsername?.editText?.text.toString().trim()
            var passwordInput = textInputPassword?.editText?.text.toString().trim()

            validateEmail()
            validateUsername()
            validatePassword()

            if (validateEmail() && validateUsername() && validatePassword()) {
                RegisterNewUser(emailInput, usernameInput, passwordInput)
            }
        }

        // Process the textInputEmail editText when typing email
        textInputEmail?.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                textInputEmail?.editText?.setTextColor(Color.DKGRAY)
                textInputEmail?.setError(null)
                textInputEmail?.editText?.setCompoundDrawables(null, null, null, null)
                if (!TextUtils.isEmpty(textInputEmail?.editText?.text) && !TextUtils.isEmpty(
                        textInputUsername?.editText?.text
                    ) && !TextUtils.isEmpty(
                        textInputPassword?.editText?.text
                    )
                ) {
                    singUpButton.alpha = 1f
                } else {
                    singUpButton.alpha = 0.65f
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // Process the textInputUsername editText when typing usename
        textInputUsername?.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                textInputUsername?.editText?.setTextColor(Color.DKGRAY)
                textInputUsername?.setError(null)
                textInputUsername?.editText?.setCompoundDrawables(null, null, null, null)
                if (!TextUtils.isEmpty(textInputEmail?.editText?.text) && !TextUtils.isEmpty(
                        textInputUsername?.editText?.text
                    ) && !TextUtils.isEmpty(
                        textInputPassword?.editText?.text
                    )
                ) {
                    singUpButton.alpha = 1f
                } else {
                    singUpButton.alpha = 0.65f
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // Process the textInputPassword editText when typing password
        textInputPassword?.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                textInputPassword?.editText?.setTextColor(Color.DKGRAY)
                textInputPassword?.setError(null)
                textInputPassword?.editText?.setCompoundDrawables(null, null, null, null)
                if (!TextUtils.isEmpty(textInputEmail?.editText?.text) && !TextUtils.isEmpty(
                        textInputUsername?.editText?.text
                    ) && !TextUtils.isEmpty(
                        textInputPassword?.editText?.text
                    )
                ) {
                    singUpButton.alpha = 1f
                } else {
                    singUpButton.alpha = 0.65f
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    fun RegisterNewUser(email: String, username: String, password: String){
        var userEntity: UserEntity = UserEntity(
            id = 0,
            email = email,
            username = username,
            password = password
        )

        userViewModel?.insert(userEntity)

        var u = userViewModel?.findUserByUsername(userEntity.username)
        Toast.makeText(this, "Added the user", Toast.LENGTH_LONG).show()
        finish()
    }

    // Check the Email in textInputEmail EditText
    fun validateEmail(): Boolean {
        var usernameInput: String = textInputEmail?.editText?.text.toString().trim()
        if (usernameInput.isEmpty()){
            textInputEmail?.error = " "
            textInputEmail?.editText?.setError("Field can't be empty", ErrorDrawable)
            return false
        } else {
            textInputEmail?.error = null
            return true
        }
    }

    // Check the Username in textInputUsername EditText
    fun validateUsername(): Boolean {
        var usernameInput: String = textInputUsername?.editText?.text.toString().trim()
        if (usernameInput.isEmpty()){
            textInputUsername?.error = " "
            textInputUsername?.editText?.setError("Field can't be empty", ErrorDrawable)
            return false
        } else {
            textInputUsername?.error = null
            return true
        }
    }

    // Check the Password in textInputPassword EditText
    fun validatePassword(): Boolean {
        var usernameInput: String = textInputPassword?.editText?.text.toString().trim()
        if (usernameInput.isEmpty()){
            textInputPassword?.error = "Field can't be empty"
            return false
        } else {
            textInputPassword?.error = null
            return true
        }
    }

    // Close the keyboard
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}
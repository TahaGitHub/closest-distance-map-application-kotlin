package com.example.myapplication.activities

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mapapplicationkotlin.data.place.PlaceViewModel
import com.example.mapapplicationkotlin.data.user.UserViewModel
import com.example.myapplication.PermissionRequest
import com.example.myapplication.R
import com.google.android.material.textfield.TextInputLayout


class MainActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private var textInputUsername: TextInputLayout? = null
    private var textInputPassword: TextInputLayout? = null

    lateinit var loadingBar: ProgressDialog
    lateinit var ErrorDrawable: Drawable

    private var userViewModel: UserViewModel? = null
    private var placeViewModel: PlaceViewModel? = null

    var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.INTERNET,
        Manifest.permission.VIBRATE
    )

    companion object {
        @JvmField
        var user_id: Long = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkPermission()){
            requestPermission()
        }
        setContentView(R.layout.activity_main)

        PermissionRequest().checkNetworkConnected(this)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        var userEntity = userViewModel?.findUserById(1)

        textInputUsername = findViewById<TextInputLayout>(R.id.text_input_username)
        textInputPassword = findViewById(R.id.text_input_password)

        ErrorDrawable = resources.getDrawable(R.drawable.icon_error)
        ErrorDrawable.setBounds(
            0,
            0,
            ErrorDrawable.getIntrinsicWidth(),
            ErrorDrawable.getIntrinsicHeight()
        )

        loadingBar = ProgressDialog(this)

        val loginButton = findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener()
        {
            validateUsername()
            validatePassword()

            if (validateUsername() && validatePassword()) {
                SignInUser(
                    textInputUsername?.editText?.text.toString(),
                    textInputPassword?.editText?.text.toString()
                )
            } else {
                Toast.makeText(this, "Email or password error", Toast.LENGTH_SHORT)
            }
        }

        // Process the InputUserName editText when typing usename
        textInputUsername?.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                textInputUsername?.editText?.setTextColor(Color.DKGRAY)
                textInputUsername?.error = null
                textInputUsername?.editText?.setCompoundDrawables(null, null, null, null)
                if (!TextUtils.isEmpty(textInputUsername?.editText?.text) && !TextUtils.isEmpty(
                        textInputUsername?.editText?.text
                    )
                ) {
                    loginButton.alpha = 1f
                } else {
                    loginButton.alpha = 0.65f
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // Process the InputUserPassword editText when typing password
        textInputPassword?.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                textInputPassword?.editText?.setTextColor(Color.DKGRAY)
                textInputPassword?.setError(null)
                if (!TextUtils.isEmpty(textInputPassword?.editText?.text) && !TextUtils.isEmpty(
                        textInputPassword?.editText?.text
                    )
                ) {
                    loginButton.alpha = 1f
                } else {
                    loginButton.alpha = 0.65f
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        textInputUsername?.editText?.setText("admin")
        textInputPassword?.editText?.setText("123")
        Toast.makeText(getApplicationContext(), "Admin user add automatically", Toast.LENGTH_SHORT).show()
    }

    // Check the permission is granted or not
    fun checkPermission(): Boolean {
        return PermissionRequest().hasPermissions(this, *permissions)
    }

    // Request the permission if is not granted
    fun requestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 200)
    }

    // Show the meesage to give permission or close the application
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(getApplicationContext(), "İzin Verildi", Toast.LENGTH_SHORT).show()
            } else {
//                Toast.makeText(getApplicationContext(), "İzin Reddedildi", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        showMessageOKCancel(
                            { dialogInterface, i ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermission()
                                }
                            }, { dialogInterface, i ->
                                finish()
                                System.exit(0)
                            }
                        )
                    }
                }
            }
        }
    }

    // Permission message dialog
    fun showMessageOKCancel(
        okListener: DialogInterface.OnClickListener,
        cancelListener: DialogInterface.OnClickListener
    ){
        val alert = AlertDialog.Builder(this)
            .setMessage("Erişim izinlerine izin vermeniz gerekiyor")
            .setPositiveButton("Evet", okListener)
            .setNegativeButton("Hayır", cancelListener)
            .create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
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

    // Login used with "Giris button" to check and loging to next activity "Map Activity"
    fun SignInUser(username: String, password: String) {
        loadingBar.setTitle("Giriyor")
        loadingBar.setMessage("Bekle Lüften...")
        loadingBar.setCanceledOnTouchOutside(true)
        loadingBar.show()

        var userEntity = userViewModel?.findUserByUsernameAndPassword(username, password)

        println(userEntity?.id)

        if (userEntity != null){
            textInputUsername?.editText?.setTextColor(Color.DKGRAY)
            textInputUsername?.error = null
            textInputUsername?.editText?.setCompoundDrawables(null, null, null, null)

            val intent: Intent = Intent(this, MapsActivity::class.java)
            user_id = userEntity.id
            startActivity(intent)
        } else {
            Toast.makeText(this, "Check the UserName or Password", Toast.LENGTH_SHORT).show()

            var u = userViewModel?.findUserByUsername(username)
            if (u != null){
                textInputPassword?.error = "Check the password"

            } else {
                textInputUsername?.error = " "
                textInputUsername?.editText?.setError("No User by this name", ErrorDrawable)
                textInputPassword?.error = "Check the password"
            }
        }
        loadingBar.dismiss()
    }

    // Sing Up new user used with "Uye Ol button"
    fun SingUpButton(view: View) {
        val intent: Intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
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
package com.tugasakhir.resq.rescuer.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tugasakhir.resq.base.view.MainActivity
import com.tugasakhir.resq.R
import com.tugasakhir.resq.rescuer.model.Rescuer
import kotlinx.android.synthetic.main.activity_rescuer_signup.*


class SignUpRescuerActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var firebaseAuth: FirebaseAuth
    private var passwordShown = false
    private var repeatPasswordShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rescuer_signup)

        actionBar = this.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.signup_actionbar)
        actionBar.elevation = 0F

        firebaseAuth = FirebaseAuth.getInstance()

        button_signup_tosignin.setOnClickListener {
            val intent = Intent(this, SignInRescuerActivity::class.java)
            startActivity(intent)
            finish()
        }

        textview_max_char_name.text =
            getString(R.string.max_char_50, edittext_signup_name.text.length)
        edittext_signup_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                textview_max_char_name.text = getString(R.string.max_char_50, p0?.length)
            }
        })

        button_signup_finish.setOnClickListener {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (currentFocus != null) inputMethodManager.hideSoftInputFromWindow(
                currentFocus!!.applicationWindowToken, 0
            )
            registerUser()
        }

        button_show_password.setOnClickListener {
            val cursor = edittext_signup_password.selectionStart
            if (!passwordShown) {
                passwordShown = true
                button_show_password.setImageResource(R.drawable.ic_password_hide)
                edittext_signup_password.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

                edittext_signup_password.setSelection(cursor)
            } else {
                passwordShown = false
                button_show_password.setImageResource(R.drawable.ic_password_show)
                edittext_signup_password.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                edittext_signup_password.setSelection(cursor)
            }
        }
        button_show_repeat_password.setOnClickListener {
            val cursor = edittext_signup_repeat_password.selectionStart
            if (!repeatPasswordShown) {
                repeatPasswordShown = true
                button_show_repeat_password.setImageResource(R.drawable.ic_password_hide)
                edittext_signup_repeat_password.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

                edittext_signup_repeat_password.setSelection(cursor)
            } else {
                repeatPasswordShown = false
                button_show_repeat_password.setImageResource(R.drawable.ic_password_show)
                edittext_signup_repeat_password.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                edittext_signup_repeat_password.setSelection(cursor)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addNewRescuerAccount(
        email: String, password: String, name: String,
        phone: String, employeeId: String,
        instansi: String, division: String
    ) {

        progressbar_signup.visibility = View.VISIBLE
        button_signup_finish.isClickable = false
        button_signup_finish.setBackgroundResource(R.drawable.shape_filled_button_clicked)
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    val id = FirebaseAuth.getInstance().currentUser!!.uid
                    val rescuer = Rescuer(
                        id,
                        name,
                        email,
                        "",
                        phone,
                        instansi,
                        division,
                        employeeId,
                        false
                    )
                    FirebaseDatabase.getInstance().getReference("Rescuers")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(rescuer).addOnCompleteListener { task ->
                            progressbar_signup.visibility = View.GONE
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this@SignUpRescuerActivity,
                                    getString(R.string.toast_account_created), Toast.LENGTH_LONG
                                ).show()

                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()

                            } else {
                                button_signup_finish.isClickable = true
                                button_signup_finish.setBackgroundResource(R.drawable.shape_filled_button)
                                Toast.makeText(
                                    this@SignUpRescuerActivity,
                                    task.exception?.message, Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                } else {
                    progressbar_signup.visibility = View.GONE
                    button_signup_finish.isClickable = true
                    button_signup_finish.setBackgroundResource(R.drawable.shape_filled_button)
                    Toast.makeText(
                        this@SignUpRescuerActivity,
                        p0.exception?.message, Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun registerUser() {
        val name = edittext_signup_name.text.toString().trim()
        val email = edittext_signup_email.text.toString().trim()
        val phone = "+62${edittext_signup_phone.text.toString().trim()}"
        val employeeID = edittext_signup_employeenum.text.toString().trim()
        val password = edittext_signup_password.text.toString().trim()
        val repeatPassword = edittext_signup_repeat_password.text.toString().trim()
        val instansi = spinner_signup_instansi.selectedItem.toString()
        val division = spinner_signup_division.selectedItem.toString()

        if (name.isEmpty()) {
            edittext_signup_name.error = getString(R.string.field_is_empty)
            edittext_signup_name.requestFocus()
            return
        }

        if (email.isEmpty()) {
            edittext_signup_email.error = getString(R.string.field_is_empty)
            edittext_signup_email.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edittext_signup_email.error = getString(R.string.email_is_not_valid)
            edittext_signup_email.requestFocus()
            return
        }

        if (phone.isEmpty()) {
            edittext_signup_phone.error = getString(R.string.field_is_empty)
            edittext_signup_phone.requestFocus()
            return
        }

        if (phone.length !in 16 downTo 9) {
            edittext_signup_phone.error = getString(R.string.phone_is_not_valid)
            edittext_signup_phone.requestFocus()
            return
        }

        if (employeeID.isEmpty()) {
            edittext_signup_employeenum.error = getString(R.string.field_is_empty)
            edittext_signup_employeenum.requestFocus()
            return
        }

        if (password.length < 6) {
            edittext_signup_password.error = getString(R.string.password_must_six_char)
            edittext_signup_password.requestFocus()
            return
        }

        if (password.isEmpty()) {
            edittext_signup_password.error = getString(R.string.field_is_empty)
            edittext_signup_password.requestFocus()
            return
        }

        if (repeatPassword.isEmpty()) {
            edittext_signup_repeat_password.error = getString(R.string.field_is_empty)
            edittext_signup_repeat_password.requestFocus()
            return
        }

        if (repeatPassword != password) {
            edittext_signup_repeat_password.error = getString(R.string.password_is_not_matched)
            edittext_signup_repeat_password.requestFocus()
            return
        }

        addNewRescuerAccount(email, password, name, phone, employeeID, instansi, division)

    }
}
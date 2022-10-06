package com.rtm.firebase

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.rtm.firebase.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var resend: PhoneAuthProvider.ForceResendingToken
    lateinit var callBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationId: String? = ""

    lateinit var preference: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preference = getSharedPreferences("baza", MODE_PRIVATE)
        if (isUserRegistered()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()

        }
        auth = FirebaseAuth.getInstance()
        binding.btnSendPhoneNumber.setOnClickListener {
            val number = binding.edNumber.text.toString()
            sentNumberToServer(number)
        }
        binding.btnSendOtp.setOnClickListener {
            val otp = binding.editOtp.text.toString()
            val crendential = PhoneAuthProvider.getCredential(verificationId.toString(), otp)
            signInWithCredential(crendential)
        }


        callBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(p0: FirebaseException) {

            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                resend = p1
                verificationId = p0
            }
        }
    }

    fun sentNumberToServer(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    saveUser()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun saveUser() {
        val myEdit = preference.edit()
        myEdit.putBoolean("isRegistered", true)
        myEdit.apply()
    }

    fun isUserRegistered(): Boolean {
        return preference.getBoolean("isRegistered", false)
    }
}

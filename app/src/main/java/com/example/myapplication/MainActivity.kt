package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import io.synaps.OnFinishListener
import io.synaps.OnInitListener
import io.synaps.OnSignListener
import io.synaps.PersonhoodButton

class MainActivity : AppCompatActivity() {
    private var pop: PersonhoodButton? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pop = findViewById(R.id.personhoodButton)

        pop!!.setOnFinishListener { session ->
            Log.d("Personhood", "finish: " + session.info)
        }

        pop!!.setOnSignListener { message, onSuccess, onError ->
            Log.d("Personhood", "sign: $message")
            try {
                onSuccess.Sign("signed message")
            } catch (e: Exception) {
                onError.Error()
            }
        }

        try {
            // An already validated personhood id
            pop!!.launch("9441475b-5d67-45a3-ab06-afc5d62a2a97");
        } catch (e: CameraAccessException) {
            ActivityCompat.requestPermissions(this,
                arrayOf<String>( Manifest.permission.CAMERA),
                10);
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            10 -> if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                pop!!.launch("9441475b-5d67-45a3-ab06-afc5d62a2a97");
                Toast.makeText(this@MainActivity, "Permission Granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
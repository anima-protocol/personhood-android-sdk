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

        pop!!.setOnFinishListener(OnFinishListener {
            Log.d("Personhood", "finish" + it.info.toString())
        })

        pop!!.setOnSignListener(OnSignListener {
            Log.d("Personhood", "sign" + it)
            pop!!.sign(it, "0x123")
        })

        pop!!.setOnInitListener(OnInitListener {
            Log.d("Personhood", "init")
        })

        try {
            // An already validated personhood id
            pop!!.launch("02da7eb6-65a7-40ef-bf60-e18e798916bb");
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
                pop!!.launch("02da7eb6-65a7-40ef-bf60-e18e798916bb");
                Toast.makeText(this@MainActivity, "Permission Granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
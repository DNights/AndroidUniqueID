package dev.dnights.androiduniqueid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //tv_device_id.text = UniqueID.getDeviceID(this)
        //tv_device_id.text = UniqueID.getSimSerialNumber(this)
        //tv_device_id.text = UniqueID.getAndroidID(this)
        tv_device_id.text = UniqueID.getWidevineID()
    }
}

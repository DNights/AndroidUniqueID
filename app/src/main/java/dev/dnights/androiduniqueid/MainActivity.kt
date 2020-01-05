package dev.dnights.androiduniqueid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //tv_device_id.text = UniqueID.getDeviceID(this) @Deprecated
        tv_sim_serial_num.text = UniqueID.getSimSerialNumber(this)
        tv_android_id.text = UniqueID.getAndroidID(this)
        tv_widevine_id.text = UniqueID.getWidevineID()
    }
}

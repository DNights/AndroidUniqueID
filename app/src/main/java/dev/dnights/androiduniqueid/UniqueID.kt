package dev.dnights.androiduniqueid

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaDrm
import android.media.UnsupportedSchemeException
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import java.util.*

object UniqueID {

    @SuppressLint("MissingPermission", "HardwareIds")
    fun getDeviceID(context: Context) : String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.deviceId
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    fun getSimSerialNumber(context: Context): String{
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.simSerialNumber
    }

    @SuppressLint("HardwareIds")
    fun getAndroidID(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun getWidevineID(): String {
        val WIDEVINE_UUID = UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)

        val wvDrm = try {
            MediaDrm(WIDEVINE_UUID)
        } catch (e: UnsupportedSchemeException) {
            //WIDEVINE is not available
            null
        }

        val widevineId = wvDrm!!.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID)
        val encodedWidevineId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getEncoder().encodeToString(widevineId).trim()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        return encodedWidevineId
    }

}
package com.outsystems

import android.app.Activity
import android.content.Intent
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.CallbackContext
import org.json.JSONArray
import org.json.JSONException
import com.yubico.yubikit.android.YubiKitManager
import com.yubico.yubikit.android.ui.OtpActivity
import com.yubico.yubikit.core.application.CommandException
import com.yubico.yubikit.management.DeviceInfo
import com.yubico.yubikit.management.ManagementSession
import com.yubico.yubikit.android.transport.nfc.NfcConfiguration
import com.yubico.yubikit.android.transport.nfc.NfcNotAvailable
import java.io.IOException

class yubico : CordovaPlugin() {

    private var callbackContext: CallbackContext? = null

    @Throws(JSONException::class)
    override fun execute(action: String, args: JSONArray, callbackContext: CallbackContext): Boolean {
        this.callbackContext = callbackContext

        return when (action) {
            "getOTP" -> {
                getOTP()
                true
            }
            "startNFCDiscovery" -> {
                startNFCDiscovery(callbackContext)
                true
            }
            "stopNFCDiscovery" -> {
                stopNFCDiscovery(callbackContext)
                true
            }
            else -> false
        }
    }

    private fun startNFCDiscovery(callbackContext: CallbackContext) {
        val yubiKitManager = YubiKitManager(cordova.context)

        try {
            yubiKitManager.startNfcDiscovery(NfcConfiguration(), cordova.activity) { device ->
                ManagementSession.create(device) { result ->
                    try {
                        val management = result.value
                        val info = management.deviceInfo
                        val serialNumber = info.serialNumber
                        if(serialNumber != null) {
                            callbackContext.success(serialNumber)
                        } else {
                            callbackContext.error("Error #001: Could not read YubiKey Serial Number.")
                        }
                        
                    } catch (e: IOException) {
                        callbackContext.error("Error #001: Could not read YubiKey Serial Number.")
                    } catch (e: CommandException) {
                        callbackContext.error("Error #001: Could not read YubiKey Serial Number.")
                    } catch (e: Exception) {
                        callbackContext.error("Error #001: Could not read YubiKey Serial Number.")
                    }
                }
            }
        } catch (e: NfcNotAvailable) {
            if (e.isDisabled) {
                callbackContext.error("Error #002: Android NFC is turned off.")
            } else {
                callbackContext.error("Error #003: This device is not supported.")
            }
        }
    }

    private fun stopNFCDiscovery(callbackContext: CallbackContext) {
        val yubiKitManager = YubiKitManager(cordova.context)
        yubiKitManager.stopNfcDiscovery(cordova.activity)
        callbackContext.success("NFC Discovery stopped")
    }

    private fun getOTP() {
        cordova.startActivityForResult(this, Intent(cordova.context, OtpActivity::class.java), 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val otp = data?.getStringExtra("otp")
            if (otp != null) {
                callbackContext?.success(otp)
            } else {
                callbackContext?.error("Error #004: Could not read OTP.")
            }
        } else {
            callbackContext?.error("Error #004: Could not read OTP.")
        }
    }
}
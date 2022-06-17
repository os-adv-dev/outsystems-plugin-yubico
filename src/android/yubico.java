package com.outsystems;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yubico.yubikit.android.YubiKitManager;
import com.yubico.yubikit.android.transport.usb.UsbConfiguration;
import com.yubico.yubikit.android.ui.OtpActivity;
import com.yubico.yubikit.core.application.ApplicationNotAvailableException;
import com.yubico.yubikit.core.smartcard.Apdu;
import com.yubico.yubikit.core.smartcard.SmartCardConnection;
import com.yubico.yubikit.core.smartcard.SmartCardProtocol;
import com.yubico.yubikit.yubiotp.HmacSha1SlotConfiguration;
import com.yubico.yubikit.yubiotp.Slot;
import com.yubico.yubikit.yubiotp.YubiOtpSession;
import com.yubico.yubikit.core.YubiKeyDevice;

import com.yubico.yubikit.android.YubiKitManager;
import com.yubico.yubikit.android.transport.nfc.NfcConfiguration;
import com.yubico.yubikit.android.transport.nfc.NfcNotAvailable;
import com.yubico.yubikit.android.transport.nfc.NfcYubiKeyManager;
import com.yubico.yubikit.android.transport.usb.UsbConfiguration;
import com.yubico.yubikit.core.Logger;
import com.yubico.yubikit.core.YubiKeyDevice;
import com.yubico.yubikit.core.application.CommandState;

import java.io.IOException;

import io.cordova.hellocordova.MainActivity;
import io.cordova.hellocordova.R;

/**
 * This class echoes a string called from JavaScript.
 */
public class yubico extends CordovaPlugin {

    YubiKitManager yubiKitManager = new YubiKitManager(cordova.getContext());

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void YubiOtpSession(CallbackContext callbackContext) {
        //YubiKitManager yubiKitManager = new YubiKitManager(cordova.getContext());
        yubiKitManager.startUsbDiscovery(new UsbConfiguration(), device -> {
            // A YubiKey was plugged in
            if(!device.hasPermission()) {
                // Using the default UsbConfiguration this will never happen, as permission will automatically
                // be requested by the YubiKitManager, and this method won't be invoked unless it is granted.

                //(((Activity) cordova.getContext())).startActivityForResult(new Intent(cordova.getContext(), OtpActivity.class), requestCode);

                /*YubiOtpSession.create(device, result -> {
                    try {
                        YubiOtpSession otp = result.getValue();
                        // Program a Challenge-Response credential in slot 2:
                        otp.putConfiguration(
                                Slot.TWO,
                                new HmacSha1SlotConfiguration(hmacKey),
                                null,
                                null
                                );

                        // Use the credential:
                        byte[] response = otp.calculateHmacSha1(Slot.TWO, challenge, null);
                    } catch (Throwable e) {
                        // Handle errors
                    }
                });*/
            }

            device.setOnClosed(() -> {
                // Do something when the YubiKey is removed
            });
        });
    }

    private void enableLogging(){
        Logger.setLogger(new Logger() {
            @Override
            protected void logDebug(String message) {
                Log.d("Yubikey logDebug: ", message);
            }

            @Override
            protected void logError(String message, Throwable throwable) {
                Log.e("Yubikey logError", message, throwable);
            }
        });
    }

//    private void connect2YubiKey(YubiKeyDevice device){
//        // Request a new SmartCardConnection:
//        device.requestConnection(SmartCardConnection.class, result -> {
//            // The result is a Result<SmartCardConnection, IOException>, which represents either a successful connection, or an error.
//            try {
//                SmartCardConnection connection = result.getValue();  // This may throw an IOException
//                // The SmartCardProtocol offers a the ability of sending APDU-based smartcard commands
//                SmartCardProtocol protocol = new SmartCardProtocol(connection);
//                byte[] aid = new byte[] {0xA0, 0x00, 0x00, 0x03, 0x08};
//                protocol.select(aid);  // Select a smartcard application (this may throw an ApplicationNotFoundException)
//                protocol.sendAndReceive(new Apdu(0x00, 0xA4, 0x00, 0x00)));
//            } catch(IOException | ApplicationNotAvailableException e) {
//                // Handle errors
//            }
//        });
//    }

    private void startNFCDiscovery(CallbackContext callbackContext) {
        //YubiKitManager yubiKitManager = new YubiKitManager(cordova.getContext());
        try {
            yubiKitManager.startNfcDiscovery(new NfcConfiguration(), cordova.getActivity(), device -> {
                // A YubiKey was brought within NFC range
                showNotification("Alert", "A YubiKey was brought within NFC range");

                int requestCode = 0;
                (((Activity) cordova.getContext())).startActivityForResult(new Intent(cordova.getContext(), OtpActivity.class), requestCode);


            });
        } catch ( NfcNotAvailable e) {
            if (e.isDisabled()) {
                // show a message that user needs to turn on NFC for this feature
                showNotification("Alert", "turn on NFC for this feature");
            } else {
                // NFC is not available so this feature does not work on this device
                showNotification("Alert", "this feature does not work on this device");
            }
        }

    }

    public void stopNFCDiscovery() {
        yubiKitManager.stopNfcDiscovery(cordova.getActivity());
    }

    private void startUsbDiscovery(CallbackContext callbackContext) {
        YubiKitManager yubiKitManager = new YubiKitManager(cordova.getContext());
        yubiKitManager.startUsbDiscovery(new UsbConfiguration(), device -> {
            // A YubiKey was plugged in
            if(!device.hasPermission()) {
                // Using the default UsbConfiguration this will never happen, as permission will automatically
                // be requested by the YubiKitManager, and this method won't be invoked unless it is granted.
            }

            device.setOnClosed(() -> {
                // Do something when the YubiKey is removed
            });
        });



        /*if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }*/
    }

/*    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            String otp = data.getStringExtra(OtpActvity.EXTRA_OTP);
        }
    }*/

    public void showNotification(String title, String message) {
        int reqCode = 1;
        Intent intent = new Intent(cordova.getContext(), MainActivity.class);

        Context context = cordova.getContext();
        PendingIntent pendingIntent = PendingIntent.getActivity(cordova.getContext(), reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        String CHANNEL_ID = "channel_name";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id

        Log.d("showNotification", "showNotification: " + reqCode);
    }

}

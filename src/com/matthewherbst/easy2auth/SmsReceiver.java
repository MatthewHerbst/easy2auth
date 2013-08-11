package com.matthewherbst.easy2auth;

import android.content.*;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] recievedMsgs;
        String str = "";
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            recievedMsgs = new SmsMessage[pdus.length];
            for (int i = 0; i < recievedMsgs.length; i++) {
                recievedMsgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                str += recievedMsgs[i].getMessageBody().toString();
            }
        }
        Pattern authCode = Pattern.compile(".*[\\D^]?(\\d\\d\\d\\d\\d\\d)[\\D$]?.*");
        Matcher matcher = authCode.matcher(str);
        matcher.matches();
        String group = matcher.group(1);
        Log.i("MATTHEW", "full match: " + matcher.group() + " six digit match: " + matcher.group(1));

        ClipboardManager clipboard = (ClipboardManager)
            context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("two-factor auth key", group);
        clipboard.setPrimaryClip(clip);

        String message = "Copied " + group + " to clipboard";
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

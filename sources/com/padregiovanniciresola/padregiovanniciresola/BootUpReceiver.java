package com.padregiovanniciresola.padregiovanniciresola;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BootUpReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        new Intent(context, MainActivity.class).addFlags(268435456);
        Intent intent2 = new Intent(context, service.class);
        intent2.putExtra("ServiceForroZappMain", "ForroZappMain");
        if (Build.VERSION.SDK_INT >= 26) {
            context.startService(intent2);
        } else {
            context.startService(intent2);
        }
    }
}

package com.kai_jan_57.showstacktrace.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.kai_jan_57.showstacktrace.activity.ShowStackTraceActivity;
import com.kai_jan_57.showstacktrace.xposed.Hook;

public class CrashReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Hook.INTENT_ACTION)) {

            Intent i = new Intent(context, ShowStackTraceActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(Hook.INTENT_PACKAGE_NAME, intent.getStringExtra(Hook.INTENT_PACKAGE_NAME))
                    .putExtra(Hook.INTENT_THROWABLE, intent.getSerializableExtra(Hook.INTENT_THROWABLE))
                    .putExtra(Hook.INTENT_TIME, intent.getLongExtra(Hook.INTENT_TIME, 0))
                    .putExtra(Hook.INTENT_SHOWDIALOG, true);
            context.startActivity(i);
        }
    }
}
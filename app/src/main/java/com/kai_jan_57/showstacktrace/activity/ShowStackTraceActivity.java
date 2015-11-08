package com.kai_jan_57.showstacktrace.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kai_jan_57.showstacktrace.R;
import com.kai_jan_57.showstacktrace.xposed.Hook;


public class ShowStackTraceActivity extends Activity {

    private String Package_name;
    private long time;
    private Throwable throwable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent myIntent = getIntent();
        Package_name = myIntent.getStringExtra(Hook.INTENT_PACKAGE_NAME);
        time = myIntent.getLongExtra(Hook.INTENT_TIME, 0);
        throwable = (Throwable)myIntent.getSerializableExtra(Hook.INTENT_THROWABLE);
        if(myIntent.getBooleanExtra(Hook.INTENT_SHOWDIALOG, false)) {
            askuser();
        }
        else {
            showStackTrace();
        }
        super.onCreate(savedInstanceState);
    }

    private void showStackTrace() {
        setTheme(R.style.AppCompat_Light);
        setContentView(R.layout.activity_show_stack_trace);
        setContent(getContent(throwable));
    }

    private void setContent(String content) {
        TextView t1 = (TextView)findViewById(R.id.textView);
        t1.setText(content);
    }

    private String getContent(Throwable throwable) {
        if (throwable == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        String string = throwable.toString() + "\n";
        builder.append(string);

        StackTraceElement[] elements = throwable.getStackTrace();
        if (elements != null) {
            for (StackTraceElement element : elements) {
                string = "    " + getString(R.string.at) + " " + element.toString() + "\n";
                builder.append(string);
            }
        }

        Throwable cause = throwable.getCause();
        if (cause != null) {
            string = getString(R.string.causedby) + " " + getContent(cause);
            builder.append(string);
        }

        return builder.toString();
    }

    private void askuser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton(R.string.report, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ShowStackTraceActivity.this.setIntent(new Intent().putExtra(Hook.INTENT_THROWABLE, throwable).putExtra(Hook.INTENT_PACKAGE_NAME, Package_name).putExtra(Hook.INTENT_TIME, time));
                ShowStackTraceActivity.this.recreate();
            }
        });
        builder.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                ShowStackTraceActivity.this.exit(null);
            }
        });

        builder.setCancelable(true);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        // Set other dialog properties
        builder.setMessage("\"" + Package_name + "\" " + getString(R.string.question_showstacktrace));

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void exit(View v) {
        this.finish();
    }

}

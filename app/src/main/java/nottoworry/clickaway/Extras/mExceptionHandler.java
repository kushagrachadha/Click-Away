package nottoworry.clickaway.Extras;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by sahil on 7/4/17.
 *
 *
 */

public class mExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
    private final Activity myContext;

    public mExceptionHandler(Activity context) {
        myContext = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();
        errorReport.append(stackTrace.toString());
        Log.d("EXP", errorReport.toString());

        Bundle b = new Bundle();
        b.putString("CAUSE_OF_ERROR", exception.getMessage()+stackTrace.toString());

        Intent intent = new Intent(myContext, ForceCloseActivity.class);
        intent.putExtras(b);
        myContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }
}

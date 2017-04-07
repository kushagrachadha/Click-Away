package nottoworry.clickaway.Extras;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import nottoworry.clickaway.MainActivity;
import nottoworry.clickaway.connections.myHTTP;
import nottoworry.myapp.R;

/**
 * Created by sahil on 7/4/17.
 *
 */

public class ForceCloseActivity extends AppCompatActivity {

    JSONObject obj;
    String COE, DB, DN, DM, DI, P, S, SR, SI, TIME, AI;
    private final String LINE_SEPARATOR = "\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new mExceptionHandler(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fc_activity);

        Toast.makeText(this, "Oh Ho, You have found a bug. :)", Toast.LENGTH_LONG).show();

        obj = new JSONObject();
        Bundle data = getIntent().getExtras();
        assert data !=null;
        COE = data.getString("CAUSE_OF_ERROR");
        DB = Build.BRAND;
        DN = Build.DEVICE;
        DM = Build.MODEL;
        DI = Build.ID;
        P = Build.PRODUCT;
        S = Build.VERSION.CODENAME+" "+Build.VERSION.SDK_INT;
        SR = Build.VERSION.RELEASE;
        SI = Build.VERSION.INCREMENTAL;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            AI = (pInfo.versionName+" "+ pInfo.versionCode);
        }catch (Exception e){
            AI = "ERROR "+e.getMessage();
        }

        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TIME = df.format(c.getTime());

        try{
            obj.accumulate("TIME", TIME);
            obj.accumulate("CAUSE_OF_ERROR",COE);
            obj.accumulate("DEVICE_BRAND",DB);
            obj.accumulate("DEVICE_NAME",DN);
            obj.accumulate("DEVICE_MODEL",DM);
            obj.accumulate("DEVICE_ID",DI);
            obj.accumulate("PRODUCT",P);
            obj.accumulate("SDK",S);
            obj.accumulate("SDK_RELEASE",SR);
            obj.accumulate("SDK_INCREMENTAL",SI);
            obj.accumulate("APP_INFO", AI);
        }catch (Exception e){
            e.printStackTrace();
        }


        StringBuilder errorReport2 = new StringBuilder();
        errorReport2.append("************ CAUSE OF ERROR ************\n\n");
        errorReport2.append(COE);

        errorReport2.append("\n************ DEVICE INFORMATION ***********\n");
        errorReport2.append("Brand: ");
        errorReport2.append(DB);
        errorReport2.append(LINE_SEPARATOR);
        errorReport2.append("Device: ");
        errorReport2.append(DB);
        errorReport2.append(LINE_SEPARATOR);
        errorReport2.append("Model: ");
        errorReport2.append(DM);
        errorReport2.append(LINE_SEPARATOR);
        errorReport2.append("Id: ");
        errorReport2.append(DI);
        errorReport2.append(LINE_SEPARATOR);
        errorReport2.append("Product: ");
        errorReport2.append(P);
        errorReport2.append(LINE_SEPARATOR);
        errorReport2.append("\n************ FIRMWARE ************\n");
        errorReport2.append("SDK: ");
        errorReport2.append(S);
        errorReport2.append(LINE_SEPARATOR);
        errorReport2.append("Release: ");
        errorReport2.append(SR);
        errorReport2.append(LINE_SEPARATOR);
        errorReport2.append("Incremental: ");
        errorReport2.append(SI);
        errorReport2.append(LINE_SEPARATOR);

        ((TextView)findViewById(R.id.fc_ai)).setText(errorReport2.toString());

    }

    public void sendButton(View v){
        Toast.makeText(this, "Thanks for helping us", Toast.LENGTH_LONG).show();
        new sendReport().execute(obj);
    }

    public void cancelButton(View v){
        Toast.makeText(this, "You could have helped us :(", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MainActivity.class));
        Toast.makeText(this, "Restarting Application", Toast.LENGTH_SHORT).show();
    }

    private class sendReport extends AsyncTask<JSONObject, Void, String> {
        @Override
        protected String doInBackground(JSONObject... params) {
            try{
                Log.d("FC Data", params[0].toString());
                myHTTP.instance().postJson("/droiderrors", params[0]);
            }catch (Exception e){
                e.printStackTrace();
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            if(aVoid== null)
                Toast.makeText(getApplicationContext(), "Report is sent", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "It looks like it's bug's season.\nError: "
                        +aVoid, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

}

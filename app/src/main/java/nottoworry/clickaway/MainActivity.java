package nottoworry.clickaway;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Set;

import nottoworry.clickaway.Extras.mExceptionHandler;
import nottoworry.clickaway.connections.myGPSProvider;
import nottoworry.clickaway.connections.myHTTP;
import nottoworry.myapp.R;

public class MainActivity extends AppCompatActivity {

    private boolean DevMode = false;
    int helpSound = R.raw.helpsound;
    Set<String> address, names;
    public static Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new mExceptionHandler(this));
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_activity);
        SharedPreferences myPrefs = getSharedPreferences("basic", 0);
        if(!myPrefs.getBoolean("SetupDone", false)){
            startActivity(new Intent(this, IntroActivity.class));
            finish();
        }
        myGPSProvider gps = new myGPSProvider(this);
        gps.refresh(this);

        helpSound = myPrefs.getInt("helpSound", R.raw.helpsound);
        address = myPrefs.getStringSet("emergencyAdd", null);
        names = myPrefs.getStringSet("emergencyNames", null);

        startService(new Intent(this, myGPSProvider.class));
        String url = "http://desolate-bastion-73012.herokuapp.com/setup";
        as a = new as();
        a.execute(url);
    }

    class as extends AsyncTask<String, Void, String>{
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result!=null)
            Log.v("get", result);
            else
                Log.v("get", "result is null");
        }

        @Override
        protected String doInBackground(String... params) {
            myHTTP my = myHTTP.instance();
            try{
                JSONObject object = new JSONObject();
                object.accumulate("lat", 28.7216);
                object.accumulate("long", 77.120);
                return my.postJson(params[0], object);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private MediaPlayer mediaPlayer;
    public void helpMeBtn(View v){
        if(DevMode)
            Toast.makeText(this, "HELP ME Clicked", Toast.LENGTH_SHORT).show();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        SharedPreferences prefs = getSharedPreferences("base", MODE_PRIVATE);
        String temp = prefs.getString("helpSound", null);
        if(temp==null) {
            mediaPlayer = MediaPlayer.create(this, helpSound);
        }else{
            try {
                mediaPlayer = new MediaPlayer();
                temp = getExternalCacheDir().getAbsolutePath() + "/" + temp;
                mediaPlayer.setDataSource(temp);
                mediaPlayer.prepare();
                Log.d("help", temp);
            }catch (Exception e){
                e.printStackTrace();
                Log.d("help", temp);
                Toast.makeText(this, "Error while Playing", Toast.LENGTH_LONG).show();
                mediaPlayer.stop();
                return;
            }
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        sendToALl();
        ((Button)findViewById(R.id.mainBtnHelpMe)).setClickable(false);
        HelpMeAlertDialog dialog = new HelpMeAlertDialog(this);
        dialog.setCancelable(false);
        dialog.show();
    }
    public void emergencyBtn(View v){
        if(DevMode)
            Toast.makeText(this, "Emergency Services Clicked", Toast.LENGTH_SHORT).show();
        sendToALl();
    }
    public void chemistBtn(View v){
        if(DevMode)
            Toast.makeText(this, "Drug Store Clicked", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ChemistActivity.class));
    }
    public void hospitalsBtn(View v){
        if(DevMode)
            Toast.makeText(this, "Hospitals Clicked", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, HospitalsActivity.class));
    }
    public void bloodBtn(View v){
        if(DevMode)
            Toast.makeText(this, "Blood Bank Clicked", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, BloodBankActivity.class));
    }

    private void sendToALl(){
        Toast.makeText(this, "Sending messages to all", Toast.LENGTH_SHORT).show();
        SharedPreferences prefs = getSharedPreferences("basic", Context.MODE_APPEND);
        address = prefs.getStringSet("emergencyNames", null);
        if(address!= null){
            for(String tempAdd : address){
                SmsManager manager = SmsManager.getDefault();
                manager.sendTextMessage(tempAdd, null, "Help needed, Please Help me.", null, null);
            }
        }
        else {
            Toast.makeText(this, "Please add some emergency contacts", Toast.LENGTH_SHORT).show();
        }
    }
}

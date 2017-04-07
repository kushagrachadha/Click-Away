package nottoworry.clickaway;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import nottoworry.clickaway.connections.myGPSProvider;
import nottoworry.clickaway.connections.myHTTP;
import nottoworry.myapp.R;

import static android.content.ContentValues.TAG;


/**
 *
 */

public class IntroActivity extends AppIntro implements
        IntroFragmentDetails.OnFragmentInteractionListener,
        PersonalDetailsFragment.OnFragmentInteractionListener,
        IntroductionFragment.OnFragmentInteractionListener{

    int helpSound = R.raw.helpsound;
    int i =0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    IntroductionFragment fragment3;
    PersonalDetailsFragment fragment2;
    IntroFragmentDetails fragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragment3 = IntroductionFragment.newInstance("","");
        addSlide(fragment3);
        fragment2 = PersonalDetailsFragment.newInstance("hey", "");
        addSlide(fragment2);
        fragment = IntroFragmentDetails.newInstance("", "");
        addSlide(fragment);
        showSkipButton(false);

        myGPSProvider gps = new myGPSProvider(this);
        gps.refresh(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        SharedPreferences prefs = getSharedPreferences("basic",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("SetupDone", true);
        editor.apply();
        super.onDonePressed(currentFragment);
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        i++;
        if(i>1)
            getPermissions();
        if(i==3){
            Toast.makeText(this, "Saving Data", Toast.LENGTH_SHORT).show();
            SharedPreferences prefs = getSharedPreferences("basic", Context.MODE_APPEND);
            SharedPreferences.Editor editor = prefs.edit();
            String age = ((EditText)findViewById(R.id.introPersonAge)).getText().toString();
            String medics = ((EditText)findViewById(R.id.introPersonMedics)).getText().toString();
            String name = ((EditText)findViewById(R.id.introPersonName)).getText().toString();
            String allergies = ((EditText)findViewById(R.id.introPersonAllergies)).getText().toString();
            String blood = ((EditText)findViewById(R.id.introPersonBlood)).getText().toString();
            Log.v("Data saved", name+age+medics+allergies+blood);
            editor.putString("age", age);
            editor.putString("medics", medics);
            editor.putString("name", name);
            editor.putString("allergies", allergies);
            editor.putString("blood", blood);

            editor.commit();
        }
        if(i==4){
            async a = new async();
            a.execute(getApplicationContext());
        }

        super.onSlideChanged(oldFragment, newFragment);
    }

    Database db;
    myHTTP my = myHTTP.instance();
    class async extends AsyncTask<Context, Void, String>{
        @Override
        protected String doInBackground(Context... params) {
            try {
                String url = "http://desolate-bastion-73012.herokuapp.com/setup";
                JSONObject object = new JSONObject();
                myGPSProvider gps = new myGPSProvider(params[0]);
                double t1=gps.getLat(), t2= gps.getLon();
                if(t1==-1|t2==-1){
                    object.accumulate("lat", 28.72);
                    object.accumulate("long", 77.120);
                }else {
                    object.accumulate("lat", t1);
                    object.accumulate("long", t2);
                }
                return my.postJson(url, object);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.v("obj", s);
            db = new Database(getApplicationContext());
            async2 a2 = new async2();
            a2.execute(s);
        }
    }

    class async2 extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            db.setup(params[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(IntroActivity.this, "The db has been created successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SharedPreferences prefs = getSharedPreferences("basic",Context.MODE_APPEND);
        SharedPreferences.Editor editor = prefs.edit();
        if (requestCode == 1504) {
            if (requestCode != Activity.RESULT_OK) {
                Toast.makeText(this, "These permissions were necessary for app to work. Sorry!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                editor.putBoolean("PermissionsDone", true);
                editor.commit();
            }
        }
        else if(resultCode == Activity.RESULT_OK){
            if(requestCode==1101){
                try {
                    Uri uri = data.getData();
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();
                    String con = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Set<String> set = prefs.getStringSet("emergencyNames", new HashSet<String>());
                    set.add(con);
                    editor.putStringSet("emergencyNames", set);
                    editor.commit();
                    ((TextView)findViewById(R.id.contact1)).setText(getName(con));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(requestCode==1102){
                try {
                    Uri uri = data.getData();
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();
                    String con = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Set<String> set = prefs.getStringSet("emergencyNames", new HashSet<String>());
                    set.add(con);
                    editor.putStringSet("emergencyNames", set);
                    editor.commit();
                    ((TextView)findViewById(R.id.contact2)).setText(getName(con));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(requestCode==1103){
                try {
                    Uri uri = data.getData();
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();
                    String con = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Set<String> set = prefs.getStringSet("emergencyNames", new HashSet<String>());
                    set.add(con);
                    editor.putStringSet("emergencyNames", set);
                    editor.commit();
                    ((TextView)findViewById(R.id.contact3)).setText(getName(con));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(requestCode==1104){
                try {
                    Uri uri = data.getData();
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();
                    String con = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Set<String> set = prefs.getStringSet("emergencyNames", new HashSet<String>());
                    set.add(con);
                    editor.putStringSet("emergencyNames", set);
                    editor.commit();
                    ((TextView)findViewById(R.id.contact4)).setText(getName(con));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    MediaPlayer mediaPlayer;
    public void playHelpSound(View v) {
        SharedPreferences prefs = getSharedPreferences("base", MODE_PRIVATE);
        String temp = prefs.getString("helpSound", null);
        if(temp==null) {
            mediaPlayer = MediaPlayer.create(this, helpSound);
            mediaPlayer.start();
        }else{
            try {
                mediaPlayer = new MediaPlayer();
                temp = getExternalCacheDir().getAbsolutePath() + "/" + temp;
                mediaPlayer.setDataSource(temp);
                mediaPlayer.prepare();
                mediaPlayer.start();
                Log.d("help", temp);
            }catch (Exception e){
                e.printStackTrace();
                Log.d("help", temp);
                Toast.makeText(this, "Error while Playing", Toast.LENGTH_LONG).show();
                mediaPlayer.stop();
                return;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.setButton("Stop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setMessage("Playing... \n");
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(mediaPlayer!= null)
                    mediaPlayer.stop();
            }
        });
    }
    MediaRecorder recorder;
    public void recordHelpSound(View v) {
        String fileName = getExternalCacheDir().getAbsolutePath();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        String t = String.valueOf(System.currentTimeMillis());
        recorder.setOutputFile(fileName+"/"+t);
        SharedPreferences.Editor editor = getSharedPreferences("base", Context.MODE_PRIVATE).edit();
        editor.putString("helpSound", t);
        editor.apply();
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        try{
            recorder.prepare();
            recorder.start();
            Log.v("Recording Audio: ", fileName);
        }catch (Exception e){
            e.printStackTrace();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.setButton("Stop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setMessage("Press Stop when done \n");
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                recorder.release();
                recorder = null;
            }
        });

    }


    private void getPermissions() {
        String[] requiredPerms = new String[]{
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission_group.PHONE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        Context context = getApplicationContext();
        int pD = PackageManager.PERMISSION_DENIED;
        int pC = 0;
        //Checking permissions
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == pD) {
            pC += 1;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == pD) {
            pC += 1;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == pD) {
            pC += 1;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == pD) {
            pC += 1;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == pD) {
            pC += 1;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == pD) {
            pC += 1;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == pD) {
            pC += 1;
        }

        if (pC != 0)
            ActivityCompat.requestPermissions(this, requiredPerms, 1504);
        else {
            SharedPreferences prefs = getSharedPreferences("basic",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("PermissionsDone", true);
            editor.commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //Nothing to do here
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Intro Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    public void contact1(View v){
        Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(in, 1101);
    }
    public void contact2(View v){
        Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(in, 1102);
    }
    public void contact3(View v){
        Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(in, 1103);
    }
    public void contact4(View v){
        Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(in, 1104);
    }

    private String getName(String address) {

        if (address == null || address.isEmpty())
            return address;

        while(address.contains(" ")){
            address = address.replace(" ", "");
        }

        Cursor cursor;

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
        ContentResolver contentResolver = getContentResolver();

        String name = address;

        try {
            cursor = contentResolver.query(uri, new String[]{BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            if (cursor.moveToNext())
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, "Failed to find name for address " + address);
            e.printStackTrace();
        }
        return name;
    }

}

package nottoworry.clickaway;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.util.List;

import nottoworry.myapp.R;

/**
 * Created by sahil on 11/2/17.
 */

public class HelpMeAlertDialog extends Dialog implements View.OnClickListener {

    public Activity activity;
    public Dialog dialog;
    private TextView name, age, allergies, medics, blood;
    HelpMeAlertDialog(Activity a){
        super(a);
        this.activity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.helpme_alert_dialog);
        name = (TextView) findViewById(R.id.dialogName);
        medics = (TextView) findViewById(R.id.dialogMedics);
        blood = (TextView) findViewById(R.id.dialogBlood);
        allergies = (TextView) findViewById(R.id.dialogAllergies);
        age = (TextView) findViewById(R.id.dialogAge);

        SharedPreferences preferences = activity.getSharedPreferences("basic",Context.MODE_PRIVATE);
        name.setText("Name: "+preferences.getString("name", "A Human"));
        age.setText("Age: "+preferences.getString("age", "Looks can tell"));
        medics.setText("Medications: "+preferences.getString("medics", " May be never had any, but now he surely needs some"));
        blood.setText("Blood Group: "+preferences.getString("blood", "NA"));
        allergies.setText("Allergies: "+preferences.getString("allergies", "death"));

    }

    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, Menu menu, int deviceId) {

    }

    @Override
    public void onClick(View v) {

    }
}

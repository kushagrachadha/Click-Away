package nottoworry.clickaway;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ListView;

import nottoworry.clickaway.Extras.mExceptionHandler;
import nottoworry.clickaway.connections.myGPSProvider;
import nottoworry.clickaway.connections.myHTTP;
import nottoworry.myapp.R;

public class BloodBankActivity extends AppCompatActivity {

    WebView webView;
    double lat = 28.72, lon = 77.120;
    String url = "http://desolate-bastion-73012.herokuapp.com/b1?long=";
    String urlLat="&lat=";
    ListAdapter adapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new mExceptionHandler(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blood_bank_activity);
        setTitle("Blood banks");

        listView = (ListView) findViewById(R.id.bloodylist);
        listView.setAdapter(new ListAdapter(this, R.layout.list_item, new Database(this).getBanks()));

        myGPSProvider gps = new myGPSProvider(this);

        if(gps.isRunning()){
            lat = gps.getLat();
            lon = gps.getLon();
        }else {

        }
        if(myHTTP.isNetAvailable(this)) {
            webView = (WebView) findViewById(R.id.bloodweb);
            webView.canGoBackOrForward(0);
            webView.loadUrl(url + lon + urlLat + lat);
        }
    }
}

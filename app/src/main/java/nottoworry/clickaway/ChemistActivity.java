package nottoworry.clickaway;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ListView;

import nottoworry.clickaway.connections.myGPSProvider;
import nottoworry.clickaway.connections.myHTTP;
import nottoworry.myapp.R;

public class ChemistActivity extends AppCompatActivity {
    WebView webView;
    double lat = 28.72, lon = 77.120;
    String url = "http://desolate-bastion-73012.herokuapp.com/c1?long=";
    String urlLat="&lat=";
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chemist_activity);
        setTitle("Drug Stores");

        listView = (ListView) findViewById(R.id.chemistlist);
        listView.setAdapter(new ListAdapter(this, R.layout.list_item, new Database(this).getChemists()));

        myGPSProvider gps = new myGPSProvider(this);

        if(gps.isRunning()){
            lat = gps.getLat();
            lon = gps.getLon();
        }else {

        }
        if(myHTTP.isNetAvailable(this)) {
            webView = (WebView) findViewById(R.id.chemistWeb);
            webView.canGoBackOrForward(0);
            webView.loadUrl(url + lon + urlLat + lat);
        }
    }
}

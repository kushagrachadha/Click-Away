package nottoworry.clickaway;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import nottoworry.clickaway.connections.myGPSProvider;
import nottoworry.clickaway.connections.myHTTP;
import nottoworry.myapp.R;

public class HospitalsActivity extends AppCompatActivity {

    WebView webView;
    double lat = 28.72, lon = 77.120;
    String url = "http://desolate-bastion-73012.herokuapp.com/h1?long=";
    String urlLat="&lat=";
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hospitals_activity);
        setTitle("Hospitals");

        listView = (ListView) findViewById(R.id.hospitallist);
        listView.setAdapter(new ListAdapter(this, R.layout.list_item, new Database(this).getHospitals()));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "HEY", Toast.LENGTH_SHORT).show();
            }
        });

        if(myHTTP.isNetAvailable(this)) {
            webView = (WebView) findViewById(R.id.hospitalweb);
            webView.canGoBackOrForward(0);
            webView.loadUrl(url + lon + urlLat + lat);
        }
    }
}

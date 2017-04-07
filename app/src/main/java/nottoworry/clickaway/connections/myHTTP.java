package nottoworry.clickaway.connections;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * It will handle post and get requests of http
 *
 * Created by sahil on 5/1/17.
 */

public class myHTTP {

    static String response;
    static myHTTP object;

    private myHTTP(){

    }

    public static myHTTP instance(){
        if(object==null){
            object = new myHTTP();
        }
        return object;
    }

    public static boolean isNetAvailable(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        boolean net = info != null && info.isConnectedOrConnecting();
        return net;
    }

    public String getJson(String url) throws Exception {
        response="";

        response = fetchJson(url);

        if(response.contains("doneSahil")){
            response = response.replace("doneSahil", "");
        }else{
            throw new Exception(response);
        }

        return response;
    }

    public String postJson(String url, JSONObject jsonObject) throws Exception{
        response = "";

        response = postToServer(url, jsonObject.toString());

        if(response.contains("doneSahil")){
            response = response.replace("doneSahil", "");
        }else{
            throw new Exception(response);
        }

        return response;
    }

    private String fetchJson(String url){
        String result = "";
        HttpURLConnection connection;
        InputStream inputStream;
        try {
            connection = (HttpURLConnection) (new URL(url).openConnection());
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            //Read the response
            StringBuffer stringBuffer = new StringBuffer();
            inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;

            while ((line = bufferedReader.readLine()) != null)
                stringBuffer.append(line + "\r\n");

            inputStream.close();
            connection.disconnect();
            bufferedReader.close();

            result = "doneSahil" + stringBuffer.toString();
        } catch (Exception e) {
            result = "Exception occured: "+e.getMessage();
            e.printStackTrace();
        }
        return result;
    }

    private String postToServer(String urlString, String JsonObject) throws Exception{

        String result ="";

            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("content-type", "application/json");
            httpURLConnection.setDoInput(true); //Sets the flag indicating whether this {@code URLConnection} allows input
            httpURLConnection.setDoOutput(true);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());

            outputStreamWriter.write(JsonObject);
            outputStreamWriter.flush();

            StringBuilder stringBuilder = new StringBuilder();
            int res = httpURLConnection.getResponseCode();
            if (res == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                result = "doneSahil" + stringBuilder.toString();

                br.close();
                httpURLConnection.disconnect();
                outputStreamWriter.close();

            } else {
                throw new Exception(httpURLConnection.getResponseMessage());
            }
            return result;
    }
}
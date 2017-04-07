package nottoworry.clickaway;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

import nottoworry.clickaway.connections.mySqlDb;

/**
 * Created by sahil on 11/2/17.
 */

public class Database extends mySqlDb {

    public static final String DB_Name = "allInOne",
            TABLE_NAME = "T1able",
            nameColumn = "name",
            addressColumn = "address",
            phoneColumn = "phone",
            typeColumn = "type",
            TABLE_CREATE_SQL = "CREATE TABLE " + TABLE_NAME
                    + "(" + " _id" + " INTEGER PRIMARY KEY, " + nameColumn + " TEXT, "
                    + addressColumn + " TEXT, " +phoneColumn+" NUMBER, "+ typeColumn + " TEXT );";

    private static final String[] fields = new String[]{nameColumn, addressColumn, phoneColumn, typeColumn};

    private Context mContext;
    int nameColIndex, addressColIndex, phoneColIndex, typeColIndex;


    Database(Context context){
        super(context, TABLE_CREATE_SQL, DB_Name, TABLE_NAME);
        this.mContext = context;
    }

    protected ContentValues makeData(String name, String Address, String type, String phone){
        ContentValues values = new ContentValues();
        values.put(nameColumn, name);
        values.put(addressColumn, Address);
        values.put(phoneColumn, phone);
        values.put(typeColumn, type);
        return values;
    }

 public void setup(String s){
     try {
         JSONObject object = new JSONObject(s);
         JSONArray hospitals = object.getJSONArray("hospitals");
         JSONArray chemists = object.getJSONArray("chemist");
         JSONArray bloodbanks = object.getJSONArray("bloodbanks");
         Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
         for(int i=0;i<hospitals.length(); i++){
             try {
                 JSONObject t = hospitals.getJSONObject(i);
                 Log.v("hospitals", t.toString());
                 ContentValues temp = makeData(t.getString("name"),
                         t.getString("address"),
                         "hospital",
                         t.getString("contact"));

                 addData(temp);
             }catch (Exception e){
                 e.printStackTrace();
             }
         }
         for(int i=0;i<bloodbanks.length(); i++){
             try {
                 JSONObject t = bloodbanks.getJSONObject(i);

                 ContentValues temp = makeData(t.getString("name"),
                         t.getString("address"),
                         "bloodbanks",
                         t.getString("contact")
                 );
                 addData(temp);

             }catch (Exception e){
                 e.printStackTrace();
             }
         }
         for(int i=0;i<chemists.length(); i++){
             try {
                 JSONObject t = chemists.getJSONObject(i);

                 ContentValues temp = makeData(t.getString("name"),
                         t.getString("address"),
                         "chemists",
                         t.getString("contact")
                 );
                 addData(temp);
             }catch (Exception e){
                 e.printStackTrace();
             }
         }

     }catch (Exception e){
         e.printStackTrace();
     }
 }
    private void initializeIndexes(Cursor cursor){
        nameColIndex = cursor.getColumnIndex(nameColumn);
        addressColIndex = cursor.getColumnIndex(addressColumn);
        typeColIndex = cursor.getColumnIndex(typeColumn);
        phoneColIndex = cursor.getColumnIndex(phoneColumn);
    }

    public ArrayList<ModelData> getHospitals(){
        ArrayList<ModelData> hos = new ArrayList<>();

        Cursor cursor = getReadableDatabase().query(TABLE_NAME, null, null, null, null, null, null);

        if(!cursor.moveToFirst()){
            return null;
        }
        initializeIndexes(cursor);
        String name, phone, address, type;
        do{
            try {
                name = cursor.getString(nameColIndex);
                phone = cursor.getString(phoneColIndex);
                address = cursor.getString(addressColIndex);
                type = cursor.getString(typeColIndex);
                if(type.equals("hospital")) {
                    ModelData t = new ModelData(name, address, phone);

                    hos.add(t);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }while (cursor.moveToNext());
        Log.d("Hospitals", hos.toString());
        return hos;
    }
    public ArrayList<ModelData> getChemists(){
        ArrayList<ModelData> hos = new ArrayList<>();
        try {
            Cursor cursor = getWritableDatabase().query(TABLE_NAME, fields, typeColumn + " =? ", new String[]{"chemists"}, null, null, null);

            if (!cursor.moveToFirst()) {
                return null;
            }
            initializeIndexes(cursor);
            String name, phone, address;
            do {
                try {
                    name = cursor.getString(nameColIndex);
                    phone = cursor.getString(phoneColIndex);
                    address = cursor.getString(addressColIndex);

                    ModelData t = new ModelData(name, address, phone);

                    hos.add(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
            cursor.close();
        }finally {
            if(hos==null){
                SharedPreferences prefs = mContext.getSharedPreferences("basic", Context.MODE_APPEND);
                Set<String> names = prefs.getStringSet("chemistName", null);
                Set<String> phone = prefs.getStringSet("chemistPhone", null);
                Set<String> address = prefs.getStringSet("chemistAddress", null);

                if(names==null || phone == null || address ==null){
                    return null;
                }

            }
        }
        Log.d("chemists", hos.toString());
        return hos;
    }
    public ArrayList<ModelData> getBanks(){
        ArrayList<ModelData> hos = new ArrayList<>();
        db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, fields, ""+typeColumn+" =? ", new String[]{"bloodbanks"}, null, null, null);

        if(!cursor.moveToFirst()){
            return null;
        }
        initializeIndexes(cursor);
        String name, phone, address;
        do{
            try {
                name = cursor.getString(nameColIndex);
                phone = cursor.getString(phoneColIndex);
                address = cursor.getString(addressColIndex);

                ModelData t = new ModelData(name, address, phone);

                hos.add(t);
            }catch (Exception e){
                e.printStackTrace();
            }
        }while (cursor.moveToNext());
        Log.d("banks", hos.toString());
        return hos;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}

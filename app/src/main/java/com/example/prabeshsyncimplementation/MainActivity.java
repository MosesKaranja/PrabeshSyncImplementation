package com.example.prabeshsyncimplementation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
    Button buttonSubmit;
    EditText editTextPhoneNumber;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter adapter;
    ArrayList<Contact> arrayList = new ArrayList<>();
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSubmit = findViewById(R.id.btnSubmit);
        editTextPhoneNumber = findViewById(R.id.phoneNumber);
        recyclerView = findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(adapter);

        //WorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(UploadWorker.class).build();
        //WorkManager.getInstance(getApplicationContext()).enqueue(uploadWorkRequest);




        PeriodicWorkRequest saveRequest = new PeriodicWorkRequest.Builder(UploadWorker.class,1, TimeUnit.HOURS).build();



        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).setRequiredNetworkType(NetworkType.UNMETERED).build();
        WorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(UploadWorker.class).setConstraints(constraints).build();

        WorkManager.getInstance(getApplicationContext()).enqueue(saveRequest);









        readFromLocalStorage();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                readFromLocalStorage();

            }
        };

        handleSSLHandshake();

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = editTextPhoneNumber.getText().toString();
                //saveToLocalStorage(number);
                saveToAppServer(number);
                editTextPhoneNumber.setText("");

            }
        });

    }

    private void readFromLocalStorage(){
        arrayList.clear();
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = dbHelper.readFromLocalDatabase(database);

        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(DbContract.NAME));
            int sync_status = cursor.getInt(cursor.getColumnIndex(DbContract.SYNC_STATUS));
            arrayList.add(new Contact(name, sync_status));
        }
        adapter.notifyDataSetChanged();
        cursor.close();
        dbHelper.close();

        Log.i("arraListData", String.valueOf(arrayList));

    }

    private void saveToAppServer(String name){
//        DbHelper dbHelper = new DbHelper(this);
//        SQLiteDatabase database = dbHelper.getWritableDatabase();


        if (checkNetworkConnection()){
            Log.i("insideIf","In side of If");

            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.i("InTry","Executing Inside try stmt");
                        Log.i("InTryresponse",response);

                        JSONObject jsonObject = new JSONObject(response);
                        Log.i("dejsonObject", String.valueOf(jsonObject));
                        String Response = jsonObject.getString("response");
                        Log.i("jsonObjectResponse", String.valueOf(jsonObject));

                        if (Response.equals("OK")){
                            saveToLocalStorage(name, DbContract.SYNC_STATUS_OK);

                        }
                        else{
                            saveToLocalStorage(name, DbContract.SYNC_STATUS_FAILED);

                        }

                    }
                    catch (JSONException e){
                        e.printStackTrace();

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("onError", "Execured On Error Response");
                    saveToLocalStorage(name, DbContract.SYNC_STATUS_FAILED);

                    Log.i("onErrorCause", String.valueOf(error.getCause()));

                    Log.i("onErrorMessage", error.getMessage());

                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError{
                    Map<String, String> params = new HashMap<>();
                    params.put("name", name);
                    return params;
                }
            }
            ;

            MySingleton.getInstance(MainActivity.this).addToRequestQue(stringRequest);

        }
        else{
            Log.i("InElse","Executed Inside Else STMT");
            //dbHelper.saveToLocalDatabase(name, DbContract.SYNC_STATUS_FAILED, database);
            saveToLocalStorage(name,DbContract.SYNC_STATUS_FAILED);

        }

//        readFromLocalStorage();
//        dbHelper.close();


    }



    public boolean checkNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo !=null && networkInfo.isConnected());

    }

    private void saveToLocalStorage(String name, int sync){
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        dbHelper.saveToLocalDatabase(name, sync, database);
        readFromLocalStorage();
        dbHelper.close();


    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver, new IntentFilter(DbContract.UI_UPDATE_BROADCAST));

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);

    }

    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }


}
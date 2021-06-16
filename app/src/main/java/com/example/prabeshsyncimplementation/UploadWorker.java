package com.example.prabeshsyncimplementation;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class UploadWorker extends Worker {

    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.i("LoggedHereiz","Doing some work");
        //return Result.success();

        if (checkNetworkConnection()){
            DbHelper dbHelper = new DbHelper(getApplicationContext());
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            Cursor cursor = dbHelper.readFromLocalDatabase(database);

            //Log.i("cursorColumnNames", String.valueOf(cursor.getColumnNames()));

            //Log.i("cursorNames", cursor.getString(cursor.getColumnIndex(DbContract.NAME)));

            while(cursor.moveToNext()){
                int sync_status = cursor.getInt(cursor.getColumnIndex(DbContract.SYNC_STATUS));
                //int id = cursor.getInt(cursor.getColumnIndex("id"));
                String Namecursor = cursor.getString(cursor.getColumnIndex(DbContract.NAME));

                //Log.i("cursorId", String.valueOf(id));
                //Log.i("cursorName",Namecursor);
                //Log.i("cursorSyncStatus", String.valueOf(sync_status));
                if (sync_status == DbContract.SYNC_STATUS_FAILED){
                    String Name = cursor.getString(cursor.getColumnIndex(DbContract.NAME));
                    Log.i("cursorNameiNiF", Name);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                Log.i("RunningHere","Running in this try statement");
                                JSONObject jsonObject = new JSONObject(response);
                                Log.i("jsonObjectResponse", String.valueOf(jsonObject));
                                String Response = jsonObject.getString("response");
                                if (Response.equals("OK")){
                                    dbHelper.updateLocalDatabase(Name, DbContract.SYNC_STATUS_OK, database);
                                    getApplicationContext().sendBroadcast(new Intent(DbContract.UI_UPDATE_BROADCAST));

                                }

                            }
                            catch (JSONException e){
                                e.printStackTrace();

                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("name", Name);
                            return params;
                        }
                    };

                    MySingleton.getInstance(getApplicationContext()).addToRequestQue(stringRequest);

                }

            }
            //dbHelper.close();
            return Result.success();

        }


        return Result.failure();
    }




//    @Override
//    public Result doWork() {
//
//        // Do the work here--in this case, upload the images.
//        uploadImages();
//
//        // Indicate whether the work finished successfully with the Result
//        return Result.success();
//    }

//
//    public boolean checkNetworkConnection(Context context){
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//        return (networkInfo !=null && networkInfo.isConnected());
//
//    }

    public boolean checkNetworkConnection(){
        try{
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress socketAddress = new InetSocketAddress("8.8.8.8", 53);
            sock.connect(socketAddress, timeoutMs);
            sock.close();
            return true;

        }

        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


}

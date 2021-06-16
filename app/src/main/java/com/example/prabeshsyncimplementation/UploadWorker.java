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

import java.util.HashMap;
import java.util.Map;

public class UploadWorker extends Worker {

    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        if (checkNetworkConnection(getApplicationContext())){
            DbHelper dbHelper = new DbHelper(getApplicationContext());
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            Cursor cursor = dbHelper.readFromLocalDatabase(database);

            while(cursor.moveToNext()){
                int sync_status = cursor.getInt(cursor.getColumnIndex(DbContract.SYNC_STATUS));
                if (sync_status == DbContract.SYNC_STATUS_FAILED){
                    String Name = cursor.getString(cursor.getColumnIndex(DbContract.NAME));
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                Log.i("RunningHere","Running in this try statement");
                                JSONObject jsonObject = new JSONObject(response);
                                String Response = jsonObject.getString("response");
                                if (Response.equals("ok")){
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
            dbHelper.close();
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


    public boolean checkNetworkConnection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo !=null && networkInfo.isConnected());

    }


}

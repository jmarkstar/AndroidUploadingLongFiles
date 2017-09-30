package com.jmarkstar.uploading;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.gson.JsonObject;
import com.jmarkstar.uploading.api.ApiService;
import com.jmarkstar.uploading.api.ProgressRequestBody;
import com.jmarkstar.uploading.api.RetrofitClient;
import java.io.File;
import java.util.Random;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadService extends Service {

    private static final String TAG = "UploadService";

    public UploadService() {}

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand()");
        String filePath = intent.getStringExtra("filepath");
        Log.v(TAG, "path: "+filePath);
        File file = new File(filePath);
        if(file.exists()){

            final int id = new Random().nextInt();
            final NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setContentTitle("Uploading "+file.getName())
            .setContentText(getString(R.string.upload_in_process_text))
            .setSmallIcon(R.drawable.ic_upload);

            ProgressRequestBody fileBody = new ProgressRequestBody(file, new ProgressRequestBody.UploadCallbacks() {
                @Override public void onProgressUpdate(int percentage) {
                    Log.v(TAG, "PERCENTAGE: "+percentage);
                    mBuilder.setProgress(100, percentage, false);
                    mNotifyManager.notify(id, mBuilder.build());
                }
            });
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("user_image", file.getName(), fileBody);

            Call<JsonObject> request = RetrofitClient.getRetrofit().create(ApiService.class).uploadImage(filePart);
            request.enqueue(new Callback<JsonObject>() {
                @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if(response.isSuccessful()){
                        String mensaje = response.body().get("MENSAJE").getAsString();
                        Log.v(TAG, "Mensaje : "+mensaje);
                        mBuilder.setContentText(getString(R.string.upload_complete_text))
                                .setProgress(0,0,false)
                                .setVibrate(new long[] { 1000, 800,300 });
                        mNotifyManager.notify(id, mBuilder.build());
                    }else{
                        Log.e(TAG, "Error: Ocurrio algun problema");
                    }
                    stopSelf();
                }

                @Override public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "Error: "+t.getMessage());
                    t.printStackTrace();
                    Log.e(TAG, "Error: Ocurrió un error.");
                    mBuilder.setContentText(getString(R.string.upload_error_text)).setProgress(0,0,false);
                    mNotifyManager.notify(id, mBuilder.build());
                    stopSelf();
                }
            });
        }else{
            stopSelf();
        }
        return Service.START_NOT_STICKY;
    }

    @Override public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

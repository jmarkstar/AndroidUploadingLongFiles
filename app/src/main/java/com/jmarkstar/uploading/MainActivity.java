package com.jmarkstar.uploading;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.jmarkstar.uploading.utils.AppUtils;

public class MainActivity extends AppCompatActivity {

    public static final Integer RESULT_LOAD_IMG = 1000;
    public static final Integer REQUEST_STORE_PERMISSION = 2000;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onPickImage(View view){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String [] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_STORE_PERMISSION);
            }else{
                pickImage();
            }
        }else{
            pickImage();
        }
    }

    private void pickImage(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_STORE_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                pickImage();
            }else{
                Toast.makeText(this, R.string.permission_needed, Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMG){
            if (resultCode == RESULT_OK) {
                final Uri imageUri = data.getData();
                String path = AppUtils.getRealPathFromURI(this, imageUri);
                Log.v("main", "Path: "+ path);

                //INICIANDO EL SERVICIO PARA SUBIR LA IMAGEN
                Intent intent = new Intent(this, UploadService.class);
                intent.putExtra("filepath", path);
                startService(intent);
            }else {
                Toast.makeText(this, R.string.image_not_found, Toast.LENGTH_LONG).show();
            }
        }
    }
}

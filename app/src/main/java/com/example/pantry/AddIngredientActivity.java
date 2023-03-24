package com.example.pantry;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.camera.CameraSourceConfig;
import com.google.mlkit.vision.camera.CameraXSource;
import com.google.mlkit.vision.camera.DetectionTaskCallback;
import com.google.mlkit.vision.common.InputImage;
import java.util.List;

public class AddIngredientActivity extends AppCompatActivity {

    CameraSourceConfig cameraSourceConfig;
    TextView textView;
    BarcodeScanner barcodeScanner;
    PreviewView previewView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        // Find views
        previewView = findViewById(R.id.preview_view);
        BarcodeScannerOptions options = new BarcodeScannerOptions
                .Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();

        barcodeScanner = BarcodeScanning.getClient(options);

        cameraSourceConfig = new CameraSourceConfig.Builder(this, barcodeScanner, new DetectionTaskCallback<List<Barcode>>() {
            @Override
            public void onDetectionTaskReceived(@NonNull Task<List<Barcode>> task) {
                task = barcodeScanner.process(InputImage.fromBitmap(previewView.getBitmap(), 0));
                task.addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        if (barcodes.isEmpty()){
                            return;
                        }
                        for (int i = 0; i < barcodes.size(); i++){
                            Toast.makeText(AddIngredientActivity.this, barcodes.get(i).getDisplayValue(), Toast.LENGTH_SHORT).show();
                        }
//                        barcodeScanner.close(); //TODO: make the API Call
                    }
                });
            }//onTaskDetectionReceived
        })//cameraSourceConfig.Builder
                .setRequestedPreviewSize(720, 480)
                .setFacing(CameraSourceConfig.CAMERA_FACING_BACK)
                .build();
        CameraXSource cameraXSource = new CameraXSource(cameraSourceConfig, previewView);
        if(ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            textView.setText("Permission to use camera not granted");
            return;
        }
        try{
            cameraXSource.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//onCreate
}
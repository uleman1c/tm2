package com.example.tm2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

public class RequestPrermission {

    public interface AfterCheck {

        void onSuccess();

    }

    private ActivityResultLauncher<String> requestPermissionLauncher;

    private Context context;

    public RequestPrermission(Context context, ActivityResultLauncher<String> requestPermissionLauncher) {
        this.context = context;
        this.requestPermissionLauncher = requestPermissionLauncher;
    }

    public void Check(String access, AfterCheck afterCheck){


        if (ContextCompat.checkSelfPermission(context, access) == PackageManager.PERMISSION_GRANTED) {

            afterCheck.onSuccess();

            // You can use the API that requires the permission.
            //performAction(...);
//                        } else if (shouldShowRequestPermissionRationale(...)) {
//                            // In an educational UI, explain to the user why your app requires this
//                            // permission for a specific feature to behave as expected, and what
//                            // features are disabled if it's declined. In this UI, include a
//                            // "cancel" or "no thanks" button that lets the user continue
//                            // using your app without granting the permission.
//                            showInContextUI(...);
        } else {

            requestPermissionLauncher.launch(access);

        }


    }



}

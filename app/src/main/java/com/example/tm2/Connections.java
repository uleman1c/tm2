package com.example.tm2;

import com.android.volley.AuthFailureError;

import java.util.HashMap;
import java.util.Map;

public class Connections {

    public static String addr = "https://ow.apx-service.ru/tech_man/hs/mob/";

    public static Map<String, String> headers() {

        HashMap headers = new HashMap<String, String>();

        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Basic ZXhjaDoxMjM0NTY=");


        return headers;

    };



}

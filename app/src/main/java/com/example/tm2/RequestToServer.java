package com.example.tm2;

import android.content.Context;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Map;

public class RequestToServer {

     interface ResponseResultInterface{

        void onResponse(JSONObject response);

    }

    public static void execute(Context context, int method, String url, JSONObject params, ResponseResultInterface responseResultInterface){

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                responseResultInterface.onResponse(response);

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, Connections.addr + url, params, listener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return Connections.headers();

            };


        };
        Volley.newRequestQueue(context).add(jsonObjectRequest);


    }

}

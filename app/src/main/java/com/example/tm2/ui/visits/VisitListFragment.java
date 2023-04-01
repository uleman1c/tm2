package com.example.tm2.ui.visits;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.tm2.ActivityForResult;
import com.example.tm2.Connections;
import com.example.tm2.DataAdapter;
import com.example.tm2.DateStr;
import com.example.tm2.GetFoto;
import com.example.tm2.GetLocation;
import com.example.tm2.JsonProcs;
import com.example.tm2.ListFragment;
import com.example.tm2.R;
import com.example.tm2.RequestPrermission;
import com.example.tm2.RequestToServer;
import com.example.tm2.objects.MoversService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

public class VisitListFragment extends ListFragment<MoversService> {

    private int REQUEST_CAMERA = 0;
    protected final ActivityForResult<Intent, ActivityResult> activityLauncher = ActivityForResult.registerActivityForResult(this);

    protected final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            getLovationForVisit();
        } else {
            // Explain to the user that the feature is unavailable because the
            // feature requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
        }
    });
    public VisitListFragment() {

        super(R.layout.fragment_filter_add_list, R.layout.visit_list_item);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                items.clear();

                String url = Connections.addrDta + "?request=getVisits&userId=" + arguments.getString("id") + "&filter=" + filter;

                RequestToServer.execute(getContext(), Request.Method.GET, url, new JSONObject(), new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject jsonObjectResponse) {

                        if (JsonProcs.getBooleanFromJSON(jsonObjectResponse, "success")) {

                            JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");

                            JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);

                            JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "MoversService");

                            for (int j = 0; j < jsonArrayObjects.length(); j++) {

                                JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, j);

                                items.add(MoversService.MoversServiceFromJson(objectItem));

                            }

                            progressBar.setVisibility(View.GONE);

                            adapter.notifyDataSetChanged();

                        }
                    }
                });

            }
        });


        setOnCreateViewElements(new OnCreateViewElements() {
            @Override
            public void execute(View root, NavController navController) {

                getAdapter().setInitViewsMaker(new DataAdapter.InitViewsMaker() {
                    @Override
                    public void init(View itemView, ArrayList<TextView> textViews) {

                        textViews.add(itemView.findViewById(R.id.tvNumberDate));
                        textViews.add(itemView.findViewById(R.id.tvDescription));
                        textViews.add(itemView.findViewById(R.id.tvStatus));
                    }
                });

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<MoversService>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, MoversService document) {

                        ((TextView) holder.getTextViews().get(0)).setText("№ " + document.number + " от " + DateStr.FromYmdhmsToDmyhms(document.date)
                                + ", c " + DateStr.FromYmdhmsToDmyhms(document.start)
                                + " по " + DateStr.FromYmdhmsToDmyhms(document.finish));
                        ((TextView) holder.getTextViews().get(1)).setText("Количество: " + String.valueOf(document.quantity) + " на сумму " + String.valueOf(document.sum));
                        ((TextView) holder.getTextViews().get(2)).setText("Комментарий: " + document.comment);
                    }
                });

                root.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();

                        TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");

                        Calendar calendar = new GregorianCalendar();
                        calendar.roll(Calendar.HOUR_OF_DAY, timeZone.getRawOffset() / (3600 * 1000));

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");


                        MoversService moversService = new MoversService(UUID.randomUUID().toString(), "",
                                simpleDateFormat.format(calendar.getTime()),
                                "", "", 0, 0.0, "", new ArrayList<>());

                        bundle.putString("record", new JSONArray(moversService.getObjectDescription()).toString());

                        RequestPrermission requestPrermission = new RequestPrermission(getContext(), requestPermissionLauncher);

                        requestPrermission.Check(Manifest.permission.ACCESS_COARSE_LOCATION, new RequestPrermission.AfterCheck() {
                                    @Override
                                    public void onSuccess() {

                                        requestPrermission.Check(Manifest.permission.ACCESS_FINE_LOCATION, new RequestPrermission.AfterCheck() {
                                            @Override
                                            public void onSuccess() {

                                                getLovationForVisit();
                                            }

                                        });
                                    }

                                });




                    }
                });



            }
        });

    }

    private void getLovationForVisit() {
        GetLocation getLocation = new GetLocation(getContext(), new GetLocation.OnLocationChanged() {
            @Override
            public void execute(android.location.Location location) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;
                //Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();

                File file = GetFoto.createImageFile(getContext());

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, GetFoto.uriFromFile(getContext(), file));

                activityLauncher.launch(intent, result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Bitmap bitmap = GetFoto.bitmapFromFile(file);

                        String url = Connections.addrFiles + "doc/ПосещениеКонтрагента/"
                                + UUID.randomUUID().toString() + "/" + UUID.randomUUID().toString() + ".jpg";

                        RequestToServer.uploadBitmap(getContext(), url, bitmap, new RequestToServer.ResponseResultInterface() {
                            @Override
                            public void onResponse(JSONObject response) {

                            }
                        });

                    }
                });

                //navController.navigate(R.id.nav_MoversServiceRecordFragment, bundle);

            }
        });
    }


}
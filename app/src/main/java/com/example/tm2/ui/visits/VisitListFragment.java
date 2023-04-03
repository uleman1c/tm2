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
import android.location.LocationRequest;
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
import com.example.tm2.DB;
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
import com.example.tm2.objects.Visit;

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

public class VisitListFragment extends ListFragment<Visit> {

    //private LocationCallback locationCallback;

    private int REQUEST_CAMERA = 0;
    protected final ActivityForResult<Intent, ActivityResult> activityLauncher = ActivityForResult.registerActivityForResult(this);

    protected final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            getLocationForVisit();
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

                JSONArray fields = new JSONArray();
                fields.put("id");
                fields.put("date");

                JSONObject table = new JSONObject();
                JsonProcs.putToJsonObject(table, "name", "visits");
                JsonProcs.putToJsonObject(table, "fields", fields);



//                let au = { name: 'available_organizations', fields: [
//                'available_organization_id'],
//                accessFilter: ['user_id = \'' + this.user_id + '\'']}
//
//                this.executeRequest('gettable', au, 'POST', result => {
//
//
//
//                String url = Connections.addrDta + "?request=getVisits&userId=" + arguments.getString("id") + "&filter=" + filter;

                RequestToServer.executeA(getContext(), Request.Method.POST, Connections.addrApo + "gettable", table, new RequestToServer.ResponseResultInterface() {
                    @Override
                    public void onResponse(JSONObject jsonObjectResponse) {

                        if (JsonProcs.getBooleanFromJSON(jsonObjectResponse, "success")) {

                            JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "result");

                            for (int j = 0; j < jsonArrayObjects.length(); j++) {

                                JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, j);

                                items.add(Visit.FromJson(objectItem));

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

                getAdapter().setDrawViewHolder(new DataAdapter.DrawViewHolder<Visit>() {
                    @Override
                    public void draw(DataAdapter.ItemViewHolder holder, Visit document) {

                        ((TextView) holder.getTextViews().get(0)).setText("№ " + document.number + " от " + DateStr.FromYmdhmsToDmyhms(document.date));
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

                                                getLocationForVisit();
                                            }

                                        });
                                    }

                                });




                    }
                });



            }
        });

    }

    private void getLocationForVisit() {
        GetLocation getLocation = new GetLocation(getContext(), new GetLocation.OnLocationChanged() {
            @Override
            public void execute(String id, android.location.Location location) {

                DB db = new DB(getContext());
                db.open();
                String locationId = db.getConstant("locationId");

                Boolean inProgress = locationId != null && locationId.equals(id);

                if (!inProgress) {

                    db.updateConstant("locationId", id);

                }
                db.close();

                if (!inProgress){

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    JSONArray fields = new JSONArray();
                    fields.put("id");
                    fields.put("date");
                    fields.put("deleted");
                    fields.put("contractor_id");
                    fields.put("author_id");
                    fields.put("comment");
                    fields.put("latitude");
                    fields.put("longitude");

                    JSONObject record = new JSONObject();
                    JsonProcs.putToJsonObject(record, "id", id);
                    JsonProcs.putToJsonObject(record, "date", DateStr.NowYmdhms());
                    JsonProcs.putToJsonObject(record, "deleted", 0);
                    JsonProcs.putToJsonObject(record, "contractor_id", "00000000-0000-0000-0000-000000000000");
                    JsonProcs.putToJsonObject(record, "author_id", arguments.get("id").toString());
                    JsonProcs.putToJsonObject(record, "comment", "");
                    JsonProcs.putToJsonObject(record, "latitude", (int) (latitude * 1000000));
                    JsonProcs.putToJsonObject(record, "longitude", (int) (longitude * 1000000));

                    JSONObject table = new JSONObject();
                    JsonProcs.putToJsonObject(table, "name", "visits");
                    JsonProcs.putToJsonObject(table, "fields", fields);
                    JsonProcs.putToJsonObject(table, "record", record);

                    RequestToServer.executeA(getContext(), Request.Method.POST, Connections.addrApo + "insertrecord", table, response -> {

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


                    });

                }



            }
        });
    }


}
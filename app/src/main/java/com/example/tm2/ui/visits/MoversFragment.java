package com.example.tm2.ui.visits;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.navigation.NavController;

import com.example.tm2.DataAdapter;
import com.example.tm2.DateStr;
import com.example.tm2.ListFragment;
import com.example.tm2.R;
import com.example.tm2.objects.MoversService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

public class MoversFragment extends ListFragment<MoversService> {


    public MoversFragment() {

        super(R.layout.fragment_filter_add_list, R.layout.visit_list_item);

        setListUpdater(new ListUpdater() {
            @Override
            public void update(ArrayList items, ProgressBar progressBar, DataAdapter adapter, String filter) {

                items.clear();

//                HttpClient httpClient = new HttpClient(getContext());
//
//                httpClient.request_get("/hs/dta/obj?request=getMoversService&warehouseId=" + getWarehouseId() + "&filter=" + filter, new HttpRequestInterface() {
//                    @Override
//                    public void setProgressVisibility(int visibility) {
//
//                        progressBar.setVisibility(visibility);
//
//                    }
//
//                    @Override
//                    public void processResponse(String response) {
//
//                        JSONObject jsonObjectResponse = JsonProcs.getJSONObjectFromString(response);
//
//                        if (JsonProcs.getBooleanFromJSON(jsonObjectResponse, "success")){
//
//                            JSONArray jsonArrayResponses = JsonProcs.getJsonArrayFromJsonObject(jsonObjectResponse, "responses");
//
//                            JSONObject jsonObjectItem = JsonProcs.getItemJSONArray(jsonArrayResponses, 0);
//
//                            JSONArray jsonArrayObjects = JsonProcs.getJsonArrayFromJsonObject(jsonObjectItem, "MoversService");
//
//                            for (int j = 0; j < jsonArrayObjects.length(); j++) {
//
//                                JSONObject objectItem = JsonProcs.getItemJSONArray(jsonArrayObjects, j);
//
//                                items.add(MoversService.MoversServiceFromJson(objectItem));
//
//                            }
//
//                            adapter.notifyDataSetChanged();
//
//                        }
//
//                    }
//
//                });



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

                        //navController.navigate(R.id.nav_MoversServiceRecordFragment, bundle);

                    }
                });



            }
        });


    }


}
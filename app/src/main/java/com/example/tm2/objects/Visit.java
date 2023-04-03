package com.example.tm2.objects;

import com.example.tm2.JsonProcs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Visit {

    public String ref, number, date, contractor, author, comment;
    public Integer latitude, longitude;
    public Double sum;
    public ArrayList<String> containers;

    public ArrayList<String> fields;

    //public String[][] f = [[]];

    public Visit(String ref, String number, String date, String contractor, String author, String comment, Integer latitude, Integer longitude) {
        this.ref = ref;
        this.number = number;
        this.date = date;
        this.contractor = contractor;
        this.author = author;
        this.latitude = latitude;
        this.longitude = longitude;
        this.comment = comment;

        this.fields = new ArrayList<>();

        this.fields.add("ref");
        this.fields.add("number");
        this.fields.add("date");
        this.fields.add("start");
        this.fields.add("finish");
        this.fields.add("quantity");
        this.fields.add("sum");
        this.fields.add("comment");
        this.fields.add("containers");

    }

    private void MakeFieldVisible(HashMap<String, String> field){

        field.put("visible", "true");

    }
    private void MakeFieldEdiable(HashMap<String, String> field){

        field.put("editable", "true");

    }

    private void MakeFieldRequired(HashMap<String, String> field){

        field.put("required", "true");

    }

    private HashMap<String, String> getFieldDescription(String curName){

        HashMap<String, String> field = new HashMap<>();

        if (curName.equals("ref")){

            field.put("type", "string");
            field.put("value", ref);

        } else if (curName.equals("number")){

            field.put("type", "string");
            field.put("value", number);
            field.put("alias", "Номер");
            MakeFieldVisible(field);

        } else if (curName.equals("date")){

            field.put("type", "date");
            field.put("value", date);
            field.put("alias", "Дата");
            MakeFieldVisible(field);

//        } else if (curName.equals("start")){
//
//            field.put("type", "date");
//            field.put("value", start);
//            field.put("alias", "Начало");
//            MakeFieldVisible(field);
//            MakeFieldEdiable(field);
//            MakeFieldRequired(field);
//
//        } else if (curName.equals("finish")){
//
//            field.put("type", "date");
//            field.put("value", finish);
//            field.put("alias", "Окончание");
//            MakeFieldVisible(field);
//            MakeFieldEdiable(field);
//            MakeFieldRequired(field);
//
//        } else if (curName.equals("quantity")) {
//
//            field.put("type", "integer");
//            field.put("value", String.valueOf(quantity));
//            field.put("alias", "Количество");
//            MakeFieldVisible(field);
//            MakeFieldEdiable(field);
//            MakeFieldRequired(field);

        } else if (curName.equals("sum")) {

            field.put("type", "double");
            field.put("value", String.valueOf(sum));
            field.put("alias", "Сумма");
            MakeFieldVisible(field);
            MakeFieldEdiable(field);

        } else if (curName.equals("comment")) {

            field.put("type", "text");
            field.put("value", comment);
            field.put("alias", "Комментарий");
            MakeFieldVisible(field);
            MakeFieldEdiable(field);

        } else if (curName.equals("containers")) {

            String strContainers = "";

            for (String curC: containers) {

                strContainers = strContainers + (strContainers.isEmpty() ? "" : ", ") + curC;

            }

            field.put("type", "ref");
            field.put("value", strContainers);
            field.put("alias", "Контейнеры");
            field.put("fragment", ""); //String.valueOf(R.id.nav_ContainersFragment));
            field.put("array", "true");
            MakeFieldVisible(field);
            MakeFieldEdiable(field);
            MakeFieldRequired(field);

        }


        return field;
    }

    public ArrayList<HashMap<String, Object>> getObjectDescription(){

        ArrayList<HashMap<String, Object>> fields = new ArrayList<>();

        for (String curName : this.fields) {

            HashMap field = new HashMap();
            field.put(curName, getFieldDescription(curName));

            fields.add(field);

        }

        return fields;
    }

    public static Visit FromJson(JSONObject task_item) {

        String ref = JsonProcs.getStringFromJSON(task_item, "ref");
        String number = JsonProcs.getStringFromJSON(task_item, "number");
        String date = JsonProcs.getStringFromJSON(task_item, "date");
        String contractor = JsonProcs.getStringFromJSON(task_item, "contractor");
        String author = JsonProcs.getStringFromJSON(task_item, "author");
        Integer latitude = JsonProcs.getIntegerFromJSON(task_item, "latitude");
        Integer longitude = JsonProcs.getIntegerFromJSON(task_item, "longitude");
        String comment = JsonProcs.getStringFromJSON(task_item, "comment");

        return new Visit(ref, number, date, contractor, author, comment, latitude, longitude);

    }

}



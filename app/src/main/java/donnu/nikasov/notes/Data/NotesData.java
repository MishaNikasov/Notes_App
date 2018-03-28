package donnu.nikasov.notes.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Миша on 12.02.2018.
 */

public class NotesData implements Serializable {

    public static ArrayList<NotesData> notesDataList;

    private String title;
    private String describe;
    private String date;
    private String tag;
    private String color;
    private int ID;
    private boolean favorite;

    public NotesData(String title, String describe, String date, String tag) {

        this.title = title;
        this.describe = describe;
        this.date = date;
        this.tag = tag;
        this.ID = getLastId()+1;
    }

    public NotesData(String title, String describe, String date, String tag, String color) {

        this.title = title;
        this.describe = describe;
        this.date = date;
        this.tag = tag;
        this.color = color;
        this.ID = getLastId()+1;
    }

    public NotesData(String title, String describe, String date, String tag, String color, int id) {

        this.color = color;
        this.title = title;
        this.describe = describe;
        this.date = date;
        this.tag = tag;
        this.ID = getLastId()+1 + id;
    }

       public static ArrayList<NotesData> getNotesDataList(Context context) {

        loadDataToArrayList(context);
        return loadDataList(context);
    }

    private static void loadDataToArrayList(Context context){

        if (notesDataList==null){
            notesDataList = new ArrayList<>();
            notesDataList = loadDataList(context);
        }
    }

    public static void
    deleteNoteFromDataList(Context context, NotesData data){

        loadDataToArrayList(context);

        for (NotesData note : notesDataList) {

            if (note.getID() == data.getID()){
                System.out.println(note.getID());
                System.out.println(data);
                notesDataList.remove(note);
                saveDataList(context, notesDataList);

                return;
            }
        }
    }

    public static void addNoteToArchive(Context context, NotesData data, NotesData newData){

        ArchiveData.addToNotesDataList(context, newData);

        deleteNoteFromDataList(context, data);

    }

    public static void addToNotesDataList(Context context, NotesData data){

        notesDataList.add(data);

        saveDataList(context, notesDataList);
    }

    public static void saveToNotesDataList(Context context, NotesData data,  NotesData lastData){

        deleteNoteFromDataList(context, lastData);

        addToNotesDataList(context, data);

        saveDataList(context, notesDataList);
    }

    public static void saveDataList(Context context, ArrayList<NotesData> param){

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(param);

        prefsEditor.putString("1notesArchiveDataList", json);
        prefsEditor.apply();
    }

    private static ArrayList<NotesData> loadDataList(Context context){

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("1notesArchiveDataList", "");

        Type type = new TypeToken<ArrayList<NotesData>>(){}.getType();
        ArrayList<NotesData> notesData = gson.fromJson(json, type);

        if (notesData==null){
            notesData = new ArrayList<>();
        }

        System.out.println("Note data size : " + notesData.size());

        return notesData;
    }

    private int getLastId(){
        int lastId = 0;

        if (notesDataList.size()!=0){
            lastId = notesDataList.get(notesDataList.size() - 1).getID();
        }
        return  lastId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public String getDescribe() {
        return describe;
    }

    public String getDate() {
        return date;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}

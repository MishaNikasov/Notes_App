package donnu.nikasov.notes.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Миша on 19.02.2018.
 */

public class TagData {

    public static ArrayList<String> tagNotesList;

    public static void deleteNoteFromDataList(Context context, String data){

        loadDataTagArrayList(context);

        tagNotesList.remove(data);

        saveTagNote(context, tagNotesList);
    }

    public static void addToNotesDataList(Context context, String data){

        tagNotesList.add(data);

        saveTagNote(context, tagNotesList);
    }

    public static ArrayList<String> getTagDataList(Context context) {

        loadDataTagArrayList(context);
        return loadTagNote(context);
    }

    public static ArrayList<String> loadTagNote(Context context){

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());

        Gson gson = new Gson();
        String json = appSharedPrefs.getString("TagData", "");

        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        ArrayList<String> tagNotesList = gson.fromJson(json, type);

        if (tagNotesList==null){
            tagNotesList = new ArrayList<>();
        }

        System.out.println("Tag data size : " + tagNotesList.size());

        return tagNotesList;
    }

    public static void saveTagNote(Context context, ArrayList<String> param){

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(param);

        System.out.printf("TAG SAVED");

        prefsEditor.putString("TagData", json);
        prefsEditor.apply();
    }


    public static void loadDataTagArrayList(Context context){

        if (tagNotesList==null){
            tagNotesList = new ArrayList<>();
            tagNotesList = loadTagNote(context);
        }
    }
}

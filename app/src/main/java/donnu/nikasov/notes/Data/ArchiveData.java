package donnu.nikasov.notes.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Миша on 23.02.2018.
 */

public class ArchiveData {

    public static ArrayList<NotesData> notesArchiveDataList;

    public static ArrayList<NotesData> getNotesDataList(Context context) {

        loadDataToArrayList(context);
        return loadDataList(context);
    }

    private static void loadDataToArrayList(Context context){

        if (notesArchiveDataList ==null){
            notesArchiveDataList = new ArrayList<>();
            notesArchiveDataList = loadDataList(context);
        }
    }

    public static void deleteNoteFromDataList(Context context, NotesData data){

        loadDataToArrayList(context);

        for (NotesData note : notesArchiveDataList) {

            if (note.getID() == data.getID()){
                System.out.println(note.getID()+ " 1");
                System.out.println(data + " 2");
                notesArchiveDataList.remove(note);
                saveDataList(context, notesArchiveDataList);

                return;
            }
            else {
                System.out.println("NE NASHEL");
            }
        }
    }
    public static void addNoteToMainList(Context context, NotesData data, NotesData lastData){

        deleteNoteFromDataList(context, data);

        NotesData.addToNotesDataList(context, lastData);
    }

    public static void addToNotesDataList(Context context, NotesData data){

        loadDataToArrayList(context);

        notesArchiveDataList.add(data);

        saveDataList(context, notesArchiveDataList);
    }

    public static void saveToNotesDataList(Context context, NotesData data,  NotesData lastData){

        deleteNoteFromDataList(context, lastData);

        addToNotesDataList(context, data);

        saveDataList(context, notesArchiveDataList);
    }

    public static void saveDataList(Context context, ArrayList<NotesData> param){

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(param);

        prefsEditor.putString("ArchiveDataList", json);
        prefsEditor.apply();
    }

    private static ArrayList<NotesData> loadDataList(Context context){

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("ArchiveDataList", "");

        Type type = new TypeToken<ArrayList<NotesData>>(){}.getType();
        ArrayList<NotesData> notesData = gson.fromJson(json, type);

        if (notesData==null){
            notesData = new ArrayList<>();
        }

        return notesData;
    }
}

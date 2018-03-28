package donnu.nikasov.notes.Activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.github.bluzwong.swipeback.SwipeBackActivityHelper;

import java.util.ArrayList;

import donnu.nikasov.notes.Data.ArchiveData;
import donnu.nikasov.notes.Data.NotesData;
import donnu.nikasov.notes.Data.TagData;
import donnu.nikasov.notes.R;

public class EditTagActivity extends AppCompatActivity {
    ListView lvMain;
    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;
    SwipeBackActivityHelper helper = new SwipeBackActivityHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tag);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Теги");

//        helper.setEdgeMode(true)
//                .setParallaxMode(true)
//                .setParallaxRatio(3)
//                .setNeedBackgroundShadow(true)
//                .init(this);

        lvMain = (ListView) findViewById(R.id.listViewTag);

        arrayList = TagData.getTagDataList(getApplicationContext());

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                arrayList);

        lvMain.setAdapter(adapter);

        lvMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String tag = lvMain.getItemAtPosition(position).toString();

                TagData.deleteNoteFromDataList(getApplicationContext(), adapter.getItem(position));
                arrayList.remove(position);

                for (NotesData note :  NotesData.notesDataList) {
                    if (note.getTag().equals(tag)){
                        note.setTag("");
                        NotesData.saveDataList(getApplicationContext(), NotesData.notesDataList);
                    }
                }

                for (NotesData note :  ArchiveData.notesArchiveDataList) {
                    if (note.getTag().equals(tag)){
                        note.setTag("");
                        ArchiveData.saveDataList(getApplicationContext(), ArchiveData.notesArchiveDataList);
                    }
                }

                reloadAllData();
                return true;
            }
        });
    }

    public static boolean empty( final String s ) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tag, menu);

        return super.onCreateOptionsMenu(menu);
    }
    private void reloadAllData(){
        arrayList = TagData.getTagDataList(getApplicationContext());
        adapter.clear();
        adapter.addAll(arrayList);
        adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        final EditText etComments = (EditText) findViewById(R.id.etComments);
        if (id == android.R.id.home) {
            finish();
        }

        if (id == R.id.add_tag) {

            final EditText input = new EditText(EditTagActivity.this);

            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditTagActivity.this);
            alertDialog.setTitle("Новая метка");

            alertDialog.setView(input);
            alertDialog.setNegativeButton("Да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String[] tags = TagData.getTagDataList(getApplicationContext()).
                            toArray(new String[TagData.getTagDataList(getApplicationContext()).size()]);

                    int ifExist = 0;
                    if (!input.getText().toString().equals("")) {
                        if (tags.length!=0) {
                            for (String tag : tags) {
                                if (tag.equals(input.getText().toString())) {
                                    ifExist = 2;
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            "Такой тег уже существует", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                            if (ifExist!=2) {
                                TagData.addToNotesDataList(getApplicationContext(), input.getText().toString());
                                reloadAllData();
                            }
                        }
                        else {
                            TagData.addToNotesDataList(getApplicationContext(), input.getText().toString());
                            reloadAllData();
                        }
                    }

                    else if (empty(input.getText().toString())) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Тег не должен быть пустым", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });

            alertDialog.setPositiveButton("Нет", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            alertDialog.create();
            alertDialog.show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

package donnu.nikasov.notes.Activity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.bluzwong.swipeback.SwipeBackActivityHelper;

import java.util.Random;

import donnu.nikasov.notes.Data.ArchiveData;
import donnu.nikasov.notes.Data.NotesData;
import donnu.nikasov.notes.Data.TagData;
import donnu.nikasov.notes.R;

public class EditNoteActivity extends AppCompatActivity implements View.OnTouchListener {

    private EditText editTitle;
    private EditText editDescribe;
    private ImageButton okEditButton;
    private Intent intent;
    private TextView editData;
    private TextView tagText;
    private NotesData note;
    private ImageButton tagButton;
    private ImageButton colorButton;
    private ImageButton buttonAlert;
    private String tagName = "";
    private String colorName = "";
    private ShareActionProvider mShareActionProvider;
    private RelativeLayout editContent;
    private Menu menu;

    final static float STEP = 200;
    float mRatio = 1.0f;
    int mBaseDist;
    float mBaseRatio;
    float fontSize = 17;

    final String[] colorList = {"Лиловый", "Розовый", "Зеленый", "Бриз", "Оранжевый", "Синий", "Желтый"};

    SwipeBackActivityHelper helper = new SwipeBackActivityHelper();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
//        android:windowSoftInputMode="stateHidden|adjustResize"

//        helper.setEdgeMode(true)
//                .setParallaxMode(true)
//                .setParallaxRatio(3)
//                .setNeedBackgroundShadow(true)
//                .init(this);

        editContent = (RelativeLayout) findViewById(R.id.editContent);
        editTitle = (EditText) findViewById(R.id.editTitle);
        editData = (TextView) findViewById(R.id.dataEditNote);
        tagText = (TextView) findViewById(R.id.tagEdit);
        editDescribe = (EditText) findViewById(R.id.editDesc);
        okEditButton = (ImageButton) findViewById(R.id.okEditButton);
        tagButton = (ImageButton) findViewById(R.id.buttonTag);
        colorButton = (ImageButton) findViewById(R.id.buttonColor);
        buttonAlert = (ImageButton) findViewById(R.id.buttonAlert);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        Typeface segoe = Typeface.createFromAsset(getAssets(),"fonts/segoe.ttf");
        Typeface segoeBold = Typeface.createFromAsset(getAssets(),"fonts/segoeBold.ttf");
        Typeface segoeNormal = Typeface.createFromAsset(getAssets(),"fonts/segoeNormal.ttf");

        editDescribe.setTextSize(mRatio + fontSize);

        editTitle.setTypeface(segoeBold);
        editDescribe.setTypeface(segoe);
        editData.setTypeface(segoe);

        intent = getIntent();
//Кнопка оповещения
        buttonAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification.Builder notification = new Notification.Builder(getApplicationContext());
                notification.setSmallIcon(R.drawable.notebook);
                notification.setContentTitle(editTitle.getText().toString());
                notification.setContentText(editDescribe.getText().toString());
                notification.setAutoCancel(true);
                notification.setDefaults(Notification.DEFAULT_ALL);
                notification.build();

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(new Random().nextInt(1000), notification.build());


                AlarmManager manager = (AlarmManager)getSystemService(
                        Context.ALARM_SERVICE);
            }
        });
//Кнопка тегов
        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] tags = TagData.getTagDataList(getApplicationContext()).
                        toArray(new String[TagData.getTagDataList(getApplicationContext()).size()]);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditNoteActivity.this);
                alertDialog.setTitle("Добавить в");
                alertDialog.setPositiveButton("Убрать тег", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (intent.getExtras().get("NewNote")==null){
                            note.setTag("");
                        }
                        else tagName ="";
                    }
                });

                alertDialog.setItems(tags, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int tag) {

                        if (intent.getExtras().get("NewNote")==null){
                            note.setTag(tags[tag]);
                        }
                        else tagName = tags[tag];
                    }
                });

                alertDialog.create();
                alertDialog.show();
            }
        });
//Кнопка цвета
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditNoteActivity.this);
                alertDialog.setTitle("Цвет заметки");

                alertDialog.setItems(colorList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int color) {

                        if (intent.getExtras().get("NewNote")==null){
                            note.setColor(colorList[color]);
                            setActivityColor();
                        }
                        else {
                            colorName = colorList[color];
                            setActivityColor();
                        }
                    }
                });

                alertDialog.create();
                alertDialog.show();
            }
        });
//Берем обьект заметки из редактирования
        if (intent.getExtras().get("EditNote")!=null){

            hideSoftKeyboard();

            note = (NotesData) intent.getExtras().get("EditNote");
            setActivityColor();

            putNoteDataToActivity();

            if (intent.getStringExtra("Type").equals("archive")){
                okEditButton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        fin(v);
                    }
                });
            }
            else {
                okEditButton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        fin(v);
                    }
                });
            }
        }
//Создаем обьект заметки
        else if (intent.getExtras().get("NewNote")!=null){

            editData.setText(getCurrentData());

            showSoftKeyboard(editTitle);

            okEditButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    fin(v);
                }
            });
        }
    }

//Сохранение заметки
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveNote(String type, View v){
//Проверка на пустые поля
        if (editTitle.getText().toString().equals("") ){
            if (editDescribe.getText().toString().equals("")){
                Snackbar.make(v, "Заполните хотя бы одно поле", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;}
        }
        switch (type) {
            case "archive":
                ArchiveData.addNoteToMainList(getApplicationContext(), note, new NotesData(editTitle.getText().toString(),
                        editDescribe.getText().toString(), getCurrentData(), note.getTag(), note.getColor()));
                break;
            case "main":
                NotesData.saveToNotesDataList(getApplicationContext(), new NotesData(editTitle.getText().toString(),
                        editDescribe.getText().toString(), getCurrentData(), note.getTag(), note.getColor()), note);
                break;
            case "newNote":
                if (colorName.equals(""))
                    colorName = "Лиловый";
                NotesData.addToNotesDataList(getApplicationContext(), new NotesData(editTitle.getText().toString(),
                        editDescribe.getText().toString(), getCurrentData(), tagName, colorName));
                break;
        }
    }
//Верхнее меню
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        if (menuItem.getItemId() == android.R.id.home) {
            fin(getWindow().getDecorView().findViewById(R.id.editContent));
        }

        int id = menuItem.getItemId();

        if (id == R.id.action_share) {
            createShareIntent();
            return true;
        }

        else if (id == R.id.action_archive) {
//Архивирование
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditNoteActivity.this);
            alertDialog.setTitle("Добавить заметку в архив?");

            alertDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    menu.findItem(R.id.action_archive).setVisible(false);
//                    menu.findItem(R.id.action_restore).setVisible(true);

                    Random r = new Random();
                    int i1 = r.nextInt(1000);

                    NotesData newnote = new NotesData(note.getTitle(), note.getDescribe(),
                            note.getDate(), note.getTag(), note.getColor(), i1);

                    NotesData.addNoteToArchive(getApplicationContext(), note, newnote);
                }
            });

            alertDialog.setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            alertDialog.create();
            alertDialog.show();

            return true;
        }
//Удаление
        else if (id == R.id.action_del) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditNoteActivity.this);
            alertDialog.setTitle("Удалить заметку?");

            alertDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (intent.getStringExtra("Type").equals("main")) {
                        NotesData.deleteNoteFromDataList(getApplicationContext(), note);
                        System.out.println("DELETE: " + note.getID() + " from archive");
                        finish();
                    }
                    else if (intent.getStringExtra("Type").equals("archive")) {
                        System.out.println("DELETE: " + note.getID() + " from archive");
                        ArchiveData.deleteNoteFromDataList(getApplicationContext(), note);
                        finish();
                    }
                }
            });

            alertDialog.setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            alertDialog.create();
            alertDialog.show();

            return true;
        }
//Восстановление
        else if (id == R.id.action_restore){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditNoteActivity.this);
            alertDialog.setTitle("Восстановить заметку?");

            alertDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

//                    menu.findItem(R.id.action_archive).setVisible(true);
                    menu.findItem(R.id.action_restore).setVisible(false);

                    NotesData newNote = new NotesData(editTitle.getText().toString(),editDescribe.getText().toString(),
                            getCurrentData(), note.getTag(), note.getColor());

                    ArchiveData.addNoteToMainList(getApplicationContext(), note, newNote);

                }
            });

            alertDialog.setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            alertDialog.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(menuItem);
    }
//Дата
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getCurrentData(){

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm");
        return sdf.format(c.getTime());
    }
//Заполнение страницы данными
    public void putNoteDataToActivity(){

        if (note.getTag()!=null){
            tagName = note.getTag();}
        if (note.getColor()!=null){
            colorName = note.getColor();}
        editData.setText(note.getDate());
        editTitle.setText(note.getTitle());
        editDescribe.setText(note.getDescribe());
    }
//Работа с клавиатурой
    public void showSoftKeyboard(View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    public void hideSoftKeyboard() {

        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);

        this.menu = menu;

        MenuItem item = menu.findItem(R.id.action_share);

        if (intent.getExtras().get("NewNote")==null) {
            if (intent.getStringExtra("Type").equals("main")) {
                menu.findItem(R.id.action_archive).setVisible(true);
                menu.findItem(R.id.action_restore).setVisible(false);
            } else {
                menu.findItem(R.id.action_archive).setVisible(false);
                menu.findItem(R.id.action_restore).setVisible(true);
            }
        }

        else {
            menu.findItem(R.id.action_share).setVisible(false);
            menu.findItem(R.id.action_restore).setVisible(false);
            menu.findItem(R.id.action_archive).setVisible(false);
            menu.findItem(R.id.action_del).setVisible(false);
        }

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBackPressed() {
        fin(getWindow().getDecorView().findViewById(R.id.editContent));
    }
//Поделиться
    private void createShareIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getTextFromNote());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
//Метод для сбора текста при шаринге
    private String getTextFromNote(){

        String text = "";
        text+=editTitle.getText().toString();
        text+="\n"+editDescribe.getText().toString();

        return text;
    }
//Проверка при завершении активности на изменения
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fin(final View v){

        if (intent.getExtras().get("NewNote")!=null) {
            saveNote("newNote", v);
            finish();
        }

        else if (!note.getTitle().equals(editTitle.getText().toString()) || !note.getDescribe().equals(editDescribe.getText().toString()) ||
                !note.getTag().equals(tagName) || !note.getColor().equals(colorName)){

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditNoteActivity.this);
            alertDialog.setTitle("Сохранить изменения?");

            alertDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (intent.getExtras().get("EditNote")!=null){
                        if (intent.getStringExtra("Type").equals("archive")){
                            saveNote("archive", v);
                            finish();
                        }
                        else {
                            saveNote("main", v);
                            finish();
                        }
                    }
                }
            });

            alertDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            alertDialog.setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.create();
            alertDialog.show();
        }

        else
            this.finish();

        MainActivity.mAdapter.notifyDataSetChanged();
    }
//Установка цвета активности
    public void setActivityColor(){
        String color;

        if (intent.getExtras().get("NewNote")==null) {
             color = note.getColor();
        }
        else color = colorName;

        switch (color) {
            case "Лиловый":
                editTitle.setTextColor(getResources().getColor(R.color.primaryColor));
                break;
            case "Розовый":
                editTitle.setTextColor(getResources().getColor(R.color.pink));
                break;
            case "Зеленый":
                editTitle.setTextColor(getResources().getColor(R.color.green));
                break;
            case "Бриз":
                editTitle.setTextColor(getResources().getColor(R.color.blue));
                break;
            case "Оранжевый":
                editTitle.setTextColor(getResources().getColor(R.color.orange));
                break;
            case "Синий":
                editTitle.setTextColor(getResources().getColor(R.color.darkBlue));
                break;
            case "Желтый":
                editTitle.setTextColor(getResources().getColor(R.color.yellow));
                break;
        }
    }

//Увеличение текста
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            int action = event.getAction();
            int pureaction = action & MotionEvent.ACTION_MASK;
            if (pureaction == MotionEvent.ACTION_POINTER_DOWN) {
                mBaseDist = getDistance(event);
                mBaseRatio = mRatio;
            } else {
                float delta = (getDistance(event) - mBaseDist) / STEP;
                float multi = (float) Math.pow(2, delta);
                mRatio = Math.min(1024.0f, Math.max(0.1f, mBaseRatio * multi));
                editDescribe.setTextSize(mRatio + fontSize);
            }
        }
        return true;
    }

    int getDistance(MotionEvent event) {
        int dx = (int) (event.getX(0) - event.getX(1));
        int dy = (int) (event.getY(0) - event.getY(1));
        return (int) (Math.sqrt(dx * dx + dy * dy));
    }

    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
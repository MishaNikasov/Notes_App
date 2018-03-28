package donnu.nikasov.notes.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import donnu.nikasov.notes.Data.ArchiveData;
import donnu.nikasov.notes.Data.NotesData;
import donnu.nikasov.notes.Data.TagData;
import donnu.nikasov.notes.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    public static MyAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private TextView headerDateText;
    private FloatingActionButton fab;
    private Menu menu;

    private static TextView backEmptyPageText;
    private static ImageView backEmptyPage;
    private boolean archOrMain;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startEditActivity();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TagData.loadDataTagArrayList(getApplicationContext());

        Typeface segoeNormal = Typeface.createFromAsset(getAssets(),"fonts/segoeNormal.ttf");

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(getApplicationContext(), NotesData.getNotesDataList(getApplicationContext()), "main");
        mRecyclerView.setAdapter(mAdapter);

        backEmptyPage = (ImageView) findViewById(R.id.backEmptyPage);
        backEmptyPageText = (TextView) findViewById(R.id.backEmptyPageText);

        backEmptyPageText.setTypeface(segoeNormal);

        checkEmptyness();
    }

    private void startEditActivity(){
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra("NewNote", "New");
//        SwipeBackActivityHelper.startSwipeActivity(this, intent, 1, true);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        this.menu = menu;

        MenuItem item = menu.findItem(R.id.searchBar);
        SearchView searchView =(SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.searchInList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.searchInList(newText);
                return true;
            }
        });

        headerDateText = (TextView) findViewById(R.id.headerDateText);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy");
        String format = sdf.format(c.getTime());

        headerDateText.setText(format);

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_archive) {

            archOrMain = true;

            fab.setVisibility(View.INVISIBLE);

            mAdapter = new MyAdapter(getApplicationContext(),ArchiveData.getNotesDataList(getApplicationContext()), "archive");
            mRecyclerView.setAdapter(mAdapter);

            mAdapter.notifyDataSetChanged();

            checkEmptyness();

        } else if (id == R.id.nav_notes) {

            fab.setVisibility(View.VISIBLE);

            archOrMain = false;

            mAdapter = new MyAdapter(getApplicationContext(), NotesData.getNotesDataList(getApplicationContext()), "main");
            mRecyclerView.setAdapter(mAdapter);

            mAdapter.notifyDataSetChanged();

            checkEmptyness();

        } else if (id == R.id.nav_add){
            Intent intent = new Intent(this, EditTagActivity.class);
            startActivity(intent);
//            SwipeBackActivityHelper.startSwipeActivity(this, intent, 0, true);

        }

        else {
            mAdapter.sortByTag(item.toString());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    public static void checkEmptyness(){

        if (mAdapter.getItemCount()>0){

            backEmptyPageText.setVisibility(View.GONE);
            backEmptyPage.setVisibility(View.GONE);
        }
        else
        {
            backEmptyPageText.setVisibility(View.VISIBLE);
            backEmptyPage.setVisibility(View.VISIBLE);}
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (menu!=null) {
            MenuItem item = menu.findItem(R.id.searchBar);
            SearchView searchView = (SearchView) item.getActionView();

        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        }
            searchView.setQuery("", false);
            searchView.clearFocus();
            getSupportActionBar().collapseActionView();
            searchView.clearAnimation();
        }
        if (archOrMain){
            mAdapter = new MyAdapter(getApplicationContext(),ArchiveData.getNotesDataList(getApplicationContext()), "archive");
            mRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter = new MyAdapter(getApplicationContext(), NotesData.getNotesDataList(getApplicationContext()), "main");
            mRecyclerView.setAdapter(mAdapter);
        }

        checkEmptyness();
        mAdapter.notifyDataSetChanged();

        Menu m = navigationView.getMenu();

        for (int i = 0; i < m.size(); i++) {
            m.removeItem(i);
        }

        final String[] tags = TagData.getTagDataList(getApplicationContext()).
                toArray(new String[TagData.getTagDataList(getApplicationContext()).size()]);

        for(int i = 0; i< tags.length; i++)
        {
            String name = m.getItem(i).getTitle().toString();
            m.add(0, i, 100, tags[i]);
        }

    }
}

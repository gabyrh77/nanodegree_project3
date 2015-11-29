package it.jaschke.alexandria;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import it.jaschke.alexandria.api.Callback;
import it.jaschke.alexandria.utils.Utils;

/**
 * GabyO:
 * Moved MessageReceiver to the corresponding fragment
 * Fix right container visible for all fragments on tablet view
 * Close the keyboard when the drawer will be shown
 * Updated the look and implementation to fit the new design of the navigation drawer
 */
public class MainActivity extends AppCompatActivity implements ScanFragment.OnScanResultListener, NavigationView.OnNavigationItemSelectedListener, Callback {
    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_SELECTED_FRAGMENT = "ARG_SF";
    private static final String TAG_FRAGMENT_LIST_BOOKS = "TAG_LIST_BOOKS";
    private static final String TAG_FRAGMENT_SCAN = "TAG_SCAN";
    private static final String TAG_FRAGMENT_ABOUT = "TAG_ABOUT";
    private int mSelectedFragment;
    private boolean mUserLearnedDrawer;
    private int mTitleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){


            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //close keyboard
                Utils.closeKeyboard(MainActivity.this);

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState!=null){
            mSelectedFragment = savedInstanceState.getInt(ARG_SELECTED_FRAGMENT);
            mTitleId = savedInstanceState.getInt(ARG_TITLE, 0);
            setTitle(mTitleId);
        }else{
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());

            mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
            Bundle args = null;
            if(!mUserLearnedDrawer){
                drawer.openDrawer(GravityCompat.START);
                args = new Bundle();
                args.putBoolean(AddBook.PREVENT_KEYBOARD_ARG, true);
            }

            int preference = sp.getInt(getString(R.string.pref_startScreen_key), 1);
            if(preference==0){
                mSelectedFragment = R.id.nav_list_books;
            }else{
                mSelectedFragment = R.id.nav_scan;
            }
            setFragment(R.id.main_content, mSelectedFragment, args);
        }
        navigationView.setCheckedItem(mSelectedFragment);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_SELECTED_FRAGMENT, mSelectedFragment);
        outState.putInt(ARG_TITLE, mTitleId);
    }

    private void setFragment(int container, int idFragment, Bundle args){
        Fragment nextFragment;
        int titleId;
        String tag;

        switch (idFragment){

            case R.id.nav_list_books:
                tag = TAG_FRAGMENT_LIST_BOOKS;
                titleId = R.string.books;
                nextFragment = new ListOfBooks();
                break;
            case R.id.nav_about:
                tag = TAG_FRAGMENT_ABOUT;
                titleId = R.string.about;
                nextFragment = new About();
                break;
            case R.id.nav_scan_bar:
                tag = TAG_FRAGMENT_SCAN;
                titleId = 0;
                nextFragment = new ScanFragment();
                break;
            case R.id.nav_detail:
                tag = TAG_FRAGMENT_SCAN;
                titleId = R.string.book_detail;
                nextFragment = new BookDetail();
                break;
            case R.id.nav_scan:
            default:
                tag = TAG_FRAGMENT_SCAN;
                titleId = R.string.scan;
                nextFragment = new AddBook();

                break;
        }
        if(args!=null){
            nextFragment.setArguments(args);
        }
        mSelectedFragment = idFragment;
        setTitle(titleId);

        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction()
                .replace(container, nextFragment, tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(tag)
                .commit();

    }

    public void setTitle(int titleId) {
        if(titleId!=0) {
            String title = getString(titleId);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
        }
    }

    public void scanBook(View v){
        Utils.closeKeyboard(this);
       // ScanFragment fragment = new ScanFragment();
        setFragment(R.id.main_content, R.id.nav_scan_bar, null);
    }

    @Override
    public void onScanResult(String scanText){
        Bundle args = new Bundle();
        args.putString(AddBook.SCAN_ARG, scanText);
        setFragment(R.id.main_content, R.id.nav_scan, args);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(getFragmentManager().getBackStackEntryCount()>1){
                getFragmentManager().popBackStack();
                return;
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String ean) {
        Bundle args = new Bundle();
        args.putString(BookDetail.EAN_KEY, ean);

       // BookDetail fragment = new BookDetail();
        //fragment.setArguments(args);

        int id = R.id.main_content;
        if(findViewById(R.id.main_detail_content) != null){
            id = R.id.main_detail_content;
        }

        setFragment(id, R.id.nav_detail, args);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        setFragment(R.id.main_content, id, null);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
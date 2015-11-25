package it.jaschke.alexandria;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
 */
public class MainActivity extends AppCompatActivity implements ScanFragment.OnScanResultListener, NavigationDrawerFragment.NavigationDrawerCallbacks, Callback {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment navigationDrawerFragment;
    private static final String ARG_SELECTED_FRAGMENT = "ARG_SF";
    private int mSelectedFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        title = getTitle();

        // Set up the drawer.
        navigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        if(savedInstanceState!=null){
            mSelectedFragment = savedInstanceState.getInt(ARG_SELECTED_FRAGMENT);
            if(mSelectedFragment!=0){
                hideRightContainer();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_SELECTED_FRAGMENT, mSelectedFragment);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment nextFragment;
        switch (position){
            default:
            case 0:
                showRightContainer();
                nextFragment = new ListOfBooks();
                break;
            case 1:
                hideRightContainer();
                nextFragment = new AddBook();
                break;
            case 2:
                hideRightContainer();
                nextFragment = new About();
                break;

        }
        mSelectedFragment = position;
        setFragment(nextFragment);
    }

    private void setFragment(Fragment nextFragment){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, nextFragment)
                .addToBackStack((String) title)
                .commit();
    }

    public void setTitle(int titleId) {
        title = getString(titleId);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(title);
        }
    }

    public void scanBook(View v){
        Utils.closeKeyboard(this);
        ScanFragment fragment = new ScanFragment();
        setFragment(fragment);
    }

    private void hideRightContainer(){
        if(findViewById(R.id.right_container) != null){
            findViewById(R.id.right_container).setVisibility(View.GONE);
        }
    }

    private void showRightContainer(){
        if(findViewById(R.id.right_container) != null){
            findViewById(R.id.right_container).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onScanResult(String scanText){
        setFragment(AddBook.newInstanceWithBarcode(scanText));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

        BookDetail fragment = new BookDetail();
        fragment.setArguments(args);

        int id = R.id.container;
        if(findViewById(R.id.right_container) != null){
            id = R.id.right_container;
        }
        getFragmentManager().beginTransaction()
                .replace(id, fragment)
                .addToBackStack("Book Detail")
                .commit();

    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount()>1){
            getFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }


}
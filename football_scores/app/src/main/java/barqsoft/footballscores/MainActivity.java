package barqsoft.footballscores;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import barqsoft.footballscores.adapters.MyPageAdapter;
import barqsoft.footballscores.api.FootballAPIService;
import barqsoft.footballscores.service.MatchesSyncAdapter;
import barqsoft.footballscores.service.SeasonsSyncAdapter;

public class MainActivity extends AppCompatActivity
{
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private MyPageAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static int selected_match_id;
    private static int DEFAULT_FRAGMENT = 2;
    public static String LOG_TAG = "MainActivity";
    private final String SAVE_TAG = "Save_fragment_tag";
    private int selectedFragment;
    private BroadcastReceiver messageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            SeasonsSyncAdapter.initializeSyncAdapter(this);
            MatchesSyncAdapter.initializeSyncAdapter(this);
            SeasonsSyncAdapter.syncImmediately(this);
            MatchesSyncAdapter.syncImmediately(this);
            selectedFragment = DEFAULT_FRAGMENT;
        }else{
            selectedFragment = savedInstanceState.getInt(SAVE_TAG);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_launcher);
        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.
        mSectionsPagerAdapter = new MyPageAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(selectedFragment);

        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter(FootballAPIService.MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, filter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about)
        {
            Intent start_about = new Intent(this,AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

   @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putInt(SAVE_TAG,mViewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    //Receive status from APIService
    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int response = intent.getIntExtra(FootballAPIService.MESSAGE_KEY, -1);
            if (response == FootballAPIService.STATUS_OK) {
                return;
            }

            int text = 0;
            switch (response) {
                case FootballAPIService.STATUS_NO_NETWORK:
                    text = R.string.no_network;
                    break;
                case FootballAPIService.STATUS_ERROR:
                    text = R.string.server_error;
                    break;
                case FootballAPIService.STATUS_NOT_FOUND:
                default:
                    break;
            }

            if(text!=0) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), text, Snackbar.LENGTH_LONG);
                if (response == FootballAPIService.STATUS_NO_NETWORK) {
                    snackbar.setAction(R.string.snackbar_action_settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    });

                }
                snackbar.show();
            }
        }
    }
}

package barqsoft.footballscores;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import barqsoft.footballscores.adapters.ScoresAdapter;
import barqsoft.footballscores.adapters.ViewHolder;
import barqsoft.footballscores.db.DatabaseContract;
import barqsoft.footballscores.service.FootballFetchService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    public ScoresAdapter mAdapter;
    public static final int SCORES_LOADER = 0;
    private String[] fragmentdate = new String[1];
    private int last_selected_item = -1;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_DATE = "section_date";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MainScreenFragment newInstance(String date) {
        MainScreenFragment fragment = new MainScreenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    public MainScreenFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final ListView score_list = (ListView) rootView.findViewById(R.id.scores_list);

        if(savedInstanceState==null){
            fragmentdate[0] = getArguments().getString(ARG_SECTION_DATE);
        }else{
            fragmentdate = savedInstanceState.getStringArray(ARG_SECTION_DATE);
        }
        mAdapter = new ScoresAdapter(getActivity(),null,0);
        score_list.setAdapter(mAdapter);
        getLoaderManager().initLoader(SCORES_LOADER,null,this);

        score_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ViewHolder selected = (ViewHolder) view.getTag();
                mAdapter.detail_match_id = selected.match_id;
                //MainActivity.selected_match_id = (int) selected.match_id;
                mAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(ARG_SECTION_DATE, fragmentdate);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(getActivity(), DatabaseContract.Score.buildScoreWithDate(),
                null, null, fragmentdate, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        Log.v(MainScreenFragment.class.getName(), "Loader query: " + String.valueOf(cursor.getCount()));
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        mAdapter.swapCursor(null);
    }

    //Receive status from FetchService
    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int response = intent.getIntExtra(FootballFetchService.MESSAGE_KEY, -1);
            if (response == FootballFetchService.STATUS_OK) {
                return;
            }

            switch (response) {
                case FootballFetchService.STATUS_NO_NETWORK:

                    break;
                case FootballFetchService.STATUS_NOT_FOUND:

                    break;
                case FootballFetchService.STATUS_ERROR:
                default:

                    break;
            }

        }
    }

}

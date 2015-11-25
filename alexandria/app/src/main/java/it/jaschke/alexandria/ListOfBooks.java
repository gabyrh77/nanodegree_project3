package it.jaschke.alexandria;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import it.jaschke.alexandria.api.BookListAdapter;
import it.jaschke.alexandria.api.Callback;
import it.jaschke.alexandria.data.AlexandriaContract;

/**
 * GabyO:
 * Replace ListView for RecyclerView
 * Added SearchView implementation
 * Added BookService call method and MessageReceiver implementation
 */
public class ListOfBooks extends Fragment implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG_SEARCH = "TAG_SEARCH";
    private static final String TAG_POSITION = "TAG_POS";
    private BookListAdapter mBookListAdapter;
    private RecyclerView mBookList;
    private int mPosition = ListView.INVALID_POSITION;
    private final int LOADER_ID = 10;
    private String mSearchString;
    private SearchView mSearchView;
    private int mChoiceMode;

    public ListOfBooks() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the options menu from XML
        inflater.inflate(R.menu.list_of_books, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setOnQueryTextListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_of_books, container, false);
        getActivity().setTitle(R.string.books);
        if(savedInstanceState!=null){
            mSearchString = savedInstanceState.getString(TAG_SEARCH);
            mPosition = savedInstanceState.getInt(TAG_POSITION, ListView.INVALID_POSITION);
        }
        mChoiceMode = getResources().getInteger(R.integer.item_choice_mode);

        mBookListAdapter = new BookListAdapter(getActivity(), rootView.findViewById(R.id.empty_view), new BookListAdapter.BookListAdapterOnClickHandler() {
            @Override
            public void onClick(String bookId, int adapterPosition) {
                mPosition = adapterPosition;
                ((Callback) getActivity()).onItemSelected(bookId);
            }
        }, mChoiceMode);
        mBookList = (RecyclerView) rootView.findViewById(R.id.list_books_view);

        mBookList.setHasFixedSize(true);
        // Set the layout manager
        mBookList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBookList.setAdapter(mBookListAdapter);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String querySelection = AlexandriaContract.BookEntry.TITLE +" LIKE ? OR " + AlexandriaContract.BookEntry.SUBTITLE + " LIKE ? ";
        if(mSearchString!=null && mSearchString.length()>0){
            String searchQuery = "%"+mSearchString+"%";
            return new CursorLoader(
                    getActivity(),
                    AlexandriaContract.BookEntry.CONTENT_URI,
                    null,
                    querySelection,
                    new String[]{searchQuery,searchQuery},
                    null
            );
        }

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mBookListAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION && mBookListAdapter.getSelectedItemPosition()<0) {
            mBookList.smoothScrollToPosition(mPosition);
          /*  if(mChoiceMode == ListView.CHOICE_MODE_SINGLE) {
                BookListAdapter.ViewHolder viewHolder = (BookListAdapter.ViewHolder) mBookList.findViewHolderForItemId(mBookListAdapter.getItemId(mPosition));
                mBookListAdapter.selectView(viewHolder);
            }*/
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TAG_SEARCH, mSearchString);
        outState.putInt(TAG_POSITION, mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookListAdapter.swapCursor(null);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mSearchString = newText;
        ListOfBooks.this.restartLoader();
        return true;
    }
}
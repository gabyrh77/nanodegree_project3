package it.jaschke.alexandria;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.utils.Utils;

/**
 * GabyO:
 * Removed onAttach method
 * Added the MessageReceiver and improved user feedback regarding fetching books
 * Fixed a bug regarding the activity title on phone's rotation
 * Showing/hiding the keyboard
 */
public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";
    private static final String SCAN_ARG = "SCAN_ARG";
    private static final String EAN_CONTENT = "eanContent";
    private final int LOADER_ID = 1;
    private BroadcastReceiver messageReceiver;
    private EditText eanEditText;
    private Button mSaveButton;
    private Button mCancelButton;
    private TextView mTitleText;
    private TextView mSubtitleText;
    private ImageView mBookCoverImageView;
    private TextView mAuthorsText;
    private TextView mCategoriesText;
    private String mScanText;


    public AddBook() {
    }

    public static AddBook newInstanceWithBarcode(String scanText) {
        AddBook myFragment = new AddBook();

        Bundle args = new Bundle();
        args.putString(SCAN_ARG, scanText);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (eanEditText != null) {
            outState.putString(EAN_CONTENT, eanEditText.getText().toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mScanText = getArguments().getString(SCAN_ARG);
        } else {
            mScanText = null;
        }

        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, filter);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        getActivity().setTitle(R.string.scan);
        eanEditText = (EditText) rootView.findViewById(R.id.ean);

        eanEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ean = s.toString();
                //catch isbn10 numbers
                if (ean.length() == 10 && !ean.startsWith("978")) {
                    ean = "978" + ean;
                }
                if (ean.length() < 13) {
                    clearFields();
                    return;
                }
                //Once we have an ISBN, start a book intent
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.FETCH_BOOK);
                getActivity().startService(bookIntent);
                AddBook.this.restartLoader();
            }
        });

        mSaveButton = (Button) rootView.findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eanEditText.setText("");
                Utils.showKeyboard(eanEditText, getActivity());
            }
        });

        mCancelButton = (Button) rootView.findViewById(R.id.delete_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, eanEditText.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                eanEditText.setText("");
                Utils.showKeyboard(eanEditText, getActivity());
            }
        });
        mBookCoverImageView = (ImageView) rootView.findViewById(R.id.bookCover);
        mTitleText = (TextView) rootView.findViewById(R.id.bookTitle);
        mSubtitleText = (TextView) rootView.findViewById(R.id.bookSubTitle);
        mCategoriesText = (TextView) rootView.findViewById(R.id.categories);
        mAuthorsText = (TextView) rootView.findViewById(R.id.authors);

        if (savedInstanceState != null) {
            eanEditText.setText(savedInstanceState.getString(EAN_CONTENT));
        } else if (mScanText != null) {
            eanEditText.setText(mScanText);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Showing keyboard
       Utils.showKeyboard(eanEditText, getActivity());
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (eanEditText.getText().length() == 0) {
            return null;
        }
        String eanStr = eanEditText.getText().toString();
        if (eanStr.length() == 10 && !eanStr.startsWith("978")) {
            eanStr = "978" + eanStr;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || !data.moveToFirst()) {
            return;
        }
        //close keyboard
        Utils.closeKeyboard(getActivity());

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        if (bookTitle != null && !bookTitle.isEmpty()) {
            mTitleText.setText(bookTitle);
            mTitleText.setVisibility(View.VISIBLE);
        } else {
            mTitleText.setVisibility(View.GONE);
        }

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        if (bookSubTitle != null && !bookSubTitle.isEmpty()) {
            mSubtitleText.setText(bookSubTitle);
            mSubtitleText.setVisibility(View.VISIBLE);
        } else {
            mSubtitleText.setVisibility(View.GONE);
        }

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        if (authors != null && !authors.isEmpty()) {
            String[] authorsArr = authors.split(",");
            mAuthorsText.setLines(authorsArr.length);
            mAuthorsText.setText(authors.replace(",", "\n"));
            mAuthorsText.setVisibility(View.VISIBLE);
        } else {
            mAuthorsText.setVisibility(View.GONE);
        }

        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        Glide.with(getActivity()).load(imgUrl).placeholder(R.drawable.placeholder).into(mBookCoverImageView);
        mBookCoverImageView.setVisibility(View.VISIBLE);
        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        if (categories != null && !categories.isEmpty()) {
            mCategoriesText.setText(categories);
            mCategoriesText.setVisibility(View.VISIBLE);
        } else {
            mCategoriesText.setVisibility(View.GONE);
        }

        mSaveButton.setVisibility(View.VISIBLE);
        mCancelButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void clearFields() {
        mTitleText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        mTitleText.setVisibility(View.GONE);

        mSubtitleText.setVisibility(View.GONE);

        mAuthorsText.setVisibility(View.GONE);
        mCategoriesText.setVisibility(View.GONE);
        mBookCoverImageView.setVisibility(View.GONE);
        mSaveButton.setVisibility(View.GONE);
        mCancelButton.setVisibility(View.GONE);
    }

    //Receive status from BookService
    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int response = intent.getIntExtra(MESSAGE_KEY, -1);
            if (response == BookService.STATUS_OK) {
                return;
            }

            switch (response) {
                case BookService.STATUS_NO_NETWORK:
                    mTitleText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sync_disabled, 0, 0, 0);
                    mTitleText.setText(R.string.no_network);
                    break;
                case BookService.STATUS_NOT_FOUND:
                    mTitleText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sync_problem, 0, 0, 0);
                    mTitleText.setText(R.string.not_found);
                    break;
                case BookService.STATUS_ERROR:
                default:
                    mTitleText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sync_problem, 0, 0, 0);
                    mTitleText.setText(R.string.bookservice_error);
                    break;
            }
            mTitleText.setVisibility(View.VISIBLE);
        }
    }
}
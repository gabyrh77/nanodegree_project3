package it.jaschke.alexandria.api;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;

/**
 * Created by saj on 11/01/15.
 */
/**
 * GabyO: change the adapter to RecyclerView, updated to display image using gridle
 */
public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder> {
    private Cursor mCursor;
    final private Context mContext;
    final private View mEmptyView;
    final private BookListAdapterOnClickHandler mClickHandler;
    final private ItemChoiceManager mICM;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String imgUrl = mCursor.getString(mCursor.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        Glide.with(mContext).load(imgUrl).placeholder(R.drawable.placeholder_small).into(holder.bookCover);
        String bookTitle = mCursor.getString(mCursor.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        holder.bookTitle.setText(bookTitle);

        String bookSubTitle = mCursor.getString(mCursor.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        holder.bookSubTitle.setText(bookSubTitle);

        mICM.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mCursor!=null?mCursor.getCount():0;
    }



    public BookListAdapter(Context context, View emptyView, BookListAdapterOnClickHandler handler, int choiceMode) {
        super();
        mContext = context;
        mEmptyView = emptyView;
        mClickHandler = handler;
        mICM = new ItemChoiceManager(this);
        mICM.setChoiceMode(choiceMode);
    }

    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if ( viewHolder instanceof ViewHolder ) {
            ViewHolder vfh = (ViewHolder)viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mICM.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        mICM.onSaveInstanceState(outState);
    }

    public int getSelectedItemPosition() {
        return mICM.getSelectedItemPosition();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView bookCover;
        public final TextView bookTitle;
        public final TextView bookSubTitle;

        public ViewHolder(View view) {
            super(view);
            bookCover = (ImageView) view.findViewById(R.id.fullBookCover);
            bookTitle = (TextView) view.findViewById(R.id.listBookTitle);
            bookSubTitle = (TextView) view.findViewById(R.id.listBookSubTitle);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String id = mCursor.getString(mCursor.getColumnIndex(AlexandriaContract.BookEntry._ID));
            mClickHandler.onClick(id, adapterPosition);
            mICM.onClick(this);
        }
    }

    public static interface BookListAdapterOnClickHandler {
        void onClick(String bookId, int adapterPosition);
    }
}

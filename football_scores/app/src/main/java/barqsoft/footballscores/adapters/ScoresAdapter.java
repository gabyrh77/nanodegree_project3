package barqsoft.footballscores.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
//import barqsoft.footballscores.adapter.SvgSoftwareLayerSetter;
//import com.bumptech.glide;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.bumptech.glide.request.RequestOptions.fitCenterTransform;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;
import barqsoft.footballscores.R;
import barqsoft.footballscores.utils.BitmapUtil;
import barqsoft.footballscores.utils.Utilities;
import barqsoft.footballscores.db.DatabaseContract;
import barqsoft.footballscores.db.ScoresProvider;
import barqsoft.footballscores.svg.SvgSoftwareLayerSetter;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter
{

    public double detail_match_id = 0;
    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";
    private RequestBuilder<PictureDrawable> requestSVGBuilder;
    private RequestBuilder<Drawable> requestBuilder;

    public ScoresAdapter(Context context, Cursor cursor, int flags)
    {
        super(context,cursor,flags);
        requestSVGBuilder = Glide.with(context)
                .as(PictureDrawable.class)
                .apply(placeholderOf(R.drawable.no_icon).error(R.drawable.no_icon)
                        .fitCenter(context).diskCacheStrategy(DiskCacheStrategy.DATA))
                .transition(withCrossFade())
                .listener(new SvgSoftwareLayerSetter());

        requestBuilder = Glide.with(context)
                .asDrawable()
                .apply(fitCenterTransform(context).placeholder(R.drawable.no_icon)
                        .error(R.drawable.no_icon).diskCacheStrategy(DiskCacheStrategy.ALL));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);

        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor)
    {
        final ViewHolder mHolder = (ViewHolder) view.getTag();
        mHolder.home_name.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.Score.HOME_COL)));
        mHolder.away_name.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.Score.AWAY_COL)));
        mHolder.date.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.Score.TIME_COL)));
        mHolder.score.setText(Utilities.getScores(cursor.getInt(cursor.getColumnIndex(DatabaseContract.Score.HOME_GOALS_COL)), cursor.getInt(cursor.getColumnIndex(DatabaseContract.Score.AWAY_GOALS_COL))));
        mHolder.match_id = cursor.getDouble(cursor.getColumnIndex(DatabaseContract.Score.MATCH_ID));

        String homeCrestUrl = (cursor.getColumnIndex(ScoresProvider.HOME_TEAM_PICTURE_ALIAS)>=0)?
                cursor.getString(cursor.getColumnIndex(ScoresProvider.HOME_TEAM_PICTURE_ALIAS)):null;
        if(homeCrestUrl!=null){
            if(homeCrestUrl.toLowerCase().endsWith(BitmapUtil.SVG_EXTENSION)) {
                requestSVGBuilder.load(homeCrestUrl).into(mHolder.home_crest);
            }else{
                requestBuilder.load(homeCrestUrl).into(mHolder.home_crest);
            }
        }else{
            mHolder.home_crest.setImageResource(R.drawable.no_icon);
        }

        String awayCrestUrl = (cursor.getColumnIndex(ScoresProvider.AWAY_TEAM_PICTURE_ALIAS)>=0)?
                cursor.getString(cursor.getColumnIndex(ScoresProvider.AWAY_TEAM_PICTURE_ALIAS)):null;
        if(awayCrestUrl!=null){
            if(awayCrestUrl.toLowerCase().endsWith(BitmapUtil.SVG_EXTENSION)) {
                requestSVGBuilder.load(awayCrestUrl).into(mHolder.away_crest);
            }else{
                requestBuilder.load(awayCrestUrl).into(mHolder.away_crest);
            }
        }else{
            mHolder.away_crest.setImageResource(R.drawable.no_icon);
        }

        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
        if(mHolder.match_id == detail_match_id){

            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Utilities.getMatchDay(cursor.getInt(cursor.getColumnIndex(DatabaseContract.Score.MATCH_DAY)),
                    cursor.getString(cursor.getColumnIndex(DatabaseContract.Season.SEASON_COD))));
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utilities.getLeague(cursor.getString(cursor.getColumnIndex(DatabaseContract.Season.SEASON_COD)), context));
            ImageButton share_button = (ImageButton) v.findViewById(R.id.share_button);
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    //add Share Action
                    context.startActivity(createShareForecastIntent(mHolder.home_name.getText()+" "
                    +mHolder.score.getText()+" "+mHolder.away_name.getText() + " "));
                }
            });
        }
        else
        {
            container.removeAllViews();
        }

    }
    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

}

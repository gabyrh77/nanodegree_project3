package barqsoft.footballscores.widget;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.RequestBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import barqsoft.footballscores.R;
import barqsoft.footballscores.utils.BitmapUtil;
import barqsoft.footballscores.utils.Utilities;
import barqsoft.footballscores.db.DatabaseContract;
import barqsoft.footballscores.db.ScoresProvider;

/**
 * Created by Gaby on 11/15/2015.
 */
public class FootballWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = FootballWidgetRemoteViewsService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;
            private BitmapUtil bitmapUtil;

            @Override
            public void onCreate() {
                bitmapUtil = new BitmapUtil(FootballWidgetRemoteViewsService.this);
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                Uri todayUri = DatabaseContract.Score.SCORES_CONTENT_URI;
                String strDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
                data = getContentResolver().query(todayUri,
                        null,
                        null,
                        null,//new String[]{strDate},
                        DatabaseContract.Score.DATE_COL + " ASC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                boolean returnHome = false;
                final RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.scores_list_item);

                views.setTextViewText(R.id.home_name, data.getString(data.getColumnIndex(DatabaseContract.Score.HOME_COL)));
                views.setTextViewText(R.id.away_name, data.getString(data.getColumnIndex(DatabaseContract.Score.AWAY_COL)));
                views.setTextViewText(R.id.data_textview, data.getString(data.getColumnIndex(DatabaseContract.Score.TIME_COL)));
                views.setTextViewText(R.id.data_textview, Utilities.getScores(data.getInt(data.getColumnIndex(DatabaseContract.Score.HOME_GOALS_COL)), data.getInt(data.getColumnIndex(DatabaseContract.Score.AWAY_GOALS_COL))));
                double match_id = data.getDouble(data.getColumnIndex(DatabaseContract.Score.MATCH_ID));

                String homeCrestUrl = (data.getColumnIndex(ScoresProvider.HOME_TEAM_PICTURE_ALIAS)>=0)?
                        data.getString(data.getColumnIndex(ScoresProvider.HOME_TEAM_PICTURE_ALIAS)):null;
                Bitmap homeTeamBitmap = null;
                if(homeCrestUrl!=null){
                    homeTeamBitmap = bitmapUtil.getBitmapFromUrl(homeCrestUrl);
                   /* try{
                        if(homeCrestUrl.toLowerCase().endsWith(".svg")) {
                            homeTeamBitmap = Glide.with(FootballWidgetRemoteViewsService.this).as(Bitmap.class).load(homeCrestUrl).into(100, 100).get();
                            //homeTeamBitmap = bitmapUtil.getBitmapFromPictureDrawable(pictureDrawable, homeCrestUrl);
                        }else {
                            homeTeamBitmap = Glide.with(FootballWidgetRemoteViewsService.this).asBitmap().load(homeCrestUrl).into(100, 100).get();
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        Log.e(LOG_TAG, "Error retrieving large icon from " + homeCrestUrl, e);
                    }*/
                }

                if(homeTeamBitmap!=null) {
                    views.setImageViewBitmap(R.id.home_crest, homeTeamBitmap);
                }else{
                    views.setImageViewResource(R.id.home_crest, R.drawable.no_icon);
                }

                String awayCrestUrl = (data.getColumnIndex(ScoresProvider.AWAY_TEAM_PICTURE_ALIAS)>=0)?
                        data.getString(data.getColumnIndex(ScoresProvider.AWAY_TEAM_PICTURE_ALIAS)):null;
                Bitmap awayTeamBitmap = null;
                if(awayCrestUrl!=null){
                    try{
                        if(awayCrestUrl.toLowerCase().endsWith(".svg")) {
                           PictureDrawable pictureDrawable = Glide.with(FootballWidgetRemoteViewsService.this).as(PictureDrawable.class).load(awayCrestUrl).into(100, 100).get();
                           awayTeamBitmap = bitmapUtil.getBitmapFromPictureDrawable(pictureDrawable, awayCrestUrl);
                        }else {
                            awayTeamBitmap = Glide.with(FootballWidgetRemoteViewsService.this).asBitmap().load(awayCrestUrl).into(100, 100).get();
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        Log.e(LOG_TAG, "Error retrieving large icon from " + awayCrestUrl, e);
                    }
                }

                if(awayTeamBitmap!=null) {
                    views.setImageViewBitmap(R.id.away_crest, awayTeamBitmap);
                }else{
                    views.setImageViewResource(R.id.away_crest, R.drawable.no_icon);
                }

               /* int weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID);
                //int weatherArtResourceId = Utility.getIconResourceForWeatherCondition(weatherId);
                Bitmap weatherArtImage = null;
                if ( !Utility.usingLocalGraphics(DetailWidgetRemoteViewsService.this) ) {
                    String weatherArtResourceUrl = Utility.getArtUrlForWeatherCondition(
                            DetailWidgetRemoteViewsService.this, weatherId);
                    try {
                        weatherArtImage = Glide.with(DetailWidgetRemoteViewsService.this)
                                .load(weatherArtResourceUrl)
                                .asBitmap()
                                .error(weatherArtResourceId)
                                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                    } catch (InterruptedException | ExecutionException e) {
                        Log.e(LOG_TAG, "Error retrieving large icon from " + weatherArtResourceUrl, e);
                    }
                }
                String description = data.getString(INDEX_WEATHER_DESC);
                long dateInMillis = data.getLong(INDEX_WEATHER_DATE);
                String formattedDate = Utility.getFriendlyDayString(
                        DetailWidgetRemoteViewsService.this, dateInMillis, false);
                double maxTemp = data.getDouble(INDEX_WEATHER_MAX_TEMP);
                double minTemp = data.getDouble(INDEX_WEATHER_MIN_TEMP);
                String formattedMaxTemperature =
                        Utility.formatTemperature(DetailWidgetRemoteViewsService.this, maxTemp);
                String formattedMinTemperature =
                        Utility.formatTemperature(DetailWidgetRemoteViewsService.this, minTemp);
                if (weatherArtImage != null) {
                    views.setImageViewBitmap(R.id.widget_icon, weatherArtImage);
                } else {
                    views.setImageViewResource(R.id.widget_icon, weatherArtResourceId);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, description);
                }
                views.setTextViewText(R.id.widget_date, formattedDate);
                views.setTextViewText(R.id.widget_description, description);
                views.setTextViewText(R.id.widget_high_temperature, formattedMaxTemperature);
                views.setTextViewText(R.id.widget_low_temperature, formattedMinTemperature);

                final Intent fillInIntent = new Intent();
                String locationSetting =
                        Utility.getPreferredLocation(DetailWidgetRemoteViewsService.this);
                Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                        locationSetting,
                        dateInMillis);
                fillInIntent.setData(weatherUri);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);*/

                return views;
            }

            private void setRemoteContentDescription(RemoteViews views,int view, String description) {
                views.setContentDescription(view, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.scores_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(data.getColumnIndex(DatabaseContract.Score._ID));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}

package barqsoft.footballscores.api;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParser;

import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.db.DatabaseContract;
import barqsoft.footballscores.svg.SvgBitmapTranscoder;
import barqsoft.footballscores.svg.SvgDecoder;
import barqsoft.footballscores.utils.Utilities;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.GsonConverterFactory;

/**
 * Created by Gaby on 11/14/2015.
 */
public class FootballAPIService {
    public static final String LOG_TAG = FootballAPIService.class.getSimpleName();
    private Retrofit mRetrofit;
    private Context mContext;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_NO_NETWORK, STATUS_NOT_FOUND, STATUS_ERROR, STATUS_OK})
    public @interface FootballAPIStatus {}
    public static final int STATUS_NO_NETWORK = 0;
    public static final int STATUS_NOT_FOUND = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_OK = 3;
    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";

    public FootballAPIService(Context context){
        mRetrofit = new Retrofit.Builder().baseUrl(FootballAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mContext = context;
    }

    public FixturesResponse fetchScores(String timeFrame) throws Exception{
        try {
            FootballAPI mApi = mRetrofit.create(FootballAPI.class);
            Call<FixturesResponse> callApi = mApi.fetchScoresData(timeFrame);
            Response<FixturesResponse> response = callApi.execute();
            if (response != null && response.isSuccess()) {
                return response.body();
            }else{
                if(response!=null && response.errorBody()!=null){
                    Log.e(LOG_TAG, "API call failed. Response code: " + String.valueOf(response.code()));
                    throw new Exception("API call failed. Response code: " + String.valueOf(response.code()));
                }
            }

        }catch (Exception e){
            Log.e(LOG_TAG, e.getMessage());
            throw e;
        }
        return null;
    }

    public List<SeasonResponse> fetchSeasons() throws Exception{
        try {
            FootballAPI mApi = mRetrofit.create(FootballAPI.class);
            Call<List<SeasonResponse>> callApi = mApi.fetchSeasonsData();
            Response<List<SeasonResponse>> response = callApi.execute();
            if (response != null && response.isSuccess()) {
                return response.body();
            }else{
                if(response!=null && response.errorBody()!=null){
                    Log.e(LOG_TAG, "API call failed. Response code: " + String.valueOf(response.code()));
                    throw new Exception("API call failed. Response code: " + String.valueOf(response.code()));
                }
            }

        }catch (Exception e){
            Log.e(LOG_TAG, e.getMessage());
            throw e;
        }
        return null;
    }

    public TeamsResponse fetchTeams(String seasonId) throws Exception{
        try {
            FootballAPI mApi = mRetrofit.create(FootballAPI.class);
            Call<TeamsResponse> callApi = mApi.fetchTeamsData(seasonId);
            Response<TeamsResponse> response = callApi.execute();
            if (response != null && response.isSuccess()) {
                return response.body();
            }else{
                if(response!=null && response.errorBody()!=null){
                    Log.e(LOG_TAG, "API call failed. Response code: " + String.valueOf(response.code()));
                    throw new Exception("API call failed. Response code: " + String.valueOf(response.code()));
                }
            }

        }catch (Exception e){
            Log.e(LOG_TAG, e.getMessage());
            throw e;
        }
        return null;
    }

    public void getScoreData(String timeFrame)
    {
        if(!isNetworkAvailable(mContext)) {
            sendStatusBroadcast(mContext, STATUS_NO_NETWORK);
        }else {
            try {
                FixturesResponse response = fetchScores(timeFrame);
                if (response != null) {
                    processJSONScoreData(response);
                } else {
                    sendStatusBroadcast(mContext, STATUS_ERROR);
                    Log.e(LOG_TAG, "Null response");
                }
            } catch (Exception e) {
                sendStatusBroadcast(mContext, STATUS_ERROR);
                Log.e(LOG_TAG, "Error fetching score data: " + e.getMessage());
            }
        }
    }

    private void processJSONScoreData (FixturesResponse response) {

        try {

            List<MatchResponse> matches = response.getFixtures();
            if(matches.size()>0) {
                //ContentValues to be inserted
                Vector<ContentValues> values = new Vector<ContentValues>(matches.size());
                for (MatchResponse match: matches) {

                    ContentValues match_values = new ContentValues();
                    int matchId = 0, seasonId = 0, awayTeamId = 0, homeTeamId = 0;
                    String mTime = "", mDate ="";
                    int awayGoals = -1, homeGoals = -1;

                    try {
                        String matchString = Uri.parse(match.getLinks().getSelf().getHref()).getLastPathSegment();
                        matchId = Integer.parseInt(matchString);
                    }catch (Exception e){
                        Log.v(LOG_TAG,"Error trying to get match id : " + e.getMessage());
                    }

                    try {
                        String seasonString = Uri.parse(match.getLinks().getSoccerSeason().getHref()).getLastPathSegment();
                        seasonId = Integer.parseInt(seasonString);
                    }catch (Exception e){
                        Log.v(LOG_TAG,"Error trying to get season id : " + e.getMessage());
                    }

                    try {
                        String homeString = Uri.parse(match.getLinks().getHomeTeam().getHref()).getLastPathSegment();
                        homeTeamId = Integer.parseInt(homeString);
                    }catch (Exception e){
                        Log.v(LOG_TAG,"Error trying to get home team id : " + e.getMessage());
                    }

                    try {
                        String awayString = Uri.parse(match.getLinks().getAwayTeam().getHref()).getLastPathSegment();
                        awayTeamId = Integer.parseInt(awayString);
                    }catch (Exception e){
                        Log.v(LOG_TAG,"Error trying to get away team id : " + e.getMessage());
                    }

                    try {
                        awayGoals = match.getResult().getGoalsAwayTeam()==null?-1:match.getResult().getGoalsAwayTeam();
                        homeGoals = match.getResult().getGoalsHomeTeam()==null?-1:match.getResult().getGoalsHomeTeam();
                    }catch (Exception e){
                        Log.v(LOG_TAG,"Error trying to get results : " + e.getMessage());
                    }

                    if(match.getDate()!=null && !match.getDate().isEmpty()) {
                        try{
                            String jsonDate = match.getDate();
                            mTime = jsonDate.substring(jsonDate.indexOf("T") + 1, jsonDate.indexOf("Z"));
                            mDate = jsonDate.substring(0, jsonDate.indexOf("T"));
                            SimpleDateFormat matchDate = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                            matchDate.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Date parsedDate = matchDate.parse(mDate + mTime);
                            SimpleDateFormat newDate = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
                            newDate.setTimeZone(TimeZone.getDefault());
                            mDate = newDate.format(parsedDate);
                            mTime = mDate.substring(mDate.indexOf(":") + 1);
                            mDate = mDate.substring(0, mDate.indexOf(":"));
                        }catch (Exception e){
                            Log.v(LOG_TAG,"Error trying to get match id : " + e.getMessage());
                        }
                    }

                    match_values.put(DatabaseContract.Score.MATCH_ID, matchId);
                    match_values.put(DatabaseContract.Score.DATE_COL, mDate);
                    match_values.put(DatabaseContract.Score.TIME_COL, mTime);
                    match_values.put(DatabaseContract.Score.HOME_COL, match.getHomeTeamName());
                    match_values.put(DatabaseContract.Score.AWAY_COL, match.getAwayTeamName());
                    match_values.put(DatabaseContract.Score.HOME_GOALS_COL, homeGoals);
                    match_values.put(DatabaseContract.Score.AWAY_GOALS_COL, awayGoals);
                    match_values.put(DatabaseContract.Score.LEAGUE_COL, seasonId);
                    match_values.put(DatabaseContract.Score.MATCH_DAY, match.getMatchday());
                    match_values.put(DatabaseContract.Score.HOME_COD_COL, homeTeamId);
                    match_values.put(DatabaseContract.Score.AWAY_COD_COL, awayTeamId);

                    values.add(match_values);

                }
                int inserted_data;
                ContentValues[] insert_data = new ContentValues[values.size()];
                values.toArray(insert_data);
                inserted_data = mContext.getContentResolver().bulkInsert(
                        DatabaseContract.Score.SCORES_CONTENT_URI, insert_data);

                Log.v(LOG_TAG, "Successfully Inserted Scores: " + String.valueOf(inserted_data));
            }else{
                Log.i(LOG_TAG, "No matches where returned on the response.");
            }
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG,e.getMessage());
        }

    }

    public void getSeasons ()
    {

        try {
            List<SeasonResponse> response = fetchSeasons();
            if(response!=null){
                processJSONSeasonsData(response);
            }else{
                Log.e(LOG_TAG,"Null response");
            }
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG,"Error fetching season data: " + e.getMessage());
        }
    }

    public void getTeams(String seasonId)
    {

        try {
            TeamsResponse response = fetchTeams(seasonId);
            if(response!=null){
                processJSONTeamsData(response);
            }else{
                Log.e(LOG_TAG, "Null response");
            }
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG,"Error fetching teams data: " + e.getMessage());
        }
    }

    private void processJSONSeasonsData (List<SeasonResponse> response) {

        try {

            if(response.size()>0) {
                //ContentValues to be inserted
                Vector<ContentValues> valuesVector = new Vector<ContentValues>(response.size());
                for (SeasonResponse season: response) {

                    ContentValues values = new ContentValues();
                    int seasonId = 0;
                    try {
                        String seasonString = Uri.parse(season.getLinks().getSelf().getHref()).getLastPathSegment();
                        seasonId = Integer.parseInt(seasonString);
                        getTeams(seasonString);
                    }catch (Exception e){
                        Log.v(LOG_TAG,"Error trying to get season id : " + e.getMessage());
                    }

                    values.put(DatabaseContract.Season.SEASON_ID, seasonId);
                    values.put(DatabaseContract.Season.NAME, season.getCaption());
                    values.put(DatabaseContract.Season.SEASON_COD, season.getLeague());

                    valuesVector.add(values);
                }
                int inserted_data;
                ContentValues[] insert_data = new ContentValues[valuesVector.size()];
                valuesVector.toArray(insert_data);
                inserted_data = mContext.getContentResolver().bulkInsert(
                        DatabaseContract.Season.SEASONS_CONTENT_URI, insert_data);

                Log.v(LOG_TAG, "Successfully Inserted seasons: " + String.valueOf(inserted_data));
            }else{
                Log.i(LOG_TAG, "No seasons where returned on the response.");
            }
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG,e.getMessage());
        }

    }

    private void processJSONTeamsData(TeamsResponse response) {

        try {
            List<TeamResponse> teams = response.getTeams();
            if(teams!=null && teams.size()>0) {
                //ContentValues to be inserted
                Vector<ContentValues> valuesVector = new Vector<ContentValues>(teams.size());
                for (TeamResponse team: teams) {

                    ContentValues values = new ContentValues();
                    int teamId = 0;
                    try {
                        String teamString = Uri.parse(team.getLinks().getSelf().getHref()).getLastPathSegment();
                        teamId = Integer.parseInt(teamString);

                    }catch (Exception e){
                        Log.v(LOG_TAG,"Error trying to get team id : " + e.getMessage());
                    }

                    values.put(DatabaseContract.Team.TEAM_ID, teamId);
                    values.put(DatabaseContract.Team.NAME, team.getName());
                    values.put(DatabaseContract.Team.PICTURE, team.getCrestUrl());

                    valuesVector.add(values);
                }
                int inserted_data;
                ContentValues[] insert_data = new ContentValues[valuesVector.size()];
                valuesVector.toArray(insert_data);
                inserted_data = mContext.getContentResolver().bulkInsert(
                        DatabaseContract.Team.TEAMS_CONTENT_URI, insert_data);

                Log.v(LOG_TAG, "Successfully Inserted teams: " + String.valueOf(inserted_data));
            }else{
                Log.i(LOG_TAG, "No teams where returned on the response.");
            }
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG,e.getMessage());
        }

    }

    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
    }

    public static void sendStatusBroadcast(Context context, @FootballAPIStatus int status){
        Intent messageIntent = new Intent(MESSAGE_EVENT);
        messageIntent.putExtra(MESSAGE_KEY, status);
        LocalBroadcastManager.getInstance(context).sendBroadcast(messageIntent);
    }

}

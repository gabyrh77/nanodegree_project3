package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.api.SeasonResponse;
import barqsoft.footballscores.api.TeamResponse;
import barqsoft.footballscores.api.TeamsResponse;
import barqsoft.footballscores.db.DatabaseContract;
import barqsoft.footballscores.api.FixturesResponse;
import barqsoft.footballscores.api.FootballAPIService;
import barqsoft.footballscores.api.MatchResponse;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class FootballFetchService extends IntentService
{
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_NO_NETWORK, STATUS_NOT_FOUND, STATUS_ERROR, STATUS_OK})
    public @interface FetchServiceStatus {}
    public static final int STATUS_NO_NETWORK = 0;
    public static final int STATUS_NOT_FOUND = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_OK = 3;
    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";
    public static final String LOG_TAG = FootballFetchService.class.getName();
    public FootballFetchService()
    {
        super(FootballFetchService.class.getName());
    }
    private FootballAPIService service;

    private void sendStatusBroadcast(@FetchServiceStatus int status){
        Intent messageIntent = new Intent(MESSAGE_EVENT);
        messageIntent.putExtra(MESSAGE_KEY, status);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        service = new FootballAPIService(getApplicationContext());
       // getSeasons();
        //getData("n2");
        //getData("p2");

        //return;
    }

    private void getData (String timeFrame)
    {

        try {
           FixturesResponse response = service.fetchScores(timeFrame);
            if(response!=null){
                processJSONScoreData(response, getApplicationContext());
            }else{
                Log.e(LOG_TAG, "Null response");
            }
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG,"Error fetching score data: " + e.getMessage());
        }
    }

    private void processJSONScoreData (FixturesResponse response, Context mContext) {

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
                        seasonId = Integer.parseInt(awayString);
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

                    //log spam
                    //Log.v(LOG_TAG,match_id);
                    //Log.v(LOG_TAG,mDate);
                    //Log.v(LOG_TAG,mTime);
                    //Log.v(LOG_TAG,Home);
                    //Log.v(LOG_TAG,Away);
                    //Log.v(LOG_TAG,Home_goals);
                    //Log.v(LOG_TAG,Away_goals);

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
            List<SeasonResponse> response = service.fetchSeasons();
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
            TeamsResponse response = service.fetchTeams(seasonId);
            if(response!=null){
                processJSONTeamsData(response);
            }else{
                Log.e(LOG_TAG,"Null response");
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
                inserted_data = getApplicationContext().getContentResolver().bulkInsert(
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
                inserted_data = getApplicationContext().getContentResolver().bulkInsert(
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
}


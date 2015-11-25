package barqsoft.footballscores.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.HashMap;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresProvider extends ContentProvider
{
    private static ScoresDBHelper mOpenHelper;
    private static final int MATCHES = 100;
    private static final int MATCHES_WITH_LEAGUE = 101;
    private static final int MATCHES_WITH_ID = 102;
    private static final int MATCHES_WITH_DATE = 103;
    private static final int SEASONS = 104;
    private static final int SEASONS_WITH_ID = 105;
    private static final int TEAMS = 106;
    private static final int TEAMS_WITH_ID = 107;

    private static final String ALL_TABLES = DatabaseContract.SCORES_TABLE +
            " LEFT JOIN " + DatabaseContract.SEASONS_TABLE + " S ON S."+ DatabaseContract.Season.SEASON_ID + " = " + DatabaseContract.Score.LEAGUE_COL  +
            " LEFT JOIN " + DatabaseContract.TEAMS_TABLE + " HT ON HT." + DatabaseContract.Team.TEAM_ID + " = " + DatabaseContract.Score.HOME_COD_COL +
            " LEFT JOIN " + DatabaseContract.TEAMS_TABLE + " AT ON AT." + DatabaseContract.Team.TEAM_ID + " = " + DatabaseContract.Score.AWAY_COD_COL;
    private static final HashMap<String, String> mProjectionMap = buildProjectionMap();
    private UriMatcher mUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder mScoreQuery = buildScoreQueryBuilder();
    private static final String SCORES_BY_LEAGUE = DatabaseContract.Score.LEAGUE_COL + " = ?";
    private static final String SCORES_BY_DATE =
            DatabaseContract.Score.DATE_COL + " LIKE ?";
    private static final String SCORES_BY_ID =
            DatabaseContract.Score.MATCH_ID + " = ?";

    static HashMap<String, String> buildProjectionMap(){
        HashMap<String, String> mProjectionMap = new HashMap<>();
        mProjectionMap.put(DatabaseContract.SCORES_TABLE + "." + DatabaseContract.Score._ID,
                DatabaseContract.SCORES_TABLE + "." + DatabaseContract.Score._ID + " AS " + DatabaseContract.Score._ID);
        mProjectionMap.put(DatabaseContract.Score.MATCH_ID, DatabaseContract.Score.MATCH_ID);
        mProjectionMap.put(DatabaseContract.Score.TIME_COL, DatabaseContract.Score.TIME_COL);
        mProjectionMap.put(DatabaseContract.Score.DATE_COL, DatabaseContract.Score.DATE_COL);
        mProjectionMap.put(DatabaseContract.Score.AWAY_COL, DatabaseContract.Score.AWAY_COL);
        mProjectionMap.put(DatabaseContract.Score.HOME_COL, DatabaseContract.Score.HOME_COL);
        mProjectionMap.put(DatabaseContract.Score.HOME_GOALS_COL, DatabaseContract.Score.HOME_GOALS_COL);
        mProjectionMap.put(DatabaseContract.Score.AWAY_GOALS_COL, DatabaseContract.Score.AWAY_GOALS_COL);
        mProjectionMap.put(DatabaseContract.Score.LEAGUE_COL, DatabaseContract.Score.LEAGUE_COL);
        mProjectionMap.put(DatabaseContract.Score.MATCH_DAY, DatabaseContract.Score.MATCH_DAY);
        mProjectionMap.put("S." + DatabaseContract.Season.SEASON_COD,
                "S." + DatabaseContract.Season.SEASON_COD + " AS " + DatabaseContract.Season.SEASON_COD);
        mProjectionMap.put("HT." + DatabaseContract.Team.PICTURE, "HT." + DatabaseContract.Team.PICTURE + " AS " + HOME_TEAM_PICTURE_ALIAS);
        mProjectionMap.put("AT." + DatabaseContract.Team.PICTURE, "AT." + DatabaseContract.Team.PICTURE + " AS " + AWAY_TEAM_PICTURE_ALIAS);
        return mProjectionMap;
    }

    static SQLiteQueryBuilder buildScoreQueryBuilder(){
        SQLiteQueryBuilder mScoreQuery = new SQLiteQueryBuilder();
        mScoreQuery.setTables(ALL_TABLES);
        mScoreQuery.setProjectionMap(mProjectionMap);
        return mScoreQuery;
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        //SCORES PATHS
        matcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.SCORES_PATH, MATCHES);
        matcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.SCORES_PATH + "/" + DatabaseContract.Score.LEAGUE_PATH  , MATCHES_WITH_LEAGUE);
        matcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.SCORES_PATH + "/#" , MATCHES_WITH_ID);
        matcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.SCORES_PATH + "/" + DatabaseContract.Score.DATE_PATH , MATCHES_WITH_DATE);
        matcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.TEAMS_PATH, TEAMS);
        matcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.TEAMS_PATH + "/#" , TEAMS_WITH_ID);
        matcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.SEASONS_PATH, SEASONS);
        matcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.SEASONS_PATH + "/#" , SEASONS_WITH_ID);
        return matcher;
    }

    public static final String AWAY_TEAM_PICTURE_ALIAS = "away_team_picture";
    public static final String HOME_TEAM_PICTURE_ALIAS = "home_team_picture";

    @Override
    public boolean onCreate()
    {
        mOpenHelper = new ScoresDBHelper(getContext());
        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        return 0;
    }

    @Override
    public String getType(Uri uri)
    {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case MATCHES:
                return DatabaseContract.Score.CONTENT_TYPE;
            case MATCHES_WITH_LEAGUE:
                return DatabaseContract.Score.CONTENT_TYPE_LEAGUE;
            case MATCHES_WITH_ID:
                return DatabaseContract.Score.CONTENT_ITEM_TYPE;
            case MATCHES_WITH_DATE:
                return DatabaseContract.Score.CONTENT_TYPE_DATE;
            case TEAMS:
                return DatabaseContract.Team.CONTENT_TYPE;
            case TEAMS_WITH_ID:
                return DatabaseContract.Team.CONTENT_ITEM_TYPE;
            case SEASONS:
                return DatabaseContract.Season.CONTENT_TYPE;
            case SEASONS_WITH_ID:
                return DatabaseContract.Season.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri );
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        Cursor retCursor;
        //Log.v(FetchScoreTask.LOG_TAG,uri.getPathSegments().toString());
        int match = mUriMatcher.match(uri);
        //Log.v(FetchScoreTask.LOG_TAG,SCORES_BY_LEAGUE);
        //Log.v(FetchScoreTask.LOG_TAG,selectionArgs[0]);
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(match));
        switch (match)
        {
            case MATCHES:
                retCursor = mScoreQuery.query(mOpenHelper.getReadableDatabase(), projection, null,
                        null, null, null, sortOrder);
                break;
            case MATCHES_WITH_DATE:
                retCursor = mScoreQuery.query(mOpenHelper.getReadableDatabase(), projection,
                        SCORES_BY_DATE, selectionArgs, null, null, sortOrder);
                break;
            case MATCHES_WITH_ID: retCursor = mOpenHelper.getReadableDatabase().query(
                    ALL_TABLES,
                    projection, SCORES_BY_ID,selectionArgs, null, null, sortOrder);
                break;
            case MATCHES_WITH_LEAGUE: retCursor = mOpenHelper.getReadableDatabase().query(
                    ALL_TABLES,
                    projection, SCORES_BY_LEAGUE,selectionArgs, null, null, sortOrder);
                break;
            case TEAMS: retCursor = mOpenHelper.getReadableDatabase().query(
                    DatabaseContract.TEAMS_TABLE,
                    projection, null, null, null, null, sortOrder);
                break;
            case SEASONS: retCursor = mOpenHelper.getReadableDatabase().query(
                    DatabaseContract.SEASONS_TABLE,
                    projection, null, null, null, null, sortOrder);
                break;
            default: throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        //db.delete(DatabaseContract.SCORES_TABLE,null,null);
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(muriMatcher.match(uri)));
        int match = mUriMatcher.match(uri);
        int returncount = 0;
        switch (match)
        {
            case MATCHES:
                db.beginTransaction();

                try
                {
                    for(ContentValues value : values)
                    {
                        long _id = db.insertWithOnConflict(DatabaseContract.SCORES_TABLE, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1)
                        {
                            returncount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returncount;
            case SEASONS:
                db.beginTransaction();
                try
                {
                    for(ContentValues value : values)
                    {
                        long _id = db.insertWithOnConflict(DatabaseContract.SEASONS_TABLE, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1)
                        {
                            returncount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returncount;
            case TEAMS:
                db.beginTransaction();
                try
                {
                    for(ContentValues value : values)
                    {
                        long _id = db.insertWithOnConflict(DatabaseContract.TEAMS_TABLE, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1)
                        {
                            returncount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returncount;
            default:
                return super.bulkInsert(uri,values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}

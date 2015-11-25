package barqsoft.footballscores.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import barqsoft.footballscores.db.DatabaseContract.*;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresDBHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "Scores.db";
    private static final int DATABASE_VERSION = 2;
    public ScoresDBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String CreateScoresTable = "CREATE TABLE " + DatabaseContract.SCORES_TABLE + " ("
                + Score._ID + " INTEGER PRIMARY KEY,"
                + Score.DATE_COL + " TEXT NOT NULL,"
                + Score.TIME_COL + " INTEGER NOT NULL,"
                + Score.HOME_COL + " TEXT NOT NULL,"
                + Score.AWAY_COL + " TEXT NOT NULL,"
                + Score.LEAGUE_COL + " INTEGER NOT NULL,"
                + Score.HOME_GOALS_COL + " INTEGER NOT NULL,"
                + Score.AWAY_GOALS_COL + " INTEGER NOT NULL,"
                + Score.MATCH_ID + " INTEGER NOT NULL,"
                + Score.MATCH_DAY + " INTEGER NOT NULL,"
                + Score.HOME_COD_COL + " INTEGER,"
                + Score.AWAY_COD_COL + " INTEGER,"
                + " UNIQUE ("+ Score.MATCH_ID+") ON CONFLICT REPLACE"
                + " );";
        final String CreateTeamsTable = "CREATE TABLE " + DatabaseContract.TEAMS_TABLE + " ("
                + Team._ID + " INTEGER PRIMARY KEY,"
                + Team.TEAM_ID + " INTEGER NOT NULL,"
                + Team.NAME + " TEXT NOT NULL,"
                + Team.PICTURE + " TEXT,"
                + " UNIQUE ("+ Team.TEAM_ID+") ON CONFLICT REPLACE"
                + " );";
        final String CreateSeasonTable = "CREATE TABLE " + DatabaseContract.SEASONS_TABLE + " ("
                + Season._ID + " INTEGER PRIMARY KEY,"
                + Season.SEASON_ID + " INTEGER NOT NULL,"
                + Season.NAME + " TEXT NOT NULL,"
                + Season.SEASON_COD + " TEXT UNIQUE NOT NULL,"
                + " UNIQUE ("+ Season.SEASON_ID+") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(CreateScoresTable);
        db.execSQL(CreateTeamsTable);
        db.execSQL(CreateSeasonTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Remove old values when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.SCORES_TABLE);
    }
}

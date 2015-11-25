package barqsoft.footballscores.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class DatabaseContract
{
    //URI data
    public static final String CONTENT_AUTHORITY = "barqsoft.footballscores";
    public static final String SCORES_PATH = "scores";
    public static final String TEAMS_PATH = "teams";
    public static final String SEASONS_PATH = "seasons";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String SCORES_TABLE = "scores_table";
    public static final String TEAMS_TABLE = "teams_table";
    public static final String SEASONS_TABLE = "seasons_table";
    public static final class Score implements BaseColumns
    {
        //Table data
        public static final String LEAGUE_COL = "league";
        public static final String DATE_COL = "date";
        public static final String TIME_COL = "time";
        public static final String HOME_COL = "home";
        public static final String AWAY_COL = "away";
        public static final String HOME_COD_COL = "home_cod";
        public static final String AWAY_COD_COL = "away_cod";
        public static final String HOME_GOALS_COL = "home_goals";
        public static final String AWAY_GOALS_COL = "away_goals";
        public static final String MATCH_ID = "match_id";
        public static final String MATCH_DAY = "match_day";

        public static Uri SCORES_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(SCORES_PATH).build();

        public static final String LEAGUE_PATH = "league";
        public static final String DATE_PATH = "date";

        //Types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + SCORES_PATH;
        public static final String CONTENT_TYPE_DATE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + SCORES_PATH+ "/" + DATE_PATH;
        public static final String CONTENT_TYPE_LEAGUE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + SCORES_PATH+ "/" + LEAGUE_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + SCORES_PATH;

        public static Uri buildScoreWithLeague()
        {
            return SCORES_CONTENT_URI.buildUpon().appendPath(LEAGUE_PATH).build();
        }
        public static Uri buildScoreWithId()
        {
            return SCORES_CONTENT_URI;
        }
        public static Uri buildScoreWithDate()
        {
            return SCORES_CONTENT_URI.buildUpon().appendPath(DATE_PATH).build();
        }
    }


    public static final class Team implements BaseColumns
    {
        //Table data
        public static final String TEAM_ID = "team_id";
        public static final String NAME = "team_name";
        public static final String PICTURE = "team_picture";

        public static Uri TEAMS_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TEAMS_PATH).build();

        //Types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TEAMS_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TEAMS_PATH;


    }

    public static final class Season implements BaseColumns
    {
        //Table data
        public static final String SEASON_ID = "season_id";
        public static final String NAME = "season_name";
        public static final String SEASON_COD = "league_cod";

        public static Uri SEASONS_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(SEASONS_PATH).build();

        //Types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + SEASONS_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + SEASONS_PATH;


    }
}

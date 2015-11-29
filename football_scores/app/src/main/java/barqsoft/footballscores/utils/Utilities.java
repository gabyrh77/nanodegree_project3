package barqsoft.footballscores.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.PictureDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import barqsoft.footballscores.R;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilities
{
    public static final String SERIE_A = "SA";
    public static final String PREMIER_LEGAUE = "PPL";
    public static final String CHAMPIONS_LEAGUE = "CL";
    public static final String PRIMERA_DIVISION = "PD";
    public static final String BUNDESLIGA = "BL1";
    public static final String BUNDESLIGA2 = "BL2";
    public static final String LIGUE1 = "FL1";
    public static final String LIGUE2 = "FL2";

    public static String getLeague(String league_cod, Context context)
    {
        if(league_cod!=null) {
            switch (league_cod) {
                case SERIE_A:
                    return context.getString(R.string.seriaa);
                case PREMIER_LEGAUE:
                    return context.getString(R.string.premierleague);
                case CHAMPIONS_LEAGUE:
                    return context.getString(R.string.champions_league);
                case PRIMERA_DIVISION:
                    return context.getString(R.string.primeradivison);
                case BUNDESLIGA:
                    return context.getString(R.string.bundesliga);
                case BUNDESLIGA2:
                    return context.getString(R.string.bundesliga2);
                case LIGUE1:
                    return context.getString(R.string.ligue1);
                case LIGUE2:
                    return context.getString(R.string.ligue2);
                default:
                    return context.getString(R.string.unknown_league);
            }
        }
        return context.getString(R.string.unknown_league);
    }

    public static String getMatchDay(Context context, int match_day, String league_cod)
    {
        if(CHAMPIONS_LEAGUE.equals(league_cod))
        {
            if (match_day <= 6)
            {
                return context.getString(R.string.group_stage_text, match_day);
            }
            else if(match_day == 7 || match_day == 8)
            {
                return context.getString(R.string.first_knockout_round);
            }
            else if(match_day == 9 || match_day == 10)
            {
                return context.getString(R.string.quarter_final);
            }
            else if(match_day == 11 || match_day == 12)
            {
                return context.getString(R.string.semi_final);
            }
            else
            {
                return context.getString(R.string.final_text);
            }
        }
        else
        {
            return context.getString(R.string.matchday_text, match_day);
        }
    }

    public static String getScores(String divider, int home_goals, int awaygoals)
    {
        if(home_goals < 0 || awaygoals < 0)
        {
            return divider;
        }
        else
        {
            return String.valueOf(home_goals) + divider + String.valueOf(awaygoals);
        }
    }


}

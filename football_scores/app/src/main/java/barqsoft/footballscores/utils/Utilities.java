package barqsoft.footballscores.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.PictureDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

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
                    return "Not known League Please report";
            }
        }
        return "Not known League Please report";
    }

    public static String getMatchDay(int match_day,String league_cod)
    {
        if(CHAMPIONS_LEAGUE.equals(league_cod))
        {
            if (match_day <= 6)
            {
                return "Group Stages, Matchday : 6";
            }
            else if(match_day == 7 || match_day == 8)
            {
                return "First Knockout round";
            }
            else if(match_day == 9 || match_day == 10)
            {
                return "QuarterFinal";
            }
            else if(match_day == 11 || match_day == 12)
            {
                return "SemiFinal";
            }
            else
            {
                return "Final";
            }
        }
        else
        {
            return "Matchday : " + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals,int awaygoals)
    {
        if(home_goals < 0 || awaygoals < 0)
        {
            return " - ";
        }
        else
        {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }
}

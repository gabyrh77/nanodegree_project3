package barqsoft.footballscores.api;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Gaby on 11/14/2015.
 */

public interface FootballAPI {
    String API_KEY = "7ccd47ca25174a57a14ddedb763f03fe";
    String BASE_URL = "http://api.football-data.org/alpha"; //Base URL
    String FIXTURES_URL = "/fixtures"; //Base URL
    String SEASONS_URL = "/soccerseasons";
    String TEAMS_URL = "/soccerseasons/{id}/teams";
    String QUERY_TIME_FRAME = "timeFrame"; //Time Frame parameter to determine days

    @Headers("X-Auth-Token: " + API_KEY)
    @GET(FIXTURES_URL)
    Call<FixturesResponse> fetchScoresData(@Query(QUERY_TIME_FRAME) String timeFrame);

    @Headers("X-Auth-Token: " + API_KEY)
    @GET(SEASONS_URL)
    Call<List<SeasonResponse>> fetchSeasonsData();

    @Headers("X-Auth-Token: " + API_KEY)
    @GET(TEAMS_URL)
    Call<TeamsResponse> fetchTeamsData(@Path("id") String seasonId);
}

package barqsoft.footballscores.api;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Gaby on 11/14/2015.
 */
public class MatchResponse {
    public static class LinksResponse {
        private LinkResponse self;
        @SerializedName("soccerseason") private LinkResponse soccerSeason;
        private LinkResponse homeTeam;
        private LinkResponse awayTeam;

        public LinkResponse getSelf() {
            return self;
        }

        public void setSelf(LinkResponse self) {
            this.self = self;
        }

        public LinkResponse getSoccerSeason() {
            return soccerSeason;
        }

        public void setSoccerSeason(LinkResponse soccerSeason) {
            this.soccerSeason = soccerSeason;
        }

        public LinkResponse getHomeTeam() {
            return homeTeam;
        }

        public void setHomeTeam(LinkResponse homeTeam) {
            this.homeTeam = homeTeam;
        }

        public LinkResponse getAwayTeam() {
            return awayTeam;
        }

        public void setAwayTeam(LinkResponse awayTeam) {
            this.awayTeam = awayTeam;
        }
    }

    public static class Result{
        private Integer goalsHomeTeam;
        private Integer goalsAwayTeam;

        public Integer getGoalsHomeTeam() {
            return goalsHomeTeam;
        }

        public void setGoalsHomeTeam(Integer goalsHomeTeam) {
            this.goalsHomeTeam = goalsHomeTeam;
        }

        public Integer getGoalsAwayTeam() {
            return goalsAwayTeam;
        }

        public void setGoalsAwayTeam(Integer goalsAwayTeam) {
            this.goalsAwayTeam = goalsAwayTeam;
        }
    }

    @SerializedName("_links") private LinksResponse links;
    private String date;
    private String status;
    private Integer matchday;
    private String homeTeamName;
    private String awayTeamName;
    private Result result;

    public LinksResponse getLinks() {
        return links;
    }

    public void setLinks(LinksResponse links) {
        this.links = links;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getMatchday() {
        return matchday;
    }

    public void setMatchday(Integer matchday) {
        this.matchday = matchday;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}

package barqsoft.footballscores.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Gaby on 11/14/2015.
 */
public class SeasonResponse {
    public static class LinksResponse {
        private LinkResponse self;
        private LinkResponse teams;

        public LinkResponse getSelf() {
            return self;
        }

        public void setSelf(LinkResponse self) {
            this.self = self;
        }

        public LinkResponse getTeams() {
            return teams;
        }

        public void setTeams(LinkResponse teams) {
            this.teams = teams;
        }
    }

    @SerializedName("_links") private LinksResponse links;
    private String caption;
    private String league;

    public LinksResponse getLinks() {
        return links;
    }

    public void setLinks(LinksResponse links) {
        this.links = links;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }
}

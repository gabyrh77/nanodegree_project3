package barqsoft.footballscores.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Gaby on 11/14/2015.
 */
public class TeamResponse {
    public static class LinksResponse {
        private LinkResponse self;
        private LinkResponse players;

        public LinkResponse getSelf() {
            return self;
        }

        public void setSelf(LinkResponse self) {
            this.self = self;
        }

        public LinkResponse getPlayers() {
            return players;
        }

        public void setPlayers(LinkResponse players) {
            this.players = players;
        }
    }
    @SerializedName("_links") private LinksResponse links;
    private String name;
    private String crestUrl;

    public LinksResponse getLinks() {
        return links;
    }

    public void setLinks(LinksResponse links) {
        this.links = links;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCrestUrl() {
        return crestUrl;
    }

    public void setCrestUrl(String crestUrl) {
        this.crestUrl = crestUrl;
    }
}

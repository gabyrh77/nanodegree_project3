package barqsoft.footballscores.api;

import java.util.List;

/**
 * Created by Gaby on 11/14/2015.
 */
public class TeamsResponse {
    private List<TeamResponse> teams;

    public List<TeamResponse> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamResponse> teams) {
        this.teams = teams;
    }
}

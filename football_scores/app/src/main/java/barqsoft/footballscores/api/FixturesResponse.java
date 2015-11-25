package barqsoft.footballscores.api;

import java.util.List;

/**
 * Created by Gaby on 11/14/2015.
 */
public class FixturesResponse {
    private String timeFrameStart;
    private String timeFrameEnd;
    private List<MatchResponse> fixtures;

    public String getTimeFrameStart() {
        return timeFrameStart;
    }

    public void setTimeFrameStart(String timeFrameStart) {
        this.timeFrameStart = timeFrameStart;
    }

    public String getTimeFrameEnd() {
        return timeFrameEnd;
    }

    public void setTimeFrameEnd(String timeFrameEnd) {
        this.timeFrameEnd = timeFrameEnd;
    }

    public List<MatchResponse> getFixtures() {
        return fixtures;
    }

    public void setFixtures(List<MatchResponse> fixtures) {
        this.fixtures = fixtures;
    }
}

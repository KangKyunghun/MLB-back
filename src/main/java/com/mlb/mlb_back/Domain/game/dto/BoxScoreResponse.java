package com.mlb.mlb_back.Domain.game.dto;

import com.mlb.mlb_back.Domain.game.entity.BoxScore;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoxScoreResponse {

    private Long id;
    private Long gameId;
    private Long playerId;
    private String playerName;
    private String playerPhotoUrl;
    private Long teamId;
    private String teamName;
    private String teamAbbreviation;
    private String playerType;

    private Integer atBats;
    private Integer hits;
    private Integer homeRuns;
    private Integer rbi;
    private Integer runs;
    private Integer walks;
    private Integer strikeOuts;
    private Integer battingOrder;
    private Integer appearanceOrder;
    private String gamePosition;

    private Double inningsPitched;
    private Integer earnedRuns;
    private Integer hitsAllowed;
    private Integer walksAllowed;
    private Integer strikeOutsPitched;
    private Integer pitchCount;
    private Boolean isWin;
    private Boolean isLoss;
    private Boolean isHold;
    private Boolean isSave;

    public static BoxScoreResponse fromEntity(BoxScore boxScore) {
        String photoUrl = boxScore.getPlayer().getId() != null
                ? "https://img.mlbstatic.com/mlb-photos/image/upload/d_people:generic:headshot:67:current.png/w_213,q_auto:best/v1/people/"
                + boxScore.getPlayer().getId() + "/headshot/67/current"
                : null;

        return BoxScoreResponse.builder()
                .id(boxScore.getId())
                .gameId(boxScore.getGame().getId())
                .playerId(boxScore.getPlayer().getId())
                .playerName(boxScore.getPlayer().getFullName())
                .playerPhotoUrl(photoUrl)
                .teamId(boxScore.getTeam().getId())
                .teamName(boxScore.getTeam().getName())
                .teamAbbreviation(boxScore.getTeam().getAbbreviation())
                .playerType(boxScore.getPlayerType())
                .atBats(boxScore.getAtBats())
                .hits(boxScore.getHits())
                .homeRuns(boxScore.getHomeRuns())
                .rbi(boxScore.getRbi())
                .runs(boxScore.getRuns())
                .walks(boxScore.getWalks())
                .strikeOuts(boxScore.getStrikeOuts())
                .battingOrder(boxScore.getBattingOrder())
                .appearanceOrder(boxScore.getAppearanceOrder())
                .gamePosition(boxScore.getGamePosition())
                .inningsPitched(boxScore.getInningsPitched())
                .earnedRuns(boxScore.getEarnedRuns())
                .hitsAllowed(boxScore.getHitsAllowed())
                .walksAllowed(boxScore.getWalksAllowed())
                .strikeOutsPitched(boxScore.getStrikeOutsPitched())
                .pitchCount(boxScore.getPitchCount())
                .isWin(boxScore.getIsWin())
                .isLoss(boxScore.getIsLoss())
                .isHold(boxScore.getIsHold())
                .isSave(boxScore.getIsSave())
                .build();
    }
}
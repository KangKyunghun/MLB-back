package com.football.Football_back.Global.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class FootballApiDto {

    // 리그 응답
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CompetitionResponse {
        private Long id;
        private String name;
        private AreaDto area;
        private String emblem;
        private List<SeasonDto> seasons;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AreaDto {
        private String name;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SeasonDto {
        private Long id;
        private String StartDate;
        private String EndDate;
        @JsonProperty("currentMatchday")
        private Integer currentMatchday;
    }

    // 팀 응답
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamsResponse {
        private List<TeamDto> teams;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamDto {
        private Long id;
        private String name;
        private String shortName;
        private String tla;
        private String crest; // 엠블럼 주소
        private Integer founded;
        private String venue;
        private List<PlayerDto> squad; // 선수 명단
    }

    // 선수 응답
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlayerDto {
        private Long id;
            private String name;
            private String firstName;
            private String lastName;
            @JsonProperty("shirtNumber")
            private Integer shirtNumber;
            private String position;
            private String nationality;
            private String dateOfBirth;
    }

    // 순위 응답
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StandingsResponse {
        private List<StandingTableDto> standings;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StandingTableDto {
        private String type;           // TOTAL / HOME / AWAY
        private List<StandingDto> table;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StandingDto {
        private Integer position;
        private TeamDto team;
        private Integer playedGames;
        private Integer won;
        private Integer draw;
        private Integer lost;
        private Integer points;
        private Integer goalsFor;
        private Integer goalsAgainst;
        private Integer goalDifference;
        private String form;
    }

    // 경기 응답
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MatchesResponse {
        private List<MatchDto> matches;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MatchDto {
        private Long id;
        private Integer matchday;
        private String status;
        private String utcDate;
        private String stage;
        private TeamDto homeTeam;
        private TeamDto awayTeam;
        private ScoreDto score;
        private SeasonDto season;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ScoreDto {
        private FullTimeDto fullTime;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FullTimeDto {
        private Integer home;
        private Integer away;
    }

    // 타임라인용 경기 상세 응답
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MatchDetailResponse {
        private Long id;
        private List<GoalDto> goals;
        private List<BookingDto> bookings;
        private List<SubstitutionDto> substitutions;
        private LineupDto homeTeam;
        private LineupDto awayTeam;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GoalDto {
        private Integer minute;
        private PlayerDto scorer;
        private PlayerDto assist;
        private TeamDto team;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BookingDto {
        private Integer minute;
        private PlayerDto player;
        private TeamDto team;
        private String card;           // YELLOW / RED
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SubstitutionDto {
        private Integer minute;
        private PlayerDto playerOut;
        private PlayerDto playerIn;
        private TeamDto team;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LineupDto {
        private TeamDto team;
        private String formation;
        private List<LineupPlayerDto> startingXI;
        private List<LineupPlayerDto> substitutes;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LineupPlayerDto {
        private PlayerDto player;
        private String position;
        @JsonProperty("shirtNumber")
        private Integer shirtNumber;
    }
}

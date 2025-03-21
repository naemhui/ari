package com.ccc.ari.global.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@Getter
public class StreamingEvent {

    private Integer trackId;
    private String trackTitle;
    private Integer albumId;
    private String albumTitle;
    private Integer genreId;
    private String genreName;
    private String artist;
    private Integer memberId;
    private String nickname;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

    @Builder
    public StreamingEvent(Integer trackId, String trackTitle, Integer albumId, String albumTitle
            , Integer genreId, String genreName, String artist, Integer memberId, String nickname
            , Instant timestamp) {

        this.trackId = trackId;
        this.trackTitle = trackTitle;
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.genreId = genreId;
        this.genreName = genreName;
        this.artist = artist;
        this.memberId = memberId;
        this.nickname = nickname;
        this.timestamp = timestamp;
    }
}

package com.streaming.movieplatform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class WatchProgressRequest {

    @NotNull(message = "Thiếu episodeId")
    private Long episodeId;

    @NotNull(message = "Thiếu vị trí xem")
    @Min(value = 0, message = "Vị trí xem không hợp lệ")
    private Integer positionSeconds;

    private Boolean completed = false;

    public Long getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Long episodeId) {
        this.episodeId = episodeId;
    }

    public Integer getPositionSeconds() {
        return positionSeconds;
    }

    public void setPositionSeconds(Integer positionSeconds) {
        this.positionSeconds = positionSeconds;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}

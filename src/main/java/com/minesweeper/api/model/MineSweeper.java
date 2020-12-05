package com.minesweeper.api.model;

import com.minesweeper.api.dto.MineSweeperRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class MineSweeper {

    private String id;
    private String userId;
    private MineSweeperRequest metadata;
    private List<Cell> cells;
    private Status status;
    @Builder.Default
    private long timePaused = 0L;
    private Date creationTime;
    private Date lastUpdate;

}

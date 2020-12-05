package com.minesweeper.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MineSweeperRequest {

    private int columns;
    private int rows;
    private int bombs;
    private String userId;

}

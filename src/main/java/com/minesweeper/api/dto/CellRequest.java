package com.minesweeper.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CellRequest {

    private int x;
    private int y;
    private String mineSweeperId;

}

package com.minesweeper.api.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@DynamoDBDocument
public class MineSweeperRequest {

    private int columns;
    private int rows;
    private int bombs;
    private String userId;

}

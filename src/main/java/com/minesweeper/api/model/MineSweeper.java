package com.minesweeper.api.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.minesweeper.api.model.exception.MinesweeperApiException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Stream;

@DynamoDBTable(tableName = "Minesweeper.Games")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MineSweeper {

    @DynamoDBAutoGeneratedKey
    @DynamoDBHashKey(attributeName = "id")
    private String id;

    @DynamoDBAttribute(attributeName = "userId")
    private String userId;

    @DynamoDBAttribute(attributeName = "rows")
    private int rows;

    @DynamoDBAttribute(attributeName = "columns")
    private int columns;

    @DynamoDBAttribute(attributeName = "bombs")
    private int bombs;

    @DynamoDBAttribute(attributeName = "cells")
    private List<Cell> cells;

    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    @DynamoDBAttribute(attributeName = "status")
    private Status status;

    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    @DynamoDBAttribute(attributeName = "timeConsumed")
    @Builder.Default
    private long timePaused = 0L;

    @DynamoDBAttribute(attributeName = "creationTime")
    @DynamoDBAutoGeneratedTimestamp(strategy = DynamoDBAutoGenerateStrategy.CREATE)
    private Date creationTime;

    @DynamoDBAttribute(attributeName = "lastUpdate")
    @DynamoDBAutoGeneratedTimestamp(strategy = DynamoDBAutoGenerateStrategy.ALWAYS)
    private Date lastUpdate;

    public void initCells() {
        this.cells = new ArrayList<>();
        for (int i = 0; i < this.getRows(); i++) {
            for (int j = 0; j < this.getColumns(); j++) {
                this.cells.add(new Cell(i, j));
            }
        }

        // Shuffle the cell list
        Collections.shuffle(this.getCells());

        // Get a bucket of cells and converted to bombs
        this.getCells().stream().limit(this.getBombs()).forEach(cellMine -> cellMine.setBomb(true));

        // Sets value for each cell (how many bombs has near)
        final Stream<Cell> aStreamOfCells = this.getCells().stream().filter((cell) -> !cell.isBomb());
        aStreamOfCells.forEach((cell) -> cell.setValue(this.calculateValue(cell)));
    }

    public void pause() {
        if (!this.getStatus().equals(Status.PAUSED)) {
            accumulateTimePaused();
        }
        this.setStatus(this.status.pause());
    }

    public Cell getCell(int x, int y) {
        return cells.stream().filter(c -> c.getX() == x && c.getY() == y).findFirst()
                .orElseThrow(() -> new MinesweeperApiException("Requested cell is out of index"));
    }

    public void recognizeCell(Cell cell) {
        if(!this.getStatus().equals(Status.ACTIVE)){
            throw new MinesweeperApiException("You could not do a move in a non active game");
        }
        cell.setRecognized(true);

        if (cell.isBomb()) {
            // game over
            this.setStatus(Status.GAME_OVER);
            return;
        }

        if (cell.getValue() == 0) {
            this.recognizeAdjacentCells(cell);
        }

        int totalRecognized = cells.stream().map(c -> c.isRecognized() ? 1 : 0).reduce(0, Integer::sum);
        int totalBombs = bombsAmount();

        if (totalRecognized + totalBombs == cells.size()) {
            // no more bombs!!!
            this.setStatus(Status.WIN);
        }
    }

    public Integer bombsAmount() {
        return cells.stream().map(c -> c.isBomb() ? 1 : 0).reduce(0, Integer::sum);
    }

    public Stream<Cell> getAdjacentCellsStream(Cell cell) {
        return cells.stream().filter(c -> c.isAdjacentTo(cell));
    }

    private void accumulateTimePaused() {
        Calendar initDate = Calendar.getInstance();
        initDate.setTime(this.lastUpdate != null ? this.lastUpdate : this.creationTime);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(new Date());
        long diffSeconds = (endDate.getTimeInMillis() - initDate.getTimeInMillis()) / 1000;
        this.timePaused = this.timePaused + diffSeconds;
    }

    private long calculateValue(final Cell aCell) {
        Stream<Cell> filtered = this.getCells().stream().filter(other -> aCell.isAdjacentTo(other) && other.isBomb());
        return filtered.count();
    }

    private void recognizeAdjacentCells(Cell aCell) {
        getAdjacentCellsStream(aCell).filter(adjCell -> !adjCell.isRecognized() && !adjCell.isBomb())
                .forEach(adjEmptyCell -> {
                    adjEmptyCell.setRecognized(true);
                    if (adjEmptyCell.getValue() == 0) {
                        recognizeAdjacentCells(adjEmptyCell);
                    }
                });
    }

}

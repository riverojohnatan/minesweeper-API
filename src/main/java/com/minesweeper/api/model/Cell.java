package com.minesweeper.api.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamoDBDocument
public class Cell {

    private int x;
    private int y;
    private long value;
    private boolean flagged;
    private boolean bomb;
    private boolean recognized;

    public Cell(final int posX, final int posY) {
        this.x = posX;
        this.y = posY;
    }

	public boolean isAdjacentTo(Cell cell) {
		return (
            Math.abs(this.getX()-cell.getX()) <=1 &&
            Math.abs(this.getY()-cell.getY()) <=1 &&
            !(this.getX() == cell.getX() && this.getY() == cell.getY())
        );
    }


	public void flag() {
        this.flagged = !this.flagged;
	}
}

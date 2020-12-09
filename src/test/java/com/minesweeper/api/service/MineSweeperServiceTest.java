package com.minesweeper.api.service;

import com.minesweeper.api.dto.CellRequest;
import com.minesweeper.api.dto.MineSweeperRequest;
import com.minesweeper.api.model.CellAction;
import com.minesweeper.api.model.MineSweeper;
import com.minesweeper.api.model.Status;
import com.minesweeper.api.model.exception.MinesweeperApiException;
import com.minesweeper.api.repository.MineSweeperRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
public class MineSweeperServiceTest {

    @Autowired
    private MineSweeperService mineSweeperService;

    @MockBean
    private MineSweeperRepository mineSweeperRepository;

    private MineSweeperRequest request;

    private MineSweeper mineSweeper;

    private final String USER_ID = "AN_USER";

    private final int COLUMNS = 10;

    private final int ROWS = 10;

    @BeforeEach
    public void setup() {
        request = MineSweeperRequest.builder().bombs(5).columns(COLUMNS).rows(ROWS).userId(USER_ID).build();
        mineSweeper = mineSweeperService.generateMineSweeper(request);
        this.mineSweeper.setCreationTime(new Date());
    }

    @Test
    void validGameRequestSavesNewGame() {
        // Mock responses
        Mockito.when(mineSweeperRepository.save(any(MineSweeper.class))).thenReturn(this.mineSweeper);

        MineSweeper mineSweeper = mineSweeperService.createMineSweeper(request);

        // Assertions
        assertNotNull(mineSweeper);
        assertEquals(request.getRows() * request.getColumns(), mineSweeper.getCells().size());
        assertEquals(request.getBombs(), mineSweeper.bombsAmount());
        assertEquals(request.getUserId(), mineSweeper.getUserId());
    }

    @Test
    void invalidGameRequestThrowsMinesweeperApiException() {
        // Prepare scenario
        MineSweeperRequest gameRequest = MineSweeperRequest.builder().bombs(999).rows(5).columns(5).build();
        try {
            mineSweeperService.createMineSweeper(gameRequest);
            // If it gets here, the test need to fail
            fail();
        } catch (MinesweeperApiException e) {
            // Assertions
            assertEquals(e.getMessage(), "Invalid request. Amount of bombs should be less than total amount of cells");
        }
    }

    @Test
    void invalidGameRequestWithZeroBombsThrowsMinesweeperApiException() {
        // Prepare scenario
        MineSweeperRequest gameRequest = MineSweeperRequest.builder().bombs(0).rows(5).columns(5).build();
        try {
            mineSweeperService.createMineSweeper(gameRequest);
            // If it gets here, the test need to fail
            fail();
        } catch (MinesweeperApiException e) {
            // Assertions
            assertEquals(e.getMessage(), "Invalid request. Columns, Rows and Bombs should be greater that 0");
        }
    }

    @Test
    void pauseActiveGame() {
        // Prepare scenario
        String gameId = UUID.randomUUID().toString();
        this.mineSweeper.setId(gameId);

        // Mock responses
        Mockito.when(mineSweeperRepository.findById(eq(gameId))).thenReturn(Optional.of(mineSweeper));
        Mockito.when(mineSweeperRepository.save(eq(mineSweeper))).thenReturn(mineSweeper);

        MineSweeper gamePaused = mineSweeperService.pauseResumeMineSweeper(gameId);

        // Assertions
        assertEquals(gamePaused.getStatus(), Status.PAUSED);
    }

    @Test
    void resumeAPausedGame() {
        // Prepare scenario
        String mineSweeperId = UUID.randomUUID().toString();
        this.mineSweeper.setId(mineSweeperId);
        this.mineSweeper.setStatus(Status.PAUSED);

        // Mock responses
        Mockito.when(mineSweeperRepository.findById(eq(mineSweeperId))).thenReturn(Optional.of(mineSweeper));
        Mockito.when(mineSweeperRepository.save(eq(mineSweeper))).thenReturn(mineSweeper);
        MineSweeper gamePaused = mineSweeperService.pauseResumeMineSweeper(mineSweeperId);

        // Assertions
        assertEquals(gamePaused.getStatus(), Status.ACTIVE);
    }

    @Test
    void pauseAnOverGameThrowsMinesweeperApiException() {
        // Prepare scenario
        String mineSweeperId = UUID.randomUUID().toString();
        this.mineSweeper.setId(mineSweeperId);
        this.mineSweeper.setStatus(Status.GAME_OVER);

        // Mock responses
        Mockito.when(mineSweeperRepository.findById(eq(mineSweeperId))).thenReturn(Optional.of(mineSweeper));
        Mockito.when(mineSweeperRepository.save(eq(mineSweeper))).thenReturn(mineSweeper);

        try {
            mineSweeperService.pauseResumeMineSweeper(mineSweeperId);
            // If it gets here, the test need to fail
            fail();
        } catch (MinesweeperApiException e) {
            // Assertions
            assertEquals(e.getMessage(), "The game is over and could not be resumed/paused");
        }
    }

    @Test
    void flagCell() {
        // Prepare scenario
        String mineSweeperId = UUID.randomUUID().toString();
        this.mineSweeper.setId(mineSweeperId);
        CellRequest cellRequest = CellRequest.builder().x(0).y(0).mineSweeperId(mineSweeperId).build();

        // Mock responses
        Mockito.when(mineSweeperRepository.findById(eq(mineSweeperId))).thenReturn(Optional.of(mineSweeper));
        Mockito.when(mineSweeperRepository.save(eq(mineSweeper))).thenReturn(mineSweeper);

        // Assertions
        assertTrue(mineSweeperService.cellAction(cellRequest, CellAction.FLAG).getCell(0,0).isFlagged());
    }

    @Test
    void unFlagCell() {
        // Prepare scenario
        String mineSweeperId = UUID.randomUUID().toString();
        this.mineSweeper.setId(mineSweeperId);
        this.mineSweeper.getCell(0, 0).flag();
        CellRequest cellRequest = CellRequest.builder().x(0).y(0).mineSweeperId(mineSweeperId).build();

        // Mock responses
        Mockito.when(mineSweeperRepository.findById(eq(mineSweeperId))).thenReturn(Optional.of(mineSweeper));
        Mockito.when(mineSweeperRepository.save(eq(mineSweeper))).thenReturn(mineSweeper);

        // Assertions
        assertFalse(mineSweeperService.cellAction(cellRequest, CellAction.FLAG).getCell(0,0).isFlagged());
    }

    @Test
    void flagCellOutOfIndexThrowsMinesweeperApiException() {
        // Prepare scenario
        String mineSweeperId = UUID.randomUUID().toString();
        this.mineSweeper.setId(mineSweeperId);
        CellRequest cellRequest = CellRequest.builder().x(COLUMNS).y(ROWS).mineSweeperId(mineSweeperId).build();

        // Mock responses
        Mockito.when(mineSweeperRepository.findById(eq(mineSweeperId))).thenReturn(Optional.of(mineSweeper));
        Mockito.when(mineSweeperRepository.save(eq(mineSweeper))).thenReturn(mineSweeper);

        try {
            mineSweeperService.cellAction(cellRequest, CellAction.FLAG);
            // If it gets here, the test need to fail
            fail();
        } catch (MinesweeperApiException e) {
            // Assertions
            assertEquals(e.getMessage(), "Requested cell is out of index");
        }
    }

}

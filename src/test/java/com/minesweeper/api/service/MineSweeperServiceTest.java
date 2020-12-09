package com.minesweeper.api.service;

import com.minesweeper.api.dto.MineSweeperRequest;
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

    @BeforeEach
    public void setup() {
        request = MineSweeperRequest.builder().bombs(5).columns(10).rows(10).userId(USER_ID).build();
        mineSweeper = mineSweeperService.generateMineSweeper(request);
        this.mineSweeper.setCreationTime(new Date());
    }

    @Test
    void validGameRequestSavesNewGame(){
        Mockito.when(mineSweeperRepository.save(any(MineSweeper.class))).thenReturn(this.mineSweeper);

        MineSweeper mineSweeper = mineSweeperService.createMineSweeper(request);

        assertNotNull(mineSweeper);
        assertEquals(request.getRows() * request.getColumns(), mineSweeper.getCells().size());
        assertEquals(request.getBombs(), mineSweeper.bombsAmount());
        assertEquals(request.getUserId(), mineSweeper.getUserId());
    }

    @Test
    void invalidGameRequestThrowsMinesweeperApiException(){
        MineSweeperRequest gameRequest = MineSweeperRequest.builder().bombs(999).rows(5).columns(5).build();

        try{
            mineSweeperService.createMineSweeper(gameRequest);
            fail();
        } catch (MinesweeperApiException e) {
            assertEquals(e.getMessage(),"Invalid request. Amount of bombs should be less than total amount of cells");
        }
    }

    @Test
    void invalidGameRequestWithZeroBombsThrowsMinesweeperApiException(){
        MineSweeperRequest gameRequest = MineSweeperRequest.builder().bombs(0).rows(5).columns(5).build();

        try{
            mineSweeperService.createMineSweeper(gameRequest);
            fail();
        } catch (MinesweeperApiException e) {
            assertEquals(e.getMessage(),"Invalid request. Columns, Rows and Bombs should be greater that 0");
        }
    }

    @Test
    void pauseActiveGame(){
        String gameId = UUID.randomUUID().toString();
        this.mineSweeper.setId(gameId);
        Mockito.when(mineSweeperRepository.findById(eq(gameId))).thenReturn(Optional.of(mineSweeper));
        Mockito.when(mineSweeperRepository.save(eq(mineSweeper))).thenReturn(mineSweeper);
        MineSweeper gamePaused = mineSweeperService.pauseResumeMineSweeper(gameId);

        assertEquals(gamePaused.getStatus(), Status.PAUSED);
    }

    @Test
    void resumeAPausedGame(){
        String gameId = UUID.randomUUID().toString();
        this.mineSweeper.setId(gameId);
        this.mineSweeper.setStatus(Status.PAUSED);
        Mockito.when(mineSweeperRepository.findById(eq(gameId))).thenReturn(Optional.of(mineSweeper));
        Mockito.when(mineSweeperRepository.save(eq(mineSweeper))).thenReturn(mineSweeper);
        MineSweeper gamePaused = mineSweeperService.pauseResumeMineSweeper(gameId);

        assertEquals(gamePaused.getStatus(), Status.ACTIVE);
    }

    @Test
    void pauseAnOverGameThrowsMinesweeperApiException(){
        String gameId = UUID.randomUUID().toString();
        this.mineSweeper.setId(gameId);
        this.mineSweeper.setStatus(Status.GAME_OVER);
        Mockito.when(mineSweeperRepository.findById(eq(gameId))).thenReturn(Optional.of(mineSweeper));
        Mockito.when(mineSweeperRepository.save(eq(mineSweeper))).thenReturn(mineSweeper);

        try{
            mineSweeperService.pauseResumeMineSweeper(gameId);
            fail();
        } catch(MinesweeperApiException e){
            assertEquals(e.getMessage(),"The game is over and could not be resumed/paused");
        }
    }

}

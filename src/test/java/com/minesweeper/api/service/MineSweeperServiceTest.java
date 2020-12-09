package com.minesweeper.api.service;

import com.minesweeper.api.dto.MineSweeperRequest;
import com.minesweeper.api.model.MineSweeper;
import com.minesweeper.api.model.exception.MinesweeperApiException;
import com.minesweeper.api.repository.MineSweeperRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

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

}

package com.minesweeper.api.service;

import com.minesweeper.api.dto.CellRequest;
import com.minesweeper.api.dto.MineSweeperRequest;
import com.minesweeper.api.model.Cell;
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

    private Cell cellWithBomb;

    private Cell cellWitValue;

    private Cell cellBlank;

    private final String USER_ID = "AN_USER";

    private final int COLUMNS = 10;

    private final int ROWS = 10;

    @BeforeEach
    public void setup() {
        request = MineSweeperRequest.builder().bombs(5).columns(COLUMNS).rows(ROWS).userId(USER_ID).build();
        mineSweeper = mineSweeperService.generateMineSweeper(request);
        this.mineSweeper.setCreationTime(new Date());

        cellBlank = this.mineSweeper.getCells().stream().filter(c -> !c.isBomb() && c.getValue() == 0).findFirst().orElseThrow();
        cellWithBomb = this.mineSweeper.getCells().stream().filter(c -> c.isBomb()).findFirst().orElseThrow();
        cellWitValue = this.mineSweeper.getCells().stream().filter(c -> !c.isBomb() && c.getValue() > 0).findFirst().orElseThrow();
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
        MineSweeperRequest mineSweeperRequest = MineSweeperRequest.builder().bombs(999).rows(5).columns(5).build();
        try {
            mineSweeperService.createMineSweeper(mineSweeperRequest);
            // If it gets here, the test need to fail
            fail();
        } catch (MinesweeperApiException e) {
            // Assertions
            assertEquals("Invalid request. Amount of bombs should be less than total amount of cells", e.getMessage());
        }
    }

    @Test
    void invalidGameRequestWithZeroBombsThrowsMinesweeperApiException() {
        // Prepare scenario
        MineSweeperRequest mineSweeperRequest = MineSweeperRequest.builder().bombs(0).rows(5).columns(5).build();
        try {
            mineSweeperService.createMineSweeper(mineSweeperRequest);
            // If it gets here, the test need to fail
            fail();
        } catch (MinesweeperApiException e) {
            // Assertions
            assertEquals("Invalid request. Columns, Rows and Bombs should be greater that 0", e.getMessage());
        }
    }

    @Test
    void pauseActiveGame() {
        // Prepare scenario
        String mineSweeperId = UUID.randomUUID().toString();
        this.mineSweeper.setId(mineSweeperId);

        // Mock responses
        Mockito.when(mineSweeperRepository.findById(eq(mineSweeperId))).thenReturn(Optional.of(mineSweeper));
        Mockito.when(mineSweeperRepository.save(eq(mineSweeper))).thenReturn(mineSweeper);

        MineSweeper gamePaused = mineSweeperService.pauseResumeMineSweeper(mineSweeperId);

        // Assertions
        assertEquals(Status.PAUSED, gamePaused.getStatus());
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
        assertEquals(Status.ACTIVE, gamePaused.getStatus());
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
            assertEquals("The game is over and could not be resumed/paused", e.getMessage());
        }
    }

    @Test
    void pauseAWinGameThrowsMinesweeperApiException() {
        // Prepare scenario
        String mineSweeperId = UUID.randomUUID().toString();
        this.mineSweeper.setId(mineSweeperId);
        this.mineSweeper.setStatus(Status.WIN);

        // Mock responses
        Mockito.when(mineSweeperRepository.findById(eq(mineSweeperId))).thenReturn(Optional.of(mineSweeper));
        Mockito.when(mineSweeperRepository.save(eq(mineSweeper))).thenReturn(mineSweeper);

        try {
            mineSweeperService.pauseResumeMineSweeper(mineSweeperId);
            // If it gets here, the test need to fail
            fail();
        } catch (MinesweeperApiException e) {
            // Assertions
            assertEquals("The game is over and could not be resumed/paused", e.getMessage());
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
            assertEquals("Requested cell is out of index", e.getMessage());
        }
    }

    @Test
    void clickCellThatIsBlank(){
        // Prepare scenario
        String mineSweeperId = UUID.randomUUID().toString();
        this.mineSweeper.setId(mineSweeperId);
        CellRequest cellRequest = CellRequest.builder().mineSweeperId(mineSweeperId).x(cellBlank.getX()).y(cellBlank.getY()).build();

        // Mock responses
        Mockito.when(mineSweeperRepository.findById(eq(mineSweeperId))).thenReturn(Optional.of(mineSweeper));
        Mockito.when(mineSweeperRepository.save(eq(mineSweeper))).thenReturn(mineSweeper);

        MineSweeper mineSweeper = mineSweeperService.cellAction(cellRequest, CellAction.CLICK);

        // Assertions
        mineSweeper.getAdjacentCellsStream(cellBlank).filter(c -> c.getValue()==0 && !c.isBomb()).forEach(c-> assertTrue(c.isRecognized()));
    }

    @Test
    void clickCellThatIsBombChangeGameStatusToGameOVer(){
        // Prepare scenario
        String mineSweeperId = UUID.randomUUID().toString();
        this.mineSweeper.setId(mineSweeperId);
        CellRequest cellRequest = CellRequest.builder().mineSweeperId(mineSweeperId).x(cellWithBomb.getX()).y(cellWithBomb.getY()).build();

        // Mock responses
        Mockito.when(mineSweeperRepository.findById(eq(mineSweeperId))).thenReturn(Optional.of(mineSweeper));
        Mockito.when(mineSweeperRepository.save(eq(mineSweeper))).thenReturn(mineSweeper);

        MineSweeper mineSweeper = mineSweeperService.cellAction(cellRequest, CellAction.CLICK);

        // Assertions
        assertEquals(Status.GAME_OVER, mineSweeper.getStatus());
    }

    @Test
    void clickCellThatIsOutOfIndexThrowsMinesweeperApiException(){
        // Prepare scenario
        String mineSweeperId = UUID.randomUUID().toString();
        this.mineSweeper.setId(mineSweeperId);
        CellRequest cellRequest = CellRequest.builder().mineSweeperId(mineSweeperId).x(COLUMNS).y(ROWS).build();

        // Mock responses
        Mockito.when(mineSweeperRepository.findById(eq(mineSweeperId))).thenReturn(Optional.of(mineSweeper));
        Mockito.when(mineSweeperRepository.save(eq(mineSweeper))).thenReturn(mineSweeper);

        try{
            mineSweeperService.cellAction(cellRequest, CellAction.CLICK);
            fail();
        } catch(MinesweeperApiException e) {
            // Assertions
            assertEquals("Requested cell is out of index", e.getMessage());
        }
    }

    @Test
    void clickAllNotBombCellsChangeStatusWin(){
        // Prepare scenario
        String mineSweeperId = UUID.randomUUID().toString();
        request = MineSweeperRequest.builder().bombs(1).columns(2).rows(2).userId(USER_ID).build();
        mineSweeper = mineSweeperService.generateMineSweeper(request);
        mineSweeper.setId(mineSweeperId);

        // Mock responses
        Mockito.when(mineSweeperRepository.findById(eq(mineSweeperId))).thenReturn(Optional.of(mineSweeper));
        Mockito.when(mineSweeperRepository.save(eq(mineSweeper))).thenReturn(mineSweeper);

        MineSweeper finalScore = mineSweeper.getCells().stream().filter(c -> !c.isBomb()).map(notBomb -> {
            CellRequest cellRequest = CellRequest.builder().mineSweeperId(mineSweeperId).x(notBomb.getX()).y(notBomb.getY()).build();
            MineSweeper mineSweeper = mineSweeperService.cellAction(cellRequest, CellAction.CLICK);
            return mineSweeper;
        }).reduce((a, b) -> a).orElseThrow();

        // Assertions
        assertEquals(Status.WIN, finalScore.getStatus());
    }

    @Test
    void clickCellInAGameThatIsNotActive(){
        // Prepare scenario
        String mineSweeperId = UUID.randomUUID().toString();
        this.mineSweeper.setId(mineSweeperId);
        this.mineSweeper.setStatus(Status.PAUSED);
        CellRequest cellRequest = CellRequest.builder().mineSweeperId(mineSweeperId).x(5).y(5).build();

        // Mock responses
        Mockito.when(mineSweeperRepository.findById(eq(mineSweeperId))).thenReturn(Optional.of(mineSweeper));

        try{
            mineSweeperService.cellAction(cellRequest, CellAction.CLICK);
            fail();
        } catch(MinesweeperApiException e) {
            // Assertions
            assertEquals("You can't play in a non active game", e.getMessage());
        }
    }

}

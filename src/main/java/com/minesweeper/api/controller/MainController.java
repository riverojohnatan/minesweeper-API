package com.minesweeper.api.controller;

import com.minesweeper.api.dto.CellRequest;
import com.minesweeper.api.dto.MineSweeperRequest;
import com.minesweeper.api.model.CellAction;
import com.minesweeper.api.model.MineSweeper;
import com.minesweeper.api.service.MineSweeperService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/minesweeper")
public class MainController {

    @Autowired
    private MineSweeperService mineSweeperService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Creates new minesweeper's game", response = MineSweeper.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Game has been created successfully") })
    public MineSweeper createMineSweeper(@RequestBody final MineSweeperRequest newGameRequest){
        return mineSweeperService.createMineSweeper(newGameRequest);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Saves the minesweeper's game", response = MineSweeper.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Game saved successfully") })
    public MineSweeper saveMineSweeper(@RequestBody final MineSweeper game) {
        return mineSweeperService.saveMineSweeper(game);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Gets minesweeper's games by user Id", response = MineSweeper[].class, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Games have been retrieved successfully") })
    public List<MineSweeper> getMinesweepersByUserId(@PathVariable("userId") final String userId) {
        return mineSweeperService.getMinesweepersByUserId(userId);
    }

    @GetMapping("/load/{mineSweeperId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Load game by Id", response = MineSweeper.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Game has been loaded successfully") })
    public MineSweeper loadMinesweeper(@PathVariable("mineSweeperId") final String mineSweeperId){
        return mineSweeperService.getMineSweeperById(mineSweeperId);
    }

    @PutMapping("/pause/{mineSweeperId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Pause/resume a game by Id", response = MineSweeper.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Game has been paused/resumed successfully") })
    public MineSweeper pauseGame(@PathVariable("mineSweeperId") final String mineSweeperId){
        return mineSweeperService.pauseResumeMineSweeper(mineSweeperId);
    }

    @PutMapping("/cell/{action}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Cell's action. FLAG/CLICK", response = MineSweeper.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Cell has been flagged/clicked successfully") })
    public MineSweeper cellAction(@PathVariable("action") CellAction action, @RequestBody final CellRequest cellRequest){
        return mineSweeperService.cellAction(cellRequest, action);
    }

}

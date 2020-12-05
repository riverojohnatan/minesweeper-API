package com.minesweeper.api.controller;

import com.minesweeper.api.dto.MineSweeperRequest;
import com.minesweeper.api.model.MineSweeper;
import com.minesweeper.api.service.MineSweeperService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/game")
public class MainController {

    @Autowired
    private MineSweeperService mineSweeperService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Creates new game", response = MineSweeper.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Game has been created successfully") })
    public MineSweeper createGame(@RequestBody final MineSweeperRequest newGameRequest){
        return mineSweeperService.create(newGameRequest);
    }

}

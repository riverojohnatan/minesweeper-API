package com.minesweeper.api.service;

import com.minesweeper.api.dto.CellRequest;
import com.minesweeper.api.dto.MineSweeperRequest;
import com.minesweeper.api.model.CellAction;
import com.minesweeper.api.model.MineSweeper;

import java.util.List;

public interface MineSweeperService {

    MineSweeper createMineSweeper(MineSweeperRequest request);
    MineSweeper saveMineSweeper(MineSweeper mineSweeper);
    List<MineSweeper> getMinesweepersByUserId(String userId);
    MineSweeper getMineSweeperById(String mineSweeperId);
    MineSweeper pauseResumeMineSweeper(String mineSweeperId);
    MineSweeper cellAction(CellRequest cellRequest, CellAction action);

}

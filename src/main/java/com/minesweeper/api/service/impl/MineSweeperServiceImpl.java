package com.minesweeper.api.service.impl;

import com.google.common.collect.Lists;
import com.minesweeper.api.dto.CellRequest;
import com.minesweeper.api.dto.MineSweeperRequest;
import com.minesweeper.api.model.Cell;
import com.minesweeper.api.model.CellAction;
import com.minesweeper.api.model.MineSweeper;
import com.minesweeper.api.model.Status;
import com.minesweeper.api.service.MineSweeperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class MineSweeperServiceImpl implements MineSweeperService {

    @Override
    public MineSweeper createMineSweeper(MineSweeperRequest request) {
        return null;
    }

    @Override
    public MineSweeper saveMineSweeper(MineSweeper game) {
        return null;
    }

    @Override
    public List<MineSweeper> getMinesweepersByUserId(String userId) {
        return null;
    }

    @Override
    public MineSweeper getMineSweeperById(String mineSweeperId) {
        return null;
    }

    @Override
    public MineSweeper pauseResumeMineSweeper(String mineSweeperId) {
        return null;
    }

    @Override
    public MineSweeper cellAction(CellRequest cellRequest, CellAction action) {
        return null;
    }
}

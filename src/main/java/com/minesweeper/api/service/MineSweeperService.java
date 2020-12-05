package com.minesweeper.api.service;

import com.minesweeper.api.dto.MineSweeperRequest;
import com.minesweeper.api.model.MineSweeper;

public interface MineSweeperService {

    MineSweeper create(MineSweeperRequest request);

}

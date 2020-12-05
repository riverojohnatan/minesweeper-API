package com.minesweeper.api.service.impl;

import com.google.common.collect.Lists;
import com.minesweeper.api.dto.MineSweeperRequest;
import com.minesweeper.api.model.Cell;
import com.minesweeper.api.model.MineSweeper;
import com.minesweeper.api.model.Status;
import com.minesweeper.api.service.MineSweeperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class MineSweeperServiceImpl implements MineSweeperService {

    @Override
    public MineSweeper create(MineSweeperRequest request) {
        log.info("Creating minesweeper for user: ".concat(request.getUserId()));
        return new MineSweeper("", request.getUserId(), request, Lists.newArrayList(new Cell()),
                Status.ACTIVE, 0L, new Date(), new Date());
    }
}

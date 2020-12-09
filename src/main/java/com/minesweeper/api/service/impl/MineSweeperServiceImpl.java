package com.minesweeper.api.service.impl;

import com.minesweeper.api.dto.CellRequest;
import com.minesweeper.api.dto.MineSweeperRequest;
import com.minesweeper.api.model.Cell;
import com.minesweeper.api.model.CellAction;
import com.minesweeper.api.model.MineSweeper;
import com.minesweeper.api.model.Status;
import com.minesweeper.api.model.exception.MinesweeperApiException;
import com.minesweeper.api.repository.MineSweeperRepository;
import com.minesweeper.api.service.MineSweeperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class MineSweeperServiceImpl implements MineSweeperService {

    @Autowired
    MineSweeperRepository mineSweeperRepository;

    @Override
    public MineSweeper generateMineSweeper(MineSweeperRequest request) {
        MineSweeper mineSweeper = MineSweeper.builder().status(Status.ACTIVE).userId(request.getUserId())
                .rows(request.getRows()).columns(request.getColumns()).bombs(request.getBombs()).build();
        mineSweeper.initCells();
        return mineSweeper;
    }

    @Override
    public MineSweeper createMineSweeper(MineSweeperRequest request) {
        MineSweeper mineSweeper = this.generateMineSweeper(request);

        return this.saveMineSweeper(mineSweeper);
    }

    @Override
    public MineSweeper saveMineSweeper(MineSweeper mineSweeper) {
        if (!Objects.isNull(mineSweeper.getId())) {
            log.info("Saving minesweeper with id: ".concat(mineSweeper.getId()));
        }

        return this.mineSweeperRepository.save(mineSweeper);
    }

    @Override
    public List<MineSweeper> getMinesweepersByUserId(String userId) {
        return this.mineSweeperRepository.getMineSweepersByUserId(userId);
    }

    @Override
    public MineSweeper getMineSweeperById(String mineSweeperId) {
        Optional<MineSweeper> response = this.mineSweeperRepository.findById(mineSweeperId);
        return response.orElseThrow(() -> new MinesweeperApiException("Minesweeper does not exist"));
    }

    @Override
    public MineSweeper pauseResumeMineSweeper(String mineSweeperId) {
        MineSweeper mineSweeper = this.getMineSweeperById(mineSweeperId);
        mineSweeper.pause();
        return this.saveMineSweeper(mineSweeper);
    }

    @Override
    public MineSweeper cellAction(CellRequest cellRequest, CellAction action) {

        MineSweeper mineSweeper = this.getMineSweeperById(cellRequest.getMineSweeperId());
        if(!mineSweeper.getStatus().equals(Status.ACTIVE)){
            throw new MinesweeperApiException("You can't play in a non active game");
        }

        Cell cell = mineSweeper.getCell(cellRequest.getX(), cellRequest.getY());

        switch (action) {
            case FLAG:
                cell.flag();
                break;
            case CLICK:
                mineSweeper.recognizeCell(cell);
                break;
        }

        return this.saveMineSweeper(mineSweeper);
    }
}

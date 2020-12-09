package com.minesweeper.api.model;

import com.minesweeper.api.model.exception.MinesweeperApiException;

public enum Status {
    ACTIVE {
        @Override
        Status pause() {
            return Status.PAUSED;
        }
    },
    PAUSED {
        @Override
        Status pause() {
            return Status.ACTIVE;
        };
    },
    GAME_OVER {
        @Override
        Status pause() {
            throw new MinesweeperApiException("The game is over and could not be resumed/paused");
        };
    },
    WIN {
        @Override
        Status pause() {
            throw new MinesweeperApiException("The game is over and could not be resumed/paused");
        };
    };

    abstract Status pause();
}

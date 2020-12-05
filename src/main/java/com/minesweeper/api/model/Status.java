package com.minesweeper.api.model;

import com.minesweeper.api.model.exception.MinesweeperApiException;

public enum Status {
    ACTIVE {
        @Override
        Status pause() {
            return Status.PAUSED;
        }

        @Override
        boolean isEnded() {
            return false;
        };
    },
    PAUSED {
        @Override
        Status pause() {
            return Status.ACTIVE;
        };
        @Override
        boolean isEnded() {
            return false;
        };
    },
    GAME_OVER {
        @Override
        Status pause() {
            throw new MinesweeperApiException("The game is over and could not be resumed/paused");
        };
        @Override
        boolean isEnded() {
            return true;
        };
    },
    WIN {
        @Override
        Status pause() {
            throw new MinesweeperApiException("The game is over and could not be resumed/paused");
        };
        @Override
        boolean isEnded() {
            return true;
        };
    };

    abstract Status pause();
    abstract boolean isEnded();
}

package com.github.athieriot.engine;

import java.security.SecureRandom;

public class Engine {

    private final Board board;
    private int playerTurn = new SecureRandom().nextInt(1) + 1;

    public Engine() {
        this(6, 6);
    }

    public Engine(int houses, int seeds) {
        this.board = new Board(houses, seeds);
    }

    public int playerTurn() {
        return playerTurn;
    }

    public void play(int player, int house) {
        //TODO: Check game over
        checkPlayersTurn(player);

        int lastIdx = board.move(player, house);
        if (captureConditions(player, lastIdx)) {
            board.capture(player, lastIdx);
        }


        // Check end of game
        // Count all remaining seeds in score

        if (lastIdx != board.playerStoreIdx(player)) {
            //TODO: Add some sort of login maybe?
            togglePlayersTurn();
        }
    }

    //TODO: Maybe log number of moves
    public int score(int player) {
        return board.seeds(board.playerStoreIdx(player));
    }

    private void checkPlayersTurn(int player) {
        if (player < 1 || player > 2) {  throw new GameException("This is 2 players game only"); }
        else if (player != playerTurn) { throw new GameException("Not your turn yet"); }
    }

    private void togglePlayersTurn() {
        if (playerTurn == 1) {
            playerTurn = 2;
        } else {
            playerTurn = 1;
        }
    }

    private boolean captureConditions(int player, int idx) {
        int opponentIdx = board.opponentIdx(idx);

        return board.isPlayersHouse(player, idx)
                && board.seeds(idx) == 1
                && board.seeds(opponentIdx) > 0;
    }
}

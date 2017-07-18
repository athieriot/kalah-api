package com.github.athieriot.engine;

import java.security.SecureRandom;

public class Engine {

    private int playerTurn = new SecureRandom().nextInt(1) + 1;

    private Board board;

    public Engine() {
        this(6, 6);
    }

    public Engine(int houses, int seeds) {
        this.board = new Board(houses, seeds);
    }

    public Board board() {
        return board;
    }

    public int playerTurn() {
        return playerTurn;
    }

    public void play(int player, int house) {
        if (player < 1 || player > 2) {
            throw new GameException("This is 2 players game only");
        } else if (player != playerTurn) {
            throw new GameException("Not your turn yet");
        }

        board.move(player, house);
        togglePlayerTurn();
    }

    private void togglePlayerTurn() {
        if (playerTurn == 1) {
            playerTurn = 2;
        } else {
            playerTurn = 1;
        }
    }
}

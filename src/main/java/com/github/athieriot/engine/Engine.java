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
        checkPlayersTurn(player);

        board.move(player, house);

        // Check if score and bank
        // Check end of game

        togglePlayersTurn();
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
}

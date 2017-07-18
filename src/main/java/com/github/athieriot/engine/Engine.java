package com.github.athieriot.engine;

import java.security.SecureRandom;

import static java.util.Arrays.stream;

//TODO: Implement the Pie Rule?
public class Engine {

    private final Board board;
    private int playerTurn;

    private boolean gameOver = false;

    public Engine() {
        this(6, 6);
    }

    public Engine(int houses, int seeds) {
        this(houses, seeds, new SecureRandom().nextInt(1) + 1);
    }

    public Engine(int houses, int seeds, int firstPlayer) {
        this.board = new Board(houses, seeds);
        this.playerTurn = firstPlayer;
    }

    public int playerTurn() {
        return playerTurn;
    }

    public void play(int player, int... houses) {
        stream(houses).forEach(h -> play(player, h));
    }

    //TODO: Add Javadoc
    public void play(int player, int house) {
        checkValidPlay(player);

        int lastIdx = board.move(player, house);
        if (captureConditions(player, lastIdx)) {
            board.capture(player, lastIdx);
        }

        if (board.seedsLeftFor(player) == 0) {
            board.collectSeeds(player == 1 ? 2 : 1);
            gameOver = true;
            return;
        }

        if (lastIdx != board.playerStoreIdx(player)) {
            //TODO: Add some sort of login maybe?
            togglePlayersTurn();
        }
    }

    //TODO: Maybe log number of moves
    public int score(int player) {
        return board.seeds(board.playerStoreIdx(player));
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int winner() {
        if (!gameOver) { throw new GameException("Game not finished yet"); }

        else if (score(1) > score(2)) { return 1; }
        else if (score(1) < score(2)) { return 2; }
        else { return 0; }
    }

    private void checkValidPlay(int player) {
        if (gameOver) { throw new GameException("The Game is over !"); }
        else if (player < 1 || player > 2) {  throw new GameException("This is 2 players game only"); }
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

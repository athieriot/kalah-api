package com.github.athieriot.engine;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.athieriot.exception.IllegalMoveException;
import com.github.athieriot.exception.GameOverException;

import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.UUID;

import static java.util.Arrays.stream;

//TODO: Implement the Pie Rule?
public class Engine {

    private final UUID id = UUID.randomUUID();

    private final Board board;

    private int playerTurn;

    private boolean gameOver = false;

    public Engine() {
        this(6, 6);
    }

    public Engine(int houses, int seeds) {
        this(houses, seeds, new SecureRandom().nextInt(2) + 1);
    }

    public Engine(int houses, int seeds, int firstPlayer) {
        this.board = new Board(houses, seeds);
        this.playerTurn = firstPlayer;
    }

    @JsonProperty
    public UUID id() {
        return id;
    }

    @JsonProperty
    public int[] board() {
        return board.list();
    }

    @JsonProperty
    public int playerTurn() {
        return playerTurn;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    @JsonProperty
    public HashMap<Integer, Integer> scores() {
        return new HashMap<Integer, Integer>() {{
            put(1, score(1));
            put(2, score(2));
        }};
    }

    public void play(int player, int... houses) {
        stream(houses).forEach(h -> play(player, h));
    }

    //TODO: Add Javadoc
    //TODO: Record individual steps?
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
            togglePlayersTurn();
        }
    }

    //TODO: Maybe log number of moves
    public int score(int player) {
        return board.seeds(board.playerStoreIdx(player));
    }

    @JsonProperty
    public Integer winner() {
        if (!gameOver) { return null; }
        else if (score(1) > score(2)) { return 1; }
        else if (score(1) < score(2)) { return 2; }
        else { return 0; }
    }

    private void checkValidPlay(int player) {
        if (gameOver) { throw new GameOverException("The Game is over !"); }
        else if (player < 1 || player > 2) {  throw new InvalidParameterException("This is 2 players game only"); }
        else if (player != playerTurn) { throw new IllegalMoveException("Not your turn yet"); }
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

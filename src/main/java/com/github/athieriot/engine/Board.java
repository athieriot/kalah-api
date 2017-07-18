package com.github.athieriot.engine;

import static java.util.Arrays.fill;

public class Board {

    private final int houses;
    private final int seeds;

    private final int southStoreIdx;
    private final int northStoreIdx;

    private final int southStartIdx;
    private final int northStartIdx;

    private int hand = 0;
    private final int[] board;

    public Board(int houses, int seeds) {
        this.houses = houses;
        this.seeds = seeds;
        this.board = new int[(houses * 2) + 2];
        this.southStoreIdx = houses;
        this.northStoreIdx = (houses * 2) + 1;

        this.southStartIdx = 0;
        this.northStartIdx = houses + 1;

        fill(board, northStartIdx, northStartIdx + houses, seeds);
        fill(board, southStartIdx, southStartIdx + houses, seeds);
    }

    //TODO: Should not allow moves from an empty house
    public int move(int player, int houseNbr) {
        if (houseNbr < 1 || houseNbr > houses) { throw new GameException("This is a party with " + houses + " houses"); }

        int houseIdx = fromHouseNbrToIdx(player, houseNbr);
        pickSeeds(houseIdx);

        int startIdx = houseIdx + 1;
        int lastIdx = startIdx;

        while (hand > 0) {
            lastIdx = sowSeedsFrom(player, startIdx);
            startIdx = 0;
        }

        return lastIdx;
    }

    private int fromHouseNbrToIdx(int player, int houseNbr) {
        return player == 1 ? houseNbr - 1 : houseNbr + houses;
    }

    private void pickSeeds(int house) {
        synchronized (board) {
            hand = board[house];
            board[house] = 0;
        }
    }

    private int sowSeedsFrom(int player, int startIdx) {
        int i;
        int opponentStoreIdx = player == 1 ? northStoreIdx : southStoreIdx;

        for(i = startIdx; i < board.length && hand > 0; i++) {
            if (i != opponentStoreIdx) {
                board[i] = board[i] + 1;
                hand = hand - 1;
            }
        }

        return i - 1;
    }

    public boolean capture(int player, int idx) {
        int opponentIdx = opponentIdx(idx);
        int playerStoreIdx = player == 1 ? southStoreIdx : northStoreIdx;

        if (captureConditions(player, idx, opponentIdx)) {
            int treasure = board[idx] + board[opponentIdx];

            synchronized (board) {
                board[idx] = 0;
                board[opponentIdx] = 0;
                board[playerStoreIdx] = board[playerStoreIdx] + treasure;
            }

            return true;
        }

        return false;
    }

    //TODO: Move caputre conditions in Engine
    private boolean captureConditions(int player, int idx, int opponentIdx) {
        return isPlayersHouse(player, idx)
                && board[idx] == 1
                && board[opponentIdx] > 0;
    }

    public boolean isPlayersHouse(int player, int houseIdx) {
        return player == 1 && houseIdx >= southStartIdx && houseIdx <= southStartIdx + houses - 1
                || player == 2 && houseIdx >= northStartIdx && houseIdx <= northStartIdx + houses - 1;
    }

    private int opponentIdx(int idx) {
        if (idx != northStoreIdx && idx != southStoreIdx) {
            return (idx - (houses * 2)) * -1;
        }

        return idx;
    }

    public int score(int player) {
        int playerStoreIdx = player == 1 ? southStoreIdx : northStoreIdx;

        return board[playerStoreIdx];
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int i = northStoreIdx - 1; i > southStoreIdx; i--) {
            builder.append(board[i]).append("\t");
        }
        builder.append("\n\r");

        builder.append(board[northStoreIdx]).append("\t");

        for (int i = 1; i < houses - 1; i++) {
            builder.append(" ").append("\t");
        }
        builder.append(board[southStoreIdx]);
        builder.append("\n\r");


        for (int i = 0; i < southStoreIdx; i++) {
            builder.append(board[i]).append("\t");
        }

        return builder.toString().trim();
    }
}

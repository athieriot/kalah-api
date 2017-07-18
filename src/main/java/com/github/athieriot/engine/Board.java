package com.github.athieriot.engine;

import static java.util.Arrays.fill;

public class Board {

    private int southStartIdx;
    private int northStartIdx;

    private int southStoreIdx;
    private int northStoreIdx;

    private int seeds;
    private int houses;

    private int hand = 0;

    private int[] board;

    public Board(int seeds, int houses) {
        this.houses = houses;
        this.seeds = seeds;

        board = new int[(houses * 2) + 2];
        southStartIdx = 0;
        northStartIdx = houses + 1;

        southStoreIdx = houses;
        northStoreIdx = (houses * 2) + 1;

        fill(board, northStartIdx, northStartIdx + houses, seeds);
        fill(board, southStartIdx, southStartIdx + houses, seeds);
    }

    //TODO: Is a move from an empty house legal?
    public boolean legalMove(int player, int house) {
        return player == 1 && house >= southStartIdx && house <= southStartIdx + houses - 1
                || player == 2 && house >= northStartIdx && house <= northStartIdx + houses - 1;
    }

    public int move(int player, int house) {
        if (!legalMove(player, house)) { throw new GameException("You can't sow from there"); }

        pickSeeds(house);

        int startIdx = house + 1;
        int lastIdx = startIdx;

        while (hand > 0) {
            lastIdx = sowSeedsFrom(player, startIdx);
            startIdx = 0;
        }

        return lastIdx;
    }

    private void pickSeeds(int house) {
        hand = board[house];
        board[house] = 0;
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

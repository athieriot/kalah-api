package com.github.athieriot.engine;

import com.github.athieriot.exception.IllegalMoveException;

import java.security.InvalidParameterException;
import java.util.stream.IntStream;

import static java.util.Arrays.fill;
import static java.util.stream.IntStream.range;

public class Board {

    private final int houses;
    private final int seeds;

    private final int southStoreIdx;
    private final int northStoreIdx;

    private final int southStartIdx;
    private final int northStartIdx;

    private int hand = 0;
    private final int[] board;

    /* package */ Board(int houses, int seeds) {
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

    /* package */ int move(int player, int houseNbr) {
        int houseIdx = fromHouseNbrToIdx(player, houseNbr);

        checkLegalMoves(houseNbr, houseIdx);
        pickSeeds(houseIdx);

        return sowSeedsInHand(player, houseIdx);
    }

    private int fromHouseNbrToIdx(int player, int houseNbr) {
        return player == 1 ? houseNbr - 1 : houseNbr + houses;
    }

    private void checkLegalMoves(int houseNbr, int houseIdx) {
        if (houseNbr < 1 || houseNbr > houses) { throw new InvalidParameterException("This is a party with " + houses + " houses"); }
        else if (board[houseIdx] == 0) { throw new IllegalMoveException("Empty house. Not a valid move"); }
    }

    private void pickSeeds(int house) {
        synchronized (board) {
            hand = board[house];
            board[house] = 0;
        }
    }

    private int sowSeedsInHand(int player, int houseIdx) {
        int startIdx = houseIdx + 1;
        int lastIdx = startIdx;

        while (hand > 0) {
            lastIdx = sowSeedsFrom(player, startIdx);
            startIdx = 0;
        }
        return lastIdx;
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

    /* package */ void capture(int player, int idx) {
        int oppositeIdx = oppositeIdx(idx);
        int playerStoreIdx = playerStoreIdx(player);
        int treasure = board[idx] + board[oppositeIdx];

        synchronized (board) {
            board[idx] = 0;
            board[oppositeIdx] = 0;
            board[playerStoreIdx] = board[playerStoreIdx] + treasure;
        }
    }

    /* package */ void collectSeeds(int player) {
        board[playerStoreIdx(player)] = board[playerStoreIdx(player)] + seedsLeftFor(player);

        streamHousesOf(player).forEach(i -> board[i] = 0);
    }

    /* package */ boolean isPlayersHouse(int player, int idx) {
        return player == 1 && idx >= southStartIdx && idx <= southStartIdx + houses - 1
                || player == 2 && idx >= northStartIdx && idx <= northStartIdx + houses - 1;
    }

    /* package */ int seedsLeftFor(int player) {
        return streamHousesOf(player).map(i -> board[i]).sum();
    }

    /* package */ int playerStoreIdx(int player) {
        return player == 1 ? southStoreIdx : northStoreIdx;
    }

    /* package */ int oppositeIdx(int idx) {
        if (idx == northStoreIdx) { return southStoreIdx; }
        else if (idx == southStoreIdx) { return northStoreIdx; }

        return (idx - (houses * 2)) * -1;
    }

    /* package */ int seendsIn(int idx) {
        return board[idx];
    }

    /* package */ int[] toArray() {
        return board;
    }

    private IntStream streamHousesOf(int player) {
        int start = player == 1 ? southStartIdx : northStartIdx;
        int end = start + houses;

        return range(start, end);
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

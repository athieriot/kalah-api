package com.github.athieriot.engine;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BoardTest {

    @Test
    public void test_to_string() {
        Board board = new Board(6, 6);

        assertThat(board.toString()).isEqualTo(
                "6\t6\t6\t6\t6\t6\t\n\r" +
                "0\t \t \t \t \t0\n\r" +
                "6\t6\t6\t6\t6\t6");
    }

    @Test
    public void test_players_house() {
        Board board = new Board(6, 6);

        assertThat(board.isPlayersHouse(1, 1)).isTrue(); // Player 1 house
        assertThat(board.isPlayersHouse(1, 6)).isFalse();// Player 1 store
        assertThat(board.isPlayersHouse(2, 2)).isFalse();// Not Player 2 house
        assertThat(board.isPlayersHouse(2, 8)).isTrue();// Player 2 house
        assertThat(board.isPlayersHouse(2, 13)).isFalse();// Player 2 store
        assertThat(board.isPlayersHouse(3, 2)).isFalse();// There is no player 3
    }

    @Test
    public void test_some_moves_and_avoid_opponent_store() {
        Board board = new Board(6, 6);

        int lastIdx = board.move(1, 1);
        assertThat(lastIdx).isEqualTo(6);
        assertThat(board.toString()).isEqualTo(
                "6\t6\t6\t6\t6\t6\t\n\r" +
                "0\t \t \t \t \t1\n\r" +
                "0\t7\t7\t7\t7\t7");

        lastIdx = board.move(1, 2);
        assertThat(lastIdx).isEqualTo(8);
        assertThat(board.toString()).isEqualTo(
                "6\t6\t6\t6\t7\t7\t\n\r" +
                "0\t \t \t \t \t2\n\r" +
                "0\t0\t8\t8\t8\t8");

        lastIdx = board.move(1, 6);
        assertThat(lastIdx).isEqualTo(0);
        assertThat(board.toString()).isEqualTo(
                "7\t7\t7\t7\t8\t8\t\n\r" +
                "0\t \t \t \t \t3\n\r" +
                "1\t0\t8\t8\t8\t0");
    }

    @Test
    public void test_illegal_move() {
        Board board = new Board(6, 6);
        assertThatThrownBy(() -> board.move(1, 8))
                .isInstanceOf(GameException.class)
                .hasMessage("This is a party with 6 houses");

        board.move(1, 1);
        assertThatThrownBy(() -> board.move(1, 1))
                .isInstanceOf(GameException.class)
                .hasMessage("Empty house. Not a valid move");
    }

    @Test
    public void test_some_moves_from_north_zone() {
        Board board = new Board(6, 6);

        int lastIdx = board.move(2, 2);
        assertThat(lastIdx).isEqualTo(0);
        assertThat(board.toString()).isEqualTo(
                "7\t7\t7\t7\t0\t6\t\n\r" +
                "1\t \t \t \t \t0\n\r" +
                "7\t6\t6\t6\t6\t6");
    }

    @Test
    public void test_capture_from_player_1() {
        Board board = new Board(6, 4);

        board.move(1, 6);
        board.move(2, 2);
        board.move(2, 5);
        board.move(1, 2);
        board.move(1, 6);
        int lastIdx = board.move(1, 1);

        assertThat(board.toString()).isEqualTo(
                "6\t0\t5\t6\t0\t5\t\n\r" +
                "2\t \t \t \t \t3\n\r" +
                "0\t1\t7\t6\t6\t1");

        board.capture(1, lastIdx);

        assertThat(board.seeds(board.playerStoreIdx(1))).isEqualTo(9);
        assertThat(board.toString()).isEqualTo(
                "6\t0\t5\t6\t0\t0\t\n\r" +
                "2\t \t \t \t \t9\n\r" +
                "0\t1\t7\t6\t6\t0");
    }

    @Test
    public void test_opponent_idx() {
        Board board = new Board(6, 6);

        assertThat(board.opponentIdx(0)).isEqualTo(12);
        assertThat(board.opponentIdx(5)).isEqualTo(7);
        assertThat(board.opponentIdx(3)).isEqualTo(9);
        assertThat(board.opponentIdx(6)).isEqualTo(13);
        assertThat(board.opponentIdx(13)).isEqualTo(6);
    }

    @Test
    public void test_to_get_scores() {
        Board board = new Board(6, 6);

        board.move(1, 1);
        board.move(1, 3);

        assertThat(board.seeds(board.playerStoreIdx(1))).isEqualTo(2);
    }
}
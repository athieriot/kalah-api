package com.github.athieriot;

import com.github.athieriot.engine.Board;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

        boolean captured = board.capture(1, lastIdx);

        assertThat(captured).isTrue();
        assertThat(board.score(1)).isEqualTo(9);
        assertThat(board.toString()).isEqualTo(
                "6\t0\t5\t6\t0\t0\t\n\r" +
                "2\t \t \t \t \t9\n\r" +
                "0\t1\t7\t6\t6\t0");
    }

    @Test
    public void test_to_get_scores() {
        Board board = new Board(6, 6);

        board.move(1, 1);
        board.move(1, 3);

        assertThat(board.score(1)).isEqualTo(2);
    }
}
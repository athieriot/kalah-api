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
    public void test_legal_moves() {
        Board board = new Board(6, 6);

        assertThat(board.legalMove(1, 1)).isTrue(); // Player 1 house
        assertThat(board.legalMove(1, 6)).isFalse();// Player 1 store
        assertThat(board.legalMove(2, 2)).isFalse();// Not Player 2 house
        assertThat(board.legalMove(2, 8)).isTrue();// Player 2 house
        assertThat(board.legalMove(2, 13)).isFalse();// Player 2 store
        assertThat(board.legalMove(3, 2)).isFalse();// There is no player 3
    }

    @Test
    public void test_some_moves() {
        Board board = new Board(6, 6);

        int lastIdx = board.move(1, 0);
        assertThat(lastIdx).isEqualTo(6);
        assertThat(board.toString()).isEqualTo(
                "6\t6\t6\t6\t6\t6\t\n\r" +
                "0\t \t \t \t \t1\n\r" +
                "0\t7\t7\t7\t7\t7");

        lastIdx = board.move(1, 1);
        assertThat(lastIdx).isEqualTo(8);
        assertThat(board.toString()).isEqualTo(
                "6\t6\t6\t6\t7\t7\t\n\r" +
                "0\t \t \t \t \t2\n\r" +
                "0\t0\t8\t8\t8\t8");

        lastIdx = board.move(1, 5);
        assertThat(lastIdx).isEqualTo(0);
        assertThat(board.toString()).isEqualTo(
                "7\t7\t7\t7\t8\t8\t\n\r" +
                "0\t \t \t \t \t3\n\r" +
                "1\t0\t8\t8\t8\t0");
    }
}
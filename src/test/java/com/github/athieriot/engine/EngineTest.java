package com.github.athieriot.engine;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringJUnit4ClassRunner.class)
public class EngineTest {

    @Test
    @Repeat(10)
    public void test_current_user_start() {
        Engine engine = new Engine();
        assertThat(engine.playerTurn()).isBetween(1, 2);
    }

    @Test
    public void test_invalid_play_extra_user() {
        Engine engine = new Engine();
        assertThatThrownBy(() -> engine.play(3, 2))
                .isInstanceOf(GameException.class)
                .hasMessage("This is 2 players game only");
    }

    @Test
    public void test_invalid_play_not_players_turn() {
        Engine engine = new Engine();
        assertThatThrownBy(() -> engine.play(engine.playerTurn() == 1 ? 2 : 1, 2))
                .isInstanceOf(GameException.class)
                .hasMessage("Not your turn yet");
    }

    @Test
    public void test_play_toggle_player_turn() {
        Engine engine = new Engine();
        int firstPlayer = engine.playerTurn();

        engine.play(engine.playerTurn(), 1); // Last move is the store. Free move !
        assertThat(engine.playerTurn()).isEqualTo(firstPlayer);

        engine.play(engine.playerTurn(), 2);
        assertThat(engine.playerTurn()).isNotEqualTo(firstPlayer);
    }

    @Test
    public void test_capture_from_player_1() {
        Engine engine = new Engine(6, 4);

        engine.play(1, 6);
        engine.play(2, 2);
        engine.play(2, 5);
        engine.play(1, 2);
        engine.play(1, 6);

        assertThat(engine.score(1)).isEqualTo(3);
        assertThat(engine.score(2)).isEqualTo(2);

        engine.play(1, 1); // Capture move

        assertThat(engine.score(1)).isEqualTo(9);
    }
}
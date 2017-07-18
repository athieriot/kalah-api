package com.github.athieriot;

import com.github.athieriot.engine.Engine;
import com.github.athieriot.engine.GameException;
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
    public void test_invalid_play_illegal_move() {
        Engine engine = new Engine();
        assertThatThrownBy(() -> engine.play(1, 10))
                .isInstanceOf(GameException.class)
                .hasMessage("You can't sow from there");
    }

    @Test
    public void test_play_toggle_player_turn() {
        Engine engine = new Engine();
        int firstPlayer = engine.playerTurn();

        engine.play(engine.playerTurn(), 1);
        assertThat(engine.playerTurn()).isNotEqualTo(firstPlayer);
    }
}
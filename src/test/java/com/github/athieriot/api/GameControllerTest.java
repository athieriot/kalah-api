package com.github.athieriot.api;

import com.github.athieriot.engine.Engine;
import com.github.athieriot.repository.EngineRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//TODO: Missing end game testing
@RunWith(SpringRunner.class)
@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EngineRepository mockEngines;

    @Test
    public void test_new_game() throws Exception {
        mvc.perform(post("/game"))
                .andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, startsWith("/game/")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.playerTurn", isOneOf(1, 2)))
                .andExpect(jsonPath("$.board", hasSize(14)))
                .andExpect(jsonPath("$.gameOver", is(false)))
                .andExpect(jsonPath("$.scores.1", is(0)))
                .andExpect(jsonPath("$.scores.2", is(0)))
                .andExpect(jsonPath("$.history", empty()));

        verify(mockEngines, times(1)).store(any(Engine.class));
    }

    @Test
    public void test_retrieve_game_not_found() throws Exception {
        given(mockEngines.find(any(UUID.class))).willThrow(new NoSuchElementException());

        mvc.perform(get("/game/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void test_retrieve_existing_game() throws Exception {
        Engine engine = new Engine();
        given(mockEngines.find(any(UUID.class))).willReturn(engine);

        mvc.perform(get("/game/" + UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(engine.id().toString())))
                .andExpect(jsonPath("$.playerTurn", is(engine.playerTurn())))
                .andExpect(jsonPath("$.board", hasSize(14)))
                .andExpect(jsonPath("$.gameOver", is(engine.isGameOver())))
                .andExpect(jsonPath("$.scores.1", is(engine.score(1))))
                .andExpect(jsonPath("$.scores.2", is(engine.score(2))))
                .andExpect(jsonPath("$.history", empty()));
    }

    @Test
    public void test_play_invalid_parameter() throws Exception {
        Engine engine = new Engine();
        given(mockEngines.find(any(UUID.class))).willReturn(engine);

        mvc.perform(post("/game/" + UUID.randomUUID() + "/play/25/5"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("This is 2 players game only"));
    }

    @Test
    public void test_play_invalid_move() throws Exception {
        Engine engine = new Engine();
        given(mockEngines.find(any(UUID.class))).willReturn(engine);

        mvc.perform(post("/game/" + UUID.randomUUID() + "/play/" + (engine.playerTurn() == 1 ? 2 : 1) + "/5"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Not your turn yet"));
    }

    @Test
    public void test_play() throws Exception {
        Engine engine = new Engine();
        given(mockEngines.find(any(UUID.class))).willReturn(engine);

        int firstPlayer = engine.playerTurn();
        mvc.perform(post("/game/" + UUID.randomUUID() + "/play/" + engine.playerTurn() + "/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerTurn", is(firstPlayer == 1 ? 2 : 1)))
                .andExpect(jsonPath("$.history", hasSize(1)));
    }
}
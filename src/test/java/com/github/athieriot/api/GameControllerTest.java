package com.github.athieriot.api;

import com.github.athieriot.engine.Engine;
import com.github.athieriot.registry.ProcessorRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//TODO: Missing end game testing
@RunWith(SpringRunner.class)
@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mvc;

    @SpyBean
    private ProcessorRegistry registry;

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

        verify(registry, times(1)).spawnGameProcessorFor(any(Engine.class));
    }

    @Test
    public void test_new_game_with_params() throws Exception {
        mvc.perform(post("/game").param("houses", "4"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.board", hasSize(10)));

        verify(registry, times(1)).spawnGameProcessorFor(any(Engine.class));
    }

    @Test
    public void test_retrieve_game_not_found() throws Exception {
        CompletableFuture<Engine> failed = new CompletableFuture<>();
        failed.completeExceptionally(new NoSuchElementException());
        doReturn(failed).when(registry).stateOf(any(UUID.class));

        MvcResult asyncRequest = mvc.perform(get("/game/" + UUID.randomUUID()))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(asyncRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    public void test_retrieve_existing_game() throws Exception {
        Engine engine = new Engine();
        doReturn(completedFuture(engine)).when(registry).stateOf(any(UUID.class));

        MvcResult asyncRequest = mvc.perform(get("/game/" + UUID.randomUUID()))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(asyncRequest))
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
        registry.spawnGameProcessorFor(engine);

        MvcResult asyncRequest = mvc.perform(post("/game/" + engine.id() + "/play/25/5"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(asyncRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("This is 2 players game only"));
    }

    @Test
    public void test_play_invalid_move() throws Exception {
        Engine engine = new Engine();
        registry.spawnGameProcessorFor(engine);

        MvcResult asyncRequest = mvc.perform(post("/game/" + engine.id() + "/play/" + (engine.playerTurn() == 1 ? 2 : 1) + "/5"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(asyncRequest))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Not your turn yet"));
    }

    @Test
    public void test_play() throws Exception {
        Engine engine = new Engine();
        registry.spawnGameProcessorFor(engine);

        int firstPlayer = engine.playerTurn();
        MvcResult asyncRequest = mvc.perform(post("/game/" + engine.id().toString() + "/play/" + engine.playerTurn() + "/5"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(asyncRequest))
                .andExpect(status().isOk())
               .andExpect(jsonPath("$.playerTurn", is(firstPlayer == 1 ? 2 : 1)))
               .andExpect(jsonPath("$.history", hasSize(1)));

    }
}
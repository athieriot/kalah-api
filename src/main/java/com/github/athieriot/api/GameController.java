package com.github.athieriot.api;

import com.github.athieriot.engine.Engine;
import com.github.athieriot.exception.GameOverException;
import com.github.athieriot.exception.IllegalMoveException;
import com.github.athieriot.repository.EngineRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.InvalidParameterException;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/game")
public class GameController {

    private final EngineRepository engines;

    @Autowired
    public GameController(EngineRepository engines) {
        this.engines = engines;
    }

    @PostMapping
    @ApiOperation("Create a new game of Kalah")
    public ResponseEntity<Engine> newGame() {
        Engine engine = new Engine(6, 6);

        engines.store(engine);

        return created(URI.create("/game/" + engine.id())).body(engine);
    }

    @GetMapping("/{id}")
    @ApiOperation("Retrieve details of a game")
    public Engine game(@PathVariable UUID id) {
        return engines.find(id);
    }

    @PostMapping("/{id}/play/{player}/{house}")
    @ApiOperation("Make a move")
    public Engine play(@PathVariable UUID id,
                       @PathVariable int player,
                       @PathVariable int house
    ) {
        Engine engine = engines.find(id);

        engine.play(player, house);
        engines.store(engine);

        return engine;
    }
}

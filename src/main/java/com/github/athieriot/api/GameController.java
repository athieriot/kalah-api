package com.github.athieriot.api;

import com.github.athieriot.engine.Engine;
import com.github.athieriot.repository.EngineRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.created;

@RestController
@RequestMapping(value = "/game", produces = "application/json")
@Api(value = "/game", description = "Kalah Game Operations")
public class GameController {

    private final EngineRepository engines;

    @Autowired
    public GameController(EngineRepository engines) {
        this.engines = engines;
    }

    @PostMapping
    @ApiOperation(
            value = "Create a new game of Kalah",
            notes = "This will create a new Kalah game (Default: 6, 6) and pick a first player randomly"
    )
    public ResponseEntity<Engine> newGame(
            @RequestParam(value = "houses", defaultValue = "6") int houses,
            @RequestParam(value = "seeds",  defaultValue = "6") int seeds
    ) {
        Engine engine = new Engine(houses, seeds);

        engines.store(engine);

        return created(URI.create("/game/" + engine.id())).body(engine);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Retrieve details of a game", notes = "Simply access game details")
    public Engine game(@PathVariable UUID id) {
        return engines.find(id);
    }

    @PostMapping("/{id}/play/{player}/{house}")
    @ApiOperation(value = "Make a move",
            notes = "It's player's 2 turn so he starts and chose to sow the fifth house of his zone.<br>" +
                "See the last part of the path: `/play/2/5`. It's for player 2, house 5 (Choice from 1 to 6)<br>" +
                "<br>" +
                "The board is a list of 14 integer and are meant to be represented like so:<br>" +
                "<pre><code>" +
                    "   <--- North<br>" +
                    "12 11 10  9  8  7<br>" +
                    "13              6<br>" +
                    " 0  1  2  3  4  5<br>" +
                    "   South --->" +
                "</code></pre>" +
                "<br>" +
                "Where:<br>" +
                    "<br>" +
                    "- 6 is the Player 1 store<br>" +
                    "- 13 is the Player 2 score<br>" +
                    "- The South zone is from 0 to 5<br>" +
                    "- The North zone from 7 to 12")
    //TODO: Return individual board steps as well?
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

package com.github.athieriot.api;

import com.github.athieriot.engine.Engine;
import com.github.athieriot.repository.EngineRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import java.net.URI;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.created;

@RestController
@RequestMapping("/game")
@Produces({"application/json"})
@Api(value = "/game", description = "Kalah Game Operations")
public class GameController {

    private final EngineRepository engines;

    @Autowired
    public GameController(EngineRepository engines) {
        this.engines = engines;
    }

    //TODO: Allow to configure the game. Ex: Start Kalah(6,4) instead of Kalah(6, 6)
    @PostMapping
    @ApiOperation(value = "Create a new game of Kalah",
            notes = "This will create a new Kalah(6, 6) game and pick a first player randomly")
    public ResponseEntity<Engine> newGame() {
        Engine engine = new Engine(6, 6);

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

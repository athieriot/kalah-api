package controllers;

import engine.Engine;
import play.mvc.Controller;
import play.mvc.Result;
import repository.EngineRepository;

import javax.inject.Inject;
import java.util.UUID;

import static play.libs.Json.toJson;

public class GameController extends Controller {

    private final EngineRepository engines;

    @Inject
    public GameController(EngineRepository engines) {
        this.engines = engines;
    }

    public Result newGame(int houses, int seeds) {
        Engine engine = new Engine(houses, seeds);

        engines.store(engine);

        response().setHeader(LOCATION, controllers.routes.GameController.game(engine.id()).url());
        return created(toJson(engine));
    }

    public Result game(UUID id) {
        return ok(toJson(engines.find(id)));
    }

    //TODO: Return individual board steps as well?
    public Result play(UUID id, int player, int house) {
        Engine engine = engines.find(id);

        engine.play(player, house);
        engines.store(engine);

        //TODO: Exception Handling
        return ok(toJson(engine));
    }
}

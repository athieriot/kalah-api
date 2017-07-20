package repository;

import engine.Engine;

import javax.inject.Singleton;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class EngineRepository {

    private final Map<UUID, Engine> games = new ConcurrentHashMap<>();

    public Engine find(UUID id) {
        if (!games.containsKey(id)) {
            throw new NoSuchElementException();
        }

        return games.get(id);
    }

    public void store(Engine engine) {
        games.put(engine.id(), engine);
    }
}

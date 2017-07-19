package com.github.athieriot.repository;

import com.github.athieriot.engine.Engine;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
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

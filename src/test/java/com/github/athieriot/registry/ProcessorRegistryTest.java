package com.github.athieriot.registry;

import akka.actor.ActorSystem;
import com.github.athieriot.engine.Engine;
import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessorRegistryTest {

    private ActorSystem system = ActorSystem.create("test");

    @Test
    public void test_spawn_and_get_state() {
        ProcessorRegistry repository = new ProcessorRegistry(system);
        Engine engine = new Engine();

        repository.spawnGameProcessorFor(engine);
        repository.stateOf(engine.id()).whenComplete((engineRes, throwable) -> {
            assertThat(throwable).isNull();
            assertThat(engineRes).isEqualTo(engine);
        });
    }

    @Test
    public void test_actor_not_found() {
        ProcessorRegistry repository = new ProcessorRegistry(system);

        repository.stateOf(UUID.randomUUID()).whenComplete((engineRes, throwable) -> {
            assertThat(throwable).isInstanceOf(NoSuchElementException.class);
        });
    }
}
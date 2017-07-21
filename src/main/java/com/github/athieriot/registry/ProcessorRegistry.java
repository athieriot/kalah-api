package com.github.athieriot.registry;

import akka.actor.ActorNotFound;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.github.athieriot.engine.Engine;
import com.github.athieriot.engine.GameProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static akka.pattern.PatternsCS.ask;
import static com.github.athieriot.engine.GameProcessor.props;
import static java.util.concurrent.TimeUnit.SECONDS;
import static scala.concurrent.duration.FiniteDuration.apply;

/**
 * Utility class to help deal with Processor Actors
 */
@Component
public class ProcessorRegistry {

    private final ActorSystem system;

    @Autowired
    public ProcessorRegistry(ActorSystem system) {
        this.system = system;
    }

    public void spawnGameProcessorFor(Engine engine) {
        system.actorOf(props(engine), engine.id().toString());
    }

    public CompletableFuture<Engine> stateOf(UUID id) {
        return findProcessor(id).thenCompose(processor ->
                ask(processor, new GameProcessor.BoardState(), 5000L)
                        .toCompletableFuture()
                        .thenApply(o -> (Engine) o)
        );
    }

    public CompletableFuture<Engine> processPlayerAction(UUID id, int player, int house) {
        return findProcessor(id).thenCompose(processor ->
                ask(processor, new GameProcessor.PlayerAction(player, house), 5000L)
                        .toCompletableFuture()
                        .thenApply(o -> (Engine) o)
        );
    }

    private CompletableFuture<ActorRef> findProcessor(UUID id) {
        ActorSelection selection = system.actorSelection("/user/" + id.toString());

        return resolveActor(selection);
    }

    private CompletableFuture<ActorRef> resolveActor(ActorSelection selection) {
        return selection.resolveOneCS(apply(2, SECONDS))
                .toCompletableFuture()
                .exceptionally(this::convertActorNotFound);
    }

    private ActorRef convertActorNotFound(Throwable e) {
        if (e instanceof ActorNotFound) {
            throw new NoSuchElementException();
        }

        throw new RuntimeException(e);
    }
}

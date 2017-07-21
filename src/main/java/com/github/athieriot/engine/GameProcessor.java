package com.github.athieriot.engine;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.Status;

/**
 * Akka Actor whose responsibility is to handle Player action on a Kalah Game
 *
 * The main reason for this is to try to prevent a situation where two players/requests
 * would attempt to play at the same time.
 *
 * The benefit here is that each player's "Action" (A move) will be queued in the Actor message box
 * and processed one by one. One Game Engine is kept as a state per Actor and each moves will
 * be resolved as they arrived. Because actors are Singletons, thread safety is guaranteed.
 */
public class GameProcessor extends AbstractActor {

    private Engine engine;

    static public Props props(Engine engine) {
        return Props.create(GameProcessor.class, () -> new GameProcessor(engine));
    }

    static public class BoardState { }

    static public class PlayerAction {
        private int player;
        private int house;

        public PlayerAction(int player, int house) {
            this.player = player;
            this.house = house;
        }

        public int player() {
            return player;
        }

        public int house() {
            return house;
        }
    }

    public GameProcessor(Engine engine) {
        this.engine = engine;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BoardState.class, s -> sender().tell(engine, getSelf()))
                .match(PlayerAction.class, action -> {
                    try {
                        engine.play(action.player(), action.house());

                        sender().tell(engine, getSelf());
                    } catch (Exception e) {
                        sender().tell(new Status.Failure(e), getSelf());
                    }
                }).build();
    }
}

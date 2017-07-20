package engine;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import exception.GameOverException;
import exception.IllegalMoveException;

import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Arrays.stream;

//TODO: Implement the Pie Rule?
@JsonInclude(NON_NULL)
public class Engine {

    private final UUID id = UUID.randomUUID();

    private final Board board;

    private int playerTurn;

    private boolean gameOver = false;

    private List<int[]> history = new ArrayList<>();

    public Engine() {
        this(6, 6);
    }

    public Engine(int houses, int seeds) {
        this(houses, seeds, new SecureRandom().nextInt(2) + 1);
    }

    public Engine(int houses, int seeds, int firstPlayer) {
        this.board = new Board(houses, seeds);
        this.playerTurn = firstPlayer;
    }

    @JsonProperty
    public UUID id() {
        return id;
    }

    @JsonProperty
    public int[] board() {
        return board.toArray();
    }

    @JsonProperty
    public int playerTurn() {
        return playerTurn;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    @JsonProperty
    public List<int[]> history() { return history; }

    @JsonProperty
    public HashMap<Integer, Integer> scores() {
        return new HashMap<Integer, Integer>() {{
            put(1, score(1));
            put(2, score(2));
        }};
    }

    public void play(int player, int house) {
        checkValidPlay(player);

        int lastIdx = board.move(player, house);
        record(player, house);

        if (captureConditions(player, lastIdx)) {
            board.capture(player, lastIdx);
        }

        if (board.seedsLeftFor(player) == 0) {
            board.collectSeeds(player == 1 ? 2 : 1);
            gameOver = true;
            return;
        }

        if (lastIdx != board.playerStoreIdx(player)) {
            togglePlayersTurn();
        }
    }

    /* package */ void play(int player, int... houses) {
        stream(houses).forEach(h -> play(player, h));
    }

    public int score(int player) {
        return board.seendsIn(board.playerStoreIdx(player));
    }

    @JsonProperty
    public Integer winner() {
        if (!gameOver) { return null; }
        else if (score(1) > score(2)) { return 1; }
        else if (score(1) < score(2)) { return 2; }
        else { return 0; }
    }

    private void checkValidPlay(int player) {
        if (gameOver) { throw new GameOverException("The Game is over !"); }
        else if (player < 1 || player > 2) {  throw new InvalidParameterException("This is 2 players game only"); }
        else if (player != playerTurn) { throw new IllegalMoveException("Not your turn yet"); }
    }

    private void record(int player, int house) {
        history.add(new int[] { history.size() + 1, player, house });
    }

    private void togglePlayersTurn() {
        if (playerTurn == 1) {
            playerTurn = 2;
        } else {
            playerTurn = 1;
        }
    }

    private boolean captureConditions(int player, int idx) {
        int opponentIdx = board.oppositeIdx(idx);

        return board.isPlayersHouse(player, idx)
                && board.seendsIn(idx) == 1
                && board.seendsIn(opponentIdx) > 0;
    }
}

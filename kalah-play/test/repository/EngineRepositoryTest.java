package repository;

import engine.Engine;
import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EngineRepositoryTest {

    @Test
    public void test_store_and_retrieve() {
        EngineRepository repository = new EngineRepository();
        Engine engine = new Engine();

        repository.store(engine);
        assertThat(repository.find(engine.id())).isEqualTo(engine);
    }

    @Test
    public void test_find_not_found() {
        EngineRepository repository = new EngineRepository();

        assertThatThrownBy(() -> repository.find(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }
}
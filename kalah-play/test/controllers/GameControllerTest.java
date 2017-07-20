package controllers;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.revinate.assertj.json.JsonPathAssert;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static play.mvc.Http.HeaderNames.LOCATION;
import static play.mvc.Http.Status.CREATED;
import static play.test.Helpers.*;

public class GameControllerTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void test_new_game() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/game");

        Result result = route(app, request);

        assertThat(result.status()).isEqualTo(CREATED);
        assertThat(result.headers()).hasEntrySatisfying(LOCATION, v -> assertThat(v).startsWith("/game"));

        DocumentContext json = JsonPath.parse(contentAsString(result));
        JsonPathAssert.assertThat(json).jsonPathAsString("$.id").isNotNull();
        JsonPathAssert.assertThat(json).jsonPathAsInteger("$.playerTurn").isIn(1, 2);
        JsonPathAssert.assertThat(json).jsonPathAsListOf("$.board", Object.class).hasSize(14);
        JsonPathAssert.assertThat(json).jsonPathAsString("$.gameOver").isEqualTo("false");
        JsonPathAssert.assertThat(json).jsonPathAsInteger("$.scores.1").isEqualTo(0);
        JsonPathAssert.assertThat(json).jsonPathAsInteger("$.scores.2").isEqualTo(0);
        JsonPathAssert.assertThat(json).jsonPathAsListOf("$.history", String.class).isEmpty();
    }

}

package xyz.firestige.qcare.server.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class ProxyApiControllerTest {

    @Test
    public void testHealthEndpoint() {
        given()
          .when().get("/api/health")
          .then()
             .statusCode(200)
             .body(containsString("UP"));
    }

    @Test
    public void testInfoEndpoint() {
        given()
          .when().get("/api/info")
          .then()
             .statusCode(200)
             .body(containsString("Qcare Proxy Server"));
    }
}

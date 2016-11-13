package uk.co.markberridge.environment.health.resource;

import static org.assertj.core.api.Assertions.assertThat;
import io.dropwizard.testing.junit.ResourceTestRule;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.ClassRule;
import org.junit.Test;

public class PingResourceTest {

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder().addResource(new PingResource()).build();

    @Test
    public void test() {

    	Response response = resources.client()
                                                 .target("/ping")
                                                 .request()
                                                 .accept(MediaType.TEXT_PLAIN)
                                                 .get(Response.class);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isEqualTo("pong");
    }
}

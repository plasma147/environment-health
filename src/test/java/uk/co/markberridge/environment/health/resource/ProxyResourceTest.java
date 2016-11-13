package uk.co.markberridge.environment.health.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.dropwizard.testing.junit.ResourceTestRule;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import uk.co.markberridge.environment.health.service.ProxyService;
import uk.co.markberridge.environment.health.service.ProxyService.ResponseDto;

public class ProxyResourceTest {

    private static ProxyService proxyService = mock(ProxyService.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
                                                                     .addResource(new ProxyResource(proxyService))
                                                                     .build();

    @Before
    public void resetMocks() {
        reset(proxyService);
    }

    @Test
    public void testSuccess() {
        when(proxyService.getProxyResponse("http://www.example.com")).thenReturn(ResponseDto.of(200, "message"));

        Response response = resources.client().target("/proxy")
								           		 .queryParam("url", "http://www.example.com")
        		                                 .request()//
                                                 .get(Response.class);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isEqualTo("message");

    }

    @Test
    public void testInvalidate() {

        Response response = resources.client()
       		                                 .target("/proxy/").request()//
                                             .delete(Response.class);

        assertThat(response.getStatus()).isEqualTo(204);
        verify(proxyService).invalidate();
    }
    
}
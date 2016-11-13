package uk.co.markberridge.environment.health.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.dropwizard.util.Duration;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.co.markberridge.environment.health.service.ProxyService.ResponseDto;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

@RunWith(MockitoJUnitRunner.class)
public class ProxyServiceTest {

    @Mock private Client client;
    @Mock private MetricRegistry metricRegistry;
    @Mock private Meter meter;
    @Mock private WebTarget webTarget;
    @Mock private Invocation.Builder builder;
    @Mock private Response response;

    private static ProxyService proxyService;

    @Before
    public void resetMocks() {
        when(client.target("http://www.example.com")).thenReturn(webTarget);
        when(webTarget.request()).thenReturn(builder);
        when(builder.get(Response.class)).thenReturn(response);
        when(metricRegistry.meter(ProxyService.METER_NAME)).thenReturn(meter);
        proxyService = new ProxyService(metricRegistry, client, Duration.seconds(1));
    }

    @Test
    public void test200() {
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn("{'data':'200'}");

        ResponseDto responseDto = proxyService.getProxyResponse("http://www.example.com");

        assertThat(responseDto.getStatus()).isEqualTo(200);
        assertThat(responseDto.getText()).isEqualTo("{'data':'200'}");
        verify(meter).mark();
    }

    @Test
    public void test500() {
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn("{'data':'200'}");

        ResponseDto responseDto = proxyService.getProxyResponse("http://www.example.com");

        assertThat(responseDto.getStatus()).isEqualTo(200);
        assertThat(responseDto.getText()).isEqualTo("{'data':'200'}");
        verify(meter).mark();
    }

    @Test
    public void checkMeterRecordsMultipleRequests() {
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn("{'data':'200'}");

        proxyService.getProxyResponse("http://www.example.com1");
        proxyService.getProxyResponse("http://www.example.com2");

        verify(meter, times(2)).mark();
    }

    @Test
    public void checkCacheWorks() {
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn("{'data':'200'}");

        proxyService.getProxyResponse("http://www.example.com");
        proxyService.getProxyResponse("http://www.example.com");

        verify(meter, times(1)).mark();
    }

    @Test
    public void testConnectionException() {

        when(response.readEntity(String.class)).thenThrow(ProcessingException.class);

        ResponseDto responseDto = proxyService.getProxyResponse("http://www.example.com");

        assertThat(responseDto.getStatus()).isEqualTo(404);
        assertThat(responseDto.getText()).isEqualTo("The health check is not available at: http://www.example.com");
        verify(meter).mark();
    }

}

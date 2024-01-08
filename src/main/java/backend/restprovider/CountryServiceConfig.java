package backend.restprovider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CountryServiceConfig {

    @Bean
    public RestTemplate restTesmplate() {
        return new RestTemplate();
    }
}

package pe.financiera.gw.pagoservicios.config.time;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

import static pe.financiera.gw.pagoservicios.util.constants.ApplicationConstants.LIMA_ZONE;

@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.system(LIMA_ZONE);
    }
}

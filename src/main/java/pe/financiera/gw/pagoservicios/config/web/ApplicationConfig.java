package pe.financiera.gw.pagoservicios.config.web;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;


@Configuration
@ComponentScan({"pe.financiera.gw.pagoservicios", "pe.financiera.framework", "pe.financieraoh.framework.logging"})
@Slf4j
public class ApplicationConfig {

   @Bean
    static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}

package br.com.eureka.config;

import br.com.eureka.model.Tarefa;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TimeConfig {

    @Bean
    public Clock clock() {
        return Clock.system(Tarefa.TIME_ZONE);
    }
}

package se.kthraven.journalservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import se.kthraven.journalservice.Model.IJournalService;
import se.kthraven.journalservice.Model.JournalService;
import se.kthraven.journalservice.Persistence.IJournalPersistence;
import se.kthraven.journalservice.Persistence.JournalPersistence;

@Configuration
public class AppConfig {

    @Primary
    @Bean
    public IJournalPersistence IJournalPersistence(){
        return new JournalPersistence();
    }

    @Bean
    public IJournalService IJournalService(){
        return new JournalService();
    }
}

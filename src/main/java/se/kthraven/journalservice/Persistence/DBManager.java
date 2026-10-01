package se.kthraven.journalservice.Persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DBManager {
    private static EntityManagerFactory emf;

    protected static void init() {
        Map<String, String> properties = new HashMap<>();

        properties.put(
            "javax.persistence.jdbc.url",
            System.getenv("DATABASE_URL")
        );

        properties.put(
            "javax.persistence.jdbc.user",
            System.getenv("DATABASE_USERNAME")
        );

        properties.put(
            "javax.persistence.jdbc.password",
            System.getenv("DATABASE_PASSWORD")
        );

        emf = Persistence.createEntityManagerFactory(
            "journalappPU",
            properties
        );

        emf = Persistence.createEntityManagerFactory("journalappPU", properties);
    }

    public static void close(){
        if(emf != null && emf.isOpen()){
            emf.close();
        }
    }

    public static EntityManager getEntityManager() {
        if(emf == null)
            init();
        return emf.createEntityManager();
    }
}

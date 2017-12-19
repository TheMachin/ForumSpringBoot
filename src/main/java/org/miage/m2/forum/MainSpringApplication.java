package org.miage.m2.forum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@SpringBootApplication
public class MainSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainSpringApplication.class, args);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("forum");
        EntityManager em = emf.createEntityManager();
        em.close();
        emf.close();
    }
}

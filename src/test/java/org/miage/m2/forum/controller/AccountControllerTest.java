package org.miage.m2.forum.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.miage.m2.forum.modele.Message;
import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Topic;
import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.miage.m2.forum.service.AccountService;
import org.miage.m2.forum.service.AccountServiceImpl;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;

import static junit.framework.TestCase.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class) //lien entre les fonctionnalités de test Spring Boot et JUnit
@WebMvcTest(AccountController.class)
//@DataJpaTest // fournit des standards pour tester la persistance
public class AccountControllerTest {

    //permet de vérifier la classe Service
    @TestConfiguration
    static class AccountServiceImplTestContextConfiguration {

        @Bean
        public AccountService accountService() {
            return new AccountServiceImpl();
        }
    }

    @Autowired
    private AccountService accountService;

    // creation d'un Mock pour le UtilisateurRepository, il peut être utilisé pour ignorer son appel (bypass)
    @MockBean
    private UtilisateurRepository utilisateurRepository;

    @Test
    public final void testCreateUser(){
        boolean test = accountService.createUser("test@test.com", "test", "testmdp", false, new HashSet<Message>(), new HashSet<Projet>(), new HashSet<Topic>(), new HashSet<Topic>());
        assertTrue(test);
    }

    @Before
    public void setUp() {
        Utilisateur userTest = new Utilisateur("test@test.com", "test", "testmdp", false, new HashSet<Message>(), new HashSet<Projet>(), new HashSet<Topic>(), new HashSet<Topic>());

        Mockito.when(utilisateurRepository.findByPseudo(userTest.getPseudo())).thenReturn(userTest);
    }

    @Test
    public void whenValidPseuso_thenUtilisateurFound() {
        String pseudo = "test";
        Utilisateur found = accountService.getUtilisateurByPseudo(pseudo);
        assertThat(found.getPseudo()).isEqualTo(pseudo);
    }

}

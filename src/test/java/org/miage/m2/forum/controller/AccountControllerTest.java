package org.miage.m2.forum.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.miage.m2.forum.modele.Message;
import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Topic;
import org.miage.m2.forum.service.AccountService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;

import static junit.framework.TestCase.*;

@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Test
    public final void testCreateUser(){
        boolean test = AccountService.createUser("test@test.com", "test", "testmdp", false, new HashSet<Message>(), new HashSet<Projet>(), new HashSet<Topic>(), new HashSet<Topic>());
        assertTrue(test);
    }

}

package org.miage.m2.forum.service;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.miage.m2.forum.MainSpringApplication;
import org.miage.m2.forum.config.H2JpaConfig;
import org.miage.m2.forum.modele.*;
import org.miage.m2.forum.query.RolesRepository;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.miage.m2.forum.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashSet;

import static junit.framework.TestCase.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class) //lien entre les fonctionnalités de test Spring Boot et JUnit
@SpringBootTest(classes = {MainSpringApplication.class, H2JpaConfig.class})
public class AccountServiceImplTest {

    Utilisateur testUser = new Utilisateur("test@test.com", "test", "testmdp", false, new HashSet<Message>(), new HashSet<Projet>(), new HashSet<Topic>(), new HashSet<Topic>());
    Utilisateur testUserAzerty = new Utilisateur("testazerty@test.com", "testazerty", "testmdp", false, new HashSet<Message>(), new HashSet<Projet>(), new HashSet<Topic>(), new HashSet<Topic>());
    Utilisateur testUser2 = new Utilisateur("test@test.com", "test2", "testmdp", false, new HashSet<Message>(), new HashSet<Projet>(), new HashSet<Topic>(), new HashSet<Topic>());
    Utilisateur testUserQwerty = new Utilisateur("testqwerty@test.com", "testqwerty", "testmdp", false, new HashSet<Message>(), new HashSet<Projet>(), new HashSet<Topic>(), new HashSet<Topic>());
    Utilisateur userEditedPseudo = new Utilisateur("test@test.com", "testEdited", "testmdp", false, new HashSet<Message>(), new HashSet<Projet>(), new HashSet<Topic>(), new HashSet<Topic>());
    Utilisateur userEditedEmail = new Utilisateur("testEdited@test.com", "test", "testmdp", false, new HashSet<Message>(), new HashSet<Projet>(), new HashSet<Topic>(), new HashSet<Topic>());
    Utilisateur userEditedMdp = new Utilisateur("test@test.com", "test", "testmdpEdited", false, new HashSet<Message>(), new HashSet<Projet>(), new HashSet<Topic>(), new HashSet<Topic>());
    Utilisateur userEditedAdmin = new Utilisateur("test@test.com", "test", "testmdp", true, new HashSet<Message>(), new HashSet<Projet>(), new HashSet<Topic>(), new HashSet<Topic>());
    Roles roleUser;
    Roles roleAdmin;

    @Resource
    private AccountService accountService;

    // creation d'un Mock pour le UtilisateurRepository, il peut être utilisé pour ignorer son appel (bypass)
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private RolesRepository rolesRepository;


    @Before
    public void setUp() {
        if(roleAdmin==null){
            roleAdmin = new Roles();
            roleAdmin.setName("ROLE_ADMIN");
            rolesRepository.save(roleAdmin);
        }
        if(roleUser==null){
            roleUser = new Roles();
            roleUser.setName("ROLE_USER");
            rolesRepository.save(roleUser);
        }
        utilisateurRepository.delete(testUser);
        utilisateurRepository.delete(testUserAzerty);
        utilisateurRepository.delete(testUser2);
        utilisateurRepository.delete(testUserQwerty);
        utilisateurRepository.delete(userEditedPseudo);
        utilisateurRepository.delete(userEditedEmail);
        utilisateurRepository.delete(userEditedMdp);
    }

    @Test
    public final void testCreateUser(){
        Utilisateur testUserExpected = accountService.createUser(testUser);
        assertThat(testUserExpected.getPseudo()).isEqualTo(testUser.getPseudo());
    }

    @Test
    public void whenValidPseuso_thenUtilisateurFound() {
        Utilisateur testUserExpected = accountService.createUser(testUser);
        String pseudo = "test";
        Utilisateur found = accountService.getUtilisateurByPseudo(pseudo);
        assertThat(found.getPseudo()).isEqualTo(pseudo);
    }

    @Test
    public void whenNotValidPseuso_thenUtilisateurNotFound() {
        Utilisateur testUserExpected = accountService.createUser(testUser);
        String pseudo = "pseudoNotFound";
        Utilisateur found = accountService.getUtilisateurByPseudo(pseudo);
        assertNull(found);
    }

    @Test
    public void testCreateDuplicateUserEmail(){
        accountService.createUser(testUser2);
        Utilisateur testUserDuplicateExpected = accountService.createUser(testUser2);
        assertThat(testUserDuplicateExpected).isNull();
    }

    @Test
    public void testCreateDuplicateUserPseudo(){
        accountService.createUser(testUserAzerty);
        Utilisateur testUserDuplicateExpected = accountService.createUser(testUserAzerty);
        assertThat(testUserDuplicateExpected).isNull();
    }

    @Test
    public void testDeleteUser(){
        accountService.createUser(testUserAzerty);
        boolean check = accountService.deleteUser(testUserAzerty);
        assertTrue(check);
    }

    @Test
    public void testDeleteUserNotExist(){
        boolean check = accountService.deleteUser(testUserQwerty);
        assertFalse(check);
    }

    @Test
    public void testModifyUserPseudo(){
        accountService.createUser(testUser);
        Utilisateur userCheck = accountService.modifyUser(testUser, userEditedPseudo.getEmail(), userEditedPseudo.getPseudo(), userEditedPseudo.getMdp(), userEditedPseudo.isAdmin());
        assertThat(userCheck.getPseudo()).isEqualTo(userEditedPseudo.getPseudo());
    }

    @Test
    public void testModifyUserEmail(){
        accountService.createUser(testUser);
        Utilisateur userCheck = accountService.modifyUser(testUser, userEditedEmail.getEmail(), userEditedEmail.getPseudo(), userEditedEmail.getMdp(), userEditedEmail.isAdmin());
        assertThat(userCheck.getPseudo()).isEqualTo(userEditedEmail.getPseudo());
    }

    @Test
    public void testModifyUserMdp(){
        accountService.createUser(testUser);
        Utilisateur userCheck = accountService.modifyUser(testUser, userEditedMdp.getEmail(), userEditedMdp.getPseudo(), userEditedMdp.getMdp(), userEditedMdp.isAdmin());
        assertThat(userCheck.getPseudo()).isEqualTo(userEditedMdp.getPseudo());
    }

    @Test
    public void testNotModifyUserMdpIfEmpty(){
        accountService.createUser(testUser);
        Utilisateur userCheck = accountService.modifyUser(testUser, userEditedMdp.getEmail(), userEditedMdp.getPseudo(), "", userEditedMdp.isAdmin());
        assertThat(userCheck.getMdp().isEmpty()).isFalse();
    }

    @Test
    public void testModifyUserAdmin(){
        accountService.createUser(testUser);
        Utilisateur userCheck = accountService.modifyUser(testUser, userEditedAdmin.getEmail(), userEditedAdmin.getPseudo(), userEditedAdmin.getMdp(), userEditedAdmin.isAdmin());
        assertThat(userCheck.getPseudo()).isEqualTo(userEditedAdmin.getPseudo());
    }

    @Test
    public void testModifyUserToAddAdminRole(){
        accountService.createUser(testUser);
        Utilisateur userCheck = accountService.modifyUser(testUser,testUser.getEmail(),testUser.getPseudo(),testUser.getMdp(),true);
        assertThat(userCheck.isAdmin()).isTrue();
    }

    @Test
    public void testModifyUserToRemoveAdminRole(){
        accountService.createUser(testUser);
        Utilisateur userCheck = accountService.modifyUser(testUser,testUser.getEmail(),testUser.getPseudo(),testUser.getMdp(),true);
        userCheck = accountService.modifyUser(userCheck,userCheck.getEmail(),userCheck.getPseudo(),userCheck.getMdp(),false);
        assertThat(userCheck.isAdmin()).isFalse();
    }
}

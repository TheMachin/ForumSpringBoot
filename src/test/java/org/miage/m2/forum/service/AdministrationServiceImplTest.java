package org.miage.m2.forum.service;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.miage.m2.forum.MainSpringApplication;
import org.miage.m2.forum.config.H2JpaConfig;
import org.miage.m2.forum.modele.Message;
import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Topic;
import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.ProjetRepository;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class) //lien entre les fonctionnalités de test Spring Boot et JUnit
@SpringBootTest(classes = {MainSpringApplication.class, H2JpaConfig.class})
public class AdministrationServiceImplTest {

    @Resource
    private AdministrationService administrationService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ProjetRepository projetRepository;

    private Projet projet;

    /**
     * On utilise le meme objet projet qu'on l'initialise avant les tests
     */
    @Before
    public void setup(){
        Utilisateur userTest = new Utilisateur("test@test.com", "test", "testmdp", true, new HashSet<Message>(), new HashSet<Projet>(), new HashSet<Topic>(), new HashSet<Topic>());
        utilisateurRepository.save(userTest);
        utilisateurRepository.save(userTest);
        projet = new Projet("titre", "description", null, true, null, userTest, null);
        projetRepository.delete(projet);
        projet = administrationService.createProject(projet);
    }

    /**
     * On vérifie si un administrateur créé un projet
     */
    @Test
    public void testCreateProject(){

        Utilisateur userTest = new Utilisateur("test@test.com", "test", "testmdp", true, null, null, null, null);
        Projet projet = new Projet(String.valueOf(new Date().getTime()), "description", null, true, null, userTest, null);

        Projet test = administrationService.createProject(projet);
        assertThat(test.getTitre()).isEqualTo(projet.getTitre());
        projetRepository.delete(projet);
    }

    /**
     * Vérifie si on rejete la création d'un autre projet avec le meme nom
     */
    @Test
    public void testCantCreateTwoSameProjectName(){

        Utilisateur userTest = new Utilisateur("test@test.com", "test", "testmdp", true, new HashSet<Message>(), new HashSet<Projet>(), new HashSet<Topic>(), new HashSet<Topic>());
        utilisateurRepository.save(userTest);

        Projet projet2 = new Projet("titre", "description", null, true, null, userTest, null);

        Projet test = administrationService.createProject(projet2);
        assertThat(test).isNull();
    }

    /**
     * On vérifie si l'utilisateur qui n'est pas administrateur ne créé pas de projet
     */
    @Test
    public void testCantCreateProject(){
        Utilisateur userTest = new Utilisateur("test@test.com", "test", "testmdp", false, new HashSet<Message>(), new HashSet<Projet>(), new HashSet<Topic>(), new HashSet<Topic>());

        Projet projet = new Projet("titre", "description", null, true, null, userTest, null);

        Projet test = administrationService.createProject(projet);
        assertThat(test).isNull();
    }

    /**
     * On test si on arrive à retourner un projet à partir de son nom
     */
    @Test
    public void testFindProjetByName(){


        String titre = "titre";
        Projet found = administrationService.findOne(titre);
        assertThat(found.getTitre()).isEqualTo(titre);
    }

    /**
     * Ajouter un sous projet à un projet
     */
    @Test
    public void addProjectToProject() {

        Utilisateur userTest = new Utilisateur("test@test.com", "test", "testmdp", true, new HashSet<Message>(), new HashSet<Projet>(), new HashSet<Topic>(), new HashSet<Topic>());
        utilisateurRepository.save(userTest);

        Projet p = new Projet(String.valueOf(new Date().getTime()), "description", null, true, null, userTest, null);

        p = administrationService.createProject(p);
        Projet succes = administrationService.addProjectToProject(projet,p);
        assertThat(projet.getSousProjet().contains(p)).isTrue();
    }

    /**
     * Test pour changer l'accès des internautes au projet
     */
    @Test
    public void changePermissionForVisitor() {

        Projet test = administrationService.changePermissionForVisitor(projet,false);
        assertThat(test.isInvite()).isFalse();
        test = administrationService.changePermissionForVisitor(projet,true);
        assertThat(test.isInvite()).isTrue();
    }

    /**
     * Test pour vérifier si on ajoute l'accès des utilisateurs au forum
     */
    @Test
    public void addUserToAccess() {

        Utilisateur userTest = new Utilisateur("test@test.com", "test", "testmdp", true, new HashSet<Message>(), new HashSet<Projet>(), new HashSet<Topic>(), new HashSet<Topic>());

        Set<Utilisateur> users = new HashSet<Utilisateur>();
        Utilisateur userTest1 = new Utilisateur("test1@test.com", "test", "testmdp", false, null, null, null, null);
        Utilisateur userTest2 = new Utilisateur("test2@test.com", "test", "testmdp", false, null, null, null, null);

        users.add(userTest);
        users.add(userTest1);
        users.add(userTest2);
        utilisateurRepository.save(users);
        Projet test  = administrationService.addUserToAccess(projet,users);

        assertThat(projet.getAcces().contains(userTest2)).isTrue();
    }

    /**
     * Test pour vérifier si on enlève l'accès des utilisateurs à un projet
     */
    @Test
    public void removeUserToAccess() {

        Utilisateur userTest = new Utilisateur("test@test.com", "test", "testmdp", true, new HashSet<Message>(), new HashSet<Projet>(), new HashSet<Topic>(), new HashSet<Topic>());

        Set<Utilisateur> users = new HashSet<Utilisateur>();
        Utilisateur userTest1 = new Utilisateur("test1@test.com", "test", "testmdp", false, null, null, null, null);
        Utilisateur userTest2 = new Utilisateur("test2@test.com", "test", "testmdp", false, null, null, null, null);

        users.add(userTest);
        users.add(userTest1);
        users.add(userTest2);

        utilisateurRepository.save(users);

        Set<Utilisateur> users2 = new HashSet<Utilisateur>();
        users2.add(userTest1);



        Projet test  = administrationService.addUserToAccess(projet,users);
        Projet test2 = administrationService.removeUserToAccess(test,users2);
        assertThat(test2.getAcces().contains(userTest1)).isFalse();
    }

    /**
     * Test pour vérifier la mise à jour de la description du projet
     */
    @Test
    public void changeDescription() {

        String description = "description changed";
        Projet test = administrationService.changeDescription(projet,description);
        assertThat(test.getDescription()).isEqualTo(description);
    }

}

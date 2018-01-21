package org.miage.m2.forum.controller;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.miage.m2.forum.MainSpringApplication;
import org.miage.m2.forum.config.H2JpaConfig;
import org.miage.m2.forum.config.SecurityConfig;
import org.miage.m2.forum.modele.*;
import org.miage.m2.forum.query.*;
import org.miage.m2.forum.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MainSpringApplication.class, H2JpaConfig.class})
@ActiveProfiles("test")
@Import(SecurityConfig.class)
public class ProjectControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    ProjectController projectController;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RolesRepository rolesRepository;

    AccountService accountService = new AccountServiceImpl();

    TopicService topicService = new TopicServiceImpl();

    MessageService messageService = new MessageServiceImpl();

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private MessageRepository messageRepository;

    Roles roleUser;
    Roles roleAdmin;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ProjetRepository projetRepository;

    private ProjetService projetService = new ProjetServiceImpl();

    private AdministrationService administrationService = new AdministrationServiceImpl();


    Utilisateur testUser = new Utilisateur("test@test.com", "test", "testmdp", false, null, null, null, null);
    Utilisateur user = new Utilisateur("user@test.com", "user", "testmdp", false, null, null, null, null);
    Projet projetInvite = new Projet("invite", "invite ok", new Date(), true, new HashSet<Utilisateur>(), user, new HashSet<Topic>());
    Projet projetNotInvite = new Projet("not invite", "invite none", new Date(), false, new HashSet<Utilisateur>(), user, new HashSet<Topic>());
    Projet projetNotAccessTestUser = new Projet("not testUser", "testUser forbidden", new Date(), false, new HashSet<Utilisateur>(), user, new HashSet<Topic>());

    Topic topicInvite = new Topic("topic invite", new Date(), true, new HashSet<Message>(), projetInvite,
    new HashSet<Utilisateur>(), new HashSet<Utilisateur>(), user, new HashSet<Utilisateur>());

    Topic topicNotInvite = new Topic("topic with not invite", new Date(), false, new HashSet<Message>(), projetInvite,
            new HashSet<Utilisateur>(Arrays.asList(user, testUser)), new HashSet<Utilisateur>(Arrays.asList(user, testUser)), user, new HashSet<Utilisateur>());


    @Before
    public void setup() {

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

        projectController = new ProjectController();
        projectController.setUtilisateurRepository(utilisateurRepository);
        accountService.setUtilisateurRepository(utilisateurRepository);
        projectController.setAccountService(accountService);
        projectController.setProjetRepository(projetRepository);
        projetService.setProjetRepository(projetRepository);
        projectController.setProjetService(projetService);
        administrationService.setProjetRepository(projetRepository);
        topicService.setTopicRepository(topicRepository);
        projectController.setTopicService(topicService);
        messageService.setMessageRepository(messageRepository);
        projectController.setProjetService(projetService);
        projectController.setMessageService(messageService);
        projectController.setTopicRepository(topicRepository);
        projectController.setMessageRepository(messageRepository);

        user.addAdminRole();
        accountService.createUser(testUser);
        accountService.createUser(user);
        projetRepository.save(projetInvite);
        projetNotInvite.addUserAccess(new HashSet<Utilisateur>(Arrays.asList(user, testUser)));
        administrationService.createProject(projetNotInvite);
        projetNotAccessTestUser.addUserAccess(new HashSet<Utilisateur>(Arrays.asList(user)));
        administrationService.createProject(projetNotAccessTestUser);

        topicService.createTopic(topicInvite);
        topicService.createTopic(topicNotInvite);






        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");


        this.mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setViewResolvers(viewResolver)
                .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
                .build();

    }

    /**
     * tester si l'invite peut consulter un seul projet
     * @throws Exception
     */
    @Test
    public void testInviteProject() throws Exception{

        List<Projet> list = new ArrayList<Projet>();
        list.add(projetInvite);
        Projet projet = projetRepository.findOne("invite");

        this.mockMvc.perform(get("/").with(anonymous()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("projets"))
                .andExpect(model().attribute("projets", Matchers.hasSize(1)))
                .andExpect(view().name("index"));
    }

    /**
     * test si on va dans la page du projet invite et qu'il n'y a pas de sous projets
     * et qu'on a bien un topic
     * sans authentification
     * @throws Exception
     */
    @Test
    public void testInviteGetProject() throws Exception{

        this.mockMvc.perform(get("/projects/"+projetInvite.getTitre()+"/").with(anonymous()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("projets"))
                .andExpect(model().attribute("projets", Matchers.hasSize(0)))
                .andExpect(model().attribute("topics", Matchers.hasSize(1)))
                .andExpect(view().name("projects"));
    }

    /**
     * test si on va dans la page du projet invite et qu'il n'y a pas de sous projets
     * et qu'on a bien deux topic
     * avec authentification
     * @throws Exception
     */
    @Test
    public void testNoInviteGetProject() throws Exception{

        this.mockMvc.perform(get("/projects/"+projetInvite.getTitre()+"/").with(user(user.getEmail())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("projets"))
                .andExpect(model().attribute("projets", Matchers.hasSize(0)))
                .andExpect(model().attribute("topics", Matchers.hasSize(2)))
                .andExpect(view().name("projects"));
    }

    /**
     * test si un utilisateur peut consulter tous les projets qu'il a accès
     * @throws Exception
     */
    @Test
    public void testNotInviteProject() throws Exception{

        List<Projet> list = new ArrayList<Projet>();
        list.add(projetInvite);
        list.add(projetNotAccessTestUser);
        list.add(projetNotInvite);
        System.out.println("start test not invite project");
        this.mockMvc.perform(get("/").with(user(user.getEmail())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("projets"))
                .andExpect(model().attribute("projets",  Matchers.hasSize(3)))
                .andExpect(view().name("index"));
    }


    /**
     * test si on a bien 2 projets sur 3. L'utlisateur testuser n'a pas les acces pour un projet
     * @throws Exception
     */
    @Test
    public void testNotAccessTestUserProject() throws Exception{

        List<Projet> list = new ArrayList<Projet>();
        list.add(projetInvite);
        list.add(projetNotInvite);
        System.out.println("start test not invite project");
        this.mockMvc.perform(get("/").with(user(testUser.getEmail())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("projets"))
                .andExpect(model().attribute("projets",  Matchers.hasSize(2)))
                .andExpect(view().name("index"));
    }

    /**
     * test de redirection à la page index si le nom du projet n'existe pas
     * @throws Exception
     */
    @Test
    public void testRedirectIfNotProjectExist() throws Exception{

        List<Projet> list = new ArrayList<Projet>();
        list.add(projetInvite);
        Projet projet = projetRepository.findOne("invite");

        this.mockMvc.perform(get("/projects/moemvemo/").with(anonymous()))
                .andExpect(redirectedUrl("/"));
    }


    /**
     * Test creation de topic
     * on insere un topic, on verifie si on a bien un nouveau topic dans la liste
     * A la fin du test on supprime le topic pour pas fausser les autres tests
     * @throws Exception
     */
    @Test
    public void testCreateTopic() throws Exception{

        this.mockMvc.perform(post("/projects/"+projetInvite.getTitre()+"/").with(user(user.getEmail()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("projet",projetInvite.getTitre())
                .param("titre","bonjour et tout")
                .param("message","premier message du topic")
                .param("invite", "true")
        )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("projets"))
                .andExpect(model().attribute("projets", Matchers.hasSize(0)))
                .andExpect(model().attribute("topics", Matchers.hasSize(3)))
                .andExpect(view().name("projects"));
        //fin du test
        Topic t = topicRepository.findOne("bonjour et tout");
        for(Message m : t.getMessage()){
            messageRepository.delete(m);
        }
        topicRepository.delete("bonjour et tout");
    }

}

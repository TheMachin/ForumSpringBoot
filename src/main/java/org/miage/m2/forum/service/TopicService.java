package org.miage.m2.forum.service;

import org.miage.m2.forum.modele.Message;
import org.miage.m2.forum.modele.Topic;
import org.miage.m2.forum.modele.Utilisateur;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface TopicService {
    public Topic createTopic(Topic topic);
    public Topic findOne(String title);
    public Topic addMessageToTopic( Topic topic, Set<Message> message);
    public Topic removeMessageToTopic( Topic topic, Set<Message> message);
    public Topic changePermissionForVisitor(Topic topic, boolean invite);
    public Topic addUserToEcriture(Topic topic, Set<Utilisateur> ecriture);
    public Topic addUserToLecture(Topic topic, Set<Utilisateur> lecture);
    public Topic addUserToSuiveur(Topic topic, Set<Utilisateur> suiveur);
    public Topic removeUserToEcriture(Topic topic, Set<Utilisateur> ecriture);
    public Topic removeUserToLecture(Topic topic, Set<Utilisateur> lecture);
    public Topic removeUserToSuiveurs(Topic topic, Set<Utilisateur> suiveurs);
}

package org.miage.m2.forum.service;

import org.miage.m2.forum.modele.Message;
import org.miage.m2.forum.modele.Topic;
import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TopicServiceImpl implements TopicService{

    @Autowired
    private TopicRepository topicRepository;

    /**
     * Créer un Topic si l'utilisateur a accès au projet
     * @param topic
     * @return l'objet topic
     */
    public Topic createTopic(Topic topic) {

        if(!topic.getProjet().getAcces().contains(topic.getCreator())){
            System.out.println("Vous n'avez pas accès au projet");
            return null;
        }

        if(findOne(topic.getTitre())!=null){
            System.out.println("find existing topic");
            return null;
        }
        Topic topicFinal = topicRepository.save(topic);

        return topicFinal;
    }


    public Topic findOne(String title){
        return topicRepository.findOne(title);
    }


    public Topic addMessageToTopic(Topic topic, Set<Message> message) {

        if(topic==null){
            return null;
        }

        topic.addMessage(message);

        return topicRepository.save(topic);
    }

    @Override
    public Topic removeMessageToTopic(Topic topic, Set<Message> message) {
        return null;
    }

    @Override
    public Topic changePermissionForVisitor(Topic topic, boolean invite) {
        return null;
    }

    @Override
    public Topic addUserToEcriture(Topic topic, Set<Utilisateur> ecriture) {
        return null;
    }

    @Override
    public Topic addUserToLecture(Topic topic, Set<Utilisateur> lecture) {
        return null;
    }

    @Override
    public Topic addUserToSuiveurs(Topic topic, Set<Utilisateur> suiveurs) {
        return null;
    }

    @Override
    public Topic removeUserToEcriture(Topic topic, Set<Utilisateur> ecriture) {
        return null;
    }

    @Override
    public Topic removeUserToLecture(Topic topic, Set<Utilisateur> lecture) {
        return null;
    }

    @Override
    public Topic removeUserToSuiveurs(Topic topic, Set<Utilisateur> suiveurs) {
        return null;
    }

    @Override
    public Topic changeDescription(Topic topic, String description) {
        return null;
    }
}

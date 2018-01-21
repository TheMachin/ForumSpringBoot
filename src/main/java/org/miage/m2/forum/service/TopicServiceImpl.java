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

        if(topic.getProjet().getAcces().size() != 0){
            if(!topic.getProjet().getAcces().contains(topic.getCreator())){
                System.out.println("Vous n'avez pas accès au projet");
                return null;
            }
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

    @Override
    public Topic addMessageToTopic(Topic topic, Set<Message> message) {

        if(topic==null){
            return null;
        }

        topic.addMessage(message);

        return topicRepository.save(topic);
    }

    @Override
    public Topic removeMessageToTopic(Topic topic, Set<Message> message) {
        if(topic==null){
            return null;
        }

        topic.removeMessage(message);

        return topicRepository.save(topic);
    }

    @Override
    public Topic changePermissionForVisitor(Topic topic, boolean invite) {
        if(topic==null){
            return null;
        }

        topic.setInvite(invite);

        return topicRepository.save(topic);
    }

    @Override
    public Topic addUserToEcriture(Topic topic, Set<Utilisateur> ecriture) {
        if(topic==null){
            return null;
        }

        topic.addEcriture(ecriture);

        return topicRepository.save(topic);
    }

    @Override
    public Topic addUserToLecture(Topic topic, Set<Utilisateur> lecture) {
        if(topic==null){
            return null;
        }

        topic.addLecture(lecture);

        return topicRepository.save(topic);
    }

    @Override
    public Topic addUserToSuiveur(Topic topic, Set<Utilisateur> suiveur) {
        if(topic==null){
            return null;
        }

        topic.addEcriture(suiveur);

        return topicRepository.save(topic);
    }

    @Override
    public Topic removeUserToEcriture(Topic topic, Set<Utilisateur> ecriture) {
        if(topic==null){
            return null;
        }

        topic.removeEcriture(ecriture);

        return topicRepository.save(topic);
    }

    @Override
    public Topic removeUserToLecture(Topic topic, Set<Utilisateur> lecture) {
        if(topic==null){
            return null;
        }

        topic.removeLecture(lecture);

        return topicRepository.save(topic);
    }

    @Override
    public Topic removeUserToSuiveurs(Topic topic, Set<Utilisateur> suiveurs) {
        if(topic==null){
            return null;
        }

        topic.removeSuiveur(suiveurs);

        return topicRepository.save(topic);
    }

    private boolean save(Topic topic){
        Topic t = topicRepository.save(topic);
        if(t==null){
            return false;
        }
        return true;
    }

    public void setTopicRepository(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }
}

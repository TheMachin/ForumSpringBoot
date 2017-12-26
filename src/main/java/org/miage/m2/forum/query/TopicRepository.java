package org.miage.m2.forum.query;

import org.miage.m2.forum.modele.Topic;
import org.springframework.data.repository.CrudRepository;

@org.springframework.stereotype.Repository
public interface TopicRepository extends CrudRepository<Topic, String>{
}

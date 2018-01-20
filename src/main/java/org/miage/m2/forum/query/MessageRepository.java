package org.miage.m2.forum.query;

import org.miage.m2.forum.modele.Message;
import org.springframework.data.repository.CrudRepository;

@org.springframework.stereotype.Repository
public interface MessageRepository extends CrudRepository<Message, String> {
}

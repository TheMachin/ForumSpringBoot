package org.miage.m2.forum.service;

import org.miage.m2.forum.modele.Message;
import org.miage.m2.forum.query.MessageRepository;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {
    public Message createMessage(Message message);
    public void setMessageRepository(MessageRepository messageRepository);
}

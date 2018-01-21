package org.miage.m2.forum.formValidation;

import org.hibernate.validator.constraints.NotEmpty;

public class ModifyTopicForm {

    @NotEmpty
    private String topic;
    private boolean invite;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isInvite() {
        return invite;
    }

    public void setInvite(boolean invite) {
        this.invite = invite;
    }
}

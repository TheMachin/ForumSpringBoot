package org.miage.m2.forum.formValidation;

import org.hibernate.validator.constraints.NotEmpty;

public class ReadWriteForm {

    @NotEmpty
    private String topic;
    private String addReadUser;
    private String addWriteUser;
    private String deleteReadUser;
    private String deleteWriteUser;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getAddReadUser() {
        return addReadUser;
    }

    public void setAddReadUser(String addReadUser) {
        this.addReadUser = addReadUser;
    }

    public String getAddWriteUser() {
        return addWriteUser;
    }

    public void setAddWriteUser(String addWriteUser) {
        this.addWriteUser = addWriteUser;
    }

    public String getDeleteReadUser() {
        return deleteReadUser;
    }

    public void setDeleteReadUser(String deleteReadUser) {
        this.deleteReadUser = deleteReadUser;
    }

    public String getDeleteWriteUser() {
        return deleteWriteUser;
    }

    public void setDeleteWriteUser(String deleteWriteUser) {
        this.deleteWriteUser = deleteWriteUser;
    }
}

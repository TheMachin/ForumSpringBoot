package org.miage.m2.forum.formValidation;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import java.util.List;

public class TopicForm {


    @NotEmpty
    private String projet;
    @Pattern(regexp = "^[\\w -=@*+.:;<>()}{]*$")
    @NotEmpty
    private String titre;
    @NotEmpty
    private String message;
    private boolean invite;

    public String getProjet() {
        return projet;
    }

    public void setProjet(String projet) {
        this.projet = projet;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isInvite() {
        return invite;
    }

    public void setInvite(boolean invite) {
        this.invite = invite;
    }
}

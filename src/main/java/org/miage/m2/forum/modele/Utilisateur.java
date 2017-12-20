package org.miage.m2.forum.modele;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Utilisateur {

	@Id
	private String email;
	private String pseudo;
	private String mdp;
	private boolean admin;
	@OneToMany(mappedBy = "author")
	private Set<Message> message = new HashSet<Message>();
	@OneToMany(mappedBy = "creators")
	private Set<Projet> creators = new HashSet<Projet>();
	@OneToMany(mappedBy = "creator")
	private Set<Topic> listTopicCreate = new HashSet<Topic>();
	@ManyToMany
	@JoinTable(name = "suiveurs_topic",
			joinColumns = @JoinColumn(name = "utilisateur_id"),
			inverseJoinColumns = @JoinColumn(name = "topic_titre")
	)
	private Set<Topic> suivi = new HashSet<Topic>();

	@Autowired
	public Utilisateur(String email, String pseudo, String mdp, boolean admin, Set<Message> message, Set<Projet> creators, Set<Topic> listTopicCreate, Set<Topic> suivi) {
		this.email = email;
		this.pseudo = pseudo;
		this.mdp = mdp;
		this.admin = admin;
		this.message = message;
		this.creators = creators;
		this.listTopicCreate = listTopicCreate;
		this.suivi = suivi;
	}

	public Utilisateur() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public String getMdp() {
		return mdp;
	}

	public void setMdp(String mdp) {
		this.mdp = mdp;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public Set<Message> getMessage() {
		return message;
	}

	public void setMessage(Set<Message> message) {
		this.message = message;
	}

	public Set<Projet> getCreators() {
		return creators;
	}

	public void setCreators(Set<Projet> creators) {
		this.creators = creators;
	}


	public Set<Topic> getListTopicCreate() {
		return listTopicCreate;
	}

	public void setListTopicCreate(Set<Topic> creator) {
		this.listTopicCreate = creator;
	}

	public Set<Topic> getSuivi() {
		return suivi;
	}

	public void setSuivi(Set<Topic> suivi) {
		this.suivi = suivi;
	}
}
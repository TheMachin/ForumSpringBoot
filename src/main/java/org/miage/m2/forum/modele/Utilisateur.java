package org.miage.m2.forum.modele;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Utilisateur implements Serializable {

	@Id
	private String email;
	private String pseudo;
	private String mdp;
	private boolean admin;
	@OneToMany
	private Set<Message> message = new HashSet<Message>();
	@ManyToMany
	private Set<Projet> acces = new HashSet<Projet>();
	@OneToMany
	private Set<Projet> creators = new HashSet<Projet>();
	@ManyToMany
	private Set<Topic> lecture = new HashSet<Topic>();
	@ManyToMany
	private Set<Topic> ecriture = new HashSet<Topic>();
	@OneToMany
	private Set<Topic> creator = new HashSet<Topic>();
	@ManyToMany
	private Set<Topic> suivi = new HashSet<Topic>();

	public Utilisateur(String email, String pseudo, String mdp, boolean admin, Set<Message> message, Set<Projet> acces, Set<Projet> creators, Set<Topic> lecture, Set<Topic> ecriture, Set<Topic> creator, Set<Topic> suivi) {
		this.email = email;
		this.pseudo = pseudo;
		this.mdp = mdp;
		this.admin = admin;
		this.message = message;
		this.acces = acces;
		this.creators = creators;
		this.lecture = lecture;
		this.ecriture = ecriture;
		this.creator = creator;
		this.suivi = suivi;
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

	public Set<Projet> getAcces() {
		return acces;
	}

	public void setAcces(Set<Projet> acces) {
		this.acces = acces;
	}

	public Set<Projet> getCreators() {
		return creators;
	}

	public void setCreators(Set<Projet> creators) {
		this.creators = creators;
	}

	public Set<Topic> getLecture() {
		return lecture;
	}

	public void setLecture(Set<Topic> lecture) {
		this.lecture = lecture;
	}

	public Set<Topic> getEcriture() {
		return ecriture;
	}

	public void setEcriture(Set<Topic> ecriture) {
		this.ecriture = ecriture;
	}

	public Set<Topic> getCreator() {
		return creator;
	}

	public void setCreator(Set<Topic> creator) {
		this.creator = creator;
	}

	public Set<Topic> getSuivi() {
		return suivi;
	}

	public void setSuivi(Set<Topic> suivi) {
		this.suivi = suivi;
	}
}
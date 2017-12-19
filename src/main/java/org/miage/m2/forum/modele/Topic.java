package org.miage.m2.forum.modele;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Topic implements Serializable {

	@Id
	private String titre;
	private Date dateCreation;
	private boolean invite;
	@OneToMany
	private Set<Message> message = new HashSet<Message>();
	@ManyToOne
	private Projet projet;
	@ManyToMany
	private Set<Utilisateur> lecture = new HashSet<Utilisateur>();
	@ManyToMany
	private Set<Utilisateur> ecriture = new HashSet<Utilisateur>();
	@ManyToOne
	private Utilisateur creator;
	@ManyToMany
	private Set<Utilisateur> suivi = new HashSet<Utilisateur>();


	public Topic(String titre, Date dateCreation, boolean invite, Set<Message> message, Projet projet, Set<Utilisateur> lecture, Set<Utilisateur> ecriture, Utilisateur creator, Set<Utilisateur> suivi) {
		this.titre = titre;
		this.dateCreation = dateCreation;
		this.invite = invite;
		this.message = message;
		this.projet = projet;
		this.lecture = lecture;
		this.ecriture = ecriture;
		this.creator = creator;
		this.suivi = suivi;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public boolean isInvite() {
		return invite;
	}

	public void setInvite(boolean invite) {
		this.invite = invite;
	}

	public Set<Message> getMessage() {
		return message;
	}

	public void setMessage(Set<Message> message) {
		this.message = message;
	}

	public Projet getProjet() {
		return projet;
	}

	public void setProjet(Projet projet) {
		this.projet = projet;
	}

	public Set<Utilisateur> getLecture() {
		return lecture;
	}

	public void setLecture(Set<Utilisateur> lecture) {
		this.lecture = lecture;
	}

	public Set<Utilisateur> getEcriture() {
		return ecriture;
	}

	public void setEcriture(Set<Utilisateur> ecriture) {
		this.ecriture = ecriture;
	}

	public Utilisateur getCreator() {
		return creator;
	}

	public void setCreator(Utilisateur creator) {
		this.creator = creator;
	}

	public Set<Utilisateur> getSuivi() {
		return suivi;
	}

	public void setSuivi(Set<Utilisateur> suivi) {
		this.suivi = suivi;
	}
}
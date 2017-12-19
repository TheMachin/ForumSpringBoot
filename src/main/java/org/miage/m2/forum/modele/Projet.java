package org.miage.m2.forum.modele;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Projet {

	@Id
	private String titre;
	private String description;
	private Date dateCreation;
	private boolean invite;
	@ManyToMany
	private Set<Utilisateur> acces = new HashSet<Utilisateur>();
	@ManyToOne
	private Utilisateur creators;
	@OneToMany
	private Set<Topic> acteur = new HashSet<Topic>();

	public Projet(String titre, String description, Date dateCreation, boolean invite, Set<Utilisateur> acces, Utilisateur creator, Set<Topic> acteur) {
	}

	public Projet() {
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Set<Utilisateur> getAcces() {
		return acces;
	}

	public void setAcces(Set<Utilisateur> acces) {
		this.acces = acces;
	}

	public Utilisateur getCreators() {
		return creators;
	}

	public void setCreators(Utilisateur creators) {
		this.creators = creators;
	}

	public Set<Topic> getActeur() {
		return acteur;
	}

	public void setActeur(Set<Topic> acteur) {
		this.acteur = acteur;
	}
}
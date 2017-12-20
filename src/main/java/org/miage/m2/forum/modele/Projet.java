package org.miage.m2.forum.modele;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Projet {

	@Id
	@NotNull
	private String titre;
	@NotNull
	private String description;
	private Date dateCreation;
	private boolean invite;
	@ManyToMany
	private Set<Utilisateur> acces = new HashSet<Utilisateur>();
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CREATOR_ID")
	@NotNull
	private Utilisateur creators;
	@OneToMany(mappedBy = "projet")
	private Set<Topic> topics = new HashSet<Topic>();
	@OneToMany
	@JoinTable(name = "Sous_projet",
			joinColumns = @JoinColumn(name = "Projet_titre"),
			inverseJoinColumns = @JoinColumn(name = "Sous_projet_titre")
	)
	private Set<Projet> sousProjet = new HashSet<Projet>();

	public Projet(String titre, String description, Date dateCreation, boolean invite, Set<Utilisateur> acces, Utilisateur creator, Set<Topic> topics) {
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

	public Set<Topic> getTopics() {
		return topics;
	}

	public void setTopics(Set<Topic> acteur) {
		this.topics = acteur;
	}
}
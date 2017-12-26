package org.miage.m2.forum.modele;

import org.springframework.beans.factory.annotation.Autowired;

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

	@Autowired
	public Projet(String titre, String description, Date dateCreation, boolean invite, Set<Utilisateur> acces,
				  Utilisateur creators, Set<Topic> topics) {
		this.titre = titre;
		this.description = description;
		this.dateCreation = dateCreation;
		this.invite = invite;
		this.acces = acces;
		this.creators = creators;
		this.topics = topics;
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

    public Set<Projet> getSousProjet() {
        return sousProjet;
    }

    public void setSousProjet(Set<Projet> sousProjet) {
        this.sousProjet = sousProjet;
    }

    /**
     * On ajoute un projet à un projet
     * On vérifie son existance à partir du nom du projet
     * @param projet
     * @return succes du traitement
     */
	public boolean addSousProjet(Projet projet){
		for(Projet p : this.sousProjet){
		    if(p.getTitre().equals(projet.getTitre())){
		        return false;
            }
        }
		this.sousProjet.add(projet);
		return true;
	}

    /**
     * Ajoute des utilisateurs pour accéder au projet
     * @param users
     */
	public void addUserAccess(Set<Utilisateur> users){
	    if(acces==null){
	        acces = new HashSet<Utilisateur>();
        }
	    if(acces.isEmpty()){
	        this.setAcces(users);
	        return;
        }
	    for(Utilisateur u : users){
	        if(!acces.contains(u)){
	            acces.add(u);
            }
        }
    }

    /**
     * Supprime l'acces des utilisateurs au projet
     * @param users
     */
    public void removeUserAccess(Set<Utilisateur> users){
        for(Utilisateur u : users){
            if(!acces.contains(u)){
                acces.remove(u);
            }
        }
    }

}
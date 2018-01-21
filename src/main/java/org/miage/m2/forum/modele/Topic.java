package org.miage.m2.forum.modele;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Topic {

	@Id
	private String titre;
	private Date dateCreation;
	private boolean invite;
	@OneToMany(mappedBy = "topic", fetch = FetchType.EAGER)
	private Set<Message> message = new HashSet<Message>();
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="TOPIC_ID")
	private Projet projet;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "lecture_topic",
			joinColumns = @JoinColumn(name = "topic_titre"),
			inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
	)
	private Set<Utilisateur> lecture = new HashSet<Utilisateur>();
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "ecriture_topic",
			joinColumns = @JoinColumn(name = "topic_titre"),
			inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
	)
	private Set<Utilisateur> ecriture = new HashSet<Utilisateur>();
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="USER_CREATOR_ID")
	private Utilisateur creator;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "suiveurs_topic",
			joinColumns = @JoinColumn(name = "topic_titre"),
			inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
	)
	private Set<Utilisateur> suiveurs = new HashSet<Utilisateur>();


	public Topic(String titre, Date dateCreation, boolean invite, Set<Message> message, Projet projet,
                 Set<Utilisateur> lecture, Set<Utilisateur> ecriture, Utilisateur creator, Set<Utilisateur> suiveurs) {
		this.titre = titre;
		this.dateCreation = dateCreation;
		this.invite = invite;
		this.message = message;
		this.projet = projet;
		this.lecture = lecture;
		this.ecriture = ecriture;
		this.creator = creator;
		this.suiveurs = suiveurs;
	}

	public Topic() {
	}

    /**
     * Ajoute des messages au topic
     * @param messages
     */
    public void addMessage(Set<Message> messages){
		if(message==null){
			message = new HashSet<Message>();
		}
        if(message.isEmpty()){
            this.setMessage(messages);
            return;
        }
        for(Message m : messages){
            if(!message.contains(m)){
                message.add(m);
            }
        }
    }

	/**
	 * Suprimer des messages du topic
	 * @param messages
	 */
	public void removeMessage(Set<Message> messages){
		for(Message m : messages){
			if(message.contains(m)){
				message.remove(m);
			}
		}
	}

	/**
	 * ajouter accès en écriture
	 * @param users
	 */
	public void addEcriture(Set<Utilisateur> users){
		if(ecriture==null){
			ecriture = new HashSet<Utilisateur>();
		}
		if(ecriture.isEmpty()){
			this.setEcriture(users);
			return;
		}
		for(Utilisateur u : users){
			if(!ecriture.contains(u)){
				ecriture.add(u);
			}
		}
	}

	/**
	 * retirer accès en écriture
	 * @param users
	 */
	public void removeEcriture(Set<Utilisateur> users){
		for(Utilisateur u : users){
			if(ecriture.contains(u)){
				ecriture.remove(u);
			}
		}
	}

	/**
	 * ajouter accès en lecture
	 * @param users
	 */
	public void addLecture(Set<Utilisateur> users){
		if(lecture==null){
			lecture = new HashSet<Utilisateur>();
		}
		if(lecture.isEmpty()){
			this.setLecture(users);
			return;
		}
		for(Utilisateur u : users){
			if(!lecture.contains(u)){
				lecture.add(u);
			}
		}
	}

	/**
	 * retirer accès en lecture
	 * @param users
	 */
	public void removeLecture(Set<Utilisateur> users){
		for(Utilisateur u : users){
			if(lecture.contains(u)){
				lecture.remove(u);
			}
		}
	}

	/**
	 * ajouter des suiveurs au topic
	 * @param users
	 */
	public void addSuiveur(Set<Utilisateur> users){
		if(suiveurs==null){
			suiveurs = new HashSet<Utilisateur>();
		}
		if(suiveurs.isEmpty()){
			this.setSuiveurs(users);
			return;
		}
		for(Utilisateur u : users){
			if(!suiveurs.contains(u)){
				suiveurs.add(u);
			}
		}
	}

	/**
	 * retirer des suiveurs du topic
	 * @param users
	 */
	public void removeSuiveur(Set<Utilisateur> users){
		for(Utilisateur u : users){
			if(suiveurs.contains(u)){
				suiveurs.remove(u);
			}
		}
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

	public Set<Utilisateur> getSuiveurs() {
		return suiveurs;
	}

	public void setSuiveurs(Set<Utilisateur> suivi) {
		this.suiveurs = suivi;
	}
}
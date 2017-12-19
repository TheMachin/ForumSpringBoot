package org.miage.m2.forum.modele;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String message;
	private Date date;
	@ManyToOne
	private Utilisateur utilisateur;
	@ManyToOne
	private Topic topic;

	public Message(String message, Date date, Utilisateur utilisateur, Topic topic) {
		this.message = message;
		this.date = date;
		this.utilisateur = utilisateur;
		this.topic = topic;
	}

	public Message() {
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Utilisateur getUtilisateur() {
		return utilisateur;
	}

	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}
}
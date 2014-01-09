package com.gamejam.artag.model;

import java.util.UUID;

public class Player {

	private UUID ID;
	private String Name;
	private int HP;
	
	public Player(UUID id, String name) {
		this.ID = id;
		this.Name = name;
	}
	
	public UUID getID() {
		return ID;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public int getHP() {
		return HP;
	}
	public void setHP(int hP) {
		HP = hP;
	}

}

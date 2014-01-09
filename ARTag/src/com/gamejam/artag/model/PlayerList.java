package com.gamejam.artag.model;

import java.util.ArrayList;

public class PlayerList {

	private static PlayerList sPlayerList;
	private ArrayList<Player> mPlayers;
	
	private PlayerList() {
		mPlayers = new ArrayList<Player>();
	}
	
	public static PlayerList getInstance() {
		if(sPlayerList == null) {
			sPlayerList = new PlayerList();
		}
		
		return sPlayerList;
	}
	
	public ArrayList<Player> getPlayers() {
		return mPlayers;
	}
	
	public void addPlayer(Player p) {
		mPlayers.add(p);
	}
	
	public void deletePlayer(Player p) {
		mPlayers.remove(p);
	}
	
}

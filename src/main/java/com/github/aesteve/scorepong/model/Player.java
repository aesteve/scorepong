package com.github.aesteve.scorepong.model;

import org.boon.json.annotations.JsonInclude;

import io.vertx.core.json.JsonObject;

public class Player {
	
	public String name;
	public int nbPlayed;
	public int nbWin;
	public int nbLoss;
	public int nbPointsWon;
	public int nbPointsLost;
	public double winRatio;
	public double pointRatio;
	
	public Player(String name) {
		this.name = name;
	}

	@JsonInclude
	public double getWinRatio() {
		if (nbPlayed == 0) {
			return 0;
		}
		return (float)nbWin / nbPlayed * 100;
	}
	
	@JsonInclude
	public double getPointRatio() {
		int totalPoints = nbPointsWon + nbPointsLost;
		if (totalPoints == 0) {
			return 0;
		}
		return (float)nbPointsWon / totalPoints * 100;
	}
	
	public Player update(JsonObject match) {
		nbPlayed++;
		if (name.equals(match.getString("winner"))) {
			nbWin++;
		} else {
			nbLoss++;
		}
		int playerIdx = name.equals(match.getString("player1")) ? 1 : 2;
		int otherIdx = playerIdx == 1 ? 2 : 1;
		nbPointsWon += match.getInteger("scorePlayer"+playerIdx);
		nbPointsLost += match.getInteger("scorePlayer"+otherIdx);
		winRatio = getWinRatio();
		pointRatio = getPointRatio();
		return this;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof Player)) {
			return false;
		}
		Player otherPlayer = (Player)other;
		return this.name.equals(otherPlayer.name);
	}
}

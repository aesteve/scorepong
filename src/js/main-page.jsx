'use strict';

import React from 'react';
import actions from './actions/matches';
import gameStore from './stores/matches';
import playerStore from './stores/players';
import GameList from './game-list.jsx';
import PlayerRankings from './player-rankings.jsx';

class MainPage extends React.Component {
	
	constructor(props) {
		super(props);
		this.state = {
            games:gameStore.getGames(),
            players:playerStore.getPlayers()
        };
	}
	
	componentDidMount() {
		gameStore.listen(this.matchesChanged.bind(this));
        playerStore.listen(this.playersChanged.bind(this));
		actions.fetchGames();
        actions.fetchPlayers();
	}
	
	matchesChanged() {
		this.setState({
			games:gameStore.getGames()
		});
	}

    playersChanged() {
		this.setState({
			players:playerStore.getPlayers()
		});
	}
    
	render() {
		return (
			<div className="main-wrapper">
				<h1>Welcome to ScorePong</h1>
				<div className="table">
					<div className="table-row">
						<div className="table-cell">
							<input className="player-input" id="player1" placeholder="Player1" /><br/>
							<input className="player-input" id="player2" placeholder="Player2" /><br/>
							<button className="player-input" onClick={this.createNewGame}>New game</button>
						</div>
						<div className="table-cell">
                            <PlayerRankings players={this.state.players} />
						</div>
					</div>
				</div>
				<h3>Past games</h3>
				<GameList games={this.state.games} />
			</div>
		);
	}
	
	createNewGame() {
		var defaultGame = {
			player1:document.querySelector("#player1").value, // jshint ignore:line
			player2:document.querySelector("#player2").value  // jshint ignore:line
		};
		actions.createGame(defaultGame);
	}
}

React.render(<MainPage />, document.querySelector(".content"));// jshint ignore:line
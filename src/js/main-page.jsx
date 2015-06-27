'use strict';

import React from 'react';
import actions from './actions/matches';
import store from './stores/matches';
import GameList from './game-list.jsx';


class MainPage extends React.Component {
	
	constructor(props) {
		super(props);
		this.state = {games:store.getGames()};
	}
	
	componentDidMount() {
		store.listen(this.matchesChanged.bind(this));
		actions.fetchGames();
	}
	
	matchesChanged() {
		this.setState({
			games:store.getGames()
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
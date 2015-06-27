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
				<button className="" onClick={this.createNewGame}>New game</button>
				<h3>Past games</h3>
				<GameList games={this.state.games} />
			</div>
		);
	}
	
	createNewGame() {
		var defaultGame = {
			player1:"Player 1",
			player2:"Player 2"
		};
		actions.createGame(defaultGame);
	}
}

React.render(<MainPage />, document.querySelector(".content"));// jshint ignore:line
'use strict';

import React from 'react';
import store from './stores/match';
import Actions from './actions/matches';
import DateFormat from './date-format.jsx';
import GameInfos from './game-infos.jsx';

class Match extends React.Component {
	
	constructor(props) {
		super(props);
		const path = document.location.pathname; // jshint ignore:line
		this.gameId = path.substring(path.lastIndexOf('/') + 1, path.length);
		this.state = {
			game:store.getGame()
		};
	}
	
	componentDidMount() {
		store.listen(this.matchChanged.bind(this));
		Actions.connect(this.gameId);
		document.addEventListener('keyup', this.keyListener); // jshint ignore:line
	}
	
	keyListener(e) {
		const key = e.keyCode || e.which;
		if (key === 37) {
			Actions.scorePoint(1);
		} else if (key === 39) {
			Actions.scorePoint(2);
	    }
	}
	
	componentWillUnmount() {
		document.removeEventListener('keyup'); // jshint ignore:line
	}
	
	matchChanged() {
		this.setState({
			game:store.getGame()
		});
	}
	
	render() {
		const game = this.state.game;
		let msg, msgStyle;
		msgStyle = "full-width alert-box radius game-msg ";
		const winner = game.get('winner');
		const winnerId = game.get('winnerId');
		const matchPoint = this.isMatchPoint();
		if (winner) {
			msg = winner + " wins !";
			msgStyle += "success";
		} else if (matchPoint){
			msg = 'Match point : ' + matchPoint;
			msgStyle += "info";
		}
		else if (this.isTossPoint()) {
			msg = 'Toss point';
			msgStyle += "info";
		}
		const scorePlayer2 = game.get('scorePlayer2') || '0';
		const scorePlayer1 = game.get('scorePlayer1') || '0';
		const defaultStyle = "table-cell score center valign-center score-wrapper ";
		const player1Style = defaultStyle + this.getPlayerStyle(1);
		const player2Style = defaultStyle + this.getPlayerStyle(2);
		return (
			<div>
				<div data-alert className={msgStyle}>
					{msg}
				</div>
				<div className="table scoreboard full-width">

					<GameInfos game={game} />
					<div className="table-row player-scores">
						<div className={player1Style}>
							{scorePlayer1}
						</div>
						<div className="table-cell center valign-center score-separator">-</div>
						<div className={player2Style}>
							{scorePlayer2}
						</div>
					</div>
					<div className="table-row player-arrows">
						<div className="table-cell center valign-center arrow-wrapper">
							<img className="arrow-key" src="/assets/img/key-left.png" />
						</div>
						<div className="table-cell center valign-center score-separator"></div>
						<div className="table-cell center valign-center arrow-wrapper">
							<img className="arrow-key" src="/assets/img/key-right.png" />
						</div>
					</div>
				</div>
			</div>
		);
	}
	
	getPlayerStyle(i) {
		const game = this.state.game;
		const winnerId = game.get('winnerId');
		if (winnerId) {
			return winnerId === i ? 'alert-box success' : '';
		}
		const total = game.get('scorePlayer1') + game.get('scorePlayer2');
		const isTossServing = (Math.floor(total / 5) % 2) === 0;
		const isToss = game.get('toss') === i;
		if (isToss) {
			return isTossServing ? 'serving' : '';
		} else {
			return isTossServing ? '' : 'serving';
		}
	}
	
	isTossPoint() {
		const game = this.state.game;
		return game.get('startDate') && !game.get('toss');
	}
	
	isMatchPoint() {
		const game = this.state.game;
		const score1 = game.get('scorePlayer1');
		const score2 = game.get('scorePlayer2');
		if (score1 >= 20 && score1 > score2) {
			return game.get('player1');
		} else if (score2 >= 20 && score2 > score1) {
			return game.get('player2');
		}
	}
}

React.render(<Match />, document.querySelector(".content")); // jshint ignore:line
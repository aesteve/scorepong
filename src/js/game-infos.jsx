'use strict';

import React from 'react';
import moment from 'moment';
import Actions from './actions/matches';

class GameDuration extends React.Component {
	
	constructor(props) {
		super(props);
		let now = moment();
		const endDate = this.props.game.get('endDate');
		if (this.props.game && endDate) {
			now = moment(endDate);
		}
		this.state = {
			duration:this.calcDuration(now)
		};
	}
	
	componentDidMount() {
		this.timerId = setInterval(this.updateDuration.bind(this), 1000); // jshint ignore:line
	}
	
	updateDuration() {
		const game = this.props.game;
		if (game && game.get('endDate')) {
			this.setState({
				duration:this.calcDuration(moment(game.get('endDate')))
			});
			clearInterval(this.timerId); // jshint ignore:line
			return;
		}
		this.setState({
			duration:this.calcDuration(moment())
		});
	}
	
	calcDuration(now) {
		const game = this.props.game;
		const start = moment(game.get('startDate'));
		return now.diff(start, 'minutes') + ' : ' + now.diff(start, 'seconds') % 60 + "''";
	}

	componentWillUnmount() {
		if (this.timerId) {
			clearInterval(this.timerId); // jshint ignore:line
		}
	}
	
	render() {
		return (
			<span className="game-duration">{this.state.duration}</span>
		);
	}
}

class GameInfos extends React.Component {
	
	render() {
		const game = this.props.game;
		return (
			<div className="table-row center valign-center player-names">
				<div className="table-cell max-cell player-name">{game.get('player1')}</div>
				<div className="table-cell score-separator">
				{game.get('startDate') && 
					<GameDuration game={game} />
				}
				{!game.get('startDate') && 
					<button onClick={this.startGame.bind(this)} className="button radius success">Start game</button>
				}
				</div>
				<div className="table-cell max-cell player-name">{game.get('player2')}</div>
			</div>

		);
	}
	
	startGame() {
		Actions.startGame(this.props.game.get('_id'));
	}
	
}

export default GameInfos;
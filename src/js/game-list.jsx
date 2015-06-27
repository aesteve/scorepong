'use strict';

import React from 'react';
import moment from 'moment';
import DateFormat from './date-format.jsx';


class GameList extends React.Component {
	
	render() {
		var games = this.props.games.map(game => {
			let gameDuration;
			const start = game.get('startDate');
			const end = game.get('endDate');
			if (start && end) {
				gameDuration = moment(end).diff(moment(start));
			}
			return (
				<tr className="pointer" onClick={this.displayGame.bind(this, game)}>
					<td><DateFormat date={game.get('startDate')} /></td>
					<td>{game.get('player1')}</td>
					<td>{game.get('player2')}</td>
					<td>{game.get('scorePlayer1')} - {game.get('scorePlayer2')}</td>
					<td>{gameDuration}</td>
					<td>{game.get('winner')}</td>
				</tr>
			);
		}).toJS();
		return (
			<table className=" full-width past-games">
				<thead>
					<tr>
						<th>Date</th>
						<th>Player 1</th>
						<th>Player 2</th>
						<th>Score</th>
						<th>Duration</th>
						<th>Winner</th>
					</tr>
				</thead>
				<tbody>
					{games}
				</tbody>
			</table>
		);
	}
	
	displayGame(game) {
		document.location = "/match/" + game.get('_id');// jshint ignore:line
	}
}

export default GameList;
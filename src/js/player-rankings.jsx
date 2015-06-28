'use strict';

import React from 'react';

class PlayerRankings extends React.Component {
	
	constructor(props) {
		super(props);
		this.state = {
			filter:'nbWin'
		};
	}
	
	render() {
		var players = null;
		if (this.props.players) {
			players = this.sortPlayers().map( player => {
				let winRatio = player.get('winRatio');
				let pointRatio = player.get('pointRatio');
				if (winRatio) {
					winRatio = Math.round(winRatio * 100) / 100;
				} else {
					winRatio = 0;
				}
				if (pointRatio) {
					pointRatio = Math.round(pointRatio * 100) / 100;
				} else {
					pointRatio = 0;
				}
				return (
					<tr>
						<td>{player.get('name')}</td>
						<td className="align-right">{player.get('nbWin')}</td>
						<td className="align-right">{player.get('nbLoss')}</td>
						<td className="align-right">{player.get('nbPlayed')}</td>
						<td className="align-right">{winRatio} %</td>
						<td className="align-right">{player.get('nbPointsWon')}</td>
						<td className="align-right">{player.get('nbPointsLost')}</td>
						<td className="align-right">{pointRatio} %</td>
					</tr>
				);
			}).toJS();
		}
		return (
			<table className="player-rankings">
				<thead>
					<tr>
						<th className={this.getClassName('name')} onClick={this.filterBy.bind(this, 'name')}>Name</th>
						<th className={this.getClassName('nbWin')} onClick={this.filterBy.bind(this, 'nbWin')}>Wins</th>
						<th className={this.getClassName('nbLoss')} onClick={this.filterBy.bind(this, 'nbLoss')}>Loss</th>
						<th className={this.getClassName('nbPlayed')} onClick={this.filterBy.bind(this, 'nbPlayed')}>Total played</th>
						<th className={this.getClassName('winRatio')} onClick={this.filterBy.bind(this, 'winRatio')}>W/L ratio</th>
						<th className={this.getClassName('nbPointsWon')} onClick={this.filterBy.bind(this, 'nbPointsWon')}>PointsWon</th>
						<th className={this.getClassName('nbPointsLoss')} onClick={this.filterBy.bind(this, 'nbPointsLost')}>PointsLoss</th>
						<th className={this.getClassName('pointRatio')} onClick={this.filterBy.bind(this, 'pointRatio')}>Points ratio</th>
					</tr>
				</thead>
				<tbody>
				{players}
				</tbody>
			</table>
		);
	}
	
	getClassName(field) {
		if(this.state.filter === field) {
			return 'underline';
		}
		return 'pointer';
	}
	
	filterBy(field) {
		this.setState({
			filter:field
		});
	}
	
	sortPlayers() {
		const field = this.state.filter;
		return this.props.players.sort( (player1, player2) => {
			console.log("compare");
			console.log(player1.get(field));
			console.log(player2.get(field));
			return player1.get(field) > player2.get(field) ? -1 : 1;
		});
	}
}

export default PlayerRankings;
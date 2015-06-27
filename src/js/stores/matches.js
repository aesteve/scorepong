'use strict';

import Immutable from 'immutable';
import Reflux from 'reflux';
import actions from '../actions/matches';

var Store = Reflux.createStore({
	
	init() {
		this.data = Immutable.Map({});
		this.listenToMany(actions);
	},
	
	onCreateGameCompleted(result) {
		this.data = this.data.push(Immutable.fromJS(result));
		this.trigger();
	},
	
	onFetchGamesCompleted(result) {
		this.data = Immutable.fromJS(result);
		this.trigger();
	},
	
	getGames() {
		return this.data;
	}
	
});

export default Store;
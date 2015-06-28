'use strict';

import Immutable from 'immutable';
import Reflux from 'reflux';
import actions from '../actions/matches';

var Store = Reflux.createStore({
	
	init() {
		this.data = Immutable.Map({});
		this.listenToMany(actions);
	},
	
	onFetchPlayersCompleted(result) {
		this.data = Immutable.fromJS(result);
		this.trigger();
	},
	
	getPlayers() {
		return this.data;
	}
});

export default Store;
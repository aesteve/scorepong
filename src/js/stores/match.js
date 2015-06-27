'use strict';

import Immutable from 'immutable';
import Reflux from 'reflux';
import actions from '../actions/matches';

var Store = Reflux.createStore({
	
	init() {
		this.data = Immutable.Map({});
		this.listenToMany(actions);
	},
	
	onFetchGameCompleted(result) {
		this.data = Immutable.fromJS(result);
		this.trigger();
	},
	
	onMsgReceived(data) {
		this.data = Immutable.fromJS(data);
		this.trigger();
	},

	
	getGame() {
		return this.data;
	}
});

export default Store;
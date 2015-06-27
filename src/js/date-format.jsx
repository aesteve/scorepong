'use strict';

import React from 'react';
import moment from 'moment';

class DateFormat extends React.Component {
	render() {
		let date = '';
		if (this.props.date) {
			date = moment(this.props.date).format('DD/MM/YYYY HH:mm');
		}
		return (
			<span className="date">{date}</span>
		);
	}
}

export default DateFormat;
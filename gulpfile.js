'use strict';

var gulp = require('gulp');
var gutil = require('gulp-util');
var rename = require('gulp-rename');
var browserify = require('browserify');
var watchify = require('watchify');
var babelify = require('babelify');
var source = require('vinyl-source-stream');
var es = require('event-stream');


var requireFiles = './node_modules/react/react.js';


function compileScripts(reload) {
    gutil.log('Starting browserify');

    var files = [
    	'./src/js/main-page.jsx',
    	'./src/js/match-page.jsx'
    ];

    var tasks = files.map(function(entry) {
       	var startName = entry.lastIndexOf('/') + 1;
    	var endName = entry.lastIndexOf('-page.jsx');
    	var newName = entry.substring(startName, endName);
    
	    var bundler;
	    if (reload) {
	        bundler = watchify;
	    } else {
	        bundler = browserify;
	    }
	
        return bundler({ entries: [entry] })
        	.transform(babelify)
            .bundle()
            .pipe(source(entry))
            .pipe(rename({
            	dirname:'./',
            	basename:newName,
                extname: '.min.js'
            }))
            .pipe(gulp.dest('./web/assets/js'));
    });
	return es.merge.apply(null, tasks);
}


gulp.task('compile', function () {
	compileScripts();
});

gulp.task('dev', function () {
	compileScripts(true);
});
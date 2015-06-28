# ScorePong
Scoreboard for ping-pong games

## Presentation

ScorePong is a simple scoreboard to manage and store ping-pong games played between friends.

It's been designed to be easy to use. Simply create a new game with the names of the two players, then start the game.

From now on, if player 1 scores, hit the left arrow key on your keyboard, if player 2 scores, well... Hit the right arrow key on your keyboard.

You're allowed to mess up counting the points, don't worry, everybody does mistakes... If it ever happens, simply hit CTRL+Z as many times as you made mistakes, and the score will go back in time.

The current version only supports 21 points games with 5 services for each player.

The player currently serving is highlighted.

The game will stop by itself once a player has scored at least 21 points and leads by at least 2 points.

Creating different types of games (11 points / 2 services) is in progress.

## How to run it

If you just want to test it locally, without creating a whole database etc. Just clone the project and run : 

```
gradlew start test
```

And point your browser to http://localhost:9000

If you already have a MongoDB database running somewhere, the edit `conf.json` to point at your MongoDB database and run :

```
gradlew start
```

If you want to build the project, just run : 

```
gradlew shadowJar
```

which will create a runnable jar. To run this jar just run : 

```
java -jar my_jar.jar -conf conf.json
```

And you're done.


## Technical details

ScorePong relies on : 

* MongoDB for storing past games and player infos
* Vert.x 3 for the server-side : 
  * REST APIs
  * Websocket handling
  * Communicating with MongoDB
* Nubes : an annotation-layer on top of Vert.x (DSL kindof) to makes things easier to read/write
* React / ES6 for front-end stuff
* Foundation as a CSS framework

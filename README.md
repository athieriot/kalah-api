[![Build Status](https://travis-ci.org/athieriot/kalah-api.svg?branch=master)](https://travis-ci.org/athieriot/kalah-api) [![Coverage Status](https://coveralls.io/repos/github/athieriot/kalah-api/badge.svg?branch=master)](https://coveralls.io/github/athieriot/kalah-api?branch=master)

# Kalah API

API service to play Kalah game

Have a play online: [https://kalah-api.herokuapp.com](https://kalah-api.herokuapp.com)

## Build

    mvn clean install
    
    docker build -t kalah-api .

## Run

    mvn clean spring-boot:run

OR

    docker run -d -p 8080:8080 --name kalah-api kalah-api

## Deploy

    git push heroku master

## Usage

Documentation URL: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Start a new game

This will create a new Kalah(6, 6) game and pick a first player randomly 

    curl -X POST "http://localhost:8080/game" | jq
    
```
{
  "id": "129c74ec-8d7a-48de-9e61-56ee0d243ded",
  "board": [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0],
  "playerTurn": 2,
  "gameOver": false,
  "history": [],
  "scores": {
    "1": 0,
    "2": 0
  } 
```
    
### Make a move
     
It's player's 2 turn so he starts and chose to sow the fifth house of his zone.
 
See the last part of the path: `/play/2/5`. It's for player 2, house 5 (Choice from 1 to 6)

    curl -X POST "http://localhost:8080/game/77903be2-dfbb-4550-94d2-2526fc71fbc6/play/2/5"
    
```
{
  "id": "77903be2-dfbb-4550-94d2-2526fc71fbc6",
  "board": [7, 7, 7, 7, 6, 6, 0, 6, 6, 6, 6, 0, 7, 1],
  "playerTurn": 1,
  "gameOver": false,
  "history": [
    [1, 2, 5]
  ],
  "scores": {
    "1": 0,
    "2": 1
  }
}
```

### Simply access game details

    curl -X GET "http://localhost:8080/game/77903be2-dfbb-4550-94d2-2526fc71fbc6"
    
### Representation
    
The board is a list of 14 integer and are meant to be represented like so:

```
   <--- North
12 11 10  9  8  7
13              6
 0  1  2  3  4  5
   South --->
```

 
Where:

- 6 is the Player 1 store
- 13 is the Player 2 score
- The South zone is from 0 to 5
- The North zone from 7 to 12

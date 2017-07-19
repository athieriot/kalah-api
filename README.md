[![Build Status](https://travis-ci.org/athieriot/kalah-api.svg?branch=master)](https://travis-ci.org/athieriot/kalah-api)

# Kalah API

API service to play Kalah game

## Build

    mvn clean install
    
    docker build -t kalah-api .

## Run

    mvn clean spring-boot:run

OR
    docker run -d -p 8080:8080 --name kalah-api kalah-api

## Deploy

## Usage

Documentation URL: [http://localhost:8080/swagger-ui.html]()

### Start a new game

This will create a new Kalah(6, 6) game and pick a first player randomly 

    curl -X POST \
        --header "Content-Type: application/json" \
        "http://localhost:8080/game" | jq
    
```
{
  "id": "129c74ec-8d7a-48de-9e61-56ee0d243ded",
  "board": [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0],
  "playerTurn": 2,
  "gameOver": false,
  "scores": {
    "1": 0,
    "2": 0
  } 
```
    
### Make a move
     
It's player's 2 turn so he starts and chose to sow the fifth house of his zone.
 
See the last part of the path: `/play/2/5`. It's for player 2, house 5 (Choice from 1 to 6)

    curl -X POST \
        --header "Content-Type: application/json" \
        "http://localhost:8080/game/77903be2-dfbb-4550-94d2-2526fc71fbc6/play/2/5"
    
```
{
  "id": "77903be2-dfbb-4550-94d2-2526fc71fbc6",
  "board": [7, 7, 7, 7, 6, 6, 0, 6, 6, 6, 6, 0, 7, 1],
  "playerTurn": 1,
  "gameOver": false,
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
# Get Started With Docker

- install and run Docker Desktop
- build and run this project using the following docker commands in the terminal at the project's root folder.
    - ```docker build -t ballrunner .```
    - ```docker run --rm -p 8080:9000 --name ballrunner ballrunner```

You should now be able to hit this service's endpoints at localhost:8080

hitting: ```localhost:8080/v1/rollBall?boardSize=6``` with boardSize as the height/width of the board you want it to generate.

should return a text print-out with a randomly generated board and a solution, if there is one, that looks like: 

```Ball Rolling Game
starting position: (0,5)
Destination: (5,0)}
Map:
|   |   |||||   |   | S |   |
|   |   |   |   ||||| * |   |
|   |   |||||   |   | * |||||
|   |   |   |   |   | * |   |
|   |   |||||   |   | * |   |
| D | * | * | * | * | * |||||
|   |   |   |   |   |||||   |


BallState((1,5),Down)

BallState((1,5),Down)

BallState((2,5),Down)

BallState((3,5),Down)

BallState((4,5),Down)

BallState((5,5),Down)

BallState((5,5),Stopped)

BallState((5,4),Left)

BallState((5,3),Left)

BallState((5,2),Left)

BallState((5,1),Left)

BallState((5,0),Left)

BallState((5,0),Stopped)
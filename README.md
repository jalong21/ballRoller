# Rolling Ball Problem

Many interview coding problems involve navigating a grid in some way.
This one involves a board with walls and a ball.
The ball has a starting position and a destination. 
It can start rolling in any direction as long as there is not a wall in its way.
Once it starts moving, it must keep moving until it hits a wall, at which point it can start moving again in any new position.
If it stops at a destination point, a solution is found.
Some boards might not have a solution, because it is impossible for the ball to stop at that position.

## Build with Docker

- Install and run Docker Desktop
- Build and run this project using the following docker commands in the terminal at the project's root folder.
    - ```docker build -t ballrunner .```
    - ```docker run --rm -p 8080:9000 --name ballrunner ballrunner```

You should now be able to hit this service's endpoints at localhost:8080

Hitting: ```localhost:8080/v1/rollBall?boardSize=6``` with boardSize as the height/width of the board you want it to generate will return a text print-out with a randomly generated board and a solution, if there is one. 

I have found that most random boards do not have a solution, so you might have to hit the endpoint a few times to find a good one. 
Solutions are more common with smaller boards.

Sample Request:

```localhost:8080/v1/rollBall?boardSize=5```

Sample Response:

```
 Ball Rolling Game
 Starting position: (0,3)
 Destination: (4,4)
 Map: 
|||||   |   |   |   |
|||||   |   |   |   |
|   |   |   |   |   |
| S |   |||||   |   |
| * | * | * | * | D |

 BallState((0,3),Stopped)
 BallState((0,4),Down)
 BallState((0,4),Stopped)
 BallState((1,4),Right)
 BallState((2,4),Right)
 BallState((3,4),Right)
 BallState((4,4),Right)
 BallState((4,4),Stopped)
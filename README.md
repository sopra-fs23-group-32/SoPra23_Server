# ![logo](https://github.com/sopra-fs23-group-32/SoPra23_Client/blob/readme_branch/images/city_logo.png?raw=true) SoPra FS23 - Guess the City Server
<p align="center">
	<img src="https://img.shields.io/github/issues-raw/sopra-fs23-group-32/SoPra23_Server"/>
	<img src="https://img.shields.io/github/milestones/progress/sopra-fs23-group-32/SoPra23_Server/1"/>
	<img src="https://img.shields.io/github/milestones/progress/sopra-fs23-group-32/SoPra23_Server/2"/>
	<img src="https://sonarcloud.io/api/project_badges/measure?project=sopra-fs23-group-32_SoPra23_Server&metric=coverage"/>
	<img src="https://sonarcloud.io/api/project_badges/measure?project=sopra-fs23-group-32_SoPra23_Server&metric=bugs"/>
	<img src="https://sonarcloud.io/api/project_badges/measure?project=sopra-fs23-group-32_SoPra23_Server&metric=vulnerabilities"/>
	<img src="https://sonarcloud.io/api/project_badges/measure?project=sopra-fs23-group-32_SoPra23_Server&metric=code_smells"/>
	<img src="https://img.shields.io/github/license/sopra-fs23-group-32/SoPra23_Server"/>
</p>

## Introduction
This app is a game called “Guess the City”. It is a captivating game that invites players to discover the cityscapes around the world by identifying corresponding city name from multiple-choice options.
The game not only serves as a source of entertainment but also offers an opportunity for players to enhance their geography knowledge. As players immerse themselves in the game, they embark on a virtual journey across continents, exploring the diverse landscapes and architectual marvels that cities have to offer.
To cater to different preferences and gaming experiences, the game offers two modes: Single Player Mode and Multiplayer Mode. The Single Player Mode is for practice, enabling players to improve their accuracy in identifying cities without the pressure of competition.
The Multiplayer Mode provides a platform for friendly competition, where players have the opportunity to challenge their friends and compete against each other.

## Technologies
- [Unsplash API](https://unsplash.com/developers) - City images API.
- [sockjs-client](https://github.com/sockjs) -  Browser JavaScript library that provides a WebSocket-like object, communicating between the browser and the web server.
- [stompjs](https://www.npmjs.com/package/@stomp/stompjs) - npm package that provides a STOMP over WebSocket client for Web browser and node.js applications.
- [react-toggle](https://www.npmjs.com/package/react-toggle) - npm package for toggle component.
- [react-toastify](https://www.npmjs.com/package/react-toastify) - npm package for notifications.
- [react-countdown-circle-timer](https://www.npmjs.com/package/react-countdown-circle-timer) - npm package for countdown timer.

## High-level Components
### [GameController](https://github.com/sopra-fs23-group-32/SoPra23_Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/controller/GameController.java)
A controller class that communicates with the client side through endpoints by handling REST requests.
The information it sends to or receives from endpoints is processed by the class [GameService](https://github.com/sopra-fs23-group-32/SoPra23_Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/service/GameService.java)

### [GameService](https://github.com/sopra-fs23-group-32/SoPra23_Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/service/GameService.java)
A service class that correlates with [GameController](https://github.com/sopra-fs23-group-32/SoPra23_Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/controller/GameController.java) to manage the game data and control the game process.

### [ScoreBoardController](https://github.com/sopra-fs23-group-32/SoPra23_Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/controller/ScoreBoardController.java)
A controller class that offers the endpoints to send user rankings to the client side. It receives the ranking type (general or specific continent category) from the endpoint, and sends to [ScoreBoardService](https://github.com/sopra-fs23-group-32/SoPra23_Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/service/ScoreBoardService.java), which will return the specified user ranking.

### [ScoreBoardService](https://github.com/sopra-fs23-group-32/SoPra23_Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/service/ScoreBoardService.java)
A service class that correlates with [ScoreBoardController](https://github.com/sopra-fs23-group-32/SoPra23_Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/controller/ScoreBoardController.java) to return specified user ranking.


## Launch & Deployment
### Preparations
#### IntelliJ
1. File -> Open... -> SoPra server template
2. Accept to import the project as a `gradle project`
3. To build right click the `build.gradle` file and choose `Run Build`
#### VS Code
The following extensions can help you get started more easily:
-   `vmware.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs23` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

### Building with Gradle
You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

#### Build
```bash
./gradlew build
```
#### Run
```bash
./gradlew bootRun
```
#### Test
```bash
./gradlew test
```
#### Development Mode
You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

### API Endpoint Testing with Postman
We recommend using [Postman](https://www.getpostman.com) to test your API Endpoints.

### Debugging
If something is not working and/or you don't know what is going on. We recommend using a debugger and step-through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command), do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug "Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

### Testing
Have a look here: https://www.baeldung.com/spring-boot-testing

## Roadmap
![image](https://github.com/sopra-fs23-group-32/SoPra23_Client/blob/readme_branch/images/illustrations/roadmap.png?raw=true)
- Difficulty Level - Implement difficulty levels that offer varying levels of challenge to players. For example, there can be easy, medium, and hard modes where the hints of images are progressively more limited. This feature would require designing new image sets and adjusting scoring mechanisms.
- Social Integration - Allow players to connect their social media accounts to the game. This feature would require new developers to integrate social media sharing APIs.
- Unlockable Content - Add a progressive map where players can unlock new cities. This feature involves tracking player achievements, integrating the global map and providing rewards for new milestones.



## Authors and Acknowledgment
### Authors
- Said-Haji Abukar - [awhoa](https://github.com/awhoa)
- Zilong Deng - [Dzl666](https://github.com/Dzl666)
- Jano-Sven Vukadinovic - [VukadinovicJS](https://github.com/VukadinovicJS)
- Dominic Vogel - [dominic1712](https://github.com/dominic1712)
- Leyi Xu - [leyixu21](https://github.com/leyixu21)

See also the list of [contributors](https://github.com/sopra-fs23-group-32/SoPra23_Client/graphs/contributors) who participated in this project.

### Acknowledgement
- The city images of this project are provided by [Unsplash API](https://unsplash.com/developers).
- Thanks to Luis Torrejón Machado - [luis-tm](https://github.com/luis-tm) who supports this project as a tutor.

## License
This project is licensed under the Apache License 2.0 - see the [LICENSE.md](https://github.com/sopra-fs23-group-32/SoPra23_Server/blob/main/LICENSE) file for details.


## REST requests Table

<table>
<thead>
  <tr>
    <th>HTTP-Method </th>
    <th>Mapping </th>
    <th>Parameter  </th>
    <th>Param. Type </th>
    <th>Status Code </th>
    <th>Response </th>
    <th>Description </th>
  </tr>
</thead>
<tbody>
  <tr>
    <td rowspan="2">POST </td>
    <td rowspan="2">/users</td>
    <td rowspan="2">
        username&lt;String&gt;
        password&lt;String&gt; 
    </td>
    <td rowspan="2">Body </td>
    <td>201 </td>
    <td>User </td>
    <td>add User</td>
  </tr>
  <tr>
    <td>409 </td>
    <td>Error&lt;String&gt; </td>
    <td>Username existed</td>
  </tr>
  <tr>
    <td rowspan="3">PUT </td>
    <td rowspan="3">/users/login</td>
    <td rowspan="3">
        username&lt;String&gt;
        password &lt;String&gt;
    </td>
    <td rowspan="3">Body </td>
    <td>200 </td>
    <td>User </td>
    <td>Login user</td>
  </tr>
  <tr>
    <td>401 </td>
    <td>Error&lt;String&gt; </td>
    <td>password incorrect</td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>can't find username</td>
  </tr>
  <tr>
    <td rowspan="2">PUT </td>
    <td rowspan="2">/users/logout</td>
    <td rowspan="2">
        username&lt;String&gt;<br>
        password&lt;String&gt; 
    </td>
    <td rowspan="2">Body </td>
    <td>200 </td>
    <td>User </td>
    <td>Log out user </td>
  </tr>
  <tr>
    <td>400 </td>
    <td>Error&lt;String&gt; </td>
    <td>user already logged out</td>
  </tr>
  <tr>
    <td rowspan="2">PUT </td>
    <td rowspan="2">/users/{userId}</td>
    <td rowspan="2">
        userId&lt;long&gt;<br>
        username&lt;String&gt;<br>
        password&lt;String&gt;<br>
        birthDay&lt;String&gt;
    </td>
    <td rowspan="2">Query &amp; Body </td>
    <td>200 </td>
    <td>User </td>
    <td>update user profile </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>userId not found</td>
  </tr>
  <tr>
    <td>GET </td>
    <td>/users </td>
    <td>- </td>
    <td>- </td>
    <td>200 </td>
    <td>List&lt;User&gt; </td>
    <td>Get all users</td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">/users/{userId}</td>
    <td rowspan="2">userId&lt;long&gt; </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>User </td>
    <td>Get a user </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>userId not found</td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">/users/ranking?category</td>
    <td rowspan="2">category&lt;String&gt;<br>(optional) </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>List of userId&lt;long&gt;<br>
        username&lt;String&gt;<br>
        createDay&lt;Date&gt;<br>
        score&lt;long&gt;<br>
        gameNum&lt;long&gt;<br>
    </td>
    <td>Get ranking of all users</td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>no user</td>
  </tr>
  <tr>
    <td rowspan="2">POST </td>
    <td rowspan="2">/games </td>
    <td rowspan="2">
        category&lt;String&gt;<br>
        totalRounds&lt;int&gt;<br>
        countdownTime&lt;int&gt;
    </td>
    <td rowspan="2">Body </td>
    <td>201 </td>
    <td>Game </td>
    <td>create a lobby</td>
  </tr>
  <tr>
    <td>406 </td>
    <td>Error&lt;String&gt; </td>
    <td>Lobby type could not be created </td>
  </tr>
  <tr>
    <td rowspan="2">POST </td>
    <td rowspan="2">
        /games/{gameId}/<br>
        players/{playerId}
    </td>
    <td rowspan="2">
        gameId&lt;long&gt;<br>
        playerId&lt;long&gt;
    </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>- </td>
    <td>Add a player to a lobby</td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId / playerId not found</td>
  </tr>
  <tr>
    <td rowspan="2">POST </td>
    <td rowspan="2">
        /games/{gameId}/players/<br>
        {playerId}/answer
    </td>
    <td rowspan="2">
        gameId&lt;long&gt;<br>
        playerId&lt;long&gt;<br>
        answer&lt;String&gt;<br>
        timeTaken&lt;int&gt;
    </td>
    <td rowspan="2">Query &amp; Body </td>
    <td>200 </td>
    <td>- </td>
    <td>Submit player’s answer and update score </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId / playerId not found</td>
  </tr>
  <tr>
    <td rowspan="3">PUT </td>
    <td rowspan="3">/games/{gameId}</td>
    <td rowspan="3">gameId&lt;long&gt; </td>
    <td rowspan="3">Query </td>
    <td>200 </td>
    <td>Question </td>
    <td>Return question for next round and go head </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId not found</td>
  </tr>
  <tr>
    <td>409 </td>
    <td>Error&lt;String&gt; </td>
    <td>Game has ended</td>
  </tr>
  <tr>
    <td>GET </td>
    <td>/games</td>
    <td>- </td>
    <td>- </td>
    <td>200 </td>
    <td>List&lt;Game&gt; </td>
    <td>Get all games in SET UP state </td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">/games/{gameId}</td>
    <td rowspan="2">gameId&lt;long&gt; </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>Game </td>
    <td>Get details of this lobby </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId not found</td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">/games/{gameId}/status</td>
    <td rowspan="2">gameId&lt;long&gt; </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>GameStatus </td>
    <td>Get status of this lobby </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId not found</td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">/games/{gameId}/players</td>
    <td rowspan="2">gameId&lt;long&gt; </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td> List&lt;User&gt; </td>
    <td>Get player's list of this lobby </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId not found</td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">/games/{gameId}/ranking</td>
    <td rowspan="2">gameId&lt;long&gt; </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>List of 
        userId&lt;long&gt;<br>
        score&lt;int&gt;<br>
        rank&lt;int&gt;
    </td>
    <td>Get player's ranking for current round in lobby </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId not found / <br>no ranking found </td>
  </tr>
  <tr>
    <td rowspan="3">GET </td>
    <td rowspan="3">/games/{gameId}/results</td>
    <td rowspan="3">gameId&lt;long&gt; </td>
    <td rowspan="3">Query </td>
    <td>201 </td>
    <td>List&lt;String&gt;</td>
    <td>Get the winnerList of a game</td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId not found </td>
  </tr>
  <tr>
    <td>409 </td>
    <td>Error&lt;String&gt; </td>
    <td>game not ended</td>
  </tr>
  <tr>
    <td rowspan="3">DELETE </td>
    <td rowspan="3">/games/{gameId}</td>
    <td rowspan="3">gameId&lt;long&gt; </td>
    <td rowspan="3">Query </td>
    <td>200 </td>
    <td>- </td>
    <td>leave and delete game </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId not found </td>
  </tr>
  <tr>
    <td>409 </td>
    <td>Error&lt;String&gt; </td>
    <td>game not ended</td>
  </tr>
  <tr>
    <td rowspan="2">DELETE </td>
    <td rowspan="2">
        /games/{gameId}/<br>
        players/{playerId}
    </td>
    <td rowspan="2">
        gameId&lt;long&gt;<br>
        playerId&lt;long&gt;
    </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>- </td>
    <td>Player left the lobby </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId not found / <br>playerId not found </td>
  </tr>
  <tr>
    <td rowspan="3"> POST </td>
    <td rowspan="3">/gameInfo/{gameId}</td>
    <td rowspan="3">gameId&lt;long&gt;</td>
    <td rowspan="3">Query </td>
    <td>200 </td>
    <td>GameInfo </td>
    <td>Create a shared GameInfo </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId not found</td>
  </tr>
  <tr>
    <td>409 </td>
    <td>Error&lt;String&gt; </td>
    <td>game not ended</td>
  </tr>
  <tr>
    <td rowspan="3"> POST </td>
    <td rowspan="3">
        /users/{userId}/<br>
        gameHistories/{gameId}
    </td>
    <td rowspan="3">
        userId&lt;long&gt;<br>
        gameId&lt;long&gt;
    </td>
    <td rowspan="3">Query </td>
    <td>200 </td>
    <td>- </td>
    <td>Create user's GameHistory </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId not found / <br>userId not found </td>
  </tr>
  <tr>
    <td>409 </td>
    <td>Error&lt;String&gt; </td>
    <td>game not ended</td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">/users/{userId}/gameInfo</td>
    <td rowspan="2">userId&lt;long&gt; </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>List&lt;GameInfo&gt; </td>
    <td>- </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>userId not found </td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">
        /users/{userId}/<br>
        gameHistories
    </td>
    <td rowspan="2">userId&lt;long&gt; </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>List&lt;GameHistory&gt; </td>
    <td>- </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>userId not found </td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">
        /users/{userId}/<br>
        gameInfo/{gameId}
    </td>
    <td rowspan="2">
        userId&lt;long&gt;<br>
        gameId&lt;long&gt;
    </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>GameInfo </td>
    <td>Get information of a game </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId not found / <br>userId not found</td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">
        /users/{userId}/gameHistories/<br>
        {gameId}/stats
    </td>
    <td rowspan="2">
        userId&lt;long&gt;<br>
        gameId&lt;long&gt;
    </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>GameHistory </td>
    <td>Get user's history of a game </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId not found / <br>userId not found</td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">
        /users/{userId}/gameHistories/<br>
        {gameId}/answer
    </td>
    <td rowspan="2">
        userId&lt;long&gt;<br>
        gameId&lt;long&gt;
    </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>List of 
        answer&lt;String&gt;<br>
        label&lt;String&gt;
    </td>
    <td>Get user's answers and correct answers of game </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId not found / <br>userId not found </td>
  </tr>
</tbody>
</table>
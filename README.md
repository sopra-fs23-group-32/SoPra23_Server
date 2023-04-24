# REST requests Table

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
    <td rowspan="2">/users </td>
    <td rowspan="2">
        username&lt;String&gt;
        password&lt;String&gt; 
    </td>
    <td rowspan="2">Body </td>
    <td>201 </td>
    <td>User </td>
    <td>add User </td>
  </tr>
  <tr>
    <td>409 </td>
    <td>Error&lt;String&gt; </td>
    <td>Username existed </td>
  </tr>
  <tr>
    <td rowspan="3">PUT </td>
    <td rowspan="3">/users/login </td>
    <td rowspan="3">
        username&lt;String&gt;
        password &lt;String&gt;
    </td>
    <td rowspan="3">Body </td>
    <td>200 </td>
    <td>User </td>
    <td>Login user </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>can't find username </td>
  </tr>
  <tr>
    <td>409 </td>
    <td>Error&lt;String&gt; </td>
    <td>password incorrect </td>
  </tr>
  <tr>
    <td rowspan="2">PUT </td>
    <td rowspan="2">/users/logout </td>
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
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>user already logged out </td>
  </tr>
  <tr>
    <td rowspan="2">PUT </td>
    <td rowspan="2">/users/{userId} </td>
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
    <td>user was not found </td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">/users </td>
    <td rowspan="2">- </td>
    <td rowspan="2">- </td>
    <td>200 </td>
    <td>List&lt;User&gt; </td>
    <td>Get all users </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td> </td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">/users/{userId} </td>
    <td rowspan="2">userId&lt;long&gt; </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>User </td>
    <td>Get a user </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>userId was not found </td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">/users/ranking </td>
    <td rowspan="2">- </td>
    <td rowspan="2">- </td>
    <td>200 </td>
    <td>List&lt;UserRanking&gt; </td>
    <td>Get ranking of users </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>no user </td>
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
    <td>create a lobby </td>
  </tr>
  <tr>
    <td><406 </td>
    <td>Error&lt;String&gt;  </td>
    <td>Lobby type could not be created </td>
  </tr>
  <tr>
    <td rowspan="2">POST </td>
    <td rowspan="2">/games/{gameId}/players/<br>{playerId} </td>
    <td rowspan="2">
        gameId&lt;long&gt;<br>
        playerId&lt;long&gt;
    </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>- </td>
    <td>Add a player to a lobby </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId / playerId not found </td>
  </tr>
  <tr>
    <td rowspan="2">POST </td>
    <td rowspan="2">/games/{gameId}/players/<br>{playerId}/answer </td>
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
    <td>401 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId / playerId not found </td>
  </tr>
  <tr>
    <td rowspan="2">PUT </td>
    <td rowspan="2">/games/{gameId} </td>
    <td rowspan="2">gameId&lt;long&gt; </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>Question </td>
    <td>Return question for next round and go head </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>Game ended / gameId not found </td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">/games/{gameId} </td>
    <td rowspan="2">gameId&lt;long&gt; </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>Game </td>
    <td>Get details of this lobby </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>lobby not found </td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">/games/{gameId}/players </td>
    <td rowspan="2">gameId&lt;long&gt; </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td> List&lt;User&gt; </td>
    <td>Get player's list of this lobby </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>lobby not found </td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">/games/{gameId}/ranking </td>
    <td rowspan="2">gameId&lt;long&gt; </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>PlayerRanking </td>
    <td>Get player's ranking for current round in lobby </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>no ranking found </td>
  </tr>
  <tr>
    <td rowspan="3">GET </td>
    <td rowspan="3">/games/{gameId}/results </td>
    <td rowspan="3">gameId&lt;long&gt; </td>
    <td rowspan="3">Query </td>
    <td>201 </td>
    <td>List&lt;PlayerRanking&gt; </td>
    <td>Get the game result </td>
  </tr>
  <tr>
    <td>404 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId not found </td>
  </tr>
  <tr>
    <td>409 </td>
    <td>Error&lt;String&gt; </td>
    <td>Lobby doesn't end / gameId not found </td>
  </tr>
  <tr>
    <td rowspan="2">DELETE </td>
    <td rowspan="2">/games/{gameId} </td>
    <td rowspan="2">gameId&lt;long&gt; </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>- </td>
    <td>lobby was ended (host only) </td>
  </tr>
  <tr>
    <td>401 </td>
    <td>Error&lt;String&gt; </td>
    <td>lobby wasn't deleted / <br>gameId not found </td>
  </tr>
  <tr>
    <td rowspan="2">DELETE </td>
    <td rowspan="2">/games/{gameId}/players/<br>{playerId}/logout </td>
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
    <td>401 </td>
    <td>Error&lt;String&gt; </td>
    <td>can't leave lobby / <br>gameId not found / <br>playerId not found </td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">/users/{userId}/gameInfo </td>
    <td rowspan="2">userId&lt;long&gt; </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>List&lt;GameInfo&gt; </td>
    <td>- </td>
  </tr>
  <tr>
    <td>401 </td>
    <td>Error&lt;String&gt; </td>
    <td>userId not found </td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">/users/{userId}/gameInfo/<br>{gameId}/details </td>
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
    <td>401 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId not found / <br>userId not found </td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">/users/{userId}/gameInfo/<br>{gameId}/score </td>
    <td rowspan="2">
        userId&lt;long&gt;<br>
        gameId&lt;long&gt;
    </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>GameDetails </td>
    <td>Get user's score in a game </td>
  </tr>
  <tr>
    <td>401 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId not found / <br>userId not found </td>
  </tr>
  <tr>
    <td rowspan="2">GET </td>
    <td rowspan="2">/users/{userId}/gameInfo/<br>{gameId}/answer </td>
    <td rowspan="2">
        userId&lt;long&gt;<br>
        gameId&lt;long&gt;
    </td>
    <td rowspan="2">Query </td>
    <td>200 </td>
    <td>List&lt;GameHistoryAnswer&gt; </td>
    <td>Get user's answers and correct answers of game </td>
  </tr>
  <tr>
    <td>401 </td>
    <td>Error&lt;String&gt; </td>
    <td>gameId not found / <br>userId not found </td>
  </tr>
</tbody>
</table>

# SoPra RESTful Service Template FS23

## Getting started with Spring Boot
-   Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
-   Guides: http://spring.io/guides
    -   Building a RESTful Web Service: http://spring.io/guides/gs/rest-service/
    -   Building REST services with Spring: https://spring.io/guides/tutorials/rest/
### IntelliJ
1. File -> Open... -> SoPra server template
2. Accept to import the project as a `gradle project`
3. To build right click the `build.gradle` file and choose `Run Build`
### VS Code
The following extensions can help you get started more easily:
-   `vmware.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs23` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

## Building with Gradle
You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

### Build
```bash
./gradlew build
```
### Run
```bash
    ./gradlew bootRun
```
### Test
```bash
./gradlew test
```
### Development Mode
You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

## API Endpoint Testing with Postman
We recommend using [Postman](https://www.getpostman.com) to test your API Endpoints.

## Debugging
If something is not working and/or you don't know what is going on. We recommend using a debugger and step-through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command), do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug "Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

## Testing
Have a look here: https://www.baeldung.com/spring-boot-testing

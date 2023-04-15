package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.CityBase;
import ch.uzh.ifi.hase.soprafs23.entity.Round;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Game Service - The "worker", responsible for all functionality related to the game
 * (creates, modifies, deletes, finds). The result will be passed back to the caller.
 */

@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    private final UserRepository userRepository;
    private Game game;

    @Autowired
    public GameService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public Game startNewGame(int rounds, int countdownTime, CityBase cityDB) {
        return new Game(rounds, countdownTime, cityDB);
    }

    public void submitAnswers(List<Answer> answers) {
        game.submitAnswers(answers);
    }

    public void addPlayers(List<Long> userIdList) {
        for (Long userId : userIdList) {
            User user = userRepository.findByUserId(userId);
            game.addPlayer(user);
        }
    }


}
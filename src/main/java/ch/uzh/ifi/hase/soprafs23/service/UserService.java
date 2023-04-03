package ch.uzh.ifi.hase.soprafs23.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * User Service - The "worker", responsible for all functionality related to the user
 * (creates, modifies, deletes, finds). The result will be passed back to the caller.
 */

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User createUser(User newUser) {
//        newUser.setToken(UUID.randomUUID().toString());
        checkIfUsernameExist(newUser.getUsername());
        newUser.setStatus(UserStatus.OFFLINE);
        newUser.setCreateDay(new Date());
        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User loginUser(User user) {
        checkLogin(user);
        User loginUser = userRepository.findByUsername(user.getUsername());
        loginUser.setStatus(UserStatus.ONLINE);
        log.debug("Login User: {}", loginUser);
        return loginUser;
    }

    public User logoutUser(User user) {
        User logoutUser = userRepository.findByUsername(user.getUsername());
        logoutUser.setStatus(UserStatus.OFFLINE);
        log.debug("Logout User: {}", logoutUser);
        return logoutUser;
    }

    public User updateUser(Long userId, User user) {
        checkIfIdExist(userId);
        User updateUser = userRepository.findByUserId(userId);
        if(user.getUsername() != null && !user.getUsername().equals(updateUser.getUsername())){
            checkIfUsernameExist(user.getUsername());
            updateUser.setUsername(user.getUsername());
        }
        if(user.getBirthDay() != null) {
            //        checkIfBirthDayValid(user.getBirthDay());
            updateUser.setBirthDay(user.getBirthDay());
        }
        if(user.getPassword() != null) {
            updateUser.setPassword(user.getPassword());
        }

        log.debug("Updated User: {}", updateUser);
        return updateUser;
    }

    public User searchUserById(Long userId){
        checkIfIdExist(userId);
        return this.userRepository.findByUserId(userId);
    }

    /**
     * Check the uniqueness criteria of the username defined in the User entity.
     * The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUsernameExist(String username) throws ResponseStatusException{
        User userByUsername = userRepository.findByUsername(username);

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!\n";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(baseErrorMessage, "username", "is"));
        }
    }

    private void checkIfIdExist(Long userId) {
        User userByUserId = userRepository.findByUserId(userId);
        if(userByUserId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("User with ID %d was not found!\n", userId));
        }
    }

    private void checkLogin(User userLoggingIn) {
        User userByUsername = userRepository.findByUsername(userLoggingIn.getUsername());

        String baseErrorMessage = "The %s provided is not %s!\n";
        if (userByUsername == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format(baseErrorMessage, "username", "exist"));
        }
        else if (!userByUsername.getPassword().equals(userLoggingIn.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format(baseErrorMessage, "password", "correct"));
        }
    }

}


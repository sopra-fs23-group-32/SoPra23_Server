package ch.uzh.ifi.hase.soprafs23.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Date;
import java.io.FileWriter;
import java.io.IOException;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
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
//    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    private final UserRepository userRepository;

    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User createUser(User newUser) {
//        newUser.setToken(UUID.randomUUID().toString());
        checkIfUsernameExist(newUser.getUsername());
        checkPasswordCondition(newUser.getPassword());
        newUser.setStatus(UserStatus.ONLINE);
        newUser.setCreateDay(new Date());
        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    private void checkPasswordCondition(String password) throws ResponseStatusException{
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;

        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            if (Character.isUpperCase(ch)) {
                hasUppercase = true;
            }
            else if (Character.isLowerCase(ch)) {
                hasLowercase = true;
            }
            else if (Character.isDigit(ch)) {
                hasDigit = true;
            }
        }
        if (!hasUppercase || !hasLowercase || !hasDigit) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
            "Invalid password format. " +
            "Please make sure your password contains at least one uppercase letter, one lowercase letter, and one number.");
        }
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
//            checkIfBirthDayValid(user.getBirthDay());
            updateUser.setBirthDay(user.getBirthDay());
        }
        if(user.getPassword() != null) {
//            checkIfPasswordValid(user.getPassword());
            checkPasswordCondition(user.getPassword());
            updateUser.setPassword(user.getPassword());
        }

        log.debug("Updated User: {}", updateUser);
        return updateUser;
    }

    public User searchUserById(Long userId){
        checkIfIdExist(userId);
        return this.userRepository.findByUserId(userId);
    }

    public User searchUserByUsername(String userName){
        return this.userRepository.findByUsername(userName);
    }

    /**
     * Check the uniqueness criteria of the username defined in the User entity.
     * The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @throws org.springframework.web.server.ResponseStatusException ResponseStatusException
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

    public void saveRepoToDatabase() {
        List<User> userList = userRepository.findAll();
        String filePath = "../database/userRepository.csv";
        try (FileWriter writer = new FileWriter(filePath, false)) {
            writer.append("userId,username,password,status,createDay,birthDay");
            writer.append(System.lineSeparator());
            // write all records
            for (User user : userList) {
                writer.append(user.getUserId().toString()).append(",");
                writer.append(user.getUsername()).append(",");
                writer.append(user.getPassword()).append(",");
                writer.append(user.getStatus().toString()).append(",");
                writer.append(user.getCreateDay().toString()).append(",");
                writer.append(user.getBirthDay().toString()).append(",");
                writer.append(System.lineSeparator());
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

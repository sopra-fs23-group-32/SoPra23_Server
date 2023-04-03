package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller - Responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution
 * to the UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get all users
     * @return DTO list of all users
     */
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();
        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }

    /**
     * Get single user
     * @param userId unique ID for user
     * @return User DTO w.r.t. userId
     */
    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserGetDTO getOneUser(@PathVariable Long userId) {
        User user = userService.searchUserById(userId);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

    /**
     * Create user
     * @param userPostDTO DTO for create user
     * @return DTO of created user
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        // create user
        User createdUser = userService.createUser(userInput);
        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }

    /**
     * Login user
     * @param userPutDTO DTO for login
     * @return DTO of user
     */
    @PutMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO loginUser(@RequestBody UserPutDTO userPutDTO) {
        User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        User loginUser = userService.loginUser(userInput);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(loginUser);
    }

    /**
     * Logout user
     * @param authorizationHeader which contains the token
     * @return DTO of created user
     */
    @PutMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public UserGetDTO logoutUser(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        User logoutUser = userService.logoutUser(token);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(logoutUser);
    }

    /**
     * Update user data
     * @param userPutDTO DTO for update user
     * @param userId ID that point to a exist user
     * @return DTO of updated user
     */
    @PutMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public UserGetDTO updateUser(@RequestBody UserPutDTO userPutDTO, @PathVariable Long userId, @RequestHeader("Authorization") String authorizationHeader) {
        User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        String token = authorizationHeader.substring(7);
        User updatedUser = userService.updateUser(userId, userInput, token);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(updatedUser);
    }
}

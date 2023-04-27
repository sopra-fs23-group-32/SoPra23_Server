package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private UserService userService;
  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    // given
    testUser = new User();
    testUser.setUserId(1L);
    testUser.setUsername("testUsername");
    testUser.setPassword("testPassword1");
    testUser.setStatus(UserStatus.OFFLINE);
    List<User> userList = Collections.singletonList(testUser);

    // when
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findAll()).thenReturn(userList);
    Mockito.when(userRepository.findByUserId(Mockito.eq(1L))).thenReturn(testUser);
    Mockito.when(userRepository.findByUserId(Mockito.eq(999L))).thenReturn(null);
    Mockito.when(userRepository.findByUsername(Mockito.eq("testUsername"))).thenReturn(testUser);
  }

  @Test
  public void testGetUsers() {
      // when
      List<User> userList = userService.getUsers();

      // then
      Mockito.verify(userRepository, Mockito.times(1)).findAll();

      assertEquals(1, userList.size());
      assertEquals(testUser.getUserId(), userList.get(0).getUserId());
      assertEquals(testUser.getUsername(), userList.get(0).getUsername());
      assertEquals(testUser.getPassword(), userList.get(0).getPassword());
      assertEquals(UserStatus.OFFLINE, userList.get(0).getStatus());
  }

  @Test
  public void testCreateUser_validInput() {
      // given
      User user = new User();
      user.setUserId(2L);
      user.setUsername("newUser");
      user.setPassword("testPassword1");
      user.setStatus(UserStatus.ONLINE);

    // when
    User createdUser = userService.createUser(user);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getUserId(), createdUser.getUserId());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertEquals(testUser.getPassword(), createdUser.getPassword());
    assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
  }

  @Test
  public void testCreateUser_userExists() {

    // verify a ResponseStatusException with status code CONFLICT is thrown
    ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userService.createUser(testUser)
    );
    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
  }

  @Test
  public void testCreateUser_invalidPassword() {
    // given
    testUser.setPassword("password");

    // verify a ResponseStatusException with status code CONFLICT is thrown
    ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userService.createUser(testUser)
    );
    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
  }

  @Test
  public void testLoginUser_validInput() {
      // given
      User user = new User();
      user.setUserId(1L);
      user.setUsername("testUsername");
      user.setPassword("testPassword1");

      // when
      User loggedInUser = userService.loginUser(user);

      // then
      Mockito.verify(userRepository, Mockito.times(2)).findByUsername(Mockito.any());

      assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
  }

  @Test
  public void testLoginUser_noUsername() {
      // given
      User user = new User();
      user.setUserId(1L);

      // verify a ResponseStatusException with status code BAD_REQUEST is thrown
      ResponseStatusException exception = assertThrows(
              ResponseStatusException.class,
              () -> userService.loginUser(user)
      );
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
  }

  @Test
  public void testLoginUser_wrongPassword() {
      // given
      User user = new User();
      user.setUserId(1L);
      user.setUsername("testUsername");
      user.setPassword("wrongPassword");

      // verify a ResponseStatusException with status code BAD_REQUEST is thrown
      ResponseStatusException exception = assertThrows(
              ResponseStatusException.class,
              () -> userService.loginUser(user)
      );
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
  }

  @Test
  public void testLogoutUser() {
      // given
      User user = new User();
      user.setUserId(1L);
      user.setUsername("testUsername");
      user.setStatus(UserStatus.ONLINE);

      // when
      User loggedOutUser = userService.logoutUser(user);

      // then
      Mockito.verify(userRepository, Mockito.times(1)).findByUsername(Mockito.any());

      assertEquals(UserStatus.OFFLINE, loggedOutUser.getStatus());
  }

  @Test
  public void testUpdateUser_validInput() {
      // given
      User user = new User();
      user.setUserId(1L);
      user.setUsername("newUsername");
      user.setPassword("newPassword1");
      user.setBirthDay(new Date());

      // when
      User updatedUser = userService.updateUser(1L, user);

      // then
      Mockito.verify(userRepository, Mockito.times(2)).findByUserId(Mockito.any());

      assertEquals(user.getUsername(), updatedUser.getUsername());
      assertEquals(user.getPassword(), updatedUser.getPassword());
      assertNotNull(updatedUser.getBirthDay());
  }

  @Test
  public void testUpdateUser_userNotExist() {
      // given
      User user = new User();
      user.setUserId(999L);
      user.setUsername("newUsername");
      user.setPassword("newPassword1");
      user.setBirthDay(new Date());

      // verify a ResponseStatusException with status code NOT_FOUND is thrown
      ResponseStatusException exception = assertThrows(
              ResponseStatusException.class,
              () -> userService.updateUser(999L, user)
      );
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
  }

  @Test
  public void testUpdateUser_invalidPassword() {
      // given
      User user = new User();
      user.setUserId(1L);
      user.setUsername("newUsername");
      user.setPassword("invalidPassword");

      // verify a ResponseStatusException with status code CONFLICT is thrown
      ResponseStatusException exception = assertThrows(
              ResponseStatusException.class,
              () -> userService.updateUser(1L, user)
      );
      assertEquals(HttpStatus.CONFLICT, exception.getStatus());
  }

  @Test
  public void testSearchUserById_userExists() {
      // when
      User user = userService.searchUserById(1L);

      // then
      Mockito.verify(userRepository, Mockito.times(2)).findByUserId(Mockito.any());

      assertEquals(testUser.getUsername(), user.getUsername());
      assertEquals(testUser.getPassword(), user.getPassword());
      assertEquals(testUser.getStatus(), user.getStatus());
  }

  @Test
  public void testSearchUserById_userNotExist() {
      // verify a ResponseStatusException with status code NOT_FOUND is thrown
      ResponseStatusException exception = assertThrows(
              ResponseStatusException.class,
              () -> userService.searchUserById(999L)
      );
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
  }

  @Test
  public void testSearchUserByUsername() {
      // when
      User user = userService.serachUserByUsername("testUsername");

      // then
      assertEquals(testUser.getUserId(), user.getUserId());
  }
}

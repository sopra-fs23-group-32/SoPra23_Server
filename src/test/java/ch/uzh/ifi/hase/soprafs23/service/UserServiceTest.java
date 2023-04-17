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
import org.springframework.web.server.ResponseStatusException;

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

    testUser.setPassword("SoPraHS23");
    // when -> any object is being save in the userRepository -> return the dummy
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);
    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getUserId(), createdUser.getUserId());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertEquals(testUser.getPassword(), createdUser.getPassword());
    assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
    assertNotNull(createdUser.getCreateDay());
  }

  @Test
  public void createUser_duplicateName_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

//  @Test
//  public void createUser_duplicateInputs_throwsException() {
//    // given -> a first user has already been created
//    userService.createUser(testUser);
//
//    // when -> setup additional mocks for UserRepository
//    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
//    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
//
//    // then -> attempt to create second user with same user -> check that an error
//    // is thrown
//    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
//  }



}

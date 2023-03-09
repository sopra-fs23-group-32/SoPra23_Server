package ch.uzh.ifi.hase.soprafs23.repository;

import java.util.Date;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByName_success() {
        // given
        User user = new User();
        user.setUsername("firstname lastname");
        user.setPassword("12345678");
        user.setStatus(UserStatus.OFFLINE);
        user.setCreateDay(new Date());
        user.setBirthDay(new Date());

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findByUsername(user.getUsername());
        // then
        assertNotNull(found.getUserId());
        assertEquals(found.getUsername(), user.getUsername());
        assertEquals(found.getPassword(), user.getPassword());
        assertEquals(found.getStatus(), user.getStatus());
        assertEquals(found.getCreateDay(), user.getCreateDay());
//        assertEquals(found.getBirthDay(), user.getBirthDay());
    }
}

package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.entity.UserStatistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserStatisticsRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserStatisticsRepository userStatisticsRepository;

    @Test
    public void findByUserId_success() {
        // given
        UserStatistics userStatistics = new UserStatistics();
        userStatistics.initUserStatistics();
        userStatistics.setUserId(1L);
        userStatistics.updateScore(100, CityCategory.ASIA);
        userStatistics.updateGameNum(CityCategory.ASIA);

        entityManager.persist(userStatistics);
        entityManager.flush();

        // when
        UserStatistics found = userStatisticsRepository.findByUserId(1L);
        // then
        assertNotNull(found.getUserId());
        assertEquals(found.getTotalScore(), userStatistics.getTotalScore());
        assertEquals(found.getTotalGameNum(), userStatistics.getTotalGameNum());
    }
}

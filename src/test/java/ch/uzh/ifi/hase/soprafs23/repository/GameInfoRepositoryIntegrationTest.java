package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.entity.GameInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class GameInfoRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameInfoRepository gameInfoRepository;

    @Test
    public void findByGameId_success() {
        // given
        GameInfo gameInfo = new GameInfo();
        gameInfo.setGameId(1L);
        gameInfo.setCategory(CityCategory.ASIA);
        gameInfo.setGameDate(new Date());
        gameInfo.setGameRounds(2);
        gameInfo.setPlayerNum(4);

        entityManager.persist(gameInfo);
        entityManager.flush();

        // when
        GameInfo found = gameInfoRepository.findByGameId(gameInfo.getGameId());
        // then
        assertNotNull(found.getGameId());
        assertEquals(found.getCategory(), gameInfo.getCategory());
        assertEquals(found.getGameDate(), gameInfo.getGameDate());
        assertEquals(found.getGameRounds(), gameInfo.getGameRounds());
        assertEquals(found.getPlayerNum(), found.getPlayerNum());
    }
}

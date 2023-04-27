package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class GameRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    @Test
    public void findByGameId_success() {
        // given
        Game game = new Game();
        game.initGame();
        game.setGameId(1L);
        game.setCategory(CityCategory.ASIA);
        game.setTotalRounds(2);
        game.setCountdownTime(10);

        Game mergedGame = entityManager.merge(game);
        entityManager.persist(mergedGame);
        entityManager.flush();

        // when
        Game found = gameRepository.findByGameId(1L);
        // then
        assertNotNull(found.getGameId());
        assertEquals(found.getCategory(), game.getCategory());
        assertEquals(found.getTotalRounds(), game.getTotalRounds());
        assertEquals(found.getCountdownTime(), game.getCountdownTime());
    }
}

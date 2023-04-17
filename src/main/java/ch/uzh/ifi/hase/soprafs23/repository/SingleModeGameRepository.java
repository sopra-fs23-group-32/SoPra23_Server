package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.SingleModeGame;

import org.h2.index.SingleRowCursor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("SingleModeGameRepository")
public interface SingleModeGameRepository extends JpaRepository<SingleModeGame, Long> {
    /**
     * @param gameId id of the game
     */
    SingleModeGame findByGameId(int gameId);

}

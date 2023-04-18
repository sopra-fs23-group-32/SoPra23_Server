package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.GameInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("gameInfoRepository")
public interface GameInfoRepository extends JpaRepository<GameInfo, Long> {
    /**
     * @param gameId id of the game (multiplayer mode)
     */
    GameInfo findByGameId(Long gameId);
}

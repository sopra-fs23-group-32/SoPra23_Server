package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("gameRepository")
public interface GameRepository extends JpaRepository<User, Long> {
    /**
     * @param userId id of the user
     */
    User findByUserId(Long userId);

    User findByUsername(String username);
}

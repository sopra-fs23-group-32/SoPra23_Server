package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.UserStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("userStatisticsRepository")
public interface UserStatisticsRepository extends JpaRepository<UserStatistics, Long> {
    /**
     * @param userId id of the user
     */
    UserStatistics findByUserId(Long userId);
}

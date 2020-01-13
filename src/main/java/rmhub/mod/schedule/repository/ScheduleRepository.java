package rmhub.mod.schedule.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rmhub.mod.schedule.domain.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

  List<Schedule> getAllByStatus(String status);
}

package rmhub.mod.schedule.task;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import rmhub.mod.schedule.service.ScheduleService;

@Component
@Log4j2
public class StartUpRunner implements ApplicationRunner {

  @Autowired
  ScheduleService taskService;

  @Override
  public void run(ApplicationArguments args) {
    log.info("Init task at startup...");
    taskService.initTask();
  }
}

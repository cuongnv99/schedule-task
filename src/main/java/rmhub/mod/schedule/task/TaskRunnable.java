package rmhub.mod.schedule.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rmhub.mod.schedule.dto.ScheduleConfigDto;

@Component
public class TaskRunnable {

  @Autowired
  RequestSender requestSender;

  public Runnable getTaskRunnable(final ScheduleConfigDto scheduleConfigDto) {
    return () -> requestSender.sendRequest(scheduleConfigDto);
  }

  public void newThreadTaskStart(ScheduleConfigDto scheduleConfigDto) {
    new Thread(getTaskRunnable(scheduleConfigDto)).start();
  }

}

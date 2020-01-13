package rmhub.mod.schedule.dto;

import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleCreateDto {

  private String name;

  @Size(max = 2, message = "Task mode: 01-cronTask, 02-fixedRateTask, 03-fixedDelayTask, 04-triggerTask")
  private String mode;

  private String url;

  private String method;

  private String content;

  private String cron;

  private String initialDelay;

  private String fixedDelay;

  private String fixedRate;

  @Size(max = 1, message = "Run immediately: Y-YES, N-NO")
  private String isImdExe;

  @Size(max = 2, message = "procstate: 00-Finished (waiting) 01-Executing")
  private String procState;

}

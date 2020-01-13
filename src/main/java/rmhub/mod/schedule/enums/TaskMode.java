package rmhub.mod.schedule.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TaskMode {

  CRON_TASK("01", "cronTask"),
  FIXED_RATE_TASK("02", "fixedRateTask"),
  FIXED_DELAY_TASK("03", "fixedDelayTask"),
  TRIGGER_TASK("04", "triggerTask");

  @Getter
  private String mode;

  @Getter
  private String name;

  public static TaskMode getTaskModeEnum(String value) {
    if (value != null) {
      for (TaskMode taskEnum : values()) {
        if (taskEnum.getMode().equals(value)) {
          return taskEnum;
        }
      }
    }
    return null;
  }

}

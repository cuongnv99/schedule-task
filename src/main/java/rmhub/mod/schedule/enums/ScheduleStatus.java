package rmhub.mod.schedule.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ScheduleStatus {

  NOT_RUN("01", "not_run"),
  RUNNING("02", "running");

  @Getter
  private String code;

  @Getter
  private String desc;

  public static ScheduleStatus getTaskStatusEnum(String value) {
    if (value != null) {
      for (ScheduleStatus taskEnum : values()) {
        if (taskEnum.getCode().equals(value)) {
          return taskEnum;
        }
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return code;
  }
}


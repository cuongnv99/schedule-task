package rmhub.mod.schedule.dto;

import static rmhub.mod.schedule.constant.ScheduleConstant.DATE_TIME_PATTERN;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleConfigDto {

  private int id;

  private String name;

  private String mode;

  private String url;

  private String method;

  private String content;

  private String cron;

  private String initialDelay;

  private String fixedDelay;

  private String fixedRate;

  private String isImdExe;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
  private Date lastExecuteBegin;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
  private Date lastExecuteEnd;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
  private Date nextExecuteTime;

  private String status;

  private String procState;

  private String createUid;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
  private Date createTime;

  private String lastModifyUid;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
  private Date lastModifyTime;

}

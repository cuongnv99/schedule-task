package rmhub.mod.schedule.service;

import java.util.List;
import rmhub.mod.schedule.dto.ScheduleCreateDto;
import rmhub.mod.schedule.dto.ScheduleResDto;
import rmhub.mod.schedule.dto.ScheduleUpdateDto;

public interface ScheduleService {

  void initTask();

  List<ScheduleResDto> list();

  boolean register(ScheduleCreateDto task);

  long count();

  ScheduleResDto getById(int taskId);

  boolean startTask(int taskId);

  boolean stopTask(int taskId);

  boolean modifyTask(Integer taskId, ScheduleUpdateDto task);

  void manualExecuteTask(int taskId);

  ScheduleResDto insert(ScheduleCreateDto task);

  ScheduleResDto update(Integer taskId, ScheduleUpdateDto task);
}

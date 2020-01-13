package rmhub.mod.schedule.service;

import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import rmhub.mod.schedule.domain.Schedule;
import rmhub.mod.schedule.dto.ScheduleConfigDto;
import rmhub.mod.schedule.dto.ScheduleCreateDto;
import rmhub.mod.schedule.dto.ScheduleResDto;
import rmhub.mod.schedule.dto.ScheduleUpdateDto;
import rmhub.mod.schedule.repository.ScheduleRepository;
import rmhub.mod.schedule.task.ScheduleConfig;
import rmhub.mod.schedule.task.TaskRunnable;

@Service("taskService")
@Log4j2
class ScheduleServiceImpl implements ScheduleService {

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private ScheduleRepository scheduleRepository;

  @Autowired
  private ScheduleConfig scheduleConfig;

  @Autowired
  TaskRunnable taskRunnable;

  public void initTask() {
    log.info("Init task...");

    // get list of task by status
    List<ScheduleConfigDto> taskList = scheduleRepository.getAllByStatus("1")
        .stream().map(scheduledTask -> modelMapper.map(scheduledTask, ScheduleConfigDto.class))
        .collect(Collectors.toList());

    if (!CollectionUtils.isEmpty(taskList)) {
      for (ScheduleConfigDto scheduleConfigDto : taskList) {
        scheduleConfig.scheduleTask(scheduleConfigDto);
      }
    }
  }

  public boolean register(ScheduleCreateDto createDto) {
    boolean ret = true;
    log.info("ScheduleService addTask content: {}", JSON.toJSONString(createDto));
    if (insert(createDto) != null) {
      ret = scheduleConfig.scheduleTask(modelMapper.map(createDto, ScheduleConfigDto.class));
    } else {
      ret = false;
      log.error("ScheduleService addTask insert to DB failed.");
    }
    log.info("ScheduleService addTask result: {}", ret);
    return ret;
  }


  public boolean stopTask(int taskId) {
    log.info("ScheduleService stopTask content is:{}", taskId);
    Schedule schedule = scheduleRepository.getOne(taskId);
    schedule.setStatus("0");// 0-remove (or pause) 1-normal
    schedule.setProcState("00");// 00 - finished (waitting run) 01 - running
    scheduleRepository.save(schedule);
    return scheduleConfig.cancelTask(taskId);

  }

  public boolean startTask(int taskId) {
    boolean ret = true;
    log.info("ScheduleService startTask content:{}.", taskId);
    Schedule task = scheduleRepository.getOne(taskId);

    if (task != null) {
      ret = scheduleConfig.scheduleTask(modelMapper.map(task, ScheduleConfigDto.class));
      task.setId(taskId);
      task.setStatus("1");
      task.setProcState("01");
      scheduleRepository.save(modelMapper.map(task, Schedule.class));
      log.info("============Start Task=============" + task);
    } else {
      ret = false;
      log.error("ScheduleService startTask Task[{}] in DB not exist.", taskId);
    }
    return ret;
  }

  /**
   * cancel, update, re-config a task
   */
  public boolean modifyTask(Integer taskId, ScheduleUpdateDto task) {

    log.info("ScheduleService modifyTask content:{}.", JSON.toJSONString(task));

    // remove from task registrar
    boolean isCanceled = scheduleConfig.cancelTask(taskId);

    if (isCanceled) {
      // update DB -> cancel
      Schedule schedule = modelMapper.map(task, Schedule.class);
      schedule.setId(taskId);
      Schedule configSchedule = scheduleRepository.save(schedule);

      // re-config task registrar
      ScheduleConfigDto newTask = modelMapper.map(configSchedule, ScheduleConfigDto.class);
      isCanceled = scheduleConfig.scheduleTask(newTask);
    }

    return isCanceled;
  }

  public void manualExecuteTask(int taskId) {
    log.info("ScheduleService manualStart content:{}.", taskId);
    Schedule task = scheduleRepository.getOne(taskId);
    if (task != null) {
      task.setIsImdExe("Y");
      taskRunnable.newThreadTaskStart(modelMapper.map(task, ScheduleConfigDto.class));

    } else {
      log.error("ScheduleService not exist!.", taskId);
    }
  }

  public List<ScheduleResDto> list() {
    List<Schedule> schedules = scheduleRepository.findAll();
    log.info("ScheduleService selectTasks result: {}", JSON.toJSONString(schedules));
    List<ScheduleResDto> scheduleResDtos = new ArrayList<ScheduleResDto>();
    for (Schedule schedule : schedules) {
      scheduleResDtos.add(modelMapper.map(schedule, ScheduleResDto.class));
    }
    return scheduleResDtos;

  }

  public long count() {
    long count = scheduleRepository.count();
    log.info("getTasksCnt content: {}", count);
    return count;
  }

  public ScheduleResDto getById(int taskId) {
    log.info("getTaskById content: {}", taskId);
    return modelMapper.map(scheduleRepository.getOne(taskId), ScheduleResDto.class);
  }

  public ScheduleResDto insert(ScheduleCreateDto task) {
    log.info("addTask content: {} ", JSON.toJSONString(task));
    // init Schedule
    Schedule schedule = modelMapper.map(task, Schedule.class);
    schedule.setProcState("00");
    schedule.setCreateUid("SYSTEM");
    schedule.setLastModifyUid("SYSTEM");

    // save into DB
    Schedule result = scheduleRepository.save(schedule);

    return modelMapper.map(result, ScheduleResDto.class);
  }

  public ScheduleResDto update(Integer taskId, ScheduleUpdateDto task) {
    log.info("updateTask content: {}", JSON.toJSONString(task));

    Schedule schedule = modelMapper.map(task, Schedule.class);
    schedule.setId(taskId);
    return modelMapper.map(scheduleRepository.save(schedule), ScheduleResDto.class);

    // get by taskId -> entity
//    Schedule schedule = scheduleRepository.getOne(taskId);

    // map UpdateDto -> entity
//    modelMapper.map(task, schedule);

    // save into DB + map and return ScheduleResDto
//    return modelMapper.map(scheduleRepository.save(schedule), ScheduleResDto.class);
  }
}

package rmhub.mod.schedule.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rmhub.mod.schedule.dto.ScheduleCreateDto;
import rmhub.mod.schedule.dto.ScheduleResDto;
import rmhub.mod.schedule.dto.ScheduleUpdateDto;
import rmhub.mod.schedule.enums.ResultCode;
import rmhub.mod.schedule.enums.TaskMode;
import rmhub.mod.schedule.service.ScheduleService;

@Log4j2
@RestController
@RequestMapping("schedule")
public class ScheduleController {

  @Autowired
  private ScheduleService taskService;

  @GetMapping("/test4")
  public ResponseEntity<String> test4() {
    log.info("test4 OK");
    return ResponseEntity.ok("test4 OK");
  }

  @GetMapping("/test3")
  public ResponseEntity<String> test3() {
    log.info("test3 OK");
    return ResponseEntity.ok("test3 OK");
  }

  @PostMapping("/test2")
  public ResponseEntity<String> test2() {
    log.info("test2 OK");
    return ResponseEntity.ok("test2 OK");
  }

  /**
   * use to collect all task scheduled from DB
   */
  @GetMapping("/listTask")
  public ResponseEntity<List<ScheduleResDto>> getAllScheduledTask() {
    return ResponseEntity.ok(taskService.list());
  }

  /**
   * Add task to DB
   */
  @PostMapping("/addTask")
  Map<String, Object> addTask(@RequestBody ScheduleCreateDto task) {
    Map<String, Object> retMap = new HashMap<String, Object>(4);
    retMap.put("retCode", ResultCode.SUCCESS.getCode());
    try {
      if (!checkParam(task)) {
        log.info("ScheduleTaskController addTask Failed!");
        retMap.put("code", ResultCode.FAILED.getCode());
        retMap.put("message", "Validation failed123");
        return retMap;
      }
      boolean ret = taskService.register(task);
      if (!ret) {
        log.info("ScheduleTaskController addTask Failed!");
        retMap.put("code", ResultCode.FAILED.getCode());
        retMap.put("message", "Failed");
        return retMap;
      }
    } catch (Exception e) {
      log.error("ScheduleTaskController addTask has a Exception: {}", e);
      retMap.put("code", ResultCode.FAILED.getCode());
      retMap.put("message", "Exception");
    }
    return retMap;
  }

  @GetMapping("/stopTask/{id}")
  Map<String, Object> stopTask(@PathVariable int taskId) {
    Map<String, Object> retMap = new HashMap<String, Object>(4);
    retMap.put("retCode", ResultCode.SUCCESS.getCode());
    try {
      boolean ret = taskService.stopTask(taskId);
      if (!ret) {
        log.info("ScheduleTaskController stopTask Failed!");
        retMap.put("code", ResultCode.FAILED.getCode());
        retMap.put("message", "Dừng thất bại");
        return retMap;
      }
    } catch (Exception e) {
      log.error("ScheduleTaskController stopTask has a Exception: {}", e);
      retMap.put("code", ResultCode.FAILED.getCode());
      retMap.put("message", "System Exception");
    }
    return retMap;
  }


  @GetMapping("/startTask/{id}")
  Map<String, Object> startTask(@PathVariable int taskId) {
    Map<String, Object> retMap = new HashMap<String, Object>(4);
    retMap.put("retCode", ResultCode.SUCCESS.getCode());
    try {
      boolean ret = taskService.startTask(taskId);
      if (!ret) {
        log.info("ScheduleTaskController startTask Failed!");
        retMap.put("code", ResultCode.FAILED.getCode());
        retMap.put("message", "Init failed");
        return retMap;
      }
    } catch (Exception e) {
      log.error("ScheduleTaskController startTask has a Exception: {}", e);
      retMap.put("retCode", ResultCode.FAILED.getCode());
      retMap.put("errorMsg", e);
    }
    return retMap;
  }

  @PutMapping("/modifyTask/{taskId}")
  Map<String, Object> modifyTask(@PathVariable("taskId") Integer taskId, ScheduleUpdateDto task) {
    Map<String, Object> retMap = new HashMap<String, Object>(4);
    retMap.put("retCode", ResultCode.SUCCESS.getCode());
    try {
      boolean ret = taskService.modifyTask(taskId, task);
      if (!ret) {
        log.info("ScheduleTaskController modifyTask Failed!");
        retMap.put("code", ResultCode.FAILED.getCode());
        retMap.put("message", "Modify failed");
      }
    } catch (Exception e) {
      log.error("ScheduleTaskController modifyTask has a Exception: {}", e);
      retMap.put("retCode", ResultCode.FAILED.getCode());
      retMap.put("errorMsg", e);
    }
    return retMap;
  }

  @PostMapping("/mannualTask/{id}")
  Map<String, Object> mannualTask(@PathVariable int taskId) {
    Map<String, Object> retMap = new HashMap<String, Object>(4);// Save TaskResult<String, Obj>
    retMap.put("retCode", ResultCode.SUCCESS.getCode());
    try {
      taskService.manualExecuteTask(taskId);
    } catch (Exception e) {
      log.error("startUp the Task[{}] has Exception: ", taskId, e);
      retMap.put("retCode", ResultCode.FAILED.getCode());
      retMap.put("errorMsg", e);
    }
    return retMap;
  }

  private boolean checkParam(ScheduleCreateDto task) {
    if (StringUtils.isEmpty(task.getMode())) {
      return false;
    }
    if (TaskMode.CRON_TASK.getMode().equals(task.getMode())) {
      if (StringUtils.isEmpty(task.getCron())) {
        return false;
      }
    }
    return !StringUtils.isEmpty(task.getUrl()) && !StringUtils.isEmpty(task.getMethod());
  }

}

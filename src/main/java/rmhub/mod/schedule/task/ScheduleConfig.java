package rmhub.mod.schedule.task;

import com.alibaba.fastjson.JSON;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.Task;
import org.springframework.util.CollectionUtils;
import rmhub.mod.schedule.dto.ScheduleConfigDto;
import rmhub.mod.schedule.enums.TaskMode;
import rmhub.mod.schedule.util.BeanUtils;

@Log4j2
@Configuration
@EnableScheduling
public class ScheduleConfig implements SchedulingConfigurer {

  @Autowired
  TaskRunnable taskRunnable;

  @Value("${schedule.threadpool.size}")
  private int size;

  private Map<Object, ScheduledTask> scheduledTaskMap = new ConcurrentHashMap<>();

  private ScheduledTaskRegistrar taskRegistrar;

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    log.info("Config scheduled tasks...");
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(size);
    scheduler.setThreadNamePrefix("task-");
    scheduler.setAwaitTerminationSeconds(60);
    scheduler.setWaitForTasksToCompleteOnShutdown(true);
    scheduler.setRemoveOnCancelPolicy(true);
    scheduler.initialize();
    taskRegistrar.setScheduler(scheduler);
    this.taskRegistrar = taskRegistrar;
  }

  public boolean scheduleTask(ScheduleConfigDto scheduleConfigDto) {
    log.info("Add a new task to registrar: {}", JSON.toJSONString(scheduleConfigDto));
    if ("Y".equalsIgnoreCase(scheduleConfigDto.getIsImdExe())) {
      log.info("Run the task immediately...");
      taskRunnable.newThreadTaskStart(scheduleConfigDto);
    }
    switch (TaskMode.getTaskModeEnum(scheduleConfigDto.getMode())) {
      case CRON_TASK:
        Task cronTask = new CronTask(taskRunnable.getTaskRunnable(scheduleConfigDto), scheduleConfigDto.getCron());
        System.out.println("scheduled task " + scheduleConfigDto);
        return addCronTask(scheduleConfigDto.getId(), cronTask);
      case FIXED_RATE_TASK:
      case FIXED_DELAY_TASK:
      case TRIGGER_TASK:
      default:
        break;
    }
    return false;
  }

  public boolean addCronTask(int taskId, Task task) {
    boolean ret = true;
    log.info("ScheduleConfig addCronTask id :{}", taskId);
    try {
      if (!hasTask(taskId)) {
        org.springframework.scheduling.config.ScheduledTask scheduledTask = taskRegistrar.scheduleCronTask((CronTask) task);
        scheduledTaskMap.put(taskId, scheduledTask);
        log.info("Add Task[{}] success!", taskId);
      } else {
        log.info("The Task[{}] has been added!", taskId);
      }
    } catch (Exception e) {
      log.error("addCronTask has a exception:{}.", e);
      ret = false;
    }
    log.info("ScheduleConfig addCronTask result :{}", ret);
    return ret;
  }


  public boolean cancelTask(int taskId) {
    boolean ret = true;
    log.info("ScheduleConfig cancelTask content: {}.", taskId);
    try {
      if (!CollectionUtils.isEmpty(scheduledTaskMap)) {
        org.springframework.scheduling.config.ScheduledTask scheduledTask = scheduledTaskMap.get(taskId);
        if (scheduledTask != null) {
          scheduledTask.cancel();
          scheduledTaskMap.remove(taskId);
          ret = ((Future<?>) BeanUtils.getProperty(scheduledTask, "future")).isCancelled();
        } else {
          log.error("The task[{}] does not exist.", taskId);
        }
      } else {
        log.error("The system does not has any scheduledTask.");
      }
    } catch (Exception e) {
      log.error("cancelTask has a exception:{}.", e);
      ret = false;
    }
    log.info("ScheduleConfig cancelTask result: {}.", ret);
    return ret;
  }

  public boolean hasTask(int taskId) {
    return scheduledTaskMap.containsKey(taskId);
  }

  public Set<Object> getRunningTaskIds() {
    return scheduledTaskMap.keySet();
  }

  /*@SuppressWarnings("unused")
  public void addTaskAnotherMethod(int id, Task task) throws Exception {
      if (!hasTask(id)) {
          TaskScheduler scheduler = taskRegistrar.getScheduler();
          ScheduledFuture<?> future = scheduler.schedule(task.getRunnable(),
                  ((CronTask) task).getTrigger());
          // ...
      }else{
          log.error("the id[{}] has been added.", id);
      }
  }*/

  /**
   * Sử dụng sự phản chiếu để có được một danh sách các nhiệm vụ được lưu trữ bởi springboot
   * Bạn cũng có thể sử dụng taskRegistrar.getCronTaskList () / getFix ... để nhận danh sách nhiệm vụ riêng.
   *
   * @return Trả về danh sách nhiệm vụ hiện tại.
   **/
/*  public Set<Schedule> getScheduledTasks() throws NoSuchFieldException {
      if (CollectionUtils.isEmpty(scheduledTasks)) {
          try {
              scheduledTasks = (Set<Schedule>) BeanUtils.getProperty(
                      taskRegistrar, "scheduledTasks");
          } catch (NoSuchFieldException e) {
              throw new NoSuchFieldException(
                      "not found Schedule field.");
          }
      }
      return scheduledTasks;
  }*/

}

package rmhub.mod.schedule.aop;

import java.util.Date;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rmhub.mod.schedule.domain.Schedule;
import rmhub.mod.schedule.dto.ScheduleConfigDto;
import rmhub.mod.schedule.repository.ScheduleRepository;

@Aspect
@Component
@Log4j2
public class AspectScheduleRun {

  @Autowired
  ScheduleRepository scheduleRepository;

  @Pointcut("execution(public * rmhub.mod.schedule.task.RequestSender.sendRequest(..))")
  private void runAspect() {
  }

  @Around(value = "runAspect()")
  public void methodAround(ProceedingJoinPoint joinPoint) {
    log.info("Update status for cheduled task! {}",
        joinPoint.getSignature());
    try {

      // pre-process
      ScheduleConfigDto configDto = (ScheduleConfigDto) joinPoint.getArgs()[0];
      log.info("================ASPECJ===================" + configDto);

      Schedule beginSchedule = scheduleRepository.getOne(configDto.getId());
      beginSchedule.setProcState("01");
      beginSchedule.setLastExecuteBegin(new Date());
      beginSchedule.setLastExecuteEnd(null);
      scheduleRepository.save(beginSchedule);

      // method call
      Object o = joinPoint.proceed();

      // post-process
      Schedule endSchedule = scheduleRepository.getOne(configDto.getId());
      endSchedule.setProcState("00");
      endSchedule.setLastExecuteEnd(new Date());
      scheduleRepository.save(endSchedule);

    } catch (Throwable e) {
      log.error("AspectScheduleTaskRun methodAround has a Exception!", e);
    }
  }
}

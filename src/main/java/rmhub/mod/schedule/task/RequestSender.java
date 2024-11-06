package rmhub.mod.schedule.task;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import rmhub.mod.schedule.constant.ScheduleConstant;
import rmhub.mod.schedule.dto.ScheduleConfigDto;

import javax.annotation.Resource;

@Component
@Log4j2
public class RequestSender {

  @Resource
  private RestTemplate restTemplate;

  public String sendRequest(ScheduleConfigDto configDto) {
    String ret = null;
    String method = configDto.getMethod();
    String url = configDto.getUrl();
    String param = configDto.getContent();

    try {
      log.info("-----Scheduled Task [{}] start-----", url);
      if (ScheduleConstant.POST_METHOD.equals(method)) {
        if (!StringUtils.isEmpty(param)) {
          ret = restTemplate.postForObject(url, param, String.class);
        } else {
          ret = restTemplate.postForObject(url, null, String.class);
        }
      } else if (ScheduleConstant.GET_METHOD.equals(method)) {
        ret = restTemplate.getForObject(url, String.class);
      } else {
        log.error("Unknown Method:{}!", method);
      }

    } catch (Exception e) {
      log.error("-----Scheduled task ends abnormally.-----", e);
    }
    log.info("----- Scheduled task [{}] ends, result:{}-----", url, ret);
    return ret;
  }
}

package rmhub.mod.schedule.domain;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Proxy(lazy = false)
public class Schedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String mode;

  @Column(nullable = false)
  private String url;

  @Column(nullable = false)
  private String method;

  private String content;

  @Column(nullable = false)
  private String cron;

  /**
   * The delay time for the first run calculation = milliseconds eg @Scheduled(fixedRate = 2000)
   */
  private String initialDelay;

  /**
   * The time interval between runs is complete
   */
  private String fixedDelay;

  /**
   * Time interval between runs
   */
  private String fixedRate;

  private String isImdExe;

  private Date lastExecuteBegin;

  private Date lastExecuteEnd;

  private Date nextExecuteTime;

  @Size(max = 2, message = "status: 01-NOT_RUN, 02-RUNNING")
  private String status;

  private String procState;

  private String createUid;

  @CreationTimestamp
  private Timestamp createTime;

  private String lastModifyUid;

  @UpdateTimestamp
  private Date lastModifyTime;

}

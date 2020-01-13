package rmhub.mod.schedule.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ResultCode {

  SUCCESS("000000", "Successful"),
  FAILED("000002", "Failed"),
  UNKNOWN("999999", "Error");

  @Getter
  private String code;

  @Getter
  private String desc;

  public static ResultCode getRetCodeEnum(String value) {
    if (value != null) {
      for (ResultCode resultCode : values()) {
        if (resultCode.getCode().equals(value)) {
          return resultCode;
        }
      }
    }
    return null;
  }

}

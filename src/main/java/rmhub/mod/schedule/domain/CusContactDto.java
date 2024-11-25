package rmhub.mod.schedule.domain;

import lombok.*;

@Data
@Getter
@Setter
public class CusContactDto {
	
	public String contractNumber;
	
	public String customerId;
	
	public String phone;

	public String fullName;
    
	public String contactType;
    
	public String note;
    
	public Integer lineNumber;

	public CusContactDto(String contractNumber, String customerId, String phone, String fullName, String contactType, String note, Integer lineNumber) {
		this.contractNumber = contractNumber;
		this.customerId = customerId;
		this.phone = phone;
		this.fullName = fullName;
		this.contactType = contactType;
		this.note = note;
		this.lineNumber = lineNumber;
	}

	public CusContactDto() {
	}

}

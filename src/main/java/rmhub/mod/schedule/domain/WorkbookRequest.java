package rmhub.mod.schedule.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.util.List;

@Getter
@Setter
public class WorkbookRequest {
	 SXSSFSheet sheet;
	 String[] header;
	 List<String[]> books;
	 int rowIndexStart;
	 boolean writeHeader;
}

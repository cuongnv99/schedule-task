package rmhub.mod.schedule.aop;

import org.apache.commons.lang3.StringUtils;
import rmhub.mod.schedule.domain.CusContactDto;

import java.util.ArrayList;
import java.util.List;

public class testExcel {
    public static final String[] CASE_SCHEDULE_HEADER = {"no", "case_code", "installment_number", "start_date", "end_date", "principle_amt", "interest_amt", "other_fees", "sum_amt", "principle_balance"};
    public static final String[] CUSTOMER_CONTACT_HEADER = {"contract_id", "cif_number", "phone", "fullname", "contact_type", "note"};
    private final static String CHAR = ";";

    public static void main(String[] args) {

        List<String[]> books = new ArrayList<>();

        List<CusContactDto> debts = createFakeData();

        for (CusContactDto debt : debts) {
            int index = 0;
            String[] data = new String[CUSTOMER_CONTACT_HEADER.length];
            data[index++] = getCellValue(debt.getContractNumber().contains(CHAR)
                    ? StringUtils.substringAfterLast(debt.getContractNumber(), CHAR)
                    : debt.getContractNumber());
            data[index++] = getCellValue(debt.getCustomerId());
            data[index++] = getCellValue(debt.getPhone());
            data[index++] = getCellValue(debt.getFullName());
            data[index++] = getCellValue(debt.getContactType());
            data[index++] = getCellValue(debt.getNote());
            books.add(data);
        }
    }

    private static List<CusContactDto> createFakeData() {
        List<CusContactDto> contacts = new ArrayList<>();
        CusContactDto c = new CusContactDto("0101.02.000133", "01b880f0-0e83-4412-84b8-03f42936b06e", "0988726071", "Thường", "Khac", "", 18);
        CusContactDto c1 = new CusContactDto("0101.02.000133", "01b880f0-0e83-4412-84b8-03f42936b06e", "0988726071", "Thường", "Khac", "", 18);
        CusContactDto c2 = new CusContactDto("0101.02.000133", "01b880f0-0e83-4412-84b8-03f42936b06e", "0988726071", "Thường", "Khac", "", 18);
        CusContactDto c3 = new CusContactDto("0101.02.000133", "01b880f0-0e83-4412-84b8-03f42936b06e", "0988726071", "Thường", "Khac", "", 18);
        CusContactDto c4 = new CusContactDto("0101.02.000133", "01b880f0-0e83-4412-84b8-03f42936b06e", "0988726071", "Thường", "Khac", "", 18);
        contacts.add(c);
        contacts.add(c1);
        contacts.add(c2);
        contacts.add(c3);
        contacts.add(c4);
        return contacts;
    }

    private static String getCellValue(String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        return value;
    }
}

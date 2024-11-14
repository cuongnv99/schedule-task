package rmhub.mod.schedule.aop;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DynamicQRGenerator {
    // Dịch vụ chuyển tiền nhanh NAPAS247 đến tài khoản
    public static final String QRIBFTTA = "QRIBFTTA";
    // Dịch vụ chuyển tiền nhanh NAPAS247 đến thẻ
    public static final String QRIBFTTC = "QRIBFTTC";
    private static final int POLYNOMIAL = 0x11021; // CRC-16-CCITT-FALSE
    private static final int INITIAL_VALUE = 0xFFFF; // Initial value for CRC-16-CCITT

    public static void main(String[] args) throws Exception {

        //"00" là mã định danh cho Payload Format Indicator, xác định định dạng của payload (dữ liệu trong mã QR).
        //"02" là độ dài của giá trị sau mã định danh.
        //"01" là giá trị thực tế của chỉ báo, cho biết phiên bản của dữ liệu QR đang được sử dụng
        // (trong trường hợp này, phiên bản là 1.0 của chuẩn EMVCo QR).
        String payloadFormatIndicator = "000201";
        //"01" là mã định danh cho Point of Initiation Method, chỉ ra phương thức khởi tạo của giao dịch (nơi giao dịch được bắt đầu).
        //"02" là độ dài của giá trị sau mã định danh.
        //"12" là giá trị của phương thức khởi tạo, thể hiện phương thức khởi tạo là QR Code trực tiếp
        // (nghĩa là người thanh toán sẽ quét mã QR để thực hiện giao dịch).
        String pointOfInitiationMethod = "010212";
        //"00" là mã định danh cho GUID, chỉ ra rằng phần tiếp theo sẽ là định danh ứng dụng.
        //getLengthAndValue("A000000727"): Hàm getLengthAndValue được sử dụng để tạo độ dài và giá trị của GUID.
        //"A000000727" là AID (Application Identifier) của hệ thống Napas.
        // Đây là mã định danh ứng dụng, thể hiện dịch vụ hoặc ứng dụng cụ thể trong hệ thống thanh toán.
        String guid = "00" + getLengthAndValue("A000000727");

        //"00": Đây là mã định danh cho trường Acquirer ID.
        //getLengthAndValue(NapasBank.BIDV.getAcquirerId()): Phương thức này sẽ lấy giá trị của Acquirer ID cho ngân hàng BIDV từ đối tượng NapasBank.BIDV,
        // sau đó tính độ dài của giá trị và kết hợp với giá trị thực tế của Acquirer ID.
        String acquirerId = "00" + getLengthAndValue(NapasBank.MSBANK.getAcquirerId()); // Acquirer ID
        //"01": Đây là mã định danh cho trường Consumer ID, chỉ ra rằng phần tiếp theo sẽ là mã định danh của người tiêu dùng.
        //getLengthAndValue("12010006991869"): Hàm getLengthAndValue lấy giá trị của Consumer ID (trong trường hợp này là "12010006991869")
        // và trả về độ dài của giá trị này, kết hợp với giá trị thực tế.
        String consumerId = "01" + getLengthAndValue("0705522899"); // Consumer ID
        //"02": Đây là mã định danh cho Mã dịch vụ. Trường này chỉ ra rằng phần tiếp theo trong mã QR sẽ là mã dịch vụ.
        //getLengthAndValue("QRIBFTTA"): Hàm getLengthAndValue sẽ lấy giá trị của Mã dịch vụ (trong trường hợp này là "QRIBFTTA")
        // và trả về độ dài của giá trị này, kết hợp với giá trị thực tế của mã dịch vụ.
        String serviceCode = "02" + getLengthAndValue(QRIBFTTA); // Mã dịch vụ

        String strTotalAccount = "01"+ getLengthAndValue(acquirerId+consumerId);

        // Kết hợp các trường vào consumerAccountInformation
        String consumerAccountInformation = "38" + getLengthAndValue(guid + strTotalAccount + serviceCode);

        //"53" là mã định danh trường dữ liệu cho mã tiền tệ trong chuẩn EMVCo QR.
        //"03" là độ dài của giá trị tiền tệ (mã tiền tệ có 3 ký tự).
        //"704" là mã ISO 4217 dành cho Việt Nam Đồng (VND).
        String transactionCurrency = "5303704";
        //"54" là mã định danh trường dữ liệu cho số tiền giao dịch trong chuẩn EMVCo QR.
        //"06" là độ dài của giá trị số tiền (ở đây là 6 ký tự).
        //"180000" là số tiền giao dịch, được biểu diễn dưới dạng số nguyên.
        String transactionAmount = "5406180000";
        //"58" là mã định danh cho trường dữ liệu "Mã quốc gia" theo chuẩn EMVCo QR.
        //"02" là độ dài của giá trị quốc gia ("VN" có 2 ký tự).
        //"VN" là mã quốc gia ISO 3166-1 alpha-2 dành cho Việt Nam.
        String countryCode = "5802VN";
        //"62" là mã định danh trường dữ liệu cho thông tin bổ sung theo chuẩn EMVCo QR.
        //"34" là độ dài của toàn bộ chuỗi dữ liệu bổ sung này (34 ký tự).
        //"0107NPS6869": Mã tham chiếu hoặc mã định danh cho giao dịch.
        //"0819thanh toan don hang": Mô tả của giao dịch, trong trường hợp này là "thanh toán đơn hàng".
        String additionalDataField = "62340107NPS68690819thanh toan don hang";

        // Chuỗi QR code không có CRC
        String qrDataWithoutCRC = payloadFormatIndicator +
                pointOfInitiationMethod +
                consumerAccountInformation +
                transactionCurrency +
                transactionAmount +
                countryCode +
                additionalDataField +
                "6304";  // CRC ID (63) và độ dài (04) là placeholder

        // Tính toán CRC cho QR data
        String crc = calculateCRC(qrDataWithoutCRC);

        // Chuỗi QR code hoàn chỉnh có CRC
//
//        String qrDataWithoutCRC = "00020101021238580010A000000727012700069704180114120100069918690208QRIBFTTA530370454061800005802VN62340107NPS68690819thanh toan don hang63";
//        String crc = calculateCRC(qrDataWithoutCRC);
        String qrDataWithCRC = qrDataWithoutCRC + crc;
        System.out.println("Generated QR Code Data: " + qrDataWithCRC);
        DynamicQRGenerator generator = new DynamicQRGenerator();
        generator.generateQrCode(qrDataWithCRC, "dynamicxxx.png");
    }

    // Hàm phụ để tính độ dài và nối với giá trị
    private static String getLengthAndValue(String value) {
        String length = String.format("%02d", value.length());
        return length + value;
    }

    public static int compute(byte[] data) {
        int crc = INITIAL_VALUE;
        for (byte b : data) {
            crc ^= (b << 8);
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ POLYNOMIAL;
                } else {
                    crc = crc << 1;
                }
            }
        }
        return crc & 0xFFFF; // Return the lower 16 bits of CRC
    }

    public static String calculateCRC(String qrData) {
        byte[] data = qrData.getBytes(StandardCharsets.UTF_8); // Explicit charset
        int crc = compute(data);
        return String.format("%04X", crc); // Return as 4-digit hex
    }

    public void generateQrCode(String content, String filePath) throws Exception {
        int width = 300;
        int height = 300;

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        // Ensure the directory exists
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        Path path = file.toPath();
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }


}


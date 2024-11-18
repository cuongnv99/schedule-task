package rmhub.mod.schedule.aop;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
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
        String acquirerId = "00" + getLengthAndValue(NapasBank.BIDV.getAcquirerId()); // Acquirer ID
        //"01": Đây là mã định danh cho trường Consumer ID, chỉ ra rằng phần tiếp theo sẽ là mã định danh của người tiêu dùng.
        //getLengthAndValue("12010006991869"): Hàm getLengthAndValue lấy giá trị của Consumer ID (trong trường hợp này là "12010006991869")
        // và trả về độ dài của giá trị này, kết hợp với giá trị thực tế.
        String consumerId = "01" + getLengthAndValue("12010006991869"); // Consumer ID
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
        String qrDataWithCRC = qrDataWithoutCRC + crc;
        System.out.println("Generated QR Code Data: " + qrDataWithCRC);

//        String qrDataWithCRC = "Your_QR_Data_Here";
        String filePath = "dynamic_qr_with_icon.png";
//        String iconPath = "khabanh.png"; // Đường dẫn đến file icon
        String text = "Khánh Sky! \n"+"Trùm kéo view số 1 VN";
        String gifPath = "Kh_B_nh_B_ch_i_l_t_c_nh_l_ng_l_n.gif";
        DynamicQRGenerator generator = new DynamicQRGenerator();
        generator.generateQrWithGifIcon(qrDataWithCRC,filePath, gifPath);

        System.out.println("QR Code with icon generated at: " + filePath);


//        DynamicQRGenerator generator = new DynamicQRGenerator();
//        generator.generateQrCodeWithIcon(qrDataWithCRC, "dynamicxxx.png");
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

//    public void generateQrCode(String content, String filePath) throws Exception {
//        int width = 300;
//        int height = 300;
//
//        Map<EncodeHintType, Object> hints = new HashMap<>();
//        hints.put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H);
//
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
//
//        // Ensure the directory exists
//        File file = new File(filePath);
//        File parentDir = file.getParentFile();
//        if (parentDir != null && !parentDir.exists()) {
//            parentDir.mkdirs();
//        }
//
//        Path path = file.toPath();
//        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
//    }

//    public void generateQrCodeWithIcon(String content, String filePath, String iconPath) throws Exception {
//        int width = 300;
//        int height = 300;
//
//        // Tạo mã QR
//        Map<EncodeHintType, Object> hints = new HashMap<>();
//        hints.put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H);
//
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
//
//        // Chuyển đổi BitMatrix thành BufferedImage
//        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
//
//        // Tải icon
//        BufferedImage icon = ImageIO.read(new File(iconPath));
//
//        // Điều chỉnh kích thước của icon
//        int iconWidth = qrImage.getWidth() / 5;  // Icon chiếm 1/5 kích thước mã QR
//        int iconHeight = qrImage.getHeight() / 5;
//        Image scaledIcon = icon.getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH);
//
//        // Vẽ icon lên mã QR
//        Graphics2D graphics = qrImage.createGraphics();
//        int centerX = (qrImage.getWidth() - iconWidth) / 2; // Xác định vị trí trung tâm
//        int centerY = (qrImage.getHeight() - iconHeight) / 2;
//        graphics.drawImage(scaledIcon, centerX, centerY, null);
//        graphics.dispose();
//
//        // Đảm bảo thư mục lưu trữ tồn tại
//        File file = new File(filePath);
//        File parentDir = file.getParentFile();
//        if (parentDir != null && !parentDir.exists()) {
//            parentDir.mkdirs();
//        }
//
//        // Ghi ảnh có chứa icon vào file
//        ImageIO.write(qrImage, "PNG", file);
//    }

//    public void generateQrCodeWithColorIcon(String content, String filePath, String iconPath) throws Exception {
//        int width = 700;
//        int height = 700;
//
//        // Tạo mã QR
//        Map<EncodeHintType, Object> hints = new HashMap<>();
//        hints.put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H);
//
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
//
//        // Chuyển đổi BitMatrix thành BufferedImage
//        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
//
//        // Tải biểu tượng (icon) từ file
//        BufferedImage icon = ImageIO.read(new File(iconPath));
//
//        // Tính toán kích thước icon để chèn vào trung tâm mã QR
//        int iconWidth = qrImage.getWidth() / 2;  // Icon chiếm 1/5 kích thước mã QR
//        int iconHeight = qrImage.getHeight() / 2;
//
//        // Chỉnh kích thước icon
//        Image scaledIcon = icon.getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH);
//        BufferedImage coloredIcon = new BufferedImage(iconWidth, iconHeight, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2dIcon = coloredIcon.createGraphics();
//        g2dIcon.drawImage(scaledIcon, 0, 0, null);
//        g2dIcon.dispose();
//
//        // Vẽ icon vào giữa mã QR
//        Graphics2D graphics = qrImage.createGraphics();
//        graphics.setComposite(AlphaComposite.SrcOver); // Đảm bảo giữ màu icon
//        int centerX = (qrImage.getWidth() - iconWidth) / 2; // Tọa độ X trung tâm
//        int centerY = (qrImage.getHeight() - iconHeight) / 2; // Tọa độ Y trung tâm
//        graphics.drawImage(coloredIcon, centerX, centerY, null);
//        graphics.dispose();
//
//        // Đảm bảo thư mục lưu trữ tồn tại
//        File file = new File(filePath);
//        File parentDir = file.getParentFile();
//        if (parentDir != null && !parentDir.exists()) {
//            parentDir.mkdirs();
//        }
//
//        // Lưu mã QR với icon vào file
//        ImageIO.write(qrImage, "PNG", file);
//    }
//public void generateQrCodeWithColorIcon(String content, String filePath, String iconPath) throws Exception {
//    int width = 300;
//    int height = 300;
//
//    // Tạo mã QR
//    Map<EncodeHintType, Object> hints = new HashMap<>();
//    hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
//
//    QRCodeWriter qrCodeWriter = new QRCodeWriter();
//    BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
//
//    // Chuyển BitMatrix thành BufferedImage hỗ trợ màu
//    BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//    for (int x = 0; x < width; x++) {
//        for (int y = 0; y < height; y++) {
//            qrImage.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
//        }
//    }
//
//    // Tải biểu tượng (icon) và đảm bảo định dạng hỗ trợ màu
//    BufferedImage icon = ImageIO.read(new File(iconPath));
//    if (icon.getType() != BufferedImage.TYPE_INT_ARGB) {
//        BufferedImage temp = new BufferedImage(icon.getWidth(), icon.getHeight(), BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2d = temp.createGraphics();
//        g2d.drawImage(icon, 0, 0, null);
//        g2d.dispose();
//        icon = temp;
//    }
//
//    // Tính toán kích thước và chèn biểu tượng
//    int iconWidth = qrImage.getWidth() / 5;  // Icon chiếm 1/5 mã QR
//    int iconHeight = qrImage.getHeight() / 5;
//    int centerX = (qrImage.getWidth() - iconWidth) / 2;
//    int centerY = (qrImage.getHeight() - iconHeight) / 2;
//
//    Graphics2D graphics = qrImage.createGraphics();
//    graphics.setComposite(AlphaComposite.SrcOver); // Hỗ trợ màu và độ trong suốt
//    graphics.drawImage(icon.getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH), centerX, centerY, null);
//    graphics.dispose();
//
//    // Lưu mã QR với biểu tượng
//    ImageIO.write(qrImage, "PNG", new File(filePath));
//}


//        public void generateQrCodeWithTextAndIcon(String content, String filePath, String iconPath, String text) throws Exception {
//            int qrWidth = 300;
//            int qrHeight = 300;
//            int padding = 20; // Khoảng cách giữa mã QR và văn bản
//
//            // Tạo mã QR
//            Map<EncodeHintType, Object> hints = new HashMap<>();
//            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
//
//            QRCodeWriter qrCodeWriter = new QRCodeWriter();
//            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);
//
//            // Chuyển BitMatrix thành BufferedImage
//            BufferedImage qrImage = new BufferedImage(qrWidth, qrHeight, BufferedImage.TYPE_INT_RGB);
//            for (int x = 0; x < qrWidth; x++) {
//                for (int y = 0; y < qrHeight; y++) {
//                    qrImage.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
//                }
//            }
//
//            // Tải biểu tượng (icon)
//            BufferedImage icon = ImageIO.read(new File(iconPath));
//            int iconWidth = qrImage.getWidth() / 5;
//            int iconHeight = qrImage.getHeight() / 5;
//            int centerX = (qrImage.getWidth() - iconWidth) / 2;
//            int centerY = (qrImage.getHeight() - iconHeight) / 2;
//
//            // Chèn biểu tượng vào QR
//            Graphics2D qrGraphics = qrImage.createGraphics();
//            qrGraphics.drawImage(icon.getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH), centerX, centerY, null);
//            qrGraphics.dispose();
//
//            // Tạo hình ảnh lớn hơn để chứa cả QR và văn bản
//            int totalHeight = qrHeight + padding + 30; // 30 là chiều cao ước tính của văn bản
//            BufferedImage finalImage = new BufferedImage(qrWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
//
//            Graphics2D g = finalImage.createGraphics();
//
//            // Vẽ nền trắng
//            g.setColor(Color.WHITE);
//            g.fillRect(0, 0, qrWidth, totalHeight);
//
//            // Vẽ mã QR
//            g.drawImage(qrImage, 0, 0, null);
//
//            // Vẽ văn bản bên dưới mã QR
//            g.setColor(Color.BLACK);
//            g.setFont(new Font("Arial", Font.PLAIN, 16));
//            FontMetrics fontMetrics = g.getFontMetrics();
//            int textWidth = fontMetrics.stringWidth(text);
//            int textX = (qrWidth - textWidth) / 2;
//            int textY = qrHeight + padding + fontMetrics.getAscent();
//
//            g.drawString(text, textX, textY);
//            g.dispose();
//
//            // Lưu hình ảnh cuối cùng
//            ImageIO.write(finalImage, "PNG", new File(filePath));
//        }
public void generateQrWithGifIcon(String content, String filePath, String gifPath) throws Exception {
    int qrWidth = 300;
    int qrHeight = 300;

    // Tạo QR Code
    Map<EncodeHintType, Object> hints = new HashMap<>();
    hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);

    BufferedImage qrImage = new BufferedImage(qrWidth, qrHeight, BufferedImage.TYPE_INT_RGB);
    for (int x = 0; x < qrWidth; x++) {
        for (int y = 0; y < qrHeight; y++) {
            qrImage.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
        }
    }

    // Lấy khung đầu tiên của GIF
    BufferedImage gifFrame = extractFirstFrameFromGif(gifPath);
    if (gifFrame == null) {
        throw new RuntimeException("Cannot read GIF file or extract first frame.");
    }

    // Resize GIF frame để phù hợp với QR
    int iconWidth = qrImage.getWidth() / 5;
    int iconHeight = qrImage.getHeight() / 5;
    Image scaledGifFrame = gifFrame.getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH);

    // Chèn GIF frame vào QR
    Graphics2D qrGraphics = qrImage.createGraphics();
    int centerX = (qrImage.getWidth() - iconWidth) / 2;
    int centerY = (qrImage.getHeight() - iconHeight) / 2;
    qrGraphics.drawImage(scaledGifFrame, centerX, centerY, null);
    qrGraphics.dispose();



    // Lưu QR với GIF
    ImageIO.write(qrImage, "PNG", new File(filePath));
    System.out.println("QR code with GIF icon saved to: " + filePath);
}

    private BufferedImage extractFirstFrameFromGif(String gifPath) throws Exception {
        File gifFile = new File(gifPath);
        ImageInputStream inputStream = ImageIO.createImageInputStream(gifFile);
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");

        if (readers.hasNext()) {
            ImageReader reader = readers.next();
            reader.setInput(inputStream);

            return reader.read(0); // Đọc khung đầu tiên
        }
        return null;
    }
}


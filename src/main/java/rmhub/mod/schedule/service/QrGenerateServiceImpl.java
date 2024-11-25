package rmhub.mod.schedule.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rmhub.mod.schedule.enums.QrGenerateData;
import rmhub.mod.schedule.util.*;

import java.awt.image.BufferedImage;


@Service
@Slf4j
public class QrGenerateServiceImpl implements QrGenerateService {

    @Override
    public String generateQr(String bank,String account, String amount) throws Exception {

        String acquirerId = QrConstants.IDENTIFIER + QrDataBuilder.getLengthAndValue(QrGenerateData.valueOf(bank).getAcquirerId());
        String consumerId = QrConstants.VALUE + QrDataBuilder.getLengthAndValue("12010006991869");
        String strTotalAccount = QrConstants.VALUE + QrDataBuilder.getLengthAndValue(acquirerId + consumerId);
        String consumerAccountInformation = "38" + QrDataBuilder.getLengthAndValue(QrConstants.GUID + strTotalAccount + QrConstants.SERVICE_CODE);
        String transactionAmount ="54"+QrDataBuilder.getLengthAndValue(amount);
        String additionalDataField = "62340107NPS68690819thanh toan don hang";

        String qrDataWithoutCRC = QrDataBuilder.buildQrData(
                QrConstants.PAYLOAD, QrConstants.POINT_OF_METHOD, consumerAccountInformation,
                QrConstants.TRAN_CURRENCY, transactionAmount, QrConstants.COUNTRY_CODE, additionalDataField).toString();

        String crc = CrcCalculator.calculateCRC(qrDataWithoutCRC);
        String qrDataWithCRC = qrDataWithoutCRC + crc;

        BufferedImage qrImage = QrCodeGenerator.generateQrCode(qrDataWithCRC);
        ImageUtils.overlayIcon(qrImage, "Icontnex.png");
        ImageUtils.saveImage(qrImage, "dynamic_qr_with.png");
        return qrDataWithCRC;
    }
}

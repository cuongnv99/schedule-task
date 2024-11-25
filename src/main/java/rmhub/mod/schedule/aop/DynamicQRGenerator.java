package rmhub.mod.schedule.aop;

import rmhub.mod.schedule.service.QrGenerateService;
import rmhub.mod.schedule.service.QrGenerateServiceImpl;

public class DynamicQRGenerator {

    public static void main(String[] args) throws Exception {

        QrGenerateService qrGenerateService = new QrGenerateServiceImpl();
        String qr = qrGenerateService.generateQr("BIDV", "12010006991869", "500000");
        System.out.println("QR Code with icon generated at: " + qr);
    }

}


package rmhub.mod.schedule.service;

public interface QrGenerateService {
    String generateQr(String bank,String account, String amount) throws Exception;
}

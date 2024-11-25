package rmhub.mod.schedule.util;


public class QrDataBuilder {
    public static String getLengthAndValue(String value) {
        String length = String.format("%02d", value.length());
        return length + value;
    }

    public static StringBuilder buildQrData(String payloadFormatIndicator, String pointOfInitiationMethod, String consumerAccountInformation,
                                     String transactionCurrency, String transactionAmount, String countryCode, String additionalDataField) {
     return new StringBuilder().append(payloadFormatIndicator)
             .append(pointOfInitiationMethod)
             .append(consumerAccountInformation)
             .append(transactionCurrency)
             .append(transactionAmount)
             .append(countryCode)
             .append(additionalDataField)
             .append(QrConstants.CRC_ID);
    }
}


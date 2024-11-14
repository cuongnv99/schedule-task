package rmhub.mod.schedule.aop;

public enum NapasBank {
    VIETCOMBANK("Vietcombank", "970436"),
    VIETINBANK("VietinBank", "970415"),
    BIDV("BIDV", "970418"),
    AGRIBANK("Agribank", "970405"),
    SACOMBANK("Sacombank", "970403"),
    TECHCOMBANK("Techcombank", "970407"),
    ACB("ACB", "970416"),
    VPBANK("VPBank", "970432"),
    TPBANK("TPBank", "970423"),
    MB("MB", "970422"),
    SHB("SHB", "970443"),
    SCB("SCB", "970429"),
    OCB("OCB", "970448"),
    HDBANK("HDBank", "970437"),
    NAMABANK("Nam A Bank", "970428"),
    SEABANK("SeABank", "970440"),
    VIB("VIB", "970441"),
    ABBANK("ABBank", "970425"),
    PVCOMBANK("PVcomBank", "970421"),
    EXIMBANK("Eximbank", "970431"),
    MSBANK("MSB", "970426"),
    KIENLONGBANK("KienlongBank", "970452"),
    BAOVIETBANK("BaoVietBank", "970444");

    private final String bankName;
    private final String acquirerId;

    NapasBank(String bankName, String acquirerId) {
        this.bankName = bankName;
        this.acquirerId = acquirerId;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAcquirerId() {
        return acquirerId;
    }

    public static NapasBank fromAcquirerId(String acquirerId) {
        for (NapasBank bank : values()) {
            if (bank.acquirerId.equals(acquirerId)) {
                return bank;
            }
        }
        return null; // hoặc có thể ném ngoại lệ nếu không tìm thấy
    }

    @Override
    public String toString() {
        return bankName + " (Acquirer ID: " + acquirerId + ")";
    }
}

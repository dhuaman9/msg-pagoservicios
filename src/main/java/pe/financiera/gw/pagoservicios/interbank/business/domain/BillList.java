package pe.financiera.gw.pagoservicios.interbank.business.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@Builder
public class BillList {

    private Client client;
    private List<Bill> bills;

    public List<Bill> getBillsOrEmpty() {
        return bills != null ? bills : Collections.emptyList();
    }

    @Data
    @Builder
    public static class Client {
        private String id;
        private String name;
    }
}

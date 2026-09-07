package pe.financiera.gw.pagoservicios.interbank.business.output;

import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransactionEvent;

/**
 * Puerto de salida: publicar resultado del pago hacia el BS (bill-completed-v2).
 */
public interface BillCompletedPublisherPort {

    void publish(TransactionEvent event);
}

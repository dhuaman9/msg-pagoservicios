package pe.financiera.gw.pagoservicios.interbank.business.output;

import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransactionEvent;

/**
 * Puerto de salida: publicar evento de verificación de estado (check-bill-status-v2).
 */
public interface CheckBillStatusPublisherPort {

    void publish(TransactionEvent event);
}

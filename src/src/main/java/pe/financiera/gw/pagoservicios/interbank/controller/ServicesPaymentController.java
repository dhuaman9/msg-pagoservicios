package pe.financiera.gw.pagoservicios.interbank.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.financiera.gw.pagoservicios.interbank.business.domain.BillList;
import pe.financiera.gw.pagoservicios.interbank.business.domain.PaymentV2;
import pe.financiera.gw.pagoservicios.interbank.business.input.BillPaymentService;
import pe.financiera.gw.pagoservicios.interbank.controller.dto.BillsApiResponse;
import pe.financiera.gw.pagoservicios.interbank.controller.mapper.BillsApiMapper;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import pe.financiera.gw.pagoservicios.util.exception.dto.ApiErrorResponse;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(value = "/payment")
@Tag(name = "ServicesPayment", description = "Operaciones de pago de servicios vía Interbank")
@Validated
public class ServicesPaymentController {

    private static final String LOG_PREFIX = "GW_SERVICE_PAY";

    private final BillPaymentService billPaymentService;
    private final BillsApiMapper billsApiMapper;

    public ServicesPaymentController(final BillPaymentService billPaymentService,
                                     final BillsApiMapper billsApiMapper) {
        this.billPaymentService = billPaymentService;
        this.billsApiMapper = billsApiMapper;
    }

    @Operation(summary = "Confirmar pago de factura V2")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago recibido y encolado para procesamiento"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "502", description = "Error retornado por Interbank",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del gateway",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/v2/billing")
    public void makeBillPaymentV2(@RequestBody PaymentV2 payment) throws IOException, RestClientException, InterbankApiException {
        log.info("{}_CONFIRMATION_STEP_4.9.1_REQUEST_RECEIVED: correlationId={} recipientId={}", LOG_PREFIX, payment.getCorrelationId(), payment.getRecipientId());
        billPaymentService.makePaymentV2(payment);
    }

    @Operation(summary = "Consultar facturas pendientes de un servicio")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de facturas",
            content = @Content(schema = @Schema(implementation = BillsApiResponse.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "502", description = "Error retornado por Interbank",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del gateway",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/recipient/{recipientId}/service/{serviceId}/bills")
    public BillsApiResponse getBills(
        @PathVariable @NotBlank String recipientId,                //  @NotBlank
        @PathVariable @NotBlank String serviceId,                  //  @NotBlank
        @RequestParam @NotBlank String clientId) throws IOException, RestClientException, InterbankApiException {
        log.info("{}_PRE_CONFIRMATION_STEP_3.2.1_REQUEST_RECEIVED: recipientId={} serviceId={} clientId={}", LOG_PREFIX, recipientId, serviceId, clientId);
        BillList billList = billPaymentService.getBills(clientId, recipientId, serviceId);
        return billsApiMapper.toApiResponse(billList);
    }
}

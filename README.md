## Microservicio de Gateway Interbank

El microservicio provee una interfaz de integracion hacia los servicio se OpenBanking de Interbank para poder consumir los servicios de Pago de Servicios y Transferencias

## Variables de Entorno

| Variable | Valor por Defecto Productivo | Descripccion | Ejemplos | Requerido |
| -------- | ----------------- |------------- | -------- | --------- |
| APP_PORT | 8080 | Puerto por defecto del Proyecto | 80, 8082 | No |
| APP_RELEASE | 0 | Número de liberación | 1, 2, 98| No |
| APP_ENVIRONMENT | dev | Variable que determina el entorno de la aplicacion | dev, qa, uat y pro | No |
| LOGGER_DIR  | var/log/us        | Directorio de almacenamiento de log | /apps | Si |
| LOG_LEVEL | INFO | Directiva para determinar que tipo de logs mostrar | DEBUG, ERROR, FATAL | No |
| MANAGEMENT_ENDPOINTS | refresh, health | Endpoints de actuator que seran visibles | refresh, environment | No |
| OPENBANKING_BASE_URL | https://apidev.digital.interbank.pe | Endpoint de los servicios de OpenBanking | https://apidev.digital.interbank.pe | Si |
| OPENBANKING_CONNECT_TIMEOUT | 100 | Tiempo maximo de conexion para OpenBanking | 30, 40, 50 | Si |
| OPENBANKING_READ_TIMEOUT | 100 | Tiempo maximo de lectura de conexion para OpenBanking | 30, 40, 50 | Si |
| OPENBANKING_WRITE_TIMEOUT | 100 | Tiempo maximo de escritura de conexion para OpenBanking | 30, 40, 50 | Si |
| OPENBANKING_MAX_REQUEST | 100 | Maxima cantidad de requests para OpenBanking | 10, 500, 34 | Si |
| OPENBANKING_APPLICATION_ID | | Identificador de la aplicacion que esta registrada en el sistema OpenBanking | abc123 | Si |
| OPENBANKING_ACCOUNT_ID | | Numero de la cuenta bancaria Interbank, puede estar cifrada como no deacuerdon con la configuracion solicitada | 8989900222221 | Si |
| OPENBANKING_CUSTOMER_ID | | Identificador del cliente de la cuenta bancaria de interbank| 12313123 | Si |
| OPENBANKING_SCOPE |  | Contexto de uso de las credenciales generadas y en formato URL Encode | token%3Aapplication | No |
| OPENBANKING_PASSWORD | | Password para generar el token de acceso a las servicios de OpenBanking | ******** | Si |
| OPENBANKING_GRANT_TYPE | | Tipo de acceso solicitado en la generacion del token | client_credentials | Si |
| OPENBANKING_BILLING_SUBSCRIPTION_KEY | | Key de acceso para poder realizar operaciones sobre el servicio Billing de OpenBanking| true, false | Si |
| OPENBANKING_ONBOARDING_SUBSCRIPTION_KEY | | Key de acceso para poder realizar operaciones sobre el servicio Onboarding OpenBanking| true, false | Si |
| OPENBANKING_TRANSACTION_SUBSCRIPTION_KEY | | Key de acceso para poder realizar operaciones sobre el servicio Transactions OpenBanking| true, false | Si |
| GCP_CREDENTIALS | | Credenciales de acceso en formato encode64 para conectarse con lo servicios de GCP| | Si |
| GCP_PROJECT_ID | | Id del proyecto GCP| pe-grupo-intercorp-cld-01 | Si |
| GCP_INTERBANK_GATEWAY_PAYMENT_SUBSCRIPTION | | Nombre del subscriptor en PubSub GCP para el proceso de pagos| local-gwpagoservicios-payment-subscription | Si |
| GCP_INTERBANK_GATEWAY_DIRECT_PAYMENT_SUBSCRIPTION | | Nombre del subscriptor en PubSub GCP para procesar recargas| local-gwpagoservicios-direct-payment-subscription | Si |
| GCP_INTERBANK_GATEWAY_TRANSACTION_SUBSCRIPTION | | Nombre del subscriptor en PubSub GCP para procesar transferencias| local-gwpagoservicios-transaction-subscription | Si |

## Empezando

Estas instrucciones le permitirán obtener una copia del proyecto en funcionamiento en su máquina local para fines de desarrollo y prueba. Consulte la implementación para ver las notas sobre cómo implementar el proyecto en un sistema en vivo.

###Conexion a dev
```
kubectl port-forward pods/gwpagoservicios-deployment-59bc98d8b-zjqtp 8080:8080 -n tunki
```

### Swagger

```
https://app.swaggerhub.com/apis/InDigitalPe/gwpagoservicios/1.0.0
```

### Prerrequisitos

Qué cosas necesita para instalar el software y cómo instalarlo

```
mvn --version
```

### Instalación

Una serie paso a paso de ejemplos que le indican cómo ejecutar un entorno de desarrollo.

Di cuál será el paso

```
mvn clean install
```
Termine con un ejemplo de cómo sacar algunos datos del sistema o usarlos para una pequeña demostración.

## Ejecutando los test

Explicar cómo ejecutar las pruebas automatizadas para este sistema.

### Desglosar las pruebas

Explica qué pruebas y por qué

```
mvn test 
```

### Estilo de codificación

Explicar un poco sobre. editorconfig

## Despliegue

Azure DevOps

## Construir con

* [Maven](https://maven.apache.org/) - Gestionado de dependencias

## Versiones

| Versión | Fecha | Resumen |
| --- | --- | --- |
| 1.0.0  | 04/2019 | Pasarela de pagos en línea|

## Autores

* **Carlos Zarate** - *Trabajo inicial* - carloszarate@indigital.pe

---

## Sistema de Logs por Steps — Service Pay GW

> Este documento describe los steps que corresponden únicamente a la capa **GW**. Para ver el flujo completo (UX → BS → GW → EXT) consultar el README de `us-ux-service-pay`.

### Convención de nomenclatura

```
GW_SERVICE_PAY_{OPERACION}_STEP_{N.M.K}_{DESCRIPCION}
```

Los números de step en GW son de 2 decimales (`N.M.K`) donde `N.M` corresponde al step del BS que disparó la llamada.

### Endpoints expuestos

| Método | Path | Operación |
|--------|------|-----------|
| `GET` | `/interbank-gateway/recipient/{id}/service/{id}/bills` | PRE_CONFIRMATION |
| `POST` | `/interbank-gateway/v2/billing` | CONFIRMATION |

---

### PRE_CONFIRMATION — steps GW

| Orden | Step | Nivel | Clase |
|-------|------|-------|-------|
| 3.2.1 | `GW_SERVICE_PAY_PRE_CONFIRMATION_STEP_3.2.1_REQUEST_RECEIVED` | INFO | `ServicesPaymentController` |
| 3.2.2 | `GW_SERVICE_PAY_PRE_CONFIRMATION_STEP_3.2.2_CALLING_IBK_BILLS` | INFO | `BillPaymentImpl` |
| 3.2.3 | `GW_SERVICE_PAY_PRE_CONFIRMATION_STEP_3.2.3_CALLING_IBK_API` | INFO | `BillPaymentRestClientAdapter` |
| 3.2.3e | `GW_SERVICE_PAY_PRE_CONFIRMATION_STEP_3.2.3_ERROR_IBK_API` | ERROR | `BillPaymentRestClientAdapter` |
| 3.2.4 | `GW_SERVICE_PAY_PRE_CONFIRMATION_STEP_3.2.4_END_COMPLETED` | INFO | `BillPaymentImpl` |

---

### CONFIRMATION — steps GW

| Orden | Step | Nivel | Clase |
|-------|------|-------|-------|
| 4.9.1 | `GW_SERVICE_PAY_CONFIRMATION_STEP_4.9.1_REQUEST_RECEIVED` | INFO | `ServicesPaymentController` |
| 4.9.2 | `GW_SERVICE_PAY_CONFIRMATION_STEP_4.9.2_CALLING_IBK_GET_BILLS` | INFO | `BillPaymentImpl` |
| 4.9.2e | `GW_SERVICE_PAY_CONFIRMATION_STEP_4.9.2_ERROR_IBK_GET_BILLS` | ERROR | `BillPaymentRestClientAdapter` |
| 4.9.3 | `GW_SERVICE_PAY_CONFIRMATION_STEP_4.9.3_CALLING_IBK_PAY_BILLING` | INFO | `BillPaymentImpl` |
| 4.9.3e | `GW_SERVICE_PAY_CONFIRMATION_STEP_4.9.3_ERROR_IBK_PAY_BILLING` | ERROR | `BillPaymentRestClientAdapter` |
| 4.9.3t | `GW_SERVICE_PAY_CONFIRMATION_STEP_4.9.3_ERROR_IBK_TIMEOUT` | ERROR | `BillPaymentRestClientAdapter` |
| 4.9.4 | `GW_SERVICE_PAY_CONFIRMATION_STEP_4.9.4_PUBLISH_CHECK_STATUS` | INFO | `BillPaymentImpl` |
| 4.9.4e | `GW_SERVICE_PAY_CONFIRMATION_STEP_4.9.4_ERROR_PUBLISH` | ERROR | `BillPaymentImpl` |
| 4.9.5 | `GW_SERVICE_PAY_CONFIRMATION_STEP_4.9.5_END_COMPLETED` | INFO | `BillPaymentImpl` |

> `4.9.4_PUBLISH_CHECK_STATUS`: publica el evento en PubSub para que el consumer `CheckBillStatusV2Consumer` verifique asíncronamente el estado del pago consultando `GET /applications/v1/correlations/{correlationId}` en Interbank.

> Para el flujo completo (UX → BS → GW) ver README de `us-ux-service-pay`.


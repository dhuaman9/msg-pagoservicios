package pe.financiera.gw.pagoservicios.config.pubsub.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.pubsub.v1.ProjectSubscriptionName;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.threeten.bp.Duration;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransactionEvent;
import pe.financiera.gw.pagoservicios.queue.service.payment.CheckBillStatusV2Consumer;
import pe.financiera.framework.pubsub.messaging.ProcessingResult;
import pe.financiera.framework.pubsub.queue.consumer.SubscriberHandler;
import pe.financiera.framework.pubsub.queue.consumer.message.DeduplicationMessageReceiver;
import pe.financiera.framework.pubsub.queue.consumer.message.MessageContextParser;

@Configuration
@Slf4j
public class ConsumerCheckBillStatusV2Configuration {

    @Value("${queue.sp.check.bill.status.v2.subscription.name}")
    private String queueSubscriptionName;

    @Value("${queue.dead.letter.publish.name}")
    private String deadLetterQueue;

    @Value("${queue.sp.check.bill.status.v2.subscription.max-retries}")
    private Integer maxRetries;

    @Value("${queue.sp.check.bill.status.v2.subscription.deduplication-lock-ttl}")
    private Integer deduplicationLockTtl;

    @Value("${queue.sp.check.bill.status.v2.subscription.key-ttl}")
    private Integer keyTtl;

    @Value("${queue.sp.check.bill.status.v2.subscription.max-ack-extension-period}")
    private Integer maxAckExtensionPeriod;

    @Bean(name = "servicePaymentCheckBillStatusV2MessageReceiver")
    public DeduplicationMessageReceiver<TransactionEvent> paymentMessageReceiver(
        @NonNull final String projectId,
        @NonNull final CredentialsProvider credentialsProvider,
        final ObjectMapper objectMapper,
        final RMapCache<String, ProcessingResult> processingResultRMap,
        final CheckBillStatusV2Consumer handlerAdapter) {

        return DeduplicationMessageReceiver.<TransactionEvent>builder()
            .maxRetries(maxRetries)
            .objectMapper(objectMapper)
            .lockMap(processingResultRMap)
            .eventConsumer(handlerAdapter)
            .clazz(TransactionEvent.class)
            .leaseTimeSeconds(deduplicationLockTtl)
            .keyTtlSeconds(keyTtl)
            .subscriptionName(queueSubscriptionName)
            .projectId(projectId)
            .credentialsProvider(credentialsProvider)
            .deadLetterQueue(deadLetterQueue)
            .messageContextParser(new MessageContextParser())
            .build();
    }

    @Bean(destroyMethod = "terminate")
    public SubscriberHandler checkBillStatusV2QueueConsumer(
        @NonNull final String projectId,
        @NonNull @Qualifier("servicePaymentCheckBillStatusV2MessageReceiver") final DeduplicationMessageReceiver<TransactionEvent> messageReceiver,
        @NonNull final CredentialsProvider credentialsProvider,
        // Inyectamos el channel provider opcionalmente por si existe en el perfil local
        @Autowired(required = false) @Qualifier("pubsubEmulatorChannelProvider") final TransportChannelProvider channelProvider) {

        final ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(
            projectId, queueSubscriptionName);

        // Creamos el builder base
        Subscriber.Builder builder = Subscriber.newBuilder(subscriptionName, messageReceiver)
            .setMaxAckExtensionPeriod(Duration.ofSeconds(maxAckExtensionPeriod))
            .setCredentialsProvider(credentialsProvider);

        // Si estamos en local y existe el provider del emulador, se lo asignamos explícitamente
        if (channelProvider != null) {
            builder.setChannelProvider(channelProvider);
        }

        final Subscriber subscriber = builder.build();

        subscriber.addListener(
            new Subscriber.Listener() {
                public void failed(Subscriber.State from, Throwable failure) {
                    String errorMsg = String.format("Failure message with state %s from queue, ", from);
                    log.error(errorMsg, failure);
                }
            },
            MoreExecutors.directExecutor());
        subscriber.startAsync();
        return new SubscriberHandler(subscriber);
    }
}

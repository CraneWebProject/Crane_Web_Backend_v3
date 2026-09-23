package crane.notificationservice.common.config;

import com.fasterxml.jackson.core.JsonParseException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// 메인 리스너 에러 핸들러: 재시도 3회 소진 후 DLT 발행 + offset 커밋, 파싱 실패는 즉시 DLT.
class KafkaConsumerConfigTest {

    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
    @SuppressWarnings("unchecked")
    private final Consumer<String, String> consumer = mock(Consumer.class);
    private final MessageListenerContainer container = mock(MessageListenerContainer.class);
    private final ConsumerRecord<String, String> record =
            new ConsumerRecord<>("reservation-events-topic", 0, 0L, null, "message");

    private DefaultErrorHandler errorHandler;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        KafkaConsumerConfig config = new KafkaConsumerConfig(kafkaTemplate);
        ReflectionTestUtils.setField(config, "bootstrapServers", "localhost:9092");
        ReflectionTestUtils.setField(config, "autoOffsetReset", "earliest");
        ReflectionTestUtils.setField(config, "groupId", "notification-group");

        ConcurrentKafkaListenerContainerFactory<String, String> factory = config.concurrentKafkaListenerContainerFactory();
        errorHandler = (DefaultErrorHandler) ReflectionTestUtils.getField(factory, "commonErrorHandler");

        // isRunning() 이 false 라 백오프 대기는 즉시 끝난다.
        when(container.getContainerProperties()).thenReturn(factory.getContainerProperties());
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void publishesToDltAfterThreeRetries() {
        Exception ex = new ListenerExecutionFailedException("fail", new RuntimeException("FCM 일시 오류"));

        for (int i = 0; i < 3; i++) {
            assertThrows(KafkaException.class,
                    () -> errorHandler.handleRemaining(ex, new ArrayList<>(List.of(record)), consumer, container));
        }
        verify(kafkaTemplate, never()).send(any(ProducerRecord.class));

        errorHandler.handleRemaining(ex, new ArrayList<>(List.of(record)), consumer, container);

        verify(kafkaTemplate).send(argThat((ProducerRecord<String, String> r) -> r.topic().equals("reservation-events-topic.DLT")));
        verify(consumer).commitSync(anyMap(), any());
    }

    @Test
    void publishesParseFailureToDltWithoutRetry() {
        Exception ex = new ListenerExecutionFailedException("fail", new JsonParseException(null, "bad json"));

        errorHandler.handleRemaining(ex, new ArrayList<>(List.of(record)), consumer, container);

        verify(kafkaTemplate).send(any(ProducerRecord.class));
    }
}

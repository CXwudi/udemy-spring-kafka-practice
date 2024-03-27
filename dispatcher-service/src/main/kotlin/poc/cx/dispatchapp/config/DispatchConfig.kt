package poc.cx.dispatchapp.config


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import poc.cx.dispatchapp.message.OrderCreated


/**
 * Showcasing the manual configuration of Kafka consumers and producers
 *
 * See [KafkaAutoConfiguration] for all autoconfiguration options
 */
//@Configuration
//class DispatchConfig {
//
//  @Bean
//  fun kafkaListenerContainerFactory(consumerFactory: ConsumerFactory<String, Any>): ConcurrentKafkaListenerContainerFactory<String, Any> {
//    val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
//    factory.consumerFactory = consumerFactory
//    return factory
//  }
//
//  @Bean
//  fun consumerFactory(@Value("\${kafka.bootstrap-servers}") bootstrapServers: String): ConsumerFactory<String, Any> {
//    val config: MutableMap<String, Any> = HashMap()
//    config[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
//    config[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = ErrorHandlingDeserializer::class.java
//    config[ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS] = JsonDeserializer::class.java
//    config[JsonDeserializer.VALUE_DEFAULT_TYPE] = OrderCreated::class.java.canonicalName
//    config[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
//    return DefaultKafkaConsumerFactory(config)
//  }
//
//  @Bean
//  fun kafkaTemplate(producerFactory: ProducerFactory<String, Any>): KafkaTemplate<String, Any> {
//    return KafkaTemplate(producerFactory)
//  }
//
//  @Bean
//  fun producerFactory(@Value("\${kafka.bootstrap-servers}") bootstrapServers: String): ProducerFactory<String, Any> {
//    val config: MutableMap<String, Any> = HashMap()
//    config[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
//    config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
//    config[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
//    return DefaultKafkaProducerFactory(config)
//  }
//}
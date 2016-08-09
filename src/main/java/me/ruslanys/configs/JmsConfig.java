package me.ruslanys.configs;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.Session;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Configuration
@EnableJms
public class JmsConfig {

    private SQSConnectionFactory connectionFactory;

    public JmsConfig(@Value("${aws.access-key}") String awsAccessKey,
                     @Value("${aws.secret-key}") String awsSecretKey) {
         connectionFactory = SQSConnectionFactory.builder()
                .withRegion(Region.getRegion(Regions.US_WEST_2))
                .withAWSCredentialsProvider(new StaticCredentialsProvider(
                        new BasicAWSCredentials(awsAccessKey, awsSecretKey)
                ))
                .build();
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setConcurrency("3-10");
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(connectionFactory);
    }

}

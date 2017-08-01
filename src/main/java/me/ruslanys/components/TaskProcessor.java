package me.ruslanys.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.Application;
import me.ruslanys.models.Event;
import me.ruslanys.models.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.Random;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Component
@Slf4j
public class TaskProcessor {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper mapper;

    private Random random = new Random();

    @Autowired
    public TaskProcessor(JmsTemplate jmsTemplate, ObjectMapper mapper) {
        this.jmsTemplate = jmsTemplate;
        this.mapper = mapper;
    }

    @JmsListener(destination = Application.PROCESSOR_QUEUE)
    public void onMessage(String message) throws JMSException {
        log.debug("Got a message <{}>", message);
        try {
            Task task = mapper.readValue(message, Task.class);
            onMessage(task);
        } catch (IOException ex) {
            log.error("Encountered error while parsing message.", ex);
            throw new JMSException("Encountered error while parsing message.");
        }
    }

    @SneakyThrows
    private void onMessage(Task task) {
        log.info("Got a task: {}", task);

        // task started notification
        sendEvent(new Event(task.getId(), task.getType(), Event.Status.STARTED));

        // emulate task processing
        Thread.sleep(10_000);

        // task finished notification
        sendEvent(new Event(
                task.getId(),
                task.getType(),
                random.nextInt(2) == 1 ? Event.Status.SUCCESSFUL : Event.Status.FAILED
        ));
    }

    @SneakyThrows
    private void sendEvent(Event event) {
        jmsTemplate.convertAndSend(Application.MANAGER_QUEUE, mapper.writeValueAsString(event));
    }

}

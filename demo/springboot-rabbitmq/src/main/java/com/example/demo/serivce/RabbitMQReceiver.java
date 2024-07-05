package com.example.demo.serivce;

import com.example.demo.modal.Employee;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
@Component
public class RabbitMQReceiver {

    @RabbitListener(queues = "${javainuse.rabbitmq.queue}")
    public void receiveMessage(Employee employee) {
        System.out.println("Received message: " + employee);
    }
}

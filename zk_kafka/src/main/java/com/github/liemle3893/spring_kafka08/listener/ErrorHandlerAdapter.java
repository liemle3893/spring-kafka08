package com.github.liemle3893.spring_kafka08.listener;

import com.github.liemle3893.spring_kafka08.KafkaException;
import kafka.message.MessageAndMetadata;
import org.springframework.util.MethodInvoker;

public class ErrorHandlerAdapter implements GenericErrorHandler {

    private final MethodInvoker methodInvoker;

    public ErrorHandlerAdapter(Object bean, String methodName) {
        this.methodInvoker = new MethodInvoker();
        methodInvoker.setTargetMethod(methodName);
        methodInvoker.setTargetObject(bean);
    }

    @Override
    public void handleException(Throwable ex, MessageAndMetadata message) {
        methodInvoker.setArguments(ex, message);
        try {
            methodInvoker.prepare();
            methodInvoker.invoke();
        } catch (Exception _ex) {
            throw new KafkaException("Invalid: Handler exception method is invalid.", ex);
        }
    }
}

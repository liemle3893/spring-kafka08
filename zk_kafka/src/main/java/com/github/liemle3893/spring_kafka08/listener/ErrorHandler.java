package com.github.liemle3893.spring_kafka08.listener;

import kafka.message.MessageAndMetadata;

public class ErrorHandler implements GenericErrorHandler {


    @Override
    public void handleException(Throwable ex, MessageAndMetadata message) {

    }
}

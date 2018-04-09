package com.sk.zk_kafka.listener;

import kafka.message.MessageAndMetadata;

public class ErrorHandler implements GenericErrorHandler {


    @Override
    public void handleException(Throwable ex, MessageAndMetadata message) {

    }
}

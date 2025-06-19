package com.technokratos.util;

public class RabbitVariables {

    private RabbitVariables() {

    }

    public static final String NOTIFICATION_EXCHANGE_NAME = "notifications";
    public static final String EMAIL_ADVERTISEMENT_QUEUE_NAME = "email-advertisement";
    public static final String EMAIL_ADVERTISEMENT_ROUTING_KEY = "email.advertisement";
    public static final String EMAIL_CODE_QUEUE_NAME = "email-code";
    public static final String EMAIL_CODE_ROUTING_KEY = "email.code";

}

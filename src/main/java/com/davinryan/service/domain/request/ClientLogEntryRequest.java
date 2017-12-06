package com.davinryan.service.domain.request;

import com.davinryan.common.restservice.domain.request.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "When a client application wants to tell the server about an event so that it can be picked up by" +
        " splunk then this object will capture all the relevant information of that event.")
public class ClientLogEntryRequest extends Request {

    @ApiModelProperty(value = "Log level to use", allowableValues = "error, warn, info, debug, trace")
    private String logLevel;

    @ApiModelProperty(value = "All other detail relevant to the logging event")
    private transient Details details;

    public String getLogLevel() {
        return logLevel;
    }

    public Details getDetails() {
        return details;
    }

    public void setLogLevel(String error) {
        this.logLevel = error;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "ClientLogEntryRequest{" +
                "correlationId=" + getCorrelationId() +
                ", logLevel='" + logLevel + '\'' +
                ", details=" + details +
                '}';
    }

    public static class Details {

        @ApiModelProperty(value = "Any name that represents the client application.")
        private String application;

        @ApiModelProperty(value = "The useragent of the client. I.e. what browser and machine is being used.")
        private String userAgent;

        @ApiModelProperty(value = "The id of the logged in user.")
        private String userId;

        @ApiModelProperty(value = "The stacktrace, error or what ever can be provided that represents the main detail of the event.")
        private String message;

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getApplication() {
            return application;
        }

        public void setApplication(String application) {
            this.application = application;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "Details{" +
                    "application='" + application + '\'' +
                    ", userAgent='" + userAgent + '\'' +
                    ", userId='" + userId + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}

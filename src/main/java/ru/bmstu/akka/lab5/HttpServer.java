package ru.bmstu.akka.lab5;

import akka.NotUsed;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.javadsl.Flow;

public class HttpServer {

    public Flow<HttpRequest, HttpResponse, NotUsed> createFlow() {
        return Flow.of(HttpRequest.class)
    }
}

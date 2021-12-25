package ru.bmstu.akka.lab5;

import akka.NotUsed;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.javadsl.Flow;
import javafx.util.Pair;

public class HttpServer {

    public Flow<HttpRequest, HttpResponse, NotUsed> createFlow() {
        return Flow.of(HttpRequest.class).
                map( req -> )
    }

    private Flow<HttpRequest, Pair<String, Integer>> parseHttp(HttpRequest req) {
        req
    }
}

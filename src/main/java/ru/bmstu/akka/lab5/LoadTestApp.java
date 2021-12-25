package ru.bmstu.akka.lab5;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

public class LoadTestApp {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create();
        final Http http = Http.get(system);
        final ActorMaterializer materializer =
                ActorMaterializer.create(system);
        HttpServer server = new HttpServer(system, materializer);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = server.createFlow();
        final Completion
    }
}

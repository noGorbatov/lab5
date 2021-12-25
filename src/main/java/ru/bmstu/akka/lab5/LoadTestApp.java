package ru.bmstu.akka.lab5;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

public class LoadTestApp {
    final static private String HOST = "localhost";
    final static private int PORT = 8080;

    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create();
        final Http http = Http.get(system);
        final ActorMaterializer materializer =
                ActorMaterializer.create(system);
        HttpServer server = new HttpServer(system, materializer);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = server.createFlow();
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(HOST, PORT),
                materializer
        );

        System.in.read();

        binding.thenCompose(ServerBinding::unbind).
                thenAccept(unbound -> {
                    system.terminate();
                    try {
                        server.freeHttpClient();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}

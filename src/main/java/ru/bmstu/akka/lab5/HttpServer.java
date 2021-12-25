package ru.bmstu.akka.lab5;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Query;
import akka.pattern.Patterns;
import akka.stream.javadsl.Flow;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class HttpServer {
    private static final String TEST_URL_PARAM = "testUrl";
    private static final String COUNT_PARAM = "count";
    private static final int PARALLEL_FUTURES = 5;

    private final ActorRef cacheActor;
    public HttpServer(ActorSystem system) {
        cacheActor = system.actorOf(Props.create(CacheActor.class));
    }

    public Flow<HttpRequest, HttpResponse, NotUsed> createFlow() {
        return Flow.of(HttpRequest.class).
                map(this::parseHttp).
                mapAsync(PARALLEL_FUTURES, )
    }

    private ParseResult parseHttp(HttpRequest req) {
        Query query = req.getUri().query();
        Optional<String> urlOptional = query.get(TEST_URL_PARAM);
        Optional<String> countOptional = query.get(COUNT_PARAM);
        if (!urlOptional.isPresent() || !countOptional.isPresent()) {
            return new ParseResult(false, 0, "");
        }

        String url = urlOptional.get();
        int count;

        try {
            count = Integer.parseInt(countOptional.get());
        } catch (NumberFormatException e) {
            return new ParseResult(false, 0, "");
        }

        return new ParseResult(true, count, url);
    }

    private CompletionStage<Object> makeRequest(ParseResult parsedRequest) {
        if (!parsedRequest.isSuccess()) {
            return new CompletionStage<TestResult>(new TestResult(false, "", -1));
        }

        Patterns.ask(cacheActor,
                new CacheActor.GetMsg(parsedRequest.getTestUrl(), parsedRequest.getCount()))
    }
}

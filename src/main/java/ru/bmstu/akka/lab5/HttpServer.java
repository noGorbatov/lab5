package ru.bmstu.akka.lab5;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.model.*;
import akka.pattern.Patterns;
import akka.pattern.PatternsCS;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.japi.Pair;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class HttpServer {
    private static final String TEST_URL_PARAM = "testUrl";
    private static final String COUNT_PARAM = "count";
    private static final int PARALLEL_FUTURES = 5;
    private static final int ASK_TIMEOUT_MS = 5000;
    private static final String FAIL_MSG = "Failed test with url %s and count %d: %s";
    private static final String SUCCESS_MSG = "Succeed test with url %s and count %d, res %f";

    private final ActorRef cacheActor;
    private final ActorMaterializer materializer;
    private final AsyncHttpClient client;

    public HttpServer(ActorSystem system, ActorMaterializer materializer) {
        cacheActor = system.actorOf(Props.create(CacheActor.class));
        this.materializer = materializer;
        client = asyncHttpClient();
    }

    public void freeHttpClient() throws IOException {
        client.close();
    }

    public Flow<HttpRequest, HttpResponse, NotUsed> createFlow() {
        return Flow.of(HttpRequest.class).
                map(this::parseHttp).
                mapAsync(PARALLEL_FUTURES, this::makeRequest).
                map(this::createResponse);
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
            return CompletableFuture.completedFuture(
                    new TestResult(false, parsedRequest.getTestUrl(),
                            -1, parsedRequest.getCount(), "fail parse"));
        }

        return PatternsCS.ask(cacheActor,
                new CacheActor.GetMsg(parsedRequest.getTestUrl(),
                                        parsedRequest.getCount()), ASK_TIMEOUT_MS).
                thenCompose( resObj -> {
                    CacheActor.ResMsg res = (CacheActor.ResMsg) resObj;
                    if (res.hasResult()) {
                        return CompletableFuture.completedFuture(
                                new TestResult(true,
                                        parsedRequest.getTestUrl(),
                                        res.getAverageTime(),
                                        parsedRequest.getCount(), "has result"));
                    }

                    Flow<Pair<String, Integer>, Long, NotUsed> flow = Flow.<Pair<String, Integer>>create().
                            mapConcat( pair ->
                                new ArrayList<>(
                                        Collections.nCopies(pair.second(), pair.first()))
                            ).mapAsync(parsedRequest.getCount(), url -> {
                                long start = System.currentTimeMillis();
                                CompletableFuture<Response> resp = client.prepareGet(url).execute().toCompletableFuture();
                                return resp.thenApply(response -> {
                                    long end = System.currentTimeMillis();
                                    return end - start;
                                });
                            });
                    return Source.single(new Pair<>(parsedRequest.getTestUrl(),
                                                parsedRequest.getCount())).
                            via(flow).
                            toMat(Sink.fold((long)0, Long::sum), Keep.right()).
                            run(materializer).
                            thenApply( sum -> new TestResult(true,
                                    parsedRequest.getTestUrl(),
                                    (double) sum / parsedRequest.getCount(),
                                    parsedRequest.getCount(), "calculated"));
                });
    }

    private HttpResponse createResponse(Object resObj) {
        TestResult res = (TestResult) resObj;
        HttpResponse resp = HttpResponse.create();

        if (!res.isSuccess()) {
            String data = String.format(FAIL_MSG, res.getUrl(), res.getCount(), res.getMsg());
            ResponseEntity entity = HttpEntities.create(data);
            return resp.withEntity(entity);
        }

        String data = String.format(SUCCESS_MSG, res.getUrl(), res.getCount(), res.getAverageTime());
        ResponseEntity entity = HttpEntities.create(data);
        return resp.withEntity(entity);
    }
}

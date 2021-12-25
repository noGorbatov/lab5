package ru.bmstu.akka.lab5;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class CacheActor extends AbstractActor {
    private final Map<Pair<String, Integer>, Double> cache = new HashMap<>();

    public static class GetMsg {
        private final String url;
        private final int count;
        public GetMsg(String url, int count) {
            this.url = url;
            this.count = count;
        }
        public int getCount() {
            return count;
        }
        public String getUrl() {
            return url;
        }
    }

    public static class ResMsg {
        private final boolean hasResult;
        private final double averageTime;
        public ResMsg(boolean has, double average) {
            this.hasResult = has;
            this.averageTime = average;
        }
        public double getAverageTime() {
            return averageTime;
        }
        public boolean hasResult() {
            return hasResult;
        }
    }

    @Override
    public Receive createReceive() {
        receiveBuilder().
                match(GetMsg.class, msg -> {
                    double average = cache.getOrDefault(
                            new Pair<>(msg.getUrl(), msg.getCount()), -1.);
                    getSender().tell(new ResMsg(average >= 0, average),
                                        ActorRef.noSender());
                }).build();
    }
}

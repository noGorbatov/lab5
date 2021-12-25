package ru.bmstu.akka.lab5;

import akka.actor.AbstractActor;

public class CacheActor extends AbstractActor {

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
        private final 
    }

    @Override
    public Receive createReceive() {
        receiveBuilder().
                match(GetMsg.class, msg -> {
                    getSender().tell();
                })
    }
}

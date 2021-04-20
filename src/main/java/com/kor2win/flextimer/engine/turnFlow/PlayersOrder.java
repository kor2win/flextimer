package com.kor2win.flextimer.engine.turnFlow;

import java.util.*;

public class PlayersOrder {
    private final Map<Player, Node> map;
    private final Node head;
    private final Node tail;

    public PlayersOrder(Collection<? extends Player> players) {
        if (players.isEmpty()) {
            throw new InvalidPlayersInitialization("Empty list");
        }

        map = new HashMap<>(players.size());

        Iterator<? extends Player> iter = players.iterator();

        Node prev = head = createUniqueNode(iter.next());
        while(iter.hasNext()) {
            linkNodes(prev, createUniqueNode(iter.next()));
            prev = prev.next;
        }

        tail = prev;

        linkNodes(tail, head);
    }

    private Node createUniqueNode(Player player) {
        Node node = new Node(player);
        Node put = map.putIfAbsent(player, node);

        if (put != null) {
            throw new InvalidPlayersInitialization("Contains duplicates");
        }

        return node;
    }

    private void linkNodes(Node prev, Node next) {
        next.prev = prev;
        prev.next = next;
    }

    public static PlayersOrderBuilder builder() {
        return new PlayersOrderBuilder();
    }

    public Player first() {
        return head.player;
    }

    public Player last() {
        return tail.player;
    }

    public Player after(Player player) {
        Node n = map.get(player);
        if (n == null) {
            throw new UnknownPlayer(player);
        }

        return n.next.player;
    }

    public Player before(Player player) {
        Node n = map.get(player);
        if (n == null) {
            throw new UnknownPlayer(player);
        }

        return n.prev.player;
    }

    public int size() {
        return map.size();
    }

    public Player get(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBounds();
        }

        Node n = head;
        for (int i = 0; i < index; i++) {
            n = n.next;
        }

        return n.player;
    }

    private static class Node {
        public Node prev;
        public Node next;
        public final Player player;

        public Node(Player player) {
            this.player = player;
        }
    }
}

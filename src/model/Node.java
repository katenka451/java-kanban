package model;

public class Node<T> {
    public Node<T> prev;
    public T task;
    public Node<T> next;

    public Node(Node<T> prev, T task, Node<T> next) {
        this.task = task;
        this.prev = prev;
        this.next = next;
    }
}

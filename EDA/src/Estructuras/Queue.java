
package Estructuras;
import Models.Quest;

public class Queue {

    // ── NODO INTERNO ───────────────────────────────────────────────────────────

    private static class Node {
        Object data; // Object para soportar Quest y lo que el juego necesite
        Node   next;

        Node(Object data) {
            this.data = data;
            this.next = null;
        }
    }

    // ── ATRIBUTOS ──────────────────────────────────────────────────────────────

    private Node front; // primer elemento (siguiente en salir)
    private Node rear;  // ultimo elemento  (ultimo en entrar)
    private int  size;

    public Queue() {
        this.front = null;
        this.rear  = null;
        this.size  = 0;
    }

    // ── OPERACIONES PRINCIPALES ────────────────────────────────────────────────

    public void enqueue(Object data) {
        Node newNode = new Node(data);
        if (isEmpty()) {
            front = newNode;
            rear  = newNode;
        } else {
            rear.next = newNode;
            rear      = newNode;
        }
        size++;
    }

    public Object dequeue() {
        if (isEmpty()) {
            System.out.println("La cola esta vacia.");
            return null;
        }
        Object data = front.data;
        front       = front.next;
        if (front == null) rear = null; // la cola quedó vacia
        size--;
        return data;
    }

    public Object peek() {
        if (isEmpty()) {
            System.out.println("La cola esta vacia.");
            return null;
        }
        return front.data;
    }

    // ── IMPRESIÓN ──────────────────────────────────────────────────────────────

    public void print() {
        if (isEmpty()) {
            System.out.println("  (cola vacia)");
            return;
        }
        System.out.println("=== Cola de misiones (frente --> final) =======");
        Node current = front;
        int  index   = 1;
        while (current != null) {
            if (current.data instanceof Quest) {
                Quest q = (Quest) current.data;
                System.out.println("║ " + index + ". " + q.getSummary());
            } else {
                System.out.println("║ " + index + ". " + current.data.toString());
            }
            current = current.next;
            index++;
        }
        System.out.println("==============================================");
    }

    // ── UTILIDADES ─────────────────────────────────────────────────────────────

    public boolean isEmpty() { return front == null; }
    public int getSize()     { return size; }
}

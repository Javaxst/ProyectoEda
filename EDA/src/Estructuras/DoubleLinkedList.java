package Estructuras;
import Models.GameEvent;
public class DoubleLinkedList{

    // ── NODO INTERNO ───────────────────────────────────────────────────────────

    private static class Node {
        GameEvent event;
        Node prev;
        Node next;

        Node(GameEvent event) {
            this.event = event;
            this.prev  = null;
            this.next  = null;
        }
    }

    // ── ATRIBUTOS ──────────────────────────────────────────────────────────────

    private Node head;
    private Node tail;
    private Node cursor; // puntero para navegar el historial
    private int  size;

    public DoubleLinkedList() {
        this.head   = null;
        this.tail   = null;
        this.cursor = null;
        this.size   = 0;
    }

    // ── INSERCIÓN ──────────────────────────────────────────────────────────────

    // Los eventos siempre se agregan al final (orden cronológico)
    public void add(GameEvent event) {
        Node newNode = new Node(event);
        if (head == null) {
            head   = newNode;
            tail   = newNode;
            cursor = newNode;
        } else {
            newNode.prev = tail;
            tail.next    = newNode;
            tail         = newNode;
            cursor       = tail; // el cursor apunta al evento más reciente
        }
        size++;
    }

    // ── NAVEGACIÓN (adelante y atrás) ──────────────────────────────────────────

    public GameEvent next() {
        if (cursor == null) {
            System.out.println("Historial vacío.");
            return null;
        }
        if (cursor.next == null) {
            System.out.println("Ya estás en el evento más reciente.");
            return cursor.event;
        }
        cursor = cursor.next;
        return cursor.event;
    }

    public GameEvent prev() {
        if (cursor == null) {
            System.out.println("Historial vacío.");
            return null;
        }
        if (cursor.prev == null) {
            System.out.println("Ya estás en el primer evento.");
            return cursor.event;
        }
        cursor = cursor.prev;
        return cursor.event;
    }

    public GameEvent current() {
        if (cursor == null) return null;
        return cursor.event;
    }

    // Regresa el cursor al evento más reciente
    public void goToLatest() {
        cursor = tail;
    }

    // Regresa el cursor al primer evento
    public void goToFirst() {
        cursor = head;
    }

    // ── IMPRESIÓN ──────────────────────────────────────────────────────────────

    // Imprime todos los eventos del más antiguo al más reciente
    public void printForward() {
        if (head == null) {
            System.out.println("  (historial vacío)");
            return;
        }
        System.out.println("╔══ Historial de eventos ══════════════════════");
        Node current = head;
        int  index   = 1;
        while (current != null) {
            String marker = (current == cursor) ? " ◄" : "";
            System.out.println("║ " + index + ". " + current.event.getSummary() + marker);
            current = current.next;
            index++;
        }
        System.out.println("╚═════════════════════════════════════════════");
    }

    // Imprime del más reciente al más antiguo
    public void printBackward() {
        if (tail == null) {
            System.out.println("  (historial vacío)");
            return;
        }
        System.out.println("╔══ Historial (reciente → antiguo) ═══════════");
        Node current = tail;
        int  index   = size;
        while (current != null) {
            String marker = (current == cursor) ? " ◄" : "";
            System.out.println("║ " + index + ". " + current.event.getSummary() + marker);
            current = current.prev;
            index--;
        }
        System.out.println("╚═════════════════════════════════════════════");
    }

    // Imprime solo los últimos N eventos
    public void printLast(int n) {
        if (head == null) {
            System.out.println("  (historial vacío)");
            return;
        }
        System.out.println("╔══ Últimos " + n + " eventos ═══════════════════════");
        Node current = tail;
        int  count   = 0;
        while (current != null && count < n) {
            System.out.println("║ • " + current.event.getSummary());
            current = current.prev;
            count++;
        }
        System.out.println("╚═════════════════════════════════════════════");
    }

    // ── UTILIDADES ─────────────────────────────────────────────────────────────

    public boolean isEmpty() { return size == 0; }
    public int getSize()     { return size; }
    public GameEvent getLatest() { return tail != null ? tail.event : null; }
}

package Estructuras;
import Models.Item;

public class LinkedList {

    // ── NODO INTERNO ───────────────────────────────────────────────────────────

    private static class Node {
        Item item;
        Node next;

        Node(Item item) {
            this.item = item;
            this.next = null;
        }
    }

    // ── ATRIBUTOS ──────────────────────────────────────────────────────────────

    private Node head;
    private int size;

    public LinkedList() {
        this.head = null;
        this.size = 0;
    }

    // ── INSERCIÓN ──────────────────────────────────────────────────────────────

    public void add(Item item) {
        Node newNode = new Node(item);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    public void addFirst(Item item) {
        Node newNode = new Node(item);
        newNode.next = head;
        head         = newNode;
        size++;
    }

    // ── BÚSQUEDA ───────────────────────────────────────────────────────────────

    public Item find(String itemName) {
        Node current = head;
        while (current != null) {
            if (current.item.getName().equalsIgnoreCase(itemName)) {
                return current.item;
            }
            current = current.next;
        }
        System.out.println("'" + itemName + "' no esta en el inventario.");
        return null;
    }

    public Item getFirst() {
        if (head == null) {
            System.out.println("El inventario esta vacio.");
            return null;
        }
        return head.item;
    }

    // ── ELIMINACIÓN ────────────────────────────────────────────────────────────

    public boolean remove(String itemName) {
        if (head == null) {
            System.out.println("El inventario esta vacio.");
            return false;
        }

        // caso: el item esta en la cabeza
        if (head.item.getName().equalsIgnoreCase(itemName)) {
            head = head.next;
            size--;
            System.out.println("'" + itemName + "' eliminado del inventario.");
            return true;
        }

        // caso: buscar en el resto de la lista
        Node current = head;
        while (current.next != null) {
            if (current.next.item.getName().equalsIgnoreCase(itemName)) {
                current.next = current.next.next;
                size--;
                System.out.println("'" + itemName + "' eliminado del inventario.");
                return true;
            }
            current = current.next;
        }

        System.out.println("'" + itemName + "' no encontrado en el inventario.");
        return false;
    }

    public Item removeFirst() {
        if (head == null) {
            System.out.println("El inventario esta vacio.");
            return null;
        }
        Item item = head.item;
        head      = head.next;
        size--;
        return item;
    }

    // ── IMPRESIÓN ──────────────────────────────────────────────────────────────

    public void print() {
        if (head == null) {
            System.out.println("  (inventario vacio)");
            return;
        }
        Node current = head;
        int  index   = 0;
        while (current != null) {
            System.out.println("  [" + index + "] " + current.item.getInfo());
            current = current.next;
            index++;
        }
    }

    // ── UTILIDADES ─────────────────────────────────────────────────────────────

    public boolean contains(String itemName) {
        return find(itemName) != null;
    }

    public boolean isEmpty() { return size == 0; }
    public int getSize()     { return size; }
}

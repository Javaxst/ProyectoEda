package Estructuras;
import Models.Item;

public class HashTable {

    // ── NODO INTERNO (encadenamiento para colisiones) ──────────────────────────

    private static class Node {
        String key;
        Object value;
        Node   next;

        Node(String key, Object value) {
            this.key   = key;
            this.value = value;
            this.next  = null;
        }
    }

    // ── ATRIBUTOS ──────────────────────────────────────────────────────────────

    private Node[] buckets;
    private int    capacity;
    private int    size;
    private static final double LOAD_FACTOR = 0.75;

    public HashTable() {
        this(16); // capacidad inicial por defecto
    }

    public HashTable(int capacity) {
        this.capacity = capacity;
        this.buckets  = new Node[capacity];
        this.size     = 0;
    }

    // ── FUNCIÓN HASH ───────────────────────────────────────────────────────────

    private int hash(String key) {
        int hash = 0;
        for (char c : key.toCharArray()) {
            hash = (hash * 31 + c) % capacity;
        }
        return Math.abs(hash);
    }

    // ── OPERACIONES PRINCIPALES ────────────────────────────────────────────────

    public void put(String key, Object value) {
        if (key == null) return;

        // rehash si superamos el factor de carga
        if ((double)(size + 1) / capacity >= LOAD_FACTOR) {
            rehash();
        }

        int  index   = hash(key);
        Node current = buckets[index];

        // si la clave ya existe, actualiza el valor
        while (current != null) {
            if (current.key.equalsIgnoreCase(key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }

        // insertar al inicio del bucket (O(1))
        Node newNode    = new Node(key, value);
        newNode.next    = buckets[index];
        buckets[index]  = newNode;
        size++;
    }

    public Object get(String key) {
        if (key == null) return null;
        int  index   = hash(key);
        Node current = buckets[index];
        while (current != null) {
            if (current.key.equalsIgnoreCase(key)) return current.value;
            current = current.next;
        }
        System.out.println("'" + key + "' no encontrado.");
        return null;
    }

    public boolean remove(String key) {
        if (key == null) return false;
        int  index   = hash(key);
        Node current = buckets[index];
        Node prev    = null;

        while (current != null) {
            if (current.key.equalsIgnoreCase(key)) {
                if (prev == null) {
                    buckets[index] = current.next; // era el primero del bucket
                } else {
                    prev.next = current.next;
                }
                size--;
                System.out.println("'" + key + "' eliminado.");
                return true;
            }
            prev    = current;
            current = current.next;
        }

        System.out.println("'" + key + "' no encontrado para eliminar.");
        return false;
    }

    public boolean containsKey(String key) {
        return get(key) != null;
    }

    // ── REHASH ─────────────────────────────────────────────────────────────────

    private void rehash() {
        int    oldCapacity = capacity;
        Node[] oldBuckets  = buckets;

        capacity = capacity * 2;
        buckets  = new Node[capacity];
        size     = 0;

        System.out.println("[HashTable] Rehash: " + oldCapacity + " --> " + capacity + " buckets");

        for (int i = 0; i < oldCapacity; i++) {
            Node current = oldBuckets[i];
            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }
    }

    // ── IMPRESIÓN ──────────────────────────────────────────────────────────────

    public void print() {
        if (size == 0) {
            System.out.println("  (tabla vacia)");
            return;
        }
        System.out.println("===== HashTable (" + size + " entradas, " + capacity + " buckets) ====");
        for (int i = 0; i < capacity; i++) {
            if (buckets[i] != null) {
                System.out.print("║ [" + i + "] ");
                Node current = buckets[i];
                while (current != null) {
                    if (current.value instanceof Item) {
                        System.out.print(((Item) current.value).getInfo());
                    } else {
                        System.out.print(current.key + " --> " + current.value);
                    }
                    if (current.next != null) System.out.print("  --->  ");
                    current = current.next;
                }
                System.out.println();
            }
        }
        System.out.println("==============================================");
        System.out.printf("  Factor de carga actual: %.2f%n", (double) size / capacity);
    }

    // ── UTILIDADES ─────────────────────────────────────────────────────────────

    public int    getSize()     { return size; }
    public int    getCapacity() { return capacity; }
    public boolean isEmpty()   { return size == 0; }
}
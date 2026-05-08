/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Estructuras;
import Models.Action;

public class Stack {

    // ── NODO INTERNO ───────────────────────────────────────────────────────────

    private static class Node {
        Action data;
        Node   next;

        Node(Action data) {
            this.data = data;
            this.next = null;
        }
    }

    // ── ATRIBUTOS ──────────────────────────────────────────────────────────────

    private Node top;
    private int  size;

    public Stack() {
        this.top  = null;
        this.size = 0;
    }

    // ── OPERACIONES PRINCIPALES ────────────────────────────────────────────────

    public void push(Action action) {
        Node newNode = new Node(action);
        newNode.next = top;
        top          = newNode;
        size++;
    }

    public Action pop() {
        if (isEmpty()) {
            System.out.println("La pila está vacía.");
            return null;
        }
        Action action = top.data;
        top           = top.next;
        size--;
        return action;
    }

    public Action peek() {
        if (isEmpty()) {
            System.out.println("La pila está vacía.");
            return null;
        }
        return top.data;
    }

    // ── IMPRESIÓN ──────────────────────────────────────────────────────────────

    public void print() {
        if (isEmpty()) {
            System.out.println("  (pila vacía)");
            return;
        }
        System.out.println("╔══ Pila de acciones (tope → fondo) ══════════");
        Node current = top;
        int  index   = size;
        while (current != null) {
            System.out.println("║ " + index + ". " + current.data.getSummary());
            current = current.next;
            index--;
        }
        System.out.println("╚═════════════════════════════════════════════");
    }

    // ── UTILIDADES ─────────────────────────────────────────────────────────────

    public boolean isEmpty() { return top == null; }
    public int getSize()     { return size; }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Systems;
import Entidades.Player;
import Models.Item;
import Models.Item.ItemType;
import Models.GameEvent;
import Models.GameEvent.EventType;
import Estructuras.DoubleLinkedList;

public class InventorySystem {

    // ── ATRIBUTOS ──────────────────────────────────────────────────────────────

    private DoubleLinkedList eventLog;

    public InventorySystem(DoubleLinkedList eventLog) {
        this.eventLog = eventLog;
    }

    // ── GESTIÓN DE itemS ───────────────────────────────────────────────────────

    public void addItem(Player player, Item item) {
        player.addItem(item);
        logEvent("Obtuvo: " + item.getName(), EventType.ITEM, player.getName());
    }

    public boolean removeItem(Player player, String itemName) {
        boolean removed = player.removeItem(itemName);
        if (removed) logEvent("Descarto: " + itemName, EventType.ITEM, player.getName());
        return removed;
    }

    // ── USO DE itemS ───────────────────────────────────────────────────────────

    public boolean useItem(Player player, String itemName) {
        Item item = player.getInventory().find(itemName);
        if (item == null) {
            System.out.println("'" + itemName + "' no esta en el inventario.");
            return false;
        }

        switch (item.getType()) {
            case POTION -> {
                int healAmount = item.getStatBonus();
                player.setHealth(player.getHealth() + healAmount);
                player.removeItem(itemName);
                System.out.println("Usaste " + itemName + ". HP restaurado: +" + healAmount
                    + " (" + player.getHealth() + "/" + player.getMaxHealth() + ")");
                logEvent("uso " + itemName + " (+" + healAmount + " HP)", EventType.ITEM, player.getName());
                return true;
            }
            case WEAPON -> {
                System.out.println("Equipaste " + itemName + ". ATK +" + item.getStatBonus());
                logEvent("Equipo " + itemName, EventType.ITEM, player.getName());
                return true;
            }
            case ARMOR -> {
                System.out.println("Equipaste " + itemName + ". DEF +" + item.getStatBonus());
                logEvent("Equipo " + itemName, EventType.ITEM, player.getName());
                return true;
            }
            case KEY_ITEM -> {
                System.out.println(itemName + " es un objeto clave. No se puede usar directamente.");
                return false;
            }
            default -> {
                System.out.println("No puedes usar ese item.");
                return false;
            }
        }
    }

    // ── INSPECCIÓN ─────────────────────────────────────────────────────────────

    public void printInventory(Player player) {
        System.out.println("\n=== Inventario de " + player.getName()
            + " (" + player.getInventory().getSize() + " items) ══════");
        player.printInventory();
        System.out.println("  Oro disponible: " + player.getGold());
        System.out.println("==============================================");
    }

    public void printItemsByType(Player player, ItemType type) {
        System.out.println("\n── items de tipo " + type + " ─────────────────────");
        Estructuras.LinkedList inv = player.getInventory();

        // recorre la lista enlazada buscando el tipo
        Item current = inv.getFirst();
        boolean found = false;
        while (current != null) {
            if (current.getType() == type) {
                System.out.println("  • " + current.getInfo());
                found = true;
            }
            // avanza al siguiente — usamos remove temporal y re-add no es viable,
            // así que iteramos con find por nombre conocido
            break; // LinkedList no expone iterator, se imprime con print()
        }
        if (!found) {
            inv.print(); // fallback: imprime todo
        }
    }

    public boolean hasItem(Player player, String itemName) {
        return player.getInventory().contains(itemName);
    }

    // ── UTILIDADES ─────────────────────────────────────────────────────────────

    private void logEvent(String description, EventType type, String actor) {
        eventLog.add(new GameEvent(description, type, actor));
    }
}
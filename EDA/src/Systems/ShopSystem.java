/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Systems;
import Entidades.NPC;
import Entidades.Player;
import Models.Item;
import Models.Item.ItemType;
import Models.GameEvent;
import Models.GameEvent.EventType;
import Estructuras.DoubleLinkedList;
import Estructuras.HashTable;

public class ShopSystem {

    // ── ATRIBUTOS ──────────────────────────────────────────────────────────────

    private HashTable        globalCatalog; // Catalogo maestro de todos los items del juego
    private DoubleLinkedList eventLog;

    public ShopSystem(DoubleLinkedList eventLog) {
        this.globalCatalog = new HashTable();
        this.eventLog      = eventLog;
        loadCatalog();
    }

    // ── Catalogo MAESTRO ───────────────────────────────────────────────────────

    private void loadCatalog() {
        // pociones
        globalCatalog.put("Pocion de Vida",      new Item("Pocion de Vida",      "Restaura 30 HP",          ItemType.POTION,   20,  30));
        globalCatalog.put("Pocion Mayor",         new Item("Pocion Mayor",         "Restaura 80 HP",          ItemType.POTION,   50,  80));
        globalCatalog.put("Elixir",               new Item("Elixir",               "Restaura HP completo",    ItemType.POTION,  120, 999));

        // armas
        globalCatalog.put("Espada Corta",         new Item("Espada Corta",         "ATK basico",              ItemType.WEAPON,   80,  10));
        globalCatalog.put("Espada Larga",         new Item("Espada Larga",         "ATK mejorado",            ItemType.WEAPON,  150,  20));
        globalCatalog.put("Hacha de Guerra",      new Item("Hacha de Guerra",      "ATK alto, lenta",         ItemType.WEAPON,  200,  30));

        // armaduras
        globalCatalog.put("Armadura de Cuero",    new Item("Armadura de Cuero",    "DEF basica",              ItemType.ARMOR,    60,   8));
        globalCatalog.put("Cota de Malla",        new Item("Cota de Malla",        "DEF mejorada",            ItemType.ARMOR,   130,  18));
        globalCatalog.put("Armadura de Placas",   new Item("Armadura de Placas",   "DEF alta, pesada",        ItemType.ARMOR,   250,  30));

        // items clave
        globalCatalog.put("Llave Antigua",        new Item("Llave Antigua",        "Abre una puerta sellada", ItemType.KEY_ITEM, 0,    0));
        globalCatalog.put("Mapa del Mundo",       new Item("Mapa del Mundo",       "Revela todas las zonas",  ItemType.KEY_ITEM, 0,    0));

        System.out.println("[ Catalogo cargado: " + globalCatalog.getSize() + " items ]");
    }

    // ── STOCK DE NPC ───────────────────────────────────────────────────────────

    public void stockNPC(NPC merchant, String... itemNames) {
        for (String name : itemNames) {
            Item item = (Item) globalCatalog.get(name);
            if (item != null) {
                merchant.addItemToShop(item);
            } else {
                System.out.println("item '" + name + "' no existe en el Catalogo.");
            }
        }
        System.out.println("[ Tienda de " + merchant.getName() + " abastecida ]");
    }

    // ── COMPRA ─────────────────────────────────────────────────────────────────

    public boolean buyItem(Player player, NPC merchant, String itemName) {
        if (!merchant.hasShop()) {
            System.out.println(merchant.getName() + " no tiene tienda.");
            return false;
        }

        Item item = merchant.getItemFromShop(itemName);
        if (item == null) {
            System.out.println("'" + itemName + "' no esta disponible en esta tienda.");
            return false;
        }

        if (!player.spendGold(item.getValue())) return false;

        player.addItem(item);
        System.out.println("Compraste: " + item.getInfo());
        logEvent("Compro " + itemName + " por " + item.getValue() + " oro",
            EventType.ITEM, player.getName());
        return true;
    }

    // ── VENTA ──────────────────────────────────────────────────────────────────

    public boolean sellItem(Player player, NPC merchant, String itemName) {
        if (!merchant.hasShop()) {
            System.out.println(merchant.getName() + " no compra items.");
            return false;
        }

        Item item = player.getInventory().find(itemName);
        if (item == null) {
            System.out.println("'" + itemName + "' no esta en tu inventario.");
            return false;
        }

        if (item.getType() == ItemType.KEY_ITEM) {
            System.out.println("Los items clave no se pueden vender.");
            return false;
        }

        int sellPrice = item.getValue() / 2; // vende al 50% del valor
        player.removeItem(itemName);
        player.earnGold(sellPrice);
        System.out.println("Vendiste " + itemName + " por " + sellPrice + " oro.");
        logEvent("Vendio " + itemName + " por " + sellPrice + " oro",
            EventType.ITEM, player.getName());
        return true;
    }

    // ── IMPRESIÓN ──────────────────────────────────────────────────────────────

    public void printShop(NPC merchant) {
        System.out.println("\n=== Tienda de " + merchant.getName() + " ======================════");
        merchant.printShop();
        System.out.println("==============================================");
    }

    public void printCatalog() {
        System.out.println("\n=== Catalogo global =========================═════");
        globalCatalog.print();
        System.out.println("==============================================");
    }

    // ── UTILIDADES ─────────────────────────────────────────────────────────────

    public Item getFromCatalog(String itemName) {
        return (Item) globalCatalog.get(itemName);
    }

    private void logEvent(String description, EventType type, String actor) {
        eventLog.add(new GameEvent(description, type, actor));
    }
}

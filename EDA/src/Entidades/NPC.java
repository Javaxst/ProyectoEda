/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;
import Models.Item;
import Estructuras.HashTable;

public class NPC extends Entity {

    private String role;
    private String[] dialogue;
    private int dialogueIndex;
    private boolean hasShop;
    private HashTable shop; // solo se inicializa si hasShop == true

    public NPC(String name, String role, String[] dialogue, boolean hasShop) {
        super(name, 999, 0, 999, 1); // los NPCs no pelean
        this.role          = role;
        this.dialogue      = dialogue;
        this.dialogueIndex = 0;
        this.hasShop       = hasShop;
        if (hasShop) this.shop = new HashTable();
    }

    public String getDialogue() {
        if (dialogue == null || dialogue.length == 0) return name + ": ...";
        String line = dialogue[dialogueIndex];
        dialogueIndex = (dialogueIndex + 1) % dialogue.length; // cicla el diálogo
        return name + ": " + line;
    }

    public void interact() {
        System.out.println(getDialogue());
        if (hasShop) System.out.println("[Presiona T para abrir la tienda]");
    }

    public void addItemToShop(Item item) {
        if (!hasShop) {
            System.out.println(name + " no tiene tienda.");
            return;
        }
        shop.put(item.getName(), item);
    }

    public Item getItemFromShop(String itemName) {
        if (!hasShop) return null;
        return (Item) shop.get(itemName);
    }

    public void printShop() {
        if (!hasShop) {
            System.out.println(name + " no tiene tienda.");
            return;
        }
        System.out.println("=== Tienda de " + name + " ===");
        shop.print();
    }

    // Los NPCs no reciben daño real
    @Override
    public void takeDamage(int damage) {
        System.out.println(name + " te mira con decepción...");
    }

    @Override
    public String getStats() {
        return String.format("[ %s ] Rol: %s  Tienda: %s", name, role, hasShop ? "Sí" : "No");
    }

    public boolean hasShop()  { return hasShop; }
    public String getRole()   { return role; }
    public HashTable getShop(){ return shop; }
}

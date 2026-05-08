package Entidades;
import Estructuras.LinkedList;
import Models.Item;
import Models.Quest;
import Estructuras.Queue;

public class Player extends Entity {

    private int experience;
    private int experienceToNextLevel;
    private int gold;
    private LinkedList inventory;
    private Queue questLog;

    public Player(String name) {
        super(name, 100, 15, 10, 1);
        this.experience            = 0;
        this.experienceToNextLevel = 100;
        this.gold                  = 50;
        this.inventory             = new LinkedList();
        this.questLog              = new Queue();
    }

    @Override
    public void takeDamage(int damage) {
        int damageTaken = Math.max(1, damage - defense);
        setHealth(health - damageTaken);
        System.out.println(name + " recibe " + damageTaken + " de daño. HP: " + health + "/" + maxHealth);
    }

    public void gainExperience(int exp) {
        this.experience += exp;
        System.out.println(name + " gana " + exp + " EXP. (" + experience + "/" + experienceToNextLevel + ")");
        if (experience >= experienceToNextLevel) levelUp();
    }

    public void levelUp() {
        level++;
        experience            = experience - experienceToNextLevel;
        experienceToNextLevel = (int)(experienceToNextLevel * 1.5);
        maxHealth  += 20;
        attack     += 5;
        defense    += 3;
        health      = maxHealth; // HP completo al subir de nivel
        System.out.println("*** " + name + " subió al nivel " + level + "! ***");
    }

    public void addItem(Item item) {
        inventory.add(item);
        System.out.println(item.getName() + " agregado al inventario.");
    }

    public boolean removeItem(String itemName) {
        return inventory.remove(itemName);
    }

    public void printInventory() {
        System.out.println("=== Inventario de " + name + " ===");
        inventory.print();
    }

    public void addQuest(Quest quest) {
        questLog.enqueue(quest);
        System.out.println("Nueva misión aceptada: " + quest.getName());
    }

    public Quest getNextQuest() {
        return (Quest) questLog.dequeue();
    }

    public void earnGold(int amount) {
        gold += amount;
        System.out.println(name + " obtuvo " + amount + " de oro. Total: " + gold);
    }

    public boolean spendGold(int amount) {
        if (gold < amount) {
            System.out.println("Oro insuficiente.");
            return false;
        }
        gold -= amount;
        return true;
    }

    @Override
    public String getStats() {
        return String.format(
            "[ %s ] Nv.%d  HP:%d/%d  ATK:%d  DEF:%d  EXP:%d/%d  Oro:%d",
            name, level, health, maxHealth, attack, defense,
            experience, experienceToNextLevel, gold
        );
    }

    public int getGold(){
        return gold; 
    }
    public LinkedList getInventory(){ 
        return inventory; 
    }
}

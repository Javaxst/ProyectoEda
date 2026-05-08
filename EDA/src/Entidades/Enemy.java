/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;
import Models.Item;

public class Enemy extends Entity {

    public enum EnemyType { NORMAL, ELITE, BOSS }

    private EnemyType type;
    private int expReward;
    private int goldReward;
    private Item lootItem; // puede ser null si no dropea nada

    public Enemy(String name, int maxHealth, int attack, int defense,
                 int level, EnemyType type, int expReward, int goldReward, Item lootItem) {
        super(name, maxHealth, attack, defense, level);
        this.type       = type;
        this.expReward  = expReward;
        this.goldReward = goldReward;
        this.lootItem   = lootItem;
    }

    @Override
    public void takeDamage(int damage) {
        int damageTaken = Math.max(1, damage - defense);
        setHealth(health - damageTaken);
        System.out.println(name + " recibe " + damageTaken + " de daño. HP: " + health + "/" + maxHealth);
    }

    // La IA del enemigo elige una acción según su HP restante
    public String getAIAction() {
        double hpPercent = (double) health / maxHealth;
        if (type == EnemyType.BOSS && hpPercent < 0.3) {
            return "HABILIDAD_ESPECIAL";
        } else if (hpPercent < 0.5) {
            return "ATAQUE_FUERTE";
        } else {
            return "ATAQUE_NORMAL";
        }
    }

    public Item dropLoot() {
        if (lootItem != null) {
            System.out.println(name + " dropea: " + lootItem.getName());
        }
        return lootItem;
    }

    @Override
    public String getStats() {
        return String.format(
            "[ %s ] Nv.%d  HP:%d/%d  ATK:%d  DEF:%d  [%s]",
            name, level, health, maxHealth, attack, defense, type
        );
    }

    public int getExpReward()  { return expReward; }
    public int getGoldReward() { return goldReward; }
    public EnemyType getType() { return type; }
}

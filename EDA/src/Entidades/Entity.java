
package Entidades;
public abstract class Entity {
    
    protected String name;
    protected int health;
    protected int maxHealth;
    protected int attack;
    protected int defense;
    protected int level;
    
    public Entity(String name, int maxHealth, int attack, int defense, int level){
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attack = attack;
        this.defense = defense;
        this.level = level;
    }
    
    //Metodos abstractos
    
    public abstract void takeDamage(int damage);
    public abstract String getStats();
    
    public boolean isAlive(){
        return health>0;
    }

    public String getName() {
        return name;
    }
    public int getHealth() {
        return health;
    }
    public int getMaxHealth() {
        return maxHealth;
    }
    public int getAttack() {
        return attack;
    }
    public int getDefense() {
        return defense;
    }
    public int getLevel() {
        return level;
    }
    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(health, maxHealth));
    }

}

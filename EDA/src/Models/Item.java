
package Models;
public class Item {

    public enum ItemType { WEAPON, ARMOR, POTION, KEY_ITEM }

    private String   name;
    private String   description;
    private ItemType type;
    private int      value;      // precio en oro
    private int      statBonus;  // cuánto sube ATK, DEF o HP según el tipo

    public Item(String name, String description, ItemType type, int value, int statBonus) {
        this.name        = name;
        this.description = description;
        this.type        = type;
        this.value       = value;
        this.statBonus   = statBonus;
    }

    public String getInfo() {
        return String.format("%-20s [%s]  +%d  %d oro  — %s",
            name, type, statBonus, value, description);
    }

    public String   getName()        { return name; }
    public String   getDescription() { return description; }
    public ItemType getType()        { return type; }
    public int      getValue()       { return value; }
    public int      getStatBonus()   { return statBonus; }
}

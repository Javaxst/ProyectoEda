package Models;
public class Zone {

    public enum ZoneType { TOWN, DUNGEON, FIELD, SHOP, BOSS_ROOM }

    private String   name;
    private String   description;
    private ZoneType type;
    private boolean  visited;
    private int      recommendedLevel;

    public Zone(String name, String description, ZoneType type, int recommendedLevel) {
        this.name             = name;
        this.description      = description;
        this.type             = type;
        this.recommendedLevel = recommendedLevel;
        this.visited          = false;
    }

    public void visit() {
        if (!visited) {
            visited = true;
            System.out.println("[ Nueva zona descubierta: " + name + " ]");
        }
    }

    public String getSummary() {
        String status = visited ? "Visitada" : "Sin explorar";
        return String.format("%-20s [%-10s] Nv.recomendado:%-3d  %s  — %s",
            name, type, recommendedLevel, status, description);
    }

    public String   getName()             { return name; }
    public String   getDescription()      { return description; }
    public ZoneType getType()             { return type; }
    public boolean  isVisited()           { return visited; }
    public int      getRecommendedLevel() { return recommendedLevel; }
    public boolean isShop()    { return type == ZoneType.SHOP; }
    public boolean isBossRoom(){ return type == ZoneType.BOSS_ROOM; }
    public boolean isDungeon() { return type == ZoneType.DUNGEON || type == ZoneType.BOSS_ROOM; }
    
}
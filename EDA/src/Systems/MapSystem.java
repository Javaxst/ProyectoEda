/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Systems;
import Models.Zone;
import Models.GameEvent;
import Models.GameEvent.EventType;
import Estructuras.Graph;
import Estructuras.DoubleLinkedList;

public class MapSystem {

    // ── ATRIBUTOS ──────────────────────────────────────────────────────────────

    private Graph            worldMap;
    private int              currentZoneIndex;
    private DoubleLinkedList eventLog;

    public MapSystem(DoubleLinkedList eventLog) {
        this.worldMap         = new Graph(20); // máximo 20 zonas
        this.currentZoneIndex = 0;
        this.eventLog         = eventLog;
    }

    // ── CONSTRUCCIÓN DEL MAPA ──────────────────────────────────────────────────

    public void buildWorld() {
        // ── zonas ──────────────────────────────────────────────────────────────
        int aldea    = worldMap.addZone(new Zone(
            "Aldea Inicial",   "Un pueblo tranquilo rodeado de campos.",   Zone.ZoneType.TOWN,      1));
        int bosque   = worldMap.addZone(new Zone(
            "Bosque Oscuro",   "Arboles densos. Se escuchan crujidos.",    Zone.ZoneType.FIELD,     2));
        int cueva    = worldMap.addZone(new Zone(
            "Cueva del Norte", "Humeda y fria. Huele a azufre.",           Zone.ZoneType.DUNGEON,   4));
        int ruinas   = worldMap.addZone(new Zone(
            "Ruinas Antiguas", "Restos de una civilizacion olvidada.",     Zone.ZoneType.DUNGEON,   6));
        int mercado  = worldMap.addZone(new Zone(
            "Mercado del Sur", "Bullicioso. Hay comerciantes por doquier.",Zone.ZoneType.SHOP,      1));
        int fortaleza= worldMap.addZone(new Zone(
            "Fortaleza Roja",  "Una imponente estructura de piedra roja.", Zone.ZoneType.BOSS_ROOM, 8));

        // ── conexiones (bidireccionales salvo la sala del jefe) ────────────────
        worldMap.addBiEdge(aldea,     bosque,    1);
        worldMap.addBiEdge(aldea,     mercado,   1);
        worldMap.addBiEdge(bosque,    cueva,     2);
        worldMap.addBiEdge(bosque,    ruinas,    3);
        worldMap.addBiEdge(cueva,     ruinas,    2);
        worldMap.addBiEdge(ruinas,    fortaleza, 4);
        worldMap.addEdge  (fortaleza, aldea,     5); // solo se puede salir, no entrar

        // la zona inicial ya fue visitada
        worldMap.getZone(aldea).visit();
        currentZoneIndex = aldea;

        System.out.println("[ Mundo construido: " + worldMap.getSize() + " zonas cargadas ]");
    }

    // ── MOVIMIENTO ─────────────────────────────────────────────────────────────

    public boolean moveTo(String zoneName) {
        int targetIndex = worldMap.findZone(zoneName);
        if (targetIndex == -1) return false;

        if (!worldMap.areConnected(currentZoneIndex, targetIndex)) {
            System.out.println("No hay camino directo desde "
                + getCurrentZone().getName() + " hacia " + zoneName + ".");
            return false;
        }

        currentZoneIndex = targetIndex;
        Zone zone = getCurrentZone();
        zone.visit();

        System.out.println("\n[ Viajaste a: " + zone.getName() + " ]");
        System.out.println("  " + zone.getDescription());

        logEvent("Viajo a " + zone.getName(), EventType.MOVEMENT, null);
        return true;
    }

    public boolean moveTo(int zoneIndex) {
        if (worldMap.getZone(zoneIndex) == null) return false;
        return moveTo(worldMap.getZone(zoneIndex).getName());
    }

    // ── INFORMACIÓN DE ZONA ACTUAL ─────────────────────────────────────────────

    public Zone getCurrentZone() {
        return worldMap.getZone(currentZoneIndex);
    }

    public int getCurrentZoneIndex() {
        return currentZoneIndex;
    }

    // Devuelve las zonas a las que se puede viajar desde la posición actual
    public void printAvailableRoutes() {
        Zone current = getCurrentZone();
        System.out.println("\n── Desde " + current.getName() + " puedes ir a ─────────");

        boolean hasRoutes = false;
        for (int i = 0; i < worldMap.getSize(); i++) {
            if (i != currentZoneIndex && worldMap.areConnected(currentZoneIndex, i)) {
                Zone    zone   = worldMap.getZone(i);
                String  status = zone.isVisited() ? "Visitada" : "Sin explorar";
                System.out.printf("  [%d] %-20s Nv.%-3d  %s%n",
                    i, zone.getName(), zone.getRecommendedLevel(), status);
                hasRoutes = true;
            }
        }
        if (!hasRoutes) System.out.println("  (no hay rutas disponibles)");
        System.out.println("──────────────────────────────────────────────");
    }

    // ── MAPA Y Exploracion ─────────────────────────────────────────────────────

    public void printMap() {
        worldMap.print();
    }

    public void printMapVisual() {
        System.out.println("\n=== Mapa del mundo =========================══════");
        for (int i = 0; i < worldMap.getSize(); i++) {
            Zone   zone    = worldMap.getZone(i);
            String current = (i == currentZoneIndex) ? " <-- AQUI" : "";
            String visited = zone.isVisited() ? "✓" : "?";
            System.out.printf("║  [%s] %-20s Nv.%-3d  %s%s%n",
                visited, zone.getName(), zone.getRecommendedLevel(),
                zone.getType(), current);
        }
        System.out.println("==============================================");
    }

    public void exploreFromCurrent() {
        System.out.println("\n── Exploracion BFS desde " + getCurrentZone().getName());
        worldMap.bfs(currentZoneIndex);
    }

    public void showShortestPaths() {
        System.out.println("\n── Rutas mas cortas desde " + getCurrentZone().getName());
        worldMap.dijkstra(currentZoneIndex);
    }

    // ── UTILIDADES ─────────────────────────────────────────────────────────────

    private void logEvent(String description, EventType type, String actor) {
        eventLog.add(new GameEvent(description, type, actor));
    }
}

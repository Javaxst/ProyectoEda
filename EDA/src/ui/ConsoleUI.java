package ui;

import java.util.Scanner;
import Entidades.Player;
import Entidades.Enemy;
import Entidades.NPC;
import Models.Zone;
import Systems.CombatSystem;
import Systems.InventorySystem;
import Systems.MapSystem;
import Systems.QuestSystem;
import Systems.ShopSystem;
import Estructuras.DoubleLinkedList;

public class ConsoleUI {

    // ── ATRIBUTOS ──────────────────────────────────────────────────────────────

    private Scanner          scanner;
    private DoubleLinkedList eventLog;

    public ConsoleUI(DoubleLinkedList eventLog) {
        this.scanner  = new Scanner(System.in);
        this.eventLog = eventLog;
    }

    // ── MENÚ PRINCIPAL ─────────────────────────────────────────────────────────

    public int showMainMenu(Player player, Zone currentZone) {
        System.out.println("\n=====================================");
        System.out.println("|  " + player.getName()
            + "  |  Nv." + player.getLevel()
            + "  |  HP:" + player.getHealth() + "/" + player.getMaxHealth()
            + "  |  Oro:" + player.getGold());
        System.out.println("|  Zona: " + currentZone.getName()
            + "  [" + currentZone.getType() + "]");
        System.out.println("======================================");
        System.out.println("|  [1] Explorar zona");                 
        System.out.println("|  [2] Viajar");
        System.out.println("|  [3] Inventario");
        System.out.println("|  [4] Misiones");
        System.out.println("|  [5] Historial de eventos");
        System.out.println("|  [6] Ver estadísticas");
        System.out.println("|  [0] Salir");
        System.out.println("======================================");
        System.out.print("  Accion: ");
        return readInt();
    }

    // ── MENÚ DE VIAJE ──────────────────────────────────────────────────────────

    public String showTravelMenu(MapSystem mapSystem) {
        mapSystem.printMapVisual();
        mapSystem.printAvailableRoutes();
        System.out.print("  ¿A dónde viajar? (nombre de zona): ");
        return readLine();
    }

    // ── MENÚ DE INVENTARIO ─────────────────────────────────────────────────────

    public int showInventoryMenu(Player player, InventorySystem inventorySystem) {
        inventorySystem.printInventory(player);
        System.out.println("  [1] Usar item");
        System.out.println("  [2] Descartar item");
        System.out.println("  [0] Volver");
        System.out.print("  Accion: ");
        return readInt();
    }

    // ── MENÚ DE MISIONES ───────────────────────────────────────────────────────

    public int showQuestMenu(QuestSystem questSystem) {
        questSystem.printQuestLog();
        System.out.println("  [1] Aceptar Mision");
        System.out.println("  [2] Ver historial");
        System.out.println("  [0] Volver");
        System.out.print("  Accion: ");
        return readInt();
    }

    // ── MENÚ DE TIENDA ─────────────────────────────────────────────────────────

    public int showShopMenu(NPC merchant, Player player, ShopSystem shopSystem) {
        shopSystem.printShop(merchant);
        System.out.println("  Oro disponible: " + player.getGold());
        System.out.println("  [1] Comprar");
        System.out.println("  [2] Vender");
        System.out.println("  [0] Salir de la tienda");
        System.out.print("  Accion: ");
        return readInt();
    }

    // ── MENÚ DE Exploracion ────────────────────────────────────────────────────

    public int showExploreMenu(Zone currentZone) {
        System.out.println("\n== Explorando: " + currentZone.getName() + " ========");
        System.out.println("  " + currentZone.getDescription());
        System.out.println("======================================");
        System.out.println("  [1] Buscar enemigos");
        System.out.println("  [2] Buscar items");
        System.out.println("  [3] Buscar NPC");
        System.out.println("  [4] Ver rutas desde aquí (BFS)");
        System.out.println("  [5] Ver caminos mas cortos (Dijkstra)");
        System.out.println("  [0] Volver");
        System.out.println("=====================================");
        System.out.print("  Accion: ");
        return readInt();
    }

    // ── HISTORIAL ──────────────────────────────────────────────────────────────

    public void showEventLog() {
        eventLog.printForward();
        System.out.println("  [N] Siguiente  [P] Anterior  [Q] Salir");
        System.out.print("  Navegacion: ");
        String input = readLine().toUpperCase();
        switch (input) {
            case "N" -> {
                eventLog.next();
                System.out.println("  --> " + (eventLog.current() != null
                    ? eventLog.current().getSummary() : "(fin)"));
            }
            case "P" -> {
                eventLog.prev();
                System.out.println("  --> " + (eventLog.current() != null
                    ? eventLog.current().getSummary() : "(inicio)"));
            }
            default -> { /* volver */ }
        }
    }

    // ── PANTALLAS ESPECIALES ───────────────────────────────────────────────────

    public void showWelcome() {
        System.out.println("=====================================");
        System.out.println("                                              ");
        System.out.println("           CHRONICLES OF DATA              ");
        System.out.println("      Un RPG construido con estructuras       ");
        System.out.println("                 de datos                         ");
        System.out.println("=====================================");
        System.out.println();
    }

    public String askPlayerName() {
        System.out.print("  Ingresa el nombre de tu heroe: ");
        return readLine();
    }

    public void showGameOver(Player player) {
        System.out.println("\n====================================");
        System.out.println("             GAME OVER                    ");
        System.out.println("  " + player.getName() + " ha caído en batalla.");
        System.out.println("======================================");
        eventLog.printLast(5);
    }

    public void showVictory(Player player) {
        System.out.println("\n====================================");
        System.out.println("             VICTORIA                      ");
        System.out.println("  " + player.getName() + " ha completado su aventura.");
        System.out.println("  Nivel final  : " + player.getLevel());
        System.out.println("  Oro total    : " + player.getGold());
        System.out.println("======================================");
    }

    public void showMessage(String message) {
        System.out.println("  " + message);
    }

    // ── LECTURA DE INPUT ───────────────────────────────────────────────────────

    public int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String readLine() {
        return scanner.nextLine().trim();
    }

    public void close() {
        scanner.close();
    }
}
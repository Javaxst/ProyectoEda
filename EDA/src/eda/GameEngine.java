
package eda;
import Entidades.Player;
import Entidades.Enemy;
import Entidades.Enemy.EnemyType;
import Entidades.NPC;
import Models.Item;
import Models.Item.ItemType;
import Models.Zone;
import Estructuras.CharacterArray;
import Estructuras.DoubleLinkedList;
import Systems.CombatSystem;
import Systems.InventorySystem;
import Systems.MapSystem;
import Systems.QuestSystem;
import Systems.ShopSystem;
import ui.ConsoleUI;

public class GameEngine {

    // ── ATRIBUTOS ──────────────────────────────────────────────────────────────

    private Player           player;
    private CharacterArray   characters;
    private DoubleLinkedList eventLog;
    private CombatSystem     combatSystem;
    private InventorySystem  inventorySystem;
    private MapSystem        mapSystem;
    private QuestSystem      questSystem;
    private ShopSystem       shopSystem;
    private ConsoleUI        ui;
    private boolean          running;

    // ── INICIALIZACIÓN ─────────────────────────────────────────────────────────

    public GameEngine() {
        this.eventLog        = new DoubleLinkedList();
        this.characters      = new CharacterArray(20);
        this.combatSystem    = new CombatSystem(eventLog);
        this.inventorySystem = new InventorySystem(eventLog);
        this.mapSystem       = new MapSystem(eventLog);
        this.questSystem     = new QuestSystem(eventLog);
        this.shopSystem      = new ShopSystem(eventLog);
        this.ui              = new ConsoleUI(eventLog);
        this.running         = true;
    }

    public void start() {
        ui.showWelcome();
        String name = ui.askPlayerName();
        player = new Player(name.isEmpty() ? "Heroe" : name);
        characters.add(player);

        // item inicial
        inventorySystem.addItem(player,
            new Item("Pocion de Vida", "Restaura 30 HP", ItemType.POTION, 20, 30));

        // construir mundo y poblar NPCs
        mapSystem.buildWorld();
        setupNPCs();

        ui.showMessage("¡Bienvenido, " + player.getName() + "! Tu aventura comienza.");
        gameLoop();
    }

    // ── SETUP DE NPCs ──────────────────────────────────────────────────────────

    private void setupNPCs() {
        NPC merchant = new NPC("Gareth",
            "Mercader",
            new String[]{
                "¡Buenos dias! Tengo lo mejor del reino.",
                "Compra algo, anda.",
                "Mis precios son los mas justos."
            },
            true);

        shopSystem.stockNPC(merchant,
            "Pocion de Vida", "Pocion Mayor",
            "Espada Corta",   "Armadura de Cuero");

        NPC guide = new NPC("Lyra",
            "Guia",
            new String[]{
                "El Bosque Oscuro es peligroso de noche.",
                "En las Ruinas dicen que hay tesoros...",
                "La Fortaleza Roja no ha sido conquistada en siglos."
            },
            false);

        characters.add(merchant);
        characters.add(guide);
    }

    // ── LOOP PRINCIPAL ─────────────────────────────────────────────────────────

    private void gameLoop() {
        while (running && player.isAlive()) {
            Zone currentZone = mapSystem.getCurrentZone();
            int  choice      = ui.showMainMenu(player, currentZone);

            switch (choice) {
                case 1 -> handleExplore();
                case 2 -> handleTravel();
                case 3 -> handleInventory();
                case 4 -> handleQuests();
                case 5 -> ui.showEventLog();
                case 6 -> System.out.println("\n" + player.getStats());
                case 0 -> {
                    running = false;
                    ui.showMessage("Hasta la proxima aventura, " + player.getName() + ".");
                }
                default -> ui.showMessage("Opcion invalida.");
            }

            if (!player.isAlive()) {
                ui.showGameOver(player);
                running = false;
            }
        }
        ui.close();
    }

    // ── Exploracion ────────────────────────────────────────────────────────────

    private void handleExplore() {
        Zone currentZone = mapSystem.getCurrentZone();
        int  choice      = ui.showExploreMenu(currentZone);

        switch (choice) {
            case 1 -> handleCombat();
            case 2 -> handleFindItem();
            case 3 -> handleNPC();
            case 4 -> mapSystem.exploreFromCurrent();
            case 5 -> mapSystem.showShortestPaths();
            case 0 -> { /* volver */ }
            default -> ui.showMessage("Opcion invalida.");
        }
    }

    // ── COMBATE ────────────────────────────────────────────────────────────────

    private void handleCombat() {
        Zone currentZone = mapSystem.getCurrentZone();

        // genera enemigo según la zona actual
        Enemy enemy = spawnEnemy(currentZone);
        if (enemy == null) {
            ui.showMessage("No hay enemigos en esta zona.");
            return;
        }

        characters.add(enemy);
        combatSystem.startCombat(player, enemy);
        characters.removeByName(enemy.getName());

        // completa Mision si aplica
        if (!enemy.isAlive()) checkQuestProgress(enemy);
    }

    private Enemy spawnEnemy(Zone zone) {
        return switch (zone.getType()) {
            case FIELD -> new Enemy("Lobo Salvaje", 35, 10, 4,
                1, EnemyType.NORMAL, 80, 20,
                new Item("Colmillo", "Diente de lobo", ItemType.KEY_ITEM, 5, 0));
            case DUNGEON -> new Enemy("Goblin Guerrero", 55, 14, 6,
                3, EnemyType.ELITE, 150, 50,
                new Item("Pocion de Vida", "Restaura 30 HP", ItemType.POTION, 20, 30));
            case BOSS_ROOM -> new Enemy("Señor de la Fortaleza", 200, 30, 15,
                8, EnemyType.BOSS, 800, 500,
                new Item("Llave Antigua", "Abre una puerta sellada", ItemType.KEY_ITEM, 0, 0));
            default -> null;
        };
    }

    private void checkQuestProgress(Enemy enemy) {
        if (enemy.getName().equals("Lobo Salvaje")
                && questSystem.hasActiveQuest("El lobo del norte")) {
            questSystem.completeQuest(player, "El lobo del norte");
        }
        if (enemy.getName().equals("Señor de la Fortaleza")
                && questSystem.hasActiveQuest("El ultimo guardian")) {
            questSystem.completeQuest(player, "El ultimo guardian");
            ui.showVictory(player);
            running = false;
        }
    }

    // ── itemS EN ZONA ──────────────────────────────────────────────────────────

    private void handleFindItem() {
        // probabilidad de encontrar item según tipo de zona
        double chance = switch (mapSystem.getCurrentZone().getType()) {
            case DUNGEON   -> 0.6;
            case FIELD     -> 0.4;
            case TOWN      -> 0.2;
            default        -> 0.1;
        };

        if (Math.random() < chance) {
            Item found = shopSystem.getFromCatalog("Pocion de Vida");
            inventorySystem.addItem(player, found);
            ui.showMessage("¡Encontraste: " + found.getName() + "!");
        } else {
            ui.showMessage("No encontraste nada esta vez.");
        }
    }

    // ── NPCs ───────────────────────────────────────────────────────────────────

    private void handleNPC() {
        Zone currentZone = mapSystem.getCurrentZone();

        // busca NPC en la zona actual
        NPC npc = findNPCInZone(currentZone);
        if (npc == null) {
            ui.showMessage("No hay nadie aquí.");
            return;
        }

        npc.interact();

        if (npc.hasShop()) {
            int choice = ui.showShopMenu(npc, player, shopSystem);
            switch (choice) {
                case 1 -> {
                    ui.showMessage("¿Que deseas comprar?");
                    String itemName = ui.readLine();
                    shopSystem.buyItem(player, npc, itemName);
                }
                case 2 -> {
                    ui.showMessage("¿Que deseas vender?");
                    String itemName = ui.readLine();
                    shopSystem.sellItem(player, npc, itemName);
                }
                case 0 -> ui.showMessage("Hasta luego.");
            }
        }
    }

    private NPC findNPCInZone(Zone zone) {
        // el mercader esta en el mercado, la guía en la aldea
        return switch (zone.getType()) {
            case SHOP -> (NPC) characters.findByName("Gareth");
            case TOWN -> (NPC) characters.findByName("Lyra");
            default   -> null;
        };
    }

    // ── VIAJE ──────────────────────────────────────────────────────────────────

    private void handleTravel() {
        String destination = ui.showTravelMenu(mapSystem);
        if (!mapSystem.moveTo(destination)) {
            ui.showMessage("No puedes ir ahi desde aqui.");
        }
    }

    // ── INVENTARIO ─────────────────────────────────────────────────────────────

    private void handleInventory() {
        int choice = ui.showInventoryMenu(player, inventorySystem);
        switch (choice) {
            case 1 -> {
                ui.showMessage("¿Qué item deseas usar?");
                String itemName = ui.readLine();
                inventorySystem.useItem(player, itemName);
            }
            case 2 -> {
                ui.showMessage("¿Qué item deseas descartar?");
                String itemName = ui.readLine();
                inventorySystem.removeItem(player, itemName);
            }
            case 3 -> inventorySystem.sortInventory(player);  // ← nuevo
            case 0 -> { /* volver */ }
        }
    }

    // ── MISIONES ───────────────────────────────────────────────────────────────

    private void handleQuests() {
        int choice = ui.showQuestMenu(questSystem);
        switch (choice) {
            case 1 -> questSystem.acceptQuest(player);
            case 2 -> questSystem.printHistory();
            case 0 -> { /* volver */ }
        }
    }

    // ── PUNTO DE ENTRADA ───────────────────────────────────────────────────────

    public static void main(String[] args) {
        new GameEngine().start();
    }
}

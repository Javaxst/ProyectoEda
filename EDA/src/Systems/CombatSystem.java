
package Systems;
import Entidades.Player;
import Entidades.Enemy;
import Models.Action;
import Models.Action.ActionType;
import Models.GameEvent;
import Models.GameEvent.EventType;
import Models.Item;
import Estructuras.Stack;
import Estructuras.DoubleLinkedList;

public class CombatSystem {

    // ── ATRIBUTOS ──────────────────────────────────────────────────────────────

    private Stack            actionStack;  // historial de acciones del turno
    private DoubleLinkedList eventLog;     // referencia al historial global
    private boolean          combatActive;

    public CombatSystem(DoubleLinkedList eventLog) {
        this.actionStack  = new Stack();
        this.eventLog     = eventLog;
        this.combatActive = false;
    }

    // ── FLUJO PRINCIPAL DE COMBATE ─────────────────────────────────────────────

    public void startCombat(Player player, Enemy enemy) {
        combatActive = true;
        System.out.println("\n╔══════════════════════════════════════════════");
        System.out.println("║  ⚔  COMBATE INICIADO");
        System.out.println("║  " + player.getName() + "  vs  " + enemy.getName());
        System.out.println("╚══════════════════════════════════════════════\n");

        logEvent("Combate iniciado contra " + enemy.getName(), EventType.COMBAT, player.getName());

        while (combatActive && player.isAlive() && enemy.isAlive()) {
            printCombatStatus(player, enemy);
            playerTurn(player, enemy);
            if (!enemy.isAlive()) break;
            enemyTurn(enemy, player);
            if (!player.isAlive()) break;
        }

        endCombat(player, enemy);
    }

    // ── TURNO DEL JUGADOR ──────────────────────────────────────────────────────

    private void playerTurn(Player player, Enemy enemy) {
        System.out.println("── Tu turno ──────────────────────────────────");
        System.out.println("  [1] Atacar");
        System.out.println("  [2] Defenderse");
        System.out.println("  [3] Usar poción");
        System.out.println("  [4] Huir");
        System.out.print("  Acción: ");

        int choice = readInput();

        switch (choice) {
            case 1 -> executeAttack(player, enemy);
            case 2 -> executeDefend(player);
            case 3 -> executeUsePotion(player);
            case 4 -> executeFlee(player, enemy);
            default -> {
                System.out.println("  Opción inválida. Pierdes el turno.");
                logEvent(player.getName() + " perdió su turno", EventType.COMBAT, player.getName());
            }
        }
    }

    // ── TURNO DEL ENEMIGO ──────────────────────────────────────────────────────

    private void enemyTurn(Enemy enemy, Player player) {
        System.out.println("\n── Turno de " + enemy.getName() + " ──────────────────");
        String aiAction = enemy.getAIAction();

        switch (aiAction) {
            case "ATAQUE_NORMAL"    -> executeEnemyAttack(enemy, player, 1.0);
            case "ATAQUE_FUERTE"    -> executeEnemyAttack(enemy, player, 1.5);
            case "HABILIDAD_ESPECIAL" -> executeEnemySpecial(enemy, player);
        }
    }

    // ── ACCIONES DEL JUGADOR ───────────────────────────────────────────────────

    private void executeAttack(Player player, Enemy enemy) {
        int damage = calculateDamage(player.getAttack(), enemy.getDefense());
        enemy.takeDamage(damage);

        Action action = new Action(ActionType.ATTACK, player.getName(), enemy.getName(), damage);
        actionStack.push(action);
        logEvent(action.getSummary(), EventType.COMBAT, player.getName());
    }

    private void executeDefend(Player player) {
        // defensa temporal: duplica la defensa hasta el próximo turno
        int bonus = player.getDefense();
        player.setHealth(Math.min(player.getHealth() + 5, player.getMaxHealth())); // recupera 5 HP
        System.out.println("  " + player.getName() + " se defiende y recupera 5 HP.");

        Action action = new Action(ActionType.DEFEND, player.getName(), player.getName());
        actionStack.push(action);
        logEvent(action.getSummary(), EventType.COMBAT, player.getName());
    }

    private void executeUsePotion(Player player) {
        Item potion = player.getInventory().find("Poción de Vida");
        if (potion == null) {
            System.out.println("  No tienes pociones en el inventario.");
            return;
        }
        int healAmount = potion.getStatBonus();
        player.setHealth(player.getHealth() + healAmount);
        player.removeItem("Poción de Vida");
        System.out.println("  Usaste Poción de Vida. HP restaurado: +" + healAmount);

        Action action = new Action(ActionType.USE_ITEM, player.getName(), player.getName(), healAmount);
        actionStack.push(action);
        logEvent(action.getSummary(), EventType.COMBAT, player.getName());
    }

    private void executeFlee(Player player, Enemy enemy) {
        // 40% de probabilidad de huir con éxito
        boolean success = Math.random() < 0.4;
        Action  action  = new Action(ActionType.FLEE, player.getName(), enemy.getName());
        actionStack.push(action);

        if (success) {
            System.out.println("  ¡Huiste exitosamente!");
            logEvent(player.getName() + " huyó del combate", EventType.COMBAT, player.getName());
            combatActive = false;
        } else {
            System.out.println("  No pudiste huir.");
            logEvent(player.getName() + " intentó huir y falló", EventType.COMBAT, player.getName());
        }
    }

    // ── ACCIONES DEL ENEMIGO ───────────────────────────────────────────────────

    private void executeEnemyAttack(Enemy enemy, Player player, double multiplier) {
        int baseDamage = (int)(enemy.getAttack() * multiplier);
        int damage     = calculateDamage(baseDamage, player.getDefense());
        player.takeDamage(damage);

        ActionType type   = multiplier > 1.0 ? ActionType.SPECIAL : ActionType.ATTACK;
        Action     action = new Action(type, enemy.getName(), player.getName(), damage);
        actionStack.push(action);
        logEvent(action.getSummary(), EventType.COMBAT, enemy.getName());
    }

    private void executeEnemySpecial(Enemy enemy, Player player) {
        System.out.println("  ¡" + enemy.getName() + " usa su habilidad especial!");
        executeEnemyAttack(enemy, player, 2.0);
    }

    // ── FIN DE COMBATE ─────────────────────────────────────────────────────────

    private void endCombat(Player player, Enemy enemy) {
        combatActive = false;
        System.out.println("\n╔══════════════════════════════════════════════");

        if (!player.isAlive()) {
            System.out.println("║  💀  DERROTA — " + player.getName() + " fue derrotado.");
            logEvent("Derrota contra " + enemy.getName(), EventType.COMBAT, player.getName());

        } else if (!enemy.isAlive()) {
            System.out.println("║  ✓  VICTORIA — " + enemy.getName() + " derrotado.");
            player.gainExperience(enemy.getExpReward());
            player.earnGold(enemy.getGoldReward());

            Item loot = enemy.dropLoot();
            if (loot != null) player.addItem(loot);

            logEvent("Victoria contra " + enemy.getName(), EventType.COMBAT, player.getName());
        }

        System.out.println("╚══════════════════════════════════════════════");
        System.out.println("\n── Resumen del combate ───────────────────────");
        actionStack.print();
    }

    // ── UTILIDADES ─────────────────────────────────────────────────────────────

    private int calculateDamage(int attack, int defense) {
        int damage = attack - defense;
        // variación aleatoria del ±20%
        double variance = 0.8 + (Math.random() * 0.4);
        return Math.max(1, (int)(damage * variance));
    }

    private void printCombatStatus(Player player, Enemy enemy) {
        System.out.println("\n── Estado ────────────────────────────────────");
        System.out.println("  " + player.getStats());
        System.out.println("  " + enemy.getStats());
        System.out.println("──────────────────────────────────────────────");
    }

    private void logEvent(String description, EventType type, String actor) {
        eventLog.add(new GameEvent(description, type, actor));
    }

    private int readInput() {
        try {
            java.util.Scanner sc = new java.util.Scanner(System.in);
            return sc.nextInt();
        } catch (Exception e) {
            return -1;
        }
    }
}

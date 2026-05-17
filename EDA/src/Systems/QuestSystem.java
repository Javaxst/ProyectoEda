
package Systems;
import Entidades.Player;
import Models.Quest;
import Models.Quest.QuestStatus;
import Models.GameEvent;
import Models.GameEvent.EventType;
import Estructuras.DoubleLinkedList;
import Estructuras.Queue;

public class QuestSystem {

    // ── ATRIBUTOS ──────────────────────────────────────────────────────────────

    private Queue            availableQuests;  // misiones aún no aceptadas
    private Queue            activeQuests;     // misiones en curso
    private DoubleLinkedList completedQuests;  // historial de misiones terminadas
    private DoubleLinkedList eventLog;
    private int              totalCompleted;
    private int              totalFailed;

    public QuestSystem(DoubleLinkedList eventLog) {
        this.availableQuests = new Queue();
        this.activeQuests    = new Queue();
        this.completedQuests = new DoubleLinkedList();
        this.eventLog        = eventLog;
        this.totalCompleted  = 0;
        this.totalFailed     = 0;
        loadQuests();
    }
    
    // ── CARGA INICIAL DE MISIONES ──────────────────────────────────────────────

    private void loadQuests() {
        availableQuests.enqueue(new Quest(
            "El lobo del norte",
            "Derrota al lobo alfa que aterroriza los campos.",
            120, 80));
        availableQuests.enqueue(new Quest(
            "Entrega urgente",
            "Lleva el paquete sellado al herrero de la aldea.",
            60, 40));
        availableQuests.enqueue(new Quest(
            "La cueva maldita",
            "Explora la Cueva del Norte y regresa con pruebas.",
            300, 150));
        availableQuests.enqueue(new Quest(
            "El ultimo guardian",
            "Derrota al jefe de la Fortaleza Roja.",
            800, 500));
        availableQuests.enqueue(new Quest(
            "Coleccionista de ruinas",
            "Encuentra 3 artefactos en las Ruinas Antiguas.",
            200, 100));

        System.out.println("[ Misiones cargadas: " + availableQuests.getSize() + " disponibles ]");
    }

    // ── GESTIÓN DE MISIONES ────────────────────────────────────────────────────

    public boolean acceptQuest(Player player) {
        if (availableQuests.isEmpty()) {
            System.out.println("No hay misiones disponibles en este momento.");
            return false;
        }

        Quest quest = (Quest) availableQuests.dequeue();
        quest.start();
        activeQuests.enqueue(quest);
        player.addQuest(quest);

        System.out.println("\n=== Nueva Mision aceptada =========================");
        System.out.println("║  " + quest.getSummary());
        System.out.println("==============================================");

        logEvent("Acepto Mision: " + quest.getName(), EventType.QUEST, player.getName());
        return true;
    }

    public boolean acceptQuestByName(Player player, String questName) {
        // busca la Mision específica en la cola de disponibles
        int size = availableQuests.getSize();
        for (int i = 0; i < size; i++) {
            Quest quest = (Quest) availableQuests.dequeue();
            if (quest.getName().equalsIgnoreCase(questName)) {
                quest.start();
                activeQuests.enqueue(quest);
                player.addQuest(quest);
                System.out.println("Mision aceptada: " + quest.getName());
                logEvent("Acepto Mision: " + quest.getName(), EventType.QUEST, player.getName());
                return true;
            }
            // si no es la que buscamos la volvemos a encolar
            availableQuests.enqueue(quest);
        }
        System.out.println("Mision '" + questName + "' no encontrada.");
        return false;
    }

    public boolean completeQuest(Player player, String questName) {
        int size = activeQuests.getSize();
        for (int i = 0; i < size; i++) {
            Quest quest = (Quest) activeQuests.dequeue();
            if (quest.getName().equalsIgnoreCase(questName)) {
                quest.complete();
                completedQuests.add(new GameEvent(
                    quest.getName() + " completada", EventType.QUEST, player.getName()));

                player.gainExperience(quest.getExpReward());
                player.earnGold(quest.getGoldReward());
                totalCompleted++;

                System.out.println("\n=== Mision completada! =========================══");
                System.out.println("║  " + quest.getSummary());
                System.out.println("║  EXP ganada : +" + quest.getExpReward());
                System.out.println("║  Oro ganado : +" + quest.getGoldReward());
                System.out.println("==============================================");

                logEvent("Completó Mision: " + quest.getName(), EventType.QUEST, player.getName());
                return true;
            }
            // si no es la que buscamos la volvemos a encolar
            activeQuests.enqueue(quest);
        }
        System.out.println("Mision '" + questName + "' no esta activa.");
        return false;
    }

    public boolean failQuest(Player player, String questName) {
        int size = activeQuests.getSize();
        for (int i = 0; i < size; i++) {
            Quest quest = (Quest) activeQuests.dequeue();
            if (quest.getName().equalsIgnoreCase(questName)) {
                quest.fail();
                totalFailed++;

                System.out.println("\n=== Mision fallida =========================════");
                System.out.println("║  " + quest.getSummary());
                System.out.println("==============================================");

                logEvent("Fallo Mision: " + quest.getName(), EventType.QUEST, player.getName());
                return true;
            }
            activeQuests.enqueue(quest);
        }
        System.out.println("Mision '" + questName + "' no esta activa.");
        return false;
    }

    // ── IMPRESIÓN ──────────────────────────────────────────────────────────────

    public void printAvailable() {
        System.out.println("\n=== Misiones disponibles (" + availableQuests.getSize() + ") =============");
        if (availableQuests.isEmpty()) {
            System.out.println("║  (ninguna disponible)");
        } else {
            availableQuests.print();
        }
        System.out.println("==============================================");
    }

    public void printActive() {
        System.out.println("\n=== Misiones activas (" + activeQuests.getSize() + ") ===============");
        if (activeQuests.isEmpty()) {
            System.out.println("║  (ninguna activa)");
        } else {
            activeQuests.print();
        }
        System.out.println("==============================================");
    }

    public void printHistory() {
        System.out.println("\n=== Historial de misiones =========================");
        System.out.println("  Completadas : " + totalCompleted);
        System.out.println("  Fallidas    : " + totalFailed);
        System.out.println("==================================================══");
        completedQuests.printForward();
        System.out.println("==============================================");
    }

    public void printQuestLog() {
        printAvailable();
        printActive();
    }

    // ── UTILIDADES ─────────────────────────────────────────────────────────────

    public boolean hasActiveQuest(String questName) {
        int size = activeQuests.getSize();
        for (int i = 0; i < size; i++) {
            Quest quest = (Quest) activeQuests.dequeue();
            boolean found = quest.getName().equalsIgnoreCase(questName);
            activeQuests.enqueue(quest);
            if (found) return true;
        }
        return false;
    }

    public boolean hasAvailableQuests()  { return !availableQuests.isEmpty(); }
    public boolean hasActiveQuests()     { return !activeQuests.isEmpty(); }
    public int     getTotalCompleted()   { return totalCompleted; }
    public int     getTotalFailed()      { return totalFailed; }
    
    private void logEvent(String description, EventType type, String actor) {
        eventLog.add(new GameEvent(description, type, actor));
    }
}

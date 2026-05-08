package Models;
public class Quest {

    public enum QuestStatus { PENDING, ACTIVE, COMPLETED, FAILED }

    private String      name;
    private String      description;
    private QuestStatus status;
    private int         expReward;
    private int         goldReward;

    public Quest(String name, String description, int expReward, int goldReward) {
        this.name        = name;
        this.description = description;
        this.status      = QuestStatus.PENDING;
        this.expReward   = expReward;
        this.goldReward  = goldReward;
    }

    public void start()    { status = QuestStatus.ACTIVE; }
    public void complete() { status = QuestStatus.COMPLETED; }
    public void fail()     { status = QuestStatus.FAILED; }

    public String getSummary() {
        return String.format("%-25s [%-9s]  EXP:+%d  Oro:+%d  — %s",
            name, status, expReward, goldReward, description);
    }

    public String      getName()        { return name; }
    public String      getDescription() { return description; }
    public QuestStatus getStatus()      { return status; }
    public int         getExpReward()   { return expReward; }
    public int         getGoldReward()  { return goldReward; }
}
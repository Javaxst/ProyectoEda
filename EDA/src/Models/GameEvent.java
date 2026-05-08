package Models;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class GameEvent {

    public enum EventType {
        COMBAT, MOVEMENT, ITEM, QUEST, DIALOGUE, SYSTEM
    }

    private String    description;
    private EventType type;
    private String    timestamp;
    private String    involvedEntity; // quién protagonizó el evento (puede ser null)

    public GameEvent(String description, EventType type, String involvedEntity) {
        this.description    = description;
        this.type           = type;
        this.involvedEntity = involvedEntity;
        this.timestamp      = LocalTime.now()
                                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    // Constructor corto para eventos sin entidad involucrada
    public GameEvent(String description, EventType type) {
        this(description, type, null);
    }

    public String getSummary() {
        String entity = (involvedEntity != null) ? "[" + involvedEntity + "] " : "";
        return String.format("%s  %-9s  %s%s",
            timestamp, "(" + type + ")", entity, description);
    }

    public String    getDescription()    { return description; }
    public EventType getType()           { return type; }
    public String    getTimestamp()      { return timestamp; }
    public String    getInvolvedEntity() { return involvedEntity; }
}

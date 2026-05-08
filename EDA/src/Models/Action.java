/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;
public class Action {

    public enum ActionType { ATTACK, DEFEND, USE_ITEM, FLEE, SPECIAL }

    private ActionType type;
    private String     actorName;
    private String     targetName;
    private int        value;      // daño, curación, etc.

    public Action(ActionType type, String actorName, String targetName, int value) {
        this.type       = type;
        this.actorName  = actorName;
        this.targetName = targetName;
        this.value      = value;
    }

    // Constructor para acciones sin valor numérico (FLEE, DEFEND)
    public Action(ActionType type, String actorName, String targetName) {
        this(type, actorName, targetName, 0);
    }

    public String getSummary() {
        return switch (type) {
            case ATTACK   -> actorName + " atacó a " + targetName + " por " + value;
            case DEFEND   -> actorName + " se defendió";
            case USE_ITEM -> actorName + " usó un ítem en " + targetName + " (+" + value + ")";
            case FLEE     -> actorName + " intentó huir";
            case SPECIAL  -> actorName + " usó habilidad especial contra " + targetName + " por " + value;
        };
    }

    public ActionType getType()       { return type; }
    public String     getActorName()  { return actorName; }
    public String     getTargetName() { return targetName; }
    public int        getValue()      { return value; }
}

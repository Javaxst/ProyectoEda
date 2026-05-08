
package Estructuras;
import Entidades.Entity;

public class CharacterArray {

    private Entity[] characters;
    private int size;
    private int capacity;

    public CharacterArray(int capacity) {
        this.capacity   = capacity;
        this.characters = new Entity[capacity];
        this.size       = 0;
    }

    // ── INSERCIÓN ──────────────────────────────────────────────────────────────

    public boolean add(Entity entity) {
        if (size == capacity) {
            System.out.println("CharacterArray lleno. Capacidad máxima: " + capacity);
            return false;
        }
        characters[size] = entity;
        size++;
        return true;
    }

    // ── ACCESO POR ÍNDICE ──────────────────────────────────────────────────────

    public Entity get(int index) {
        if (!isValidIndex(index)) return null;
        return characters[index];
    }

    public boolean set(int index, Entity entity) {
        if (!isValidIndex(index)) return false;
        characters[index] = entity;
        return true;
    }

    // ── BÚSQUEDA ───────────────────────────────────────────────────────────────

    public Entity findByName(String name) {
        for (int i = 0; i < size; i++) {
            if (characters[i].getName().equalsIgnoreCase(name)) {
                return characters[i];
            }
        }
        System.out.println("Personaje '" + name + "' no encontrado.");
        return null;
    }

    public int indexOf(String name) {
        for (int i = 0; i < size; i++) {
            if (characters[i].getName().equalsIgnoreCase(name)) return i;
        }
        return -1;
    }

    // ── ELIMINACIÓN ────────────────────────────────────────────────────────────

    public boolean remove(int index) {
        if (!isValidIndex(index)) return false;
        // desplaza los elementos hacia la izquierda
        for (int i = index; i < size - 1; i++) {
            characters[i] = characters[i + 1];
        }
        characters[size - 1] = null;
        size--;
        return true;
    }

    public boolean removeByName(String name) {
        int index = indexOf(name);
        if (index == -1) {
            System.out.println("No se encontró a '" + name + "' para eliminar.");
            return false;
        }
        return remove(index);
    }

    // ── FILTROS ÚTILES PARA EL JUEGO ───────────────────────────────────────────

    // Devuelve todos los personajes vivos
    public CharacterArray getAlive() {
        CharacterArray alive = new CharacterArray(size);
        for (int i = 0; i < size; i++) {
            if (characters[i].isAlive()) alive.add(characters[i]);
        }
        return alive;
    }

    // Devuelve el personaje con menos HP (útil para IA o habilidades)
    public Entity getLowestHealth() {
        if (size == 0) return null;
        Entity lowest = characters[0];
        for (int i = 1; i < size; i++) {
            if (characters[i].getHealth() < lowest.getHealth()) {
                lowest = characters[i];
            }
        }
        return lowest;
    }

    // ── IMPRESIÓN ──────────────────────────────────────────────────────────────

    public void print() {
        if (size == 0) {
            System.out.println("No hay personajes registrados.");
            return;
        }
        System.out.println("╔══ Personajes (" + size + "/" + capacity + ") ═══════════════════════");
        for (int i = 0; i < size; i++) {
            System.out.println("║ [" + i + "] " + characters[i].getStats());
        }
        System.out.println("╚═════════════════════════════════════════════");
    }

    // ── UTILIDADES ─────────────────────────────────────────────────────────────

    private boolean isValidIndex(int index) {
        if (index < 0 || index >= size) {
            System.out.println("Índice " + index + " fuera de rango. Tamaño actual: " + size);
            return false;
        }
        return true;
    }

    public int getSize()     { return size; }
    public int getCapacity() { return capacity; }
    public boolean isFull()  { return size == capacity; }
    public boolean isEmpty() { return size == 0; }
}
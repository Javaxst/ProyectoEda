package Estructuras;
import Models.Zone;

public class Graph {

    // ── NODO DE ADYACENCIA (lista de conexiones) ───────────────────────────────

    private static class AdjNode {
        int     zoneIndex; // Indice de la zona destino
        int     weight;    // costo de desplazamiento (distancia, dificultad, etc.)
        AdjNode next;

        AdjNode(int zoneIndex, int weight) {
            this.zoneIndex = zoneIndex;
            this.weight    = weight;
            this.next      = null;
        }
    }

    // ── ATRIBUTOS ──────────────────────────────────────────────────────────────

    private Zone[]    zones;       // array de zonas (nodos)
    private AdjNode[] adjList;     // lista de adyacencia
    private int       capacity;
    private int       size;

    public Graph(int capacity) {
        this.capacity = capacity;
        this.zones    = new Zone[capacity];
        this.adjList  = new AdjNode[capacity];
        this.size     = 0;
    }

    // ── GESTIÓN DE ZONAS (nodos) ───────────────────────────────────────────────

    public int addZone(Zone zone) {
        if (size == capacity) {
            System.out.println("Grafo lleno. No se pueden agregar mas zonas.");
            return -1;
        }
        zones[size]   = zone;
        adjList[size] = null;
        int index     = size;
        size++;
        return index; // devuelve el Indice asignado
    }

    public Zone getZone(int index) {
        if (!isValidIndex(index)) return null;
        return zones[index];
    }

    public int findZone(String zoneName) {
        for (int i = 0; i < size; i++) {
            if (zones[i].getName().equalsIgnoreCase(zoneName)) return i;
        }
        System.out.println("Zona '" + zoneName + "' no encontrada.");
        return -1;
    }

    // ── GESTIÓN DE CONEXIONES (aristas) ───────────────────────────────────────

    // Conexion dirigida: desde --> hasta
    public void addEdge(int fromIndex, int toIndex, int weight) {
        if (!isValidIndex(fromIndex) || !isValidIndex(toIndex)) return;

        // verifica que la Conexion no exista ya
        AdjNode current = adjList[fromIndex];
        while (current != null) {
            if (current.zoneIndex == toIndex) {
                System.out.println("Conexion ya existe: " +
                    zones[fromIndex].getName() +  " --> " + zones[toIndex].getName());
                return;
            }
            current = current.next;
        }

        AdjNode newNode       = new AdjNode(toIndex, weight);
        newNode.next          = adjList[fromIndex];
        adjList[fromIndex]    = newNode;
    }

    // Conexion bidireccional (la mas común en el mapa del RPG)
    public void addBiEdge(int fromIndex, int toIndex, int weight) {
        addEdge(fromIndex, toIndex, weight);
        addEdge(toIndex, fromIndex, weight);
    }

    public boolean areConnected(int fromIndex, int toIndex) {
        if (!isValidIndex(fromIndex) || !isValidIndex(toIndex)) return false;
        AdjNode current = adjList[fromIndex];
        while (current != null) {
            if (current.zoneIndex == toIndex) return true;
            current = current.next;
        }
        return false;
    }

    // ── BFS — zonas alcanzables desde un origen ────────────────────────────────

    public void bfs(int startIndex) {
        if (!isValidIndex(startIndex)) return;

        boolean[] visited = new boolean[size];
        int[]     queue   = new int[size]; // cola manual con array
        int       front   = 0;
        int       rear    = 0;

        visited[startIndex]  = true;
        queue[rear++]        = startIndex;

        System.out.println("==== BFS desde: " + zones[startIndex].getName() + " ==========");
        while (front < rear) {
            int     current = queue[front++];
            System.out.println("  Visitando: " + zones[current].getName());

            AdjNode adj = adjList[current];
            while (adj != null) {
                if (!visited[adj.zoneIndex]) {
                    visited[adj.zoneIndex]  = true;
                    queue[rear++]           = adj.zoneIndex;
                }
                adj = adj.next;
            }
        }
        System.out.println("=====================================");
    }

    // ── DFS — Exploracion profunda desde un origen ─────────────────────────────

    public void dfs(int startIndex) {
        if (!isValidIndex(startIndex)) return;
        boolean[] visited = new boolean[size];
        System.out.println("=== DFS desde: " + zones[startIndex].getName() + " =========");
        dfsRecursive(startIndex, visited);
        System.out.println("==================================");
    }

    private void dfsRecursive(int index, boolean[] visited) {
        visited[index] = true;
        System.out.println("  Visitando: " + zones[index].getName());
        AdjNode current = adjList[index];
        while (current != null) {
            if (!visited[current.zoneIndex]) {
                dfsRecursive(current.zoneIndex, visited);
            }
            current = current.next;
        }
    }

    // ── RUTA mas CORTA — Dijkstra ─────────────────────────────────────────────

    public void dijkstra(int startIndex) {
        if (!isValidIndex(startIndex)) return;

        int[]     dist    = new int[size];
        boolean[] visited = new boolean[size];
        int[]     prev    = new int[size];  // para reconstruir el camino

        for (int i = 0; i < size; i++) {
            dist[i] = Integer.MAX_VALUE;
            prev[i] = -1;
        }
        dist[startIndex] = 0;

        for (int i = 0; i < size; i++) {
            // nodo no visitado con menor distancia
            int u = -1;
            for (int j = 0; j < size; j++) {
                if (!visited[j] && (u == -1 || dist[j] < dist[u])) u = j;
            }
            if (dist[u] == Integer.MAX_VALUE) break;
            visited[u] = true;

            AdjNode adj = adjList[u];
            while (adj != null) {
                int v      = adj.zoneIndex;
                int newDist = dist[u] + adj.weight;
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    prev[v] = u;
                }
                adj = adj.next;
            }
        }

        // imprime resultados
        System.out.println("=== Dijkstra desde: " + zones[startIndex].getName() + " =======");
        for (int i = 0; i < size; i++) {
            if (dist[i] == Integer.MAX_VALUE) {
                System.out.println("  " + zones[i].getName() + ": inalcanzable");
            } else {
                System.out.print("  " + zones[i].getName() + " (costo:" + dist[i] + ")  ruta: ");
                printPath(prev, i);
                System.out.println();
            }
        }
        System.out.println("====================================");
    }

    private void printPath(int[] prev, int index) {
        if (prev[index] == -1) {
            System.out.print(zones[index].getName());
            return;
        }
        printPath(prev, prev[index]);
        System.out.print(" --> " + zones[index].getName());
    }

    // ── IMPRESIÓN DEL MAPA ─────────────────────────────────────────────────────

    public void print() {
        System.out.println("=== Mapa del mundo (" + size + " zonas) =============");
        for (int i = 0; i < size; i++) {
            System.out.print("  [" + i + "] " + zones[i].getName() + "  -->  ");
            AdjNode current = adjList[i];
            if (current == null) {
                System.out.print("(sin conexiones)");
            }
            while (current != null) {
                System.out.print(zones[current.zoneIndex].getName()
                    + "(w:" + current.weight + ")");
                if (current.next != null) System.out.print(", ");
                current = current.next;
            }
            System.out.println();
        }
        System.out.println("======================================");
    }

    // ── UTILIDADES ─────────────────────────────────────────────────────────────

    private boolean isValidIndex(int index) {
        if (index < 0 || index >= size) {
            System.out.println("Indice de zona " + index + " invalido.");
            return false;
        }
        return true;
    }

    public int getSize()     { return size; }
    public int getCapacity() { return capacity; }
}
import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> tasksHistory = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (tasksHistory.containsKey(task.getId())) {
            // Удаляем существующий узел, если задача уже есть
            remove(task.getId());
        }

        // Создаем новый узел и добавляем его в конец списка
        Node newNode = new Node(task);
        addToTail(newNode);
        tasksHistory.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        Node node = tasksHistory.get(id);
        if (node == null) {
            return; // Если узла нет, ничего не делаем
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next; // Если узел был головой, обновляем голову
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev; // Если узел был хвостом, обновляем хвост
        }

        // Удаляем узел из карты
        tasksHistory.remove(id);
    }

    /**
     * Добавляет узел в конец списка.
     */
    private void addToTail(Node node) {
        if (tail == null) {
            // Если список пуст, устанавливаем голову и хвост
            head = tail = node;
        } else {
            // Добавляем новый узел после текущего хвоста
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;

        while (current != null) {
            history.add(current.task); // Добавляем задачу в список
            current = current.next;    // Переходим к следующему узлу
        }

        return history; // Возвращаем ArrayList<Task>
    }

    private static class Node {
        Task task; // Храним саму задачу
        Node prev;
        Node next;

        public Node(Task task) {
            this.task = task;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(task, node.task);
        }

        @Override
        public int hashCode() {
            return Objects.hash(task);
        }
    }
}
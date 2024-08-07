import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> historyList;
    private Node<Task> head;
    private Node<Task> tail;
    private int size;

    public InMemoryHistoryManager() {
        historyList = new HashMap<>();
        size = 0;
    }

    @Override
    public void add(Task task) {
        Task taskClone;
        switch (task) {
            case Epic epic -> taskClone = new Epic(epic);
            case Subtask subtask -> taskClone = new Subtask(subtask);
            default -> taskClone = new Task(task);
        }
        remove(taskClone.getId());
        linkLast(taskClone);
    }

    @Override
    public void remove(int id) {
        Node<Task> toRemove = historyList.get(id);
        if (toRemove == null) {
            return;
        }

        Node<Task> previousNode = toRemove.prev;
        Node<Task> nextNode = toRemove.next;

        if (previousNode != null) previousNode.next = nextNode;
        if (nextNode != null) nextNode.prev = previousNode;

        if (toRemove.task == head.task) head = head.next;
        if (toRemove.task == tail.task) tail = tail.prev;

        historyList.remove(id);
        size--;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasksList = new ArrayList<>();

        if (tail == null) {
            return tasksList;
        }

        Node<Task> current = tail;
        boolean isPrevExist = true;
        while (isPrevExist) {
            tasksList.add(current.task);
            if (current.prev != null) {
                current = current.prev;
            } else {
                isPrevExist = false;
            }
        }

        return tasksList;
    }

    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else
            oldTail.next = newNode;
        size++;

        historyList.put(task.getId(), newNode);
    }

}

package tasks;

import enums.Status;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {

    private List<Integer> subTaskIds = new ArrayList<>();

    public EpicTask(String title, String description, int id, Status status) {
        super(title, description, id, status);
    }

    public EpicTask(int id, String title, String description, Status status) {
        super(id, title, description, status);
    }

    public EpicTask(String title, String description) {
        super(title, description);
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(List<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    public void clearSubTask() {
        subTaskIds.clear();
    }

    public void addSubTaskId(int subTaskId) {
        subTaskIds.add(subTaskId);
    }

    @Override
    public String toString() {
        if (subTaskIds.isEmpty()) {
            return "Эпик{" +
                    "название='" + title + '\'' +
                    ", описание='" + description + '\'' +
                    ", id='" + id + '\'' +
                    ", статус='" + status + "}";
        } else {
            return "Эпик{" +
                    "название='" + title + '\'' +
                    ", описание='" + description + '\'' +
                    ", id='" + id + '\'' +
                    ", статус='" + status + '\'' +
                    ", id подзадачи=" + subTaskIds + "}";
        }
    }
}

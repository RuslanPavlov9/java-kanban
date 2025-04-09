package tasks;

import enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;
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

    public EpicTask(String title, String description, Status status) {
        super(title, description, status);
    }

    public EpicTask(int id, String title, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(id, title, description, status, duration, startTime);
    }

    public EpicTask(String title, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(title, description, status, duration, startTime);
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
    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        if (subTaskIds.isEmpty()) {
            return "Эпик{" +
                    "название='" + title + '\'' +
                    ", описание='" + description + '\'' +
                    ", id='" + id + '\'' +
                    ", статус='" + status + '\'' +
                    ", продолжительность=" + duration +
                    ", время начала=" + startTime +
                    "}";
        } else {
            return "Эпик{" +
                    "название='" + title + '\'' +
                    ", описание='" + description + '\'' +
                    ", id='" + id + '\'' +
                    ", статус='" + status + '\'' +
                    ", id подзадачи=" + subTaskIds +
                    ", продолжительность=" + duration +
                    ", время начала=" + startTime +
                    "}";
        }
    }
}

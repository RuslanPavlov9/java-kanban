import java.util.Objects;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String title, String description, int id, Status status, int epicId) {
        super(title, description, id, status);
        this.epicId = epicId;
    }

    public SubTask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public SubTask(String title, String description) {
        super(title, description);
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Подзадача{" +
                "Имя задачи='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", epicId=" + epicId +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask subtask = (SubTask) o;
        return Objects.equals(title, subtask.title) &&
                Objects.equals(description, subtask.description) &&
                Objects.equals(id, subtask.id) &&
                Objects.equals(status, subtask.status) &&
                (epicId == subtask.epicId);
    }

}




import enums.Status;
import tasks.EpicTask;


public class EpicCantAddAsSubTask {

    private EpicTask epicTask;
    private final String name = "Эпик-задача";
    private final String description = "Описание эпика";
    private final int id = 1;
    private final Status status = Status.NEW;

    private static TaskManager taskManager;

//    @BeforeEach
//    void setUp() {
//        taskManager = Managers.getInMemoryTaskManager();
//        epicTask = new EpicTask(name, description, id, status);
//        Task task = new Task("fdsf","fdsf");
//        taskManager.addSubTask(task);
//        taskManager.addTask(task);
//        //SubTask subTask = epicTask;
//    }

    //прошу разъяснить вот эти вот пункты:
    //-проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
    //-проверьте, что объект Subtask нельзя сделать своим же эпиком;

    //Так как методы addEpicTask и addSubTask ожидают на вход объекты  EpicTask и SubTask то IDE не позволяет передать эти объекты. Или же что-то другое имелось ввиду?
}

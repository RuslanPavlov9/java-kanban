public class Main {

     static TaskManager taskManager = new TaskManager();

        public static void main(String[] args) {

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTask(task1);
        Task task2= new Task("Задача 2", "Описание задачи 2" );
        taskManager.addTask(task2);

        EpicTask epicTask1 = new EpicTask("Эпик 1", "Описание эпика 1");
        taskManager.addEpicTask(epicTask1);
        SubTask subtask1 = new SubTask("Подзадача 1", "Описание подзадчи 1", epicTask1.getId());
        taskManager.addSubTask(subtask1);
        SubTask subtask2 = new SubTask("Подзадача 2", "Описание подзадчи 2", epicTask1.getId());
        taskManager.addSubTask(subtask2);

        EpicTask epicTask2 = new EpicTask("Эпик 2", "Описание эпика 2");
        taskManager.addEpicTask(epicTask2);
        SubTask subtask3 = new SubTask("Подзадача 1 Эпика 2", "Описание подзадачи 1 эпика 2", epicTask2.getId());
        taskManager.addSubTask(subtask3);

        System.out.println(task1);
        System.out.println(task2);
        System.out.println(epicTask1);
        System.out.println(subtask1);
        System.out.println(subtask2);

        System.out.println(epicTask2);
        System.out.println(subtask3);

        task1.setStatus(Status.IN_PROGRESS);
        task2.setStatus(Status.DONE);
        epicTask1.setStatus(Status.DONE);
        taskManager.updateTask(task1);
        taskManager.updateEpicTask(epicTask1);

        subtask3.setStatus(Status.DONE);
        taskManager.updateSubTask(subtask3);

        System.out.println("________Результаты обновления статусов_________");
        System.out.println(task1);
        System.out.println(task2);
        System.out.println(epicTask1);
        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println(epicTask2);
        System.out.println(subtask3);


        System.out.println("________удаление____________");
        taskManager.removeTaskById(task1.getId());
        taskManager.removeEpicTaskById(epicTask1.getId());

    }

}


public class Managers {

        public static InMemoryTaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static InMemoryHistoryManager getInMemoryHistoryManager(){
        return new InMemoryHistoryManager();
    }
}

package task.tasks;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import task.domain.Task;

public interface TaskManager {
    @Transactional
    public void addTask(Task task);

    @Transactional
    public Task getTaskByID(Integer id);

    @Transactional(readOnly = true)
    public List<Task> listTask();

    @Transactional
    public Task readTask(int id);

    @Transactional
    public void removeTask(Integer id);

    @Transactional
    public void updateTask(Task task);
}

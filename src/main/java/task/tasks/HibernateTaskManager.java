package task.tasks;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import task.domain.Task;

@Repository
public class HibernateTaskManager implements TaskManager {

    @Resource(name = "sessionFactory")
    private SessionFactory sessionFactory;

    public HibernateTaskManager() {
    }

    @Autowired
    public HibernateTaskManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    @Override
    public void addTask(Task task) {
        getCurrentSession().save(task);
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Task getTaskByID(Integer id) {
        Task temptask;
        temptask = (Task) getCurrentSession().get(Task.class, id);
        //  System.out.println(temptask.getTaskName());
        return temptask;
    }

    @SuppressWarnings("unchecked")
    //@Transactional(readOnly = true)
    @Override
    public List<Task> listTask() {
        return getCurrentSession().createQuery("FROM Task").list();
    }

    @Override
    @Transactional(readOnly = true)
    public Task readTask(int id) {
        // TODO Auto-generated method stub
        Task task = (Task) getCurrentSession().get(Task.class, id);
        return task;
    }

    @Transactional
    @Override
    public void removeTask(Integer id) {
        Task task = (Task) getCurrentSession().load(Task.class, id);
        if (task != null) {
            getCurrentSession().delete(task);
        }
    }

    protected void setCurrentSession(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /* @Transactional
     public void update(Task task) {
         getCurrentSession().merge(task);
     }*/

    @Override
    @Transactional
    public void updateTask(Task task) {
        // TODO Auto-generated method stub
        getCurrentSession().merge(task);

    }
}

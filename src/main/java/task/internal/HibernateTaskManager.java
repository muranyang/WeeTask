package task.internal;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import task.domain.Task;
import task.tasks.TaskManager;

@Repository
public class HibernateTaskManager implements TaskManager
{

    @Resource(name = "sessionFactory")
    private SessionFactory sessionFactory;

    public HibernateTaskManager()
    {
    }

    @Autowired
    public HibernateTaskManager(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    @Override
    public void addTask(Task task)
    {
        getCurrentSession().save(task);
    }

    protected Session getCurrentSession()
    {
        return sessionFactory.getCurrentSession();
    }

    @Override
    @Transactional
    public Task getTaskByID(Integer id)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    @Override
    public List<Task> listTask()
    {
        return getCurrentSession().createQuery("FROM Task").list();
    }

    @Override
    @Transactional
    public Task readTask(int id)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Transactional
    @Override
    public void removeTask(Integer id)
    {
        Task task = (Task) sessionFactory.getCurrentSession().load(Task.class, id);
        if (task != null)
        {
            getCurrentSession().delete(task);
        }
    }

    @Transactional
    public void update(Task task)
    {
        getCurrentSession().update(task);
    }

    @Override
    @Transactional
    public void updateTask(Task task)
    {
        // TODO Auto-generated method stub

    }
}

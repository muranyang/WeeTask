package task.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

/**
 * @author MYANG
 *
 */
@Entity
@Table(name = "TASK")
public class Task {
    @Id
    @Column(name = "TASK_ID")
    @SequenceGenerator(name = "SEQ_STORE", sequenceName = "ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_STORE")
    private Integer taskID;

    @Column(name = "TASK_NAME")
    @NotBlank
    @Size(max = 30)
    @NotNull
    private String  taskName;

    @Column(name = "ASSIGNEE")
    @NotBlank
    @Size(max = 30)
    private String  assignee;

    @Column(name = "DESCRIPTION")
    @Size(max = 150)
    private String  description;

    @Column(name = "D_DATE")
    @Temporal(TemporalType.DATE)
    private Date    date;

    public Task() {
    }

    public String getAssignee() {
        return assignee;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public Integer getTaskID() {
        return taskID;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setID(int taskID) {
        this.taskID = taskID;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}

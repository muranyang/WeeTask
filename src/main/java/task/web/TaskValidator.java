package task.web;

import java.util.Locale;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import task.domain.Task;

@Component
public class TaskValidator implements Validator {

    // private TaskManager taskManager;

    public TaskValidator() {

    }

    /**
     * Tell Spring that this validator supports instances of Task.
     *
     * @param clazz The class name.
     * @return true if we can validate instances of clazz; false otherwse.
     */

    @Override
    public boolean supports(Class<?> clazz) {
        return Task.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {

        validateDate(obj, errors);
        validateDescription(obj, errors);
        // validateTaskName(obj, errors);
        // validateAsignee(obj, errors);

    }

    public void validateAsignee(Object obj, Errors errors) {
        Task task = (Task) obj;

        if (task.getAssignee().trim().isEmpty()) {

            errors.rejectValue("assignee", "assigneeEmpty");
        }
    }

    public void validateDate(Object obj, Errors errors) {
        Task task = (Task) obj;

        String date;

        if (task.getDate() != null) {
            date = task.getDate().toString();
            if (date.substring(24).length() > 4) {
                errors.rejectValue("date", "yeartoolong");
            }
            if (!date.substring(24).matches("^(19|20)[0-9]{2}$")) {
                errors.rejectValue("date", "dtwrongformat");
            }
        }
    }

    public void validateDescription(Object obj, Errors errors) {
        Task task = (Task) obj;
        if (task.getDescription().toLowerCase(Locale.CANADA).contains("http://") || task.getDescription().contains("<script") || task.getDescription().toLowerCase(Locale.CANADA).contains("img")) {

            errors.rejectValue("description", "Pattern.task.description");

        }
    }

    public void validateTaskName(Object obj, Errors errors) {
        Task task = (Task) obj;

        if (task.getTaskName().trim().isEmpty()) {
            errors.rejectValue("taskName", "taskNameEmpty");
        }
    }

}

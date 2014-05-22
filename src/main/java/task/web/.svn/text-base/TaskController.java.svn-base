package task.web;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;

import task.domain.Task;
import task.tasks.TaskManager;

/**
 * Handles requests for the Task home page.
 */
@Controller
@SessionAttributes("task")
@RequestMapping("/")
public class TaskController {

    @Autowired
    private TaskManager   taskManager;

    @Autowired
    private TaskValidator taskValidator;

    public TaskController() {
    }

    /**
     * Adds tasks to the database
     * @param task
     * @param result
     * @param model
     * @return
     */

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addTask(@ModelAttribute("task") @Valid Task task, BindingResult result, Model model) {

        taskValidator.validate(task, result);
        if (result.hasErrors()) {
            //  taskManager.addTask(task);
            return listTask(model);
        }
        else {

            taskManager.addTask(task);
            model.addAttribute("task", new Task());
            return "redirect:/list";
        }
    }

    @ModelAttribute("task")
    public Task createTask() {
        return new Task();
    }

    /**
     * 
     * @param taskID
     * @return
     */
    @RequestMapping(value = "/delete/{taskID}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteTask(@PathVariable("taskID") Integer taskID) {
        taskManager.removeTask(taskID);
        return "redirect:/list";

    }

    /**
     * Format start date for update manager page
     * @param date
     * @return the proper date format for updating
     */
    private String fDate(Date date) {
        String d = null;
        String year = null;
        String day = null;
        String month = null;

        if (date == null) {
            return "";
        }
        else {
            d = date.toString();
            year = d.substring(0, 4);
            month = d.substring(5, 7);
            day = d.substring(8, 10);
        }
        return month + "/" + day + "/" + year;
    }

    @ModelAttribute("task")
    private Task getTask() {
        return new Task();
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setLenient(false);
        dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));

    }

    /**
     * List Task Manager model
     * @param model
     * @return home page 
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String listTask(Model model) {
        model.addAttribute("taskList", taskManager.listTask());
        return "home";
    }

    /**
     * 
     * 
     */

    @RequestMapping(value = { "/updateTask" }, method = RequestMethod.POST)
    public String update(HttpServletRequest request, @ModelAttribute("task") @Valid Task task, BindingResult result, Model model) {
        Integer id = (Integer) request.getSession().getAttribute("id");
        Task taskTemp = taskManager.readTask(id);

        if (taskTemp != null) {
            taskValidator.validate(task, result);
            if (result.hasErrors()) {
                model.addAttribute("update", taskTemp);
                listTask(model);
                return "UpdateTask";
            }
            else {
                task.setID(id);
                taskManager.updateTask(task);
                request.getSession().invalidate();
                model.addAttribute("task", new Task());

            }
        }
        return "redirect:/list";

    }

    /**
     * 
     * @param id
     * @param request
     * @param model
     * @return update page
     */
    @RequestMapping(value = "/updates/{taskID}", method = RequestMethod.GET)
    // @ResponseStatus(HttpStatus.NO_CONTENT)
    public String updateDisplay(@PathVariable("taskID") Integer id, HttpServletRequest request, Model model) {
        Task tempTask;

        tempTask = taskManager.getTaskByID(id);

        model.addAttribute("taskName", tempTask.getTaskName());
        model.addAttribute("assignee", tempTask.getAssignee());
        model.addAttribute("description", tempTask.getDescription());
        model.addAttribute("date", fDate(tempTask.getDate()));

        //model.addAttribute("date", dt.format(tempTask.getDate()));

        request.getSession().setAttribute("id", id);

        listTask(model);
        // model.addAttribute(tempTask);

        return "UpdateTask";

    }
}

import model.*;
import spark.*;
import spark.template.velocity.*;
import java.util.*;
import static spark.Spark.*;

/**
 * This class contains exactly the same functionality as TodoList,
 * but it's following normal Spark conventions more closely.
 */
public class BasicTodoList {

    private static TodoDao todoDao = new TodoDaoImplWithJdbc();

    public static void main(String[] args) {

        exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
        staticFiles.location("/public");
        port(9999);

        // Render main UI
        get("/", (req, res) -> renderTodos(req));

        // Add new
        post("/todos", (req, res) -> {
            todoDao.add(Todo.create(req.queryParams("todo-title")));
            return renderTodos(req);
        });

        // Remove all completed
        delete("/todos/completed", (req, res) -> {
            todoDao.removeCompleted();
            return renderTodos(req);
        });

        // Toggle all status
        put("/todos/toggle_status", (req, res) -> {
            todoDao.toggleAll(req.queryParams("toggle-all") != null);
            return renderTodos(req);
        });

        // Remove by id
        delete("/todos/:id", (req, res) -> {
            todoDao.remove(req.params("id"));
            return renderTodos(req);
        });

        // Update by id
        put("/todos/:id", (req, res) -> {
            todoDao.update(req.params("id"), req.queryParams("todo-title"));
            return renderTodos(req);
        });

        // Toggle status by id
        put("/todos/:id/toggle_status", (req, res) -> {
            todoDao.toggleStatus(req.params("id"));
            return renderTodos(req);
        });

        // Edit by id
        get("/todos/:id/edit", (req, res) -> renderEditTodo(req));

    }

    private static String renderEditTodo(Request req) {
        Map<String, Object> context = new HashMap<>();
        context.put("todo", todoDao.find(req.params("id")));
        return renderTemplate("velocity/editTodo.vm", context);
    }

    private static String renderTodos(Request req) {
        String statusStr = req.queryParams("status");
        Map<String, Object> model = new HashMap<>();
        model.put("todos", todoDao.ofStatus(statusStr));
        model.put("filter", Optional.ofNullable(statusStr).orElse(""));
        model.put("activeCount", todoDao.ofStatus(Status.ACTIVE).size());
        model.put("anyCompleteTodos", todoDao.ofStatus(Status.COMPLETE).size() > 0);
        model.put("allComplete", todoDao.all().size() == todoDao.ofStatus(Status.COMPLETE).size());
        model.put("status", Optional.ofNullable(statusStr).orElse(""));
        if ("true".equals(req.queryParams("ic-request"))) {
            return renderTemplate("velocity/todoList.vm", model);
        }
        return renderTemplate("velocity/index.vm", model);
    }

    private static String renderTemplate(String template, Map<String, Object> model) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, template));
    }

}

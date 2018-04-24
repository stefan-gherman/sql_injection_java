
import model.Todo;
import model.TodoDao;
import model.TodoDaoImplWithJdbc;
import model.TodoDaoImplWithList;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class BasicTodoList {

    TodoDao todoDao = new TodoDaoImplWithList();

    public static final String SUCCESS = "success";

    public static void main(String[] args) {
        BasicTodoList todoApp = new BasicTodoList();
        todoApp.run();
    }

    public void run() {

        addSampleData();

        exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
        staticFiles.location("/public");
        port(9999);

        // Render main UI
        get("/", (req, res) -> {
                Map<String, Object> model = new HashMap<>();
                return new ThymeleafTemplateEngine().render(new ModelAndView(model, "index"));
            });

        // Add new
        post("/addTodo", (req, res) -> {
                Todo newTodo = Todo.create(req.queryParams("todo-title"));
                todoDao.add(newTodo);
                return SUCCESS;
            });

        // List by id
        post("/list", (req, resp) -> {
                List<Todo> todos = todoDao.ofStatus(req.queryParams("status"));
                JSONArray jsons = new JSONArray();
                for (Todo todo : todos) {
                    JSONObject jo = new JSONObject();
                    jo.put("id", todo.getId());
                    jo.put("title", todo.getTitle());
                    jo.put("completed", todo.isComplete());
                    jsons.put(jo);
                }
                final int indentFactor = 2;
                return jsons.toString(indentFactor);
            });

        // Remove all completed
        delete("/todos/completed", (req, res) -> {
                todoDao.removeCompleted();
                return SUCCESS;
            });

        // Toggle all status
        put("/todos/toggle_all", (req, res) -> {
                String complete = req.queryParams("toggle-all");
                todoDao.toggleAll(complete.equals("true"));
                return SUCCESS;
            });

        // Remove by id
        delete("/todos/:id", (req, res) -> {
                todoDao.remove(req.params("id"));
                return SUCCESS;
            });

        // Update by id
        put("/todos/:id", (req, res) -> {
                todoDao.update(req.params("id"), req.queryParams("todo-title"));
                return SUCCESS;
            });

        // Find by id
        get("/todos/:id", (req, res) -> todoDao.find(req.params("id")).getTitle());

        // Toggle status by id
        put("/todos/:id/toggle_status", (req, res) -> {
                // boolean completed = Boolean.valueOf(req.queryParams("status"));
                todoDao.toggleStatus(req.params("id"));
                return SUCCESS;
            });
    }

    private void addSampleData() {
        todoDao.add(Todo.create("first TODO item"));
        todoDao.add(Todo.create("second TODO item"));
        todoDao.add(Todo.create("third TODO item"));
    }

}

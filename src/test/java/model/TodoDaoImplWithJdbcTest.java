package model;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by ani on 2016.11.13..
 * These are integration tests
 * because the class under test (TodoDaoImplWithJdbc)
 * talks to the database.
 */
public class TodoDaoImplWithJdbcTest {


    private TodoDaoImplWithJdbc dao;
    private Todo activeTodo;
    private Todo completedTodo;

    @Before
    public void setUp() throws Exception {
        dao = new TodoDaoImplWithJdbc();
        dao.deleteAll();

        activeTodo = Todo.create("active todo");
        completedTodo = Todo.create("completed todo");
        completedTodo.status = Status.COMPLETE;
    }

    @Test
    public void find_shouldReturnAddedTodos() throws Exception {
        dao.add(activeTodo);
        Todo todoFromDao = dao.find(activeTodo.id);

        assertEquals(activeTodo, todoFromDao);
    }

    @Test
    public void find_forNonexistingId_shouldRetudnNull() throws Exception {
        Todo todoFromDao = dao.find("42");

        assertNull(todoFromDao);
    }

    @Test
    public void deleteAll_shoudClearAllTodos() throws Exception {
        Todo firstTodo = Todo.create("first");
        Todo secondTodo = Todo.create("second");
        dao.add(firstTodo);
        dao.add(secondTodo);

        dao.deleteAll();

        assertNull(dao.find(firstTodo.id));
        assertNull(dao.find(secondTodo.id));
    }

    @Test
    public void all_shoudReturnAllTodos() throws Exception {
        Todo firstTodo = Todo.create("first");
        Todo secondTodo = Todo.create("second");
        dao.add(firstTodo);
        dao.add(secondTodo);

        List<Todo> todos = dao.all();

        assertEquals(2, todos.size());
        assertTrue(todos.contains(firstTodo));
        assertTrue(todos.contains(secondTodo));
    }

    @Test
    public void update_shouldChangeTheTitle() throws Exception {
        Todo todo = Todo.create("whatever");
        dao.add(todo);

        dao.update(todo.id, "an other title");

        assertEquals("an other title", dao.find(todo.id).title);
    }

    @Test
    public void remove_shouldRemoveTheTodoById() throws Exception {
        dao.add(activeTodo);

        dao.remove(activeTodo.id);

        assertEquals(0, dao.all().size());
    }

    @Test
    public void remove_shouldKeepOtherTodos() throws Exception {
        Todo todoToRemove = Todo.create("todo to remove");
        dao.add(todoToRemove);
        Todo otherTodo = Todo.create("other");
        dao.add(otherTodo);

        dao.remove(todoToRemove.id);

        List<Todo> allTodos = dao.all();
        assertEquals(1, allTodos.size());
        assertEquals(otherTodo.title, allTodos.get(0).title);
    }

    @Test
    public void toggleStatus_whenGetNonexistingId_shouldDoNothing() throws Exception {
        dao.toggleStatus("42");

        assertEquals(0, dao.all().size());
    }

    @Test
    public void toggleStatus_whenGetActiveTodo_shouldCahngeStatusToCompleted() throws Exception {
        dao.add(activeTodo);

        dao.toggleStatus(activeTodo.id);

        assertEquals(Status.COMPLETE, dao.find(activeTodo.id).status);
    }

    @Test
    public void toggleStatus_whenGetCompletedTodo_shouldChangeStatusToActive() throws Exception {
        dao.add(completedTodo);

        dao.toggleStatus(completedTodo.id);

        assertEquals(Status.ACTIVE, dao.find(completedTodo.id).status);
    }

    @Test
    public void removeCompleted_shouldRemoveCompleted() throws Exception {
        Todo todo = Todo.create("whatever");
        todo.status = Status.COMPLETE;
        dao.add(todo);

        dao.removeCompleted();

        assertEquals(0, dao.all().size());
    }

    @Test
    public void removeCompleted_shouldLeaveActive() throws Exception {
        dao.add(activeTodo);

        dao.removeCompleted();

        assertEquals(1, dao.all().size());
    }

    @Test
    public void ofStatusStatus_active_shouldReturnActive() throws Exception {
        dao.add(activeTodo);

        List<Todo> activeTodoList = dao.ofStatus(Status.ACTIVE);

        assertEquals(1, activeTodoList.size());
        assertEquals(activeTodo.title, activeTodoList.get(0).title);
    }

    @Test
    public void ofStatusStatus_active_shouldLeaveOutCompleted() throws Exception {
        dao.add(completedTodo);

        List<Todo> activeTodoList = dao.ofStatus(Status.ACTIVE);

        assertEquals(0, activeTodoList.size());
    }

    @Test
    public void ofStatusStatus_complete_shouldReturnComplete() throws Exception {
        dao.add(completedTodo);

        List<Todo> completedTodoList = dao.ofStatus(Status.COMPLETE);

        assertEquals(1, completedTodoList.size());
        assertEquals(completedTodo.title, completedTodoList.get(0).title);
    }

    @Test
    public void ofStatusStatus_complete_shouldLeaveOutActive() throws Exception {
        dao.add(activeTodo);

        List<Todo> completedTodoList = dao.ofStatus(Status.COMPLETE);

        assertEquals(0, completedTodoList.size());
    }

    @Ignore
    @Test
    public void ofStatusString_whenGetsNull_heheCannotCallWithNull() throws Exception {
        // Hehe, cannot call with 'null',
        // as compiled cannot decide which ofStatus.
        // Left here only for documentation purpose.
    }

    @Test
    public void ofStatusString_whenGetsEmptyString_shouldReturnAll() throws Exception {
        dao.add(activeTodo);
        dao.add(completedTodo);

        List<Todo> todoList = dao.ofStatus("");

        assertEquals(2, todoList.size());
    }

    @Test
    public void ofStatusString_active_shouldReturnActive() throws Exception {
        dao.add(activeTodo);

        List<Todo> activeTodoList = dao.ofStatus(Status.ACTIVE.toString());

        assertEquals(1, activeTodoList.size());
        assertEquals(activeTodo.title, activeTodoList.get(0).title);
    }

    @Test
    public void ofStatusString_active_shouldLeaveOutCompleted() throws Exception {
        dao.add(completedTodo);

        List<Todo> activeTodoList = dao.ofStatus(Status.ACTIVE.toString());

        assertEquals(0, activeTodoList.size());
    }

    @Test
    public void ofStatusString_complete_shouldReturnComplete() throws Exception {
        dao.add(completedTodo);

        List<Todo> completedTodoList = dao.ofStatus(Status.COMPLETE.toString());

        assertEquals(1, completedTodoList.size());
        assertEquals(completedTodo.title, completedTodoList.get(0).title);
    }

    @Test
    public void ofStatusString_complete_shouldLeaveOutActive() throws Exception {
        dao.add(activeTodo);

        List<Todo> completedTodoList = dao.ofStatus(Status.COMPLETE.toString());

        assertEquals(0, completedTodoList.size());
    }

    @Test
    public void ofStatusString_shouldWorkWithLowerCaseOrMixedString() throws Exception {
        dao.add(activeTodo);

        List<Todo> activeTodoList = dao.ofStatus("actiVe");

        assertEquals(1, activeTodoList.size());
        assertEquals(activeTodo.title, activeTodoList.get(0).title);
    }

    @Test
    public void toggleAll_true_shouldToggleActiveToComplete() throws Exception {
        dao.add(activeTodo);

        dao.toggleAll(true);

        assertEquals(Status.COMPLETE, dao.find(activeTodo.id).status);
    }

    @Test
    public void toggleAll_true_shouldLeaveCompleteUntouched() throws Exception {
        dao.add(completedTodo);

        dao.toggleAll(true);

        assertEquals(Status.COMPLETE, dao.find(completedTodo.id).status);
    }

    @Test
    public void toggleAll_false_shouldLeaveActiveUntouched() throws Exception {
        dao.add(activeTodo);

        dao.toggleAll(false);

        assertEquals(Status.ACTIVE, dao.find(activeTodo.id).status);
    }

    @Test
    public void toggleAll_true_shouldToggleCompleteToActive() throws Exception {
        dao.add(completedTodo);

        dao.toggleAll(false);

        assertEquals(Status.ACTIVE, dao.find(completedTodo.id).status);
    }
}
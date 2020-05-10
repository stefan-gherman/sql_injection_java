package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ani on 2016.11.13..
 */
public class TodoDaoImplWithJdbc implements TodoDao {

    private static final String DATABASE = "jdbc:postgresql://localhost:5432/todolist";
    private static final String DB_USER = "stefan_g";
    private static final String DB_PASSWORD = "lenovoideapadS340";

    @Override
    public void add(Todo todo) {
//        String query = "INSERT INTO todos (title, id, status) " +
//                "VALUES ('" + todo.title + "', '" + todo.id + "', '" + todo.status + "');";
        String query = "INSERT INTO todos (title, id, status) VALUES (?, ?, ?)";
        try(
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query);

        )   {
            statement.setString(1, todo.title);
            statement.setString(2, todo.id);
            statement.setString(3, String.valueOf(todo.status));
            statement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(query);
        //executeQuery(query);
    }

    @Override
    public Todo find(String id) {

        String query = "SELECT * FROM todos WHERE id ='" + id + "';";

        try (Connection connection = getConnection();
             Statement statement =connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query);
        ){
            if (resultSet.next()){
                Todo result = new Todo(resultSet.getString("title"),
                        resultSet.getString("id"),
                        Status.valueOf(resultSet.getString("status")));
                return result;
            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
   }

    @Override
    public void update(String id, String title) {
        String query = "UPDATE todos SET title = '" + title + "' WHERE id = '" + id + "';";
        executeQuery(query);
    }

    @Override
    public List<Todo> ofStatus(String statusString) {
        return (statusString == null || statusString.isEmpty()) ?
                all() : ofStatus(Status.valueOf(statusString.toUpperCase()));
    }

    @Override
    public List<Todo> ofStatus(Status status) {
        String query = "SELECT * FROM todos WHERE status ='" + status + "';";;

        List<Todo> resultList = new ArrayList<>();

        try (Connection connection = getConnection();
             Statement statement =connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query);
        ){
            while (resultSet.next()){
                Todo actTodo = new Todo(resultSet.getString("title"),
                        resultSet.getString("id"),
                        Status.valueOf(resultSet.getString("status")));
                resultList.add(actTodo);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    @Override
    public void remove(String id) {
        String query = "DELETE FROM todos WHERE id = '" + id +"';";
        executeQuery(query);
    }

    @Override
    public void removeCompleted() {
        String query = "DELETE FROM todos WHERE status = '" + Status.COMPLETE +"';";
        executeQuery(query);
    }

    @Override
    public void toggleStatus(String id) {
        Todo todo = find(id);

        if (null == todo) {
            return;
        }

        Status newStatus = (todo.status == Status.ACTIVE) ? Status.COMPLETE : Status.ACTIVE;
        String query = "UPDATE todos SET status = '" + newStatus + "' WHERE id = '" + id + "';";
        executeQuery(query);

    }

    @Override
    public void toggleAll(boolean complete) {
        Status newStatus = complete ? Status.COMPLETE : Status.ACTIVE;
        String query = "UPDATE todos SET status = '" + newStatus + "';";
        executeQuery(query);
    }

    @Override
    public List<Todo> all() {
        String query = "SELECT * FROM todos;";

        List<Todo> resultList = new ArrayList<>();

        try (Connection connection = getConnection();
             Statement statement =connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query);
        ){
            while (resultSet.next()){
                Todo actTodo = new Todo(resultSet.getString("title"),
                        resultSet.getString("id"),
                        Status.valueOf(resultSet.getString("status")));
                resultList.add(actTodo);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    // package private so test can see it, but TodoList not
    void deleteAll() {
        String query = "DELETE FROM todos;";
        executeQuery(query);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DATABASE,
                DB_USER,
                DB_PASSWORD);
    }

    private void executeQuery(String query) {
        try (Connection connection = getConnection();
             Statement statement =connection.createStatement();
        ){
            statement.execute(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

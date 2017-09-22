package model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ani on 2016-11-13.
 */
public interface TodoDao {

    public void add(Todo todo);

    public Todo find(String id);

    public void update(String id, String title);

    public List<Todo> ofStatus(String statusString);

    public List<Todo> ofStatus(Status status);

    public void remove(String id);

    public void removeCompleted();

    public void toggleStatus(String id);

    public void toggleAll(boolean complete);

    public List<Todo> all();
}

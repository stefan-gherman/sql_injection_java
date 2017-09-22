package model;

import java.util.*;
import java.util.stream.*;

public class TodoDaoImplWithList implements TodoDao{

    private final List<Todo> DATA = new ArrayList<>();

    public void add(Todo todo) {
        DATA.add(todo);
    }

    public Todo find(String id) {
        return DATA.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
    }

    public void update(String id, String title) {
        find(id).setTitle(title);
    }

    public List<Todo> ofStatus(String statusString) {
        return (statusString == null || statusString.isEmpty()) ? DATA : ofStatus(Status.valueOf(statusString.toUpperCase()));
    }

    public List<Todo> ofStatus(Status status) {
        return DATA.stream().filter(t -> t.getStatus().equals(status)).collect(Collectors.toList());
    }

    public void remove(String id) {
        DATA.remove(find(id));
    }

    public void removeCompleted() {
        ofStatus(Status.COMPLETE).forEach(t -> remove(t.getId()));
    }

    public void toggleStatus(String id) {
        find(id).toggleStatus();
    }

    public void toggleAll(boolean complete) {
        all().forEach(t -> t.setStatus(complete ? Status.COMPLETE : Status.ACTIVE));
    }

    public List<Todo> all() {
        return DATA;
    }
}

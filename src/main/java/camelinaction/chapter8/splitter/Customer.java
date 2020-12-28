package camelinaction.chapter8.splitter;

import java.util.List;

public class Customer {
    private int id;
    private String name;
    private List<Department> departments;

    public Customer() {
    }

    public Customer(int id, String name, List<Department> departments) {
        this.id = id;
        this.name = name;
        this.departments = departments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}

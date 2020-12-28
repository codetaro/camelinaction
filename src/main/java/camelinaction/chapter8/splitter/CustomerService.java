package camelinaction.chapter8.splitter;

import java.util.ArrayList;
import java.util.List;

public class CustomerService {

    public List<Department> splitDepartments(Customer customer) {
        return customer.getDepartments();
    }

    public static Customer createCustomer() {
        List<Department> departments = new ArrayList<Department>();
        departments.add(new Department(222, "Oceanview 66", "89210", "USA"));
        departments.add(new Department(333, "Lakeside 41", "22020", "USA"));
        departments.add(new Department(444, "Highstreet 341", "11030", "USA"));

        Customer customer = new Customer(123, "Honda", departments);
        return customer;
    }
}

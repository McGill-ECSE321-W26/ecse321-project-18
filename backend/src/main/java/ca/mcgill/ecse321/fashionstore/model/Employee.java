/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.36.0.8183.32a6408a9 modeling language!*/

package ca.mcgill.ecse321.fashionstore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.*;

// line 26 "../../../../../../model.ump"
// line 98 "../../../../../../model.ump"
@Entity
public class Employee extends Customer {

    // ------------------------
    // MEMBER VARIABLES
    // ------------------------

    // Employee Associations
    @OneToMany(mappedBy = "employee")
    private List<Order> assignedOrders;

    // ------------------------
    // CONSTRUCTOR
    // ------------------------

    public Employee() {
        super();
        assignedOrders = new ArrayList<Order>();
    }

    // ------------------------
    // INTERFACE
    // ------------------------
    /* Code from template association_GetMany */
    public Order getAssignedOrder(int index) {
        Order aAssignedOrder = assignedOrders.get(index);
        return aAssignedOrder;
    }

    public List<Order> getAssignedOrders() {
        List<Order> newAssignedOrders = Collections.unmodifiableList(assignedOrders);
        return newAssignedOrders;
    }

    public int numberOfAssignedOrders() {
        int number = assignedOrders.size();
        return number;
    }

    public boolean hasAssignedOrders() {
        boolean has = assignedOrders.size() > 0;
        return has;
    }

    public int indexOfAssignedOrder(Order aAssignedOrder) {
        int index = assignedOrders.indexOf(aAssignedOrder);
        return index;
    }

    /* Code from template association_MinimumNumberOfMethod */
    public static int minimumNumberOfAssignedOrders() {
        return 0;
    }

    /* Code from template association_AddManyToOptionalOne */
    public boolean addAssignedOrder(Order aAssignedOrder) {
        boolean wasAdded = false;
        if (assignedOrders.contains(aAssignedOrder)) {
            return false;
        }
        Employee existingEmployee = aAssignedOrder.getEmployee();
        if (existingEmployee == null) {
            aAssignedOrder.setEmployee(this);
        } else if (!this.equals(existingEmployee)) {
            existingEmployee.removeAssignedOrder(aAssignedOrder);
            addAssignedOrder(aAssignedOrder);
        } else {
            assignedOrders.add(aAssignedOrder);
        }
        wasAdded = true;
        return wasAdded;
    }

    public boolean removeAssignedOrder(Order aAssignedOrder) {
        boolean wasRemoved = false;
        if (assignedOrders.contains(aAssignedOrder)) {
            assignedOrders.remove(aAssignedOrder);
            aAssignedOrder.setEmployee(null);
            wasRemoved = true;
        }
        return wasRemoved;
    }

    /* Code from template association_AddIndexControlFunctions */
    public boolean addAssignedOrderAt(Order aAssignedOrder, int index) {
        boolean wasAdded = false;
        if (addAssignedOrder(aAssignedOrder)) {
            if (index < 0) {
                index = 0;
            }
            if (index > numberOfAssignedOrders()) {
                index = numberOfAssignedOrders() - 1;
            }
            assignedOrders.remove(aAssignedOrder);
            assignedOrders.add(index, aAssignedOrder);
            wasAdded = true;
        }
        return wasAdded;
    }

    public boolean addOrMoveAssignedOrderAt(Order aAssignedOrder, int index) {
        boolean wasAdded = false;
        if (assignedOrders.contains(aAssignedOrder)) {
            if (index < 0) {
                index = 0;
            }
            if (index > numberOfAssignedOrders()) {
                index = numberOfAssignedOrders() - 1;
            }
            assignedOrders.remove(aAssignedOrder);
            assignedOrders.add(index, aAssignedOrder);
            wasAdded = true;
        } else {
            wasAdded = addAssignedOrderAt(aAssignedOrder, index);
        }
        return wasAdded;
    }

    public void delete() {
        while (!assignedOrders.isEmpty()) {
            assignedOrders.get(0).setEmployee(null);
        }
        super.delete();
    }
}

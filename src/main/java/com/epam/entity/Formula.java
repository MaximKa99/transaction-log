package com.epam.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Set;

@Entity
@Table(name = "formulas")
public class Formula {
    @Id
    @GeneratedValue
    private long id;
    private String expression;
    private Set<String> variables;

    public Formula() {
    }

    public Formula(long id, String expression, Set<String> variables) {
        this.id = id;
        this.expression = expression;
        this.variables = variables;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Set<String> getVariables() {
        return variables;
    }

    public void setVariables(Set<String> variables) {
        this.variables = variables;
    }
}

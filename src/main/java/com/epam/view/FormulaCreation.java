package com.epam.view;

import java.util.Collections;
import java.util.Set;

public class FormulaCreation {
    private String expression;
    private Set<String> variables = Collections.emptySet();

    public FormulaCreation() {
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

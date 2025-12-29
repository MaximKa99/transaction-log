package com.epam;

import com.epam.entity.Formula;
import com.epam.service.NumberSequenceGenerator;
import com.epam.view.CalculationResult;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Calculator {
    private final Long id;
    private final Formula formula;
    private final Expression expression;

    public Calculator(Formula formula) {
        this.formula = formula;
        this.id = formula.getId();
        this.expression = new ExpressionBuilder(formula.getExpression())
                .variables(formula.getVariables())
                .build();
    }

    public CalculationResult calculate(Double value) {
        Map<String, Double> setVars = new HashMap<>();

        formula.getVariables().forEach(var -> {
            setVars.put(var, value);
        });
        expression.setVariables(setVars);

        Double result = expression.evaluate();
        CalculationResult calculationResult = new CalculationResult();
        calculationResult.setResult(result);

        return calculationResult;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Calculator that = (Calculator) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

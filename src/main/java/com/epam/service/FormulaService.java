package com.epam.service;

import com.epam.entity.Formula;
import com.epam.exception.FormulaNotFoundException;
import com.epam.repository.FormulaRepository;
import jakarta.transaction.Transactional;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FormulaService {
    private final FormulaRepository formulaRepository;

    public FormulaService(FormulaRepository formulaRepository) {
        this.formulaRepository = formulaRepository;
    }

    public Formula getFormulaById(Long id) {
        return formulaRepository.findById(id)
                .orElseThrow(() -> new FormulaNotFoundException(FormulaNotFoundException.FORMULA_NOT_FOUND_TEMPLATE.formatted(id)));
    }

    @Transactional
    public Formula saveFormula(String expression, Set<String> variables) {
        Formula created = new Formula();
        created.setExpression(expression);
        created.setVariables(variables);

        Expression expr = new ExpressionBuilder(expression)
                .variables(variables)
                .build();

        return formulaRepository.save(created);
    }

    @Transactional
    public Formula updateFormula(Long id, String expression, Set<String> variables) {
        Formula toBeUpdated = formulaRepository.findById(id)
                .orElseThrow(() -> new FormulaNotFoundException(FormulaNotFoundException.FORMULA_NOT_FOUND_TEMPLATE.formatted(id)));

        toBeUpdated.setExpression(expression);
        toBeUpdated.setVariables(variables);

        return formulaRepository.save(toBeUpdated);
    }

    @Transactional
    public Formula deleteFormula(Long id) {
        Formula toBeDeleted = formulaRepository.findById(id)
                .orElseThrow(() -> new FormulaNotFoundException(FormulaNotFoundException.FORMULA_NOT_FOUND_TEMPLATE.formatted(id)));
        formulaRepository.delete(toBeDeleted);
        return toBeDeleted;
    }
}

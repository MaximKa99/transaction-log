package com.epam.controller;

import com.epam.entity.Formula;
import com.epam.service.FormulaService;
import com.epam.view.FormulaCreation;
import com.epam.view.FormulaUpdate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/formula")
public class FormulaController {
    private final FormulaService formulaService;

    public FormulaController(FormulaService formulaService) {
        this.formulaService = formulaService;
    }

    @GetMapping("/{id}")
    public Formula getFormula(@PathVariable Long id) {
        return formulaService.getFormulaById(id);
    }

    @PostMapping
    public Formula createFormula(@RequestBody FormulaCreation formulaCreation) {
        return formulaService.saveFormula(formulaCreation.getExpression(), formulaCreation.getVariables());
    }

    @PutMapping
    public Formula updateFormula(@RequestBody FormulaUpdate formulaUpdate) {
        return formulaService.updateFormula(formulaUpdate.getId(), formulaUpdate.getExpression(), formulaUpdate.getVariables());
    }

    @DeleteMapping("/{id}")
    public Formula deleteFormula(@PathVariable Long id) {
        return formulaService.deleteFormula(id);
    }
}

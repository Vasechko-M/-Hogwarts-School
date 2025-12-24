package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.service.ParallelStreamSumService;


@RestController
public class ParallelStreamSumController {

    private final ParallelStreamSumService parallelStreamSumService;

    @Autowired
    public ParallelStreamSumController(ParallelStreamSumService parallelStreamSumService) {
        this.parallelStreamSumService = parallelStreamSumService;
    }

    @Operation(summary = "Сравнение времени вычисления суммы с использованием параллельного и последовательного стрима")
    @GetMapping("/parallel-sum")
    public String getSumComparison() {
        return parallelStreamSumService.getSumComparison();
    }
}

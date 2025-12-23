package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

@RestController
public class ParallelStreamSumController {
    @Operation(summary = "Сравнение времени вычисления суммы с использованием параллельного и последовательного стрима")
    @GetMapping("/parallel-sum")
    public String getSumComparison() {
        long startSeq = System.currentTimeMillis();
        int sumSeq = Stream.iterate(1, a -> a + 1)
                .limit(1_000_000)
                .reduce(0, Integer::sum);
        long endSeq = System.currentTimeMillis();
        long durationSeq = endSeq - startSeq;

        long startPar = System.currentTimeMillis();
        int sumPar = Stream.iterate(1, a -> a + 1)
                .limit(1_000_000)
                .parallel()
                .reduce(0, Integer::sum);
        long endPar = System.currentTimeMillis();
        long durationPar = endPar - startPar;

        String bestPath;
        long minTime;
        if (durationSeq < durationPar) {
            bestPath = "последовательный поток";
            minTime = durationSeq;
        } else {
            bestPath = "параллельный поток";
            minTime = durationPar;
        }

        String result = String.format(
                "Последовательный стрим: сумма = %d, время = %d мс\n" +
                        "Параллельный стрим: сумма = %d, время = %d мс\n" +
                        "Вывод: наименее затратным по времени будет путь — %s (время %d мс).",
                sumSeq, durationSeq,
                sumPar, durationPar,
                bestPath, minTime
        );
        return result;
    }
}

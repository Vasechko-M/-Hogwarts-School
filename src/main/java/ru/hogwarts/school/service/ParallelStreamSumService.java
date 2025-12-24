package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class ParallelStreamSumService {

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

        return String.format(
                "Последовательный стрим: сумма = %d, время = %d мс\n" +
                        "Параллельный стрим: сумма = %d, время = %d мс\n" +
                        "Вывод: наименее затратным по времени будет путь — %s (время %d мс).",
                sumSeq, durationSeq,
                sumPar, durationPar,
                bestPath, minTime
        );
    }
}
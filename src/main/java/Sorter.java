import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class Sorter {
    FileInputStream[] inputStreamArray;
    Scanner[] scanners;
    BufferedWriter writer;
    boolean isAscending;
    boolean isStrings;

    public Sorter(String outputFileName, List<String> inputFileNames, boolean isAscending, boolean isStrings) {
        try {
            this.isAscending = isAscending;
            this.isStrings = isStrings;
            inputStreamArray = new FileInputStream[inputFileNames.size()];
            scanners = new Scanner[inputFileNames.size()];
            writer = new BufferedWriter(new FileWriter("./output/" + outputFileName));

            for (int i = 0; i < inputFileNames.size(); i++) {
                inputStreamArray[i] = new FileInputStream("./input/" + inputFileNames.get(i));
                scanners[i] = new Scanner(inputStreamArray[i], StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.error("Проблема при открытии файлов {}", e.getMessage());
        }
    }

    public void sort() {
        List<Object> list = readFirstValues(scanners);

        try {
            while (!list.stream().allMatch(Objects::isNull)) {
                List<Object> listWithoutNull = list.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                // Получить min или max значение в зависимости от вида сортировки
                Object currentValue = getMinOrMaxValueFromList(listWithoutNull);
                int currentValueIndex = list.indexOf(currentValue);

                // Прочитать следующее значение из файла, где было записанное значение
                Object nextValue = readNextValue(scanners[currentValueIndex], currentValue);

                // Установить прочитанное значение в list вместо записанного
                list.set(currentValueIndex, nextValue);

                // Записать текущее значение в выходной файл
                writer.write(currentValue + "\n");
                writer.flush();
            }

            for (FileInputStream inputStream : inputStreamArray) {
                if (inputStream != null) {
                    inputStream.close();
                }
            }

            for (Scanner scanner : scanners) {
                if (scanner != null) {
                    scanner.close();
                }
            }

            if (writer != null) {
                writer.close();
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке метода {}", e.getMessage());
        }
    }

    public List<Object> readFirstValues(Scanner[] scanners) {
        List<Object> list = new ArrayList<>();

        for (Scanner scanner : scanners) {
            Object value = readNextValue(scanner, null);
            list.add(value);
        }
        return list;
    }

    public Object getMinOrMaxValueFromList(List<Object> listWithoutNull) {
        Object value;
        Stream<Object> objectStream = listWithoutNull.stream();

        if (!isStrings) {
            IntStream intStream = objectStream.mapToInt(v -> (int) v);
            value = isAscending ? intStream.min().orElseThrow(NoSuchElementException::new)
                    : intStream.max().orElseThrow(NoSuchElementException::new);
        } else {
            Comparator<Object> comparator = Comparator.comparing(String::valueOf);
            value = isAscending ? objectStream.min(comparator).orElse(null) :
                    objectStream.max(comparator).orElse(null);
        }
        return value;
    }

    public Object readNextValue(Scanner scanner, Object currentValue) {
        Object nextValue = null;

        try {
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (!line.matches("^.*\\s+.*$") && !line.matches("^$")) {
                    if (!isStrings) {
                        try {
                            nextValue = Integer.parseInt(line);
                        } catch (NumberFormatException e) {
                            log.warn("Ошибка при преобразовании строки \"{}\" в число", line);
                            nextValue = readNextValue(scanner, currentValue);
                        }
                    } else {
                        nextValue = line;
                    }
                    // Проверка на соответствие последовательности сортировки
                    if (!isOrderOfSortingRight(nextValue, currentValue)) {
                        log.warn("Нарушена последовательность заданного типа сортировки. " +
                                "Предыдущее значение \"{}\", следующее значение \"{}\"", currentValue, line);
                        nextValue = readNextValue(scanner, currentValue);
                    }
                } else {
                    log.warn("Прочитанная строка \"{}\" содержит пробельный символ", line);
                    nextValue = readNextValue(scanner, currentValue);
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при чтении файла {}", e.getMessage());
        }
        return nextValue;
    }

    public boolean isOrderOfSortingRight(Object nextValue, Object currentValue) {
        if (currentValue != null && !nextValue.equals(currentValue)) {
            if (!isStrings) {
                return isAscending && ((int) nextValue > (int) currentValue) ||
                        !isAscending && ((int) nextValue < (int) currentValue);
            } else {
                int compareResult = nextValue.toString().compareTo(currentValue.toString());

                return isAscending && (compareResult > 0) || !isAscending && (compareResult < 0);
            }
        }
        return true;
    }
}

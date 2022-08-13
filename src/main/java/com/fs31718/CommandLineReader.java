package com.fs31718;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CommandLineReader {
    private final String[] args;
    public boolean isAscending = true;
    public boolean isStrings = true;
    public String outputFileName = "";
    public List<String> inputFileNames = new ArrayList<>();

    CommandLineReader(String... args) {
        this.args = args;
    }

    void parse() {
        Options options = new Options();
        options.addOption("s", false, "Файлы содержат строки. Опция обязательна, взаимоисключительна с -i.");
        options.addOption("i", false, "Файлы содержат целые числа. Опция обязательна, взаимоисключительна с -s.");
        options.addOption("a", false, "Сортировка по возрастанию. Опция необязательна. Применяется по умолчанию");
        options.addOption("d", false, "Сортировка по убыванию. Опция необязательна");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (UnrecognizedOptionException e) {
            log.warn("Неизвестная опция \'{}\'", e.getOption());
            System.exit(-1);
        } catch (ParseException e) {
            log.error("Сбой разбора командной строки \'{}\'", e.getMessage());
            System.exit(-1);
        }

        if (cmd == null) {
            log.error("Сбой разбора командной строки по неведомой причине");
            System.exit(-1);
        } else {
            if (!(cmd.hasOption('i') || cmd.hasOption('s'))) {
                log.warn("Отсутствует обязательная опция -s или -i");
                System.exit(-1);
            }
            if (cmd.hasOption('i') && cmd.hasOption('s')) {
                log.warn("Должна быть только одна опция или -s или -i");
                System.exit(-1);
            }
            if (cmd.hasOption('a') && cmd.hasOption('d')) {
                log.warn("Должна быть только одна опция или -a или -d");
                System.exit(-1);
            }

            List<String> files = cmd.getArgList();

            if (files.size() < 2) {
                log.warn("Отсутствует имя выходного файла или имя входного файла.");
                System.exit(-1);
            }

            if (cmd.hasOption('d')) isAscending = false;
            if (cmd.hasOption('i')) isStrings = false;

            outputFileName = files.get(0);
            files.remove(0);
            inputFileNames = files;
        }
    }
}

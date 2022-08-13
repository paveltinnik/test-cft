package com.fs31718;

public class Main {
    public static void main(String[] args) {
        CommandLineReader cli = new CommandLineReader(args);
        cli.parse();

        Sorter sorter = new Sorter(cli.outputFileName, cli.inputFileNames,
                cli.isAscending, cli.isStrings);
        sorter.sort();

        System.out.println("Сортировка завершена.");
    }
}
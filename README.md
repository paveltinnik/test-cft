# test-cft

Требуются

1) jdk-11.0.13
2) apache-maven-3.8.6

Для запуска выполнить следующие действия

1) Клонировать репозиторий
2) Выполнить mvn package
3) Перейти в папку target
4) Выполнить команду chcp 1251 для смены кодировки
5) Выполнить команду со своими аргументами. Пример: java -jar test-cft-jar-with-dependencies.jar -d -s out.txt in1desc.txt in2desc.txt in3desc.txt
6) В папке output будет выходной файл out.txt

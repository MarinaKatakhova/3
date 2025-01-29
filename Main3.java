import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

// Задание 1: Работа с потоками ввода-вывода
class FileProcessor {
    public static void processFile(String inputFilePath, String outputFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line.toUpperCase());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Задание 2: Реализация паттерна Декоратор
interface TextProcessor {
    String process(String text);
}

class SimpleTextProcessor implements TextProcessor {
    @Override
    public String process(String text) {
        return text;
    }
}

abstract class TextProcessorDecorator implements TextProcessor {
    protected TextProcessor textProcessor;

    public TextProcessorDecorator(TextProcessor textProcessor) {
        this.textProcessor = textProcessor;
    }

    @Override
    public String process(String text) {
        return textProcessor.process(text);
    }
}

class UpperCaseDecorator extends TextProcessorDecorator {
    public UpperCaseDecorator(TextProcessor textProcessor) {
        super(textProcessor);
    }

    @Override
    public String process(String text) {
        return super.process(text).toUpperCase();
    }
}

class TrimDecorator extends TextProcessorDecorator {
    public TrimDecorator(TextProcessor textProcessor) {
        super(textProcessor);
    }

    @Override
    public String process(String text) {
        return super.process(text).trim();
    }
}

class ReplaceDecorator extends TextProcessorDecorator {
    public ReplaceDecorator(TextProcessor textProcessor) {
        super(textProcessor);
    }

    @Override
    public String process(String text) {
        return super.process(text).replace(" ", "_");
    }
}

// Задание 3: Сравнение производительности IO и NIO
class FilePerformanceTest {
    public static void testIO(String inputFilePath, String outputFilePath) {
        long startTime = System.currentTimeMillis();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Время выполнения IO: " + (endTime - startTime) + " ms");
    }

    public static void testNIO(String inputFilePath, String outputFilePath) {
        long startTime = System.currentTimeMillis();
        try (FileChannel inChannel = FileChannel.open(Paths.get(inputFilePath), StandardOpenOption.READ);
             FileChannel outChannel = FileChannel.open(Paths.get(outputFilePath), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (inChannel.read(buffer) > 0) {
                buffer.flip();
                outChannel.write(buffer);
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Время выполнения NIO: " + (endTime - startTime) + " ms");
    }
}

// Задание 4: Программа с использованием Java NIO
class FileCopier {
    public static void copyFile(String source, String dest) {
        try (FileChannel srcChannel = new FileInputStream(source).getChannel();
             FileChannel destChannel = new FileOutputStream(dest).getChannel()) {
            srcChannel.transferTo(0, srcChannel.size(), destChannel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Задание 5: Асинхронное чтение файла с использованием NIO.2
class AsyncFileReader {
    public static void readFileAsync(String filePath) {
        try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(Paths.get(filePath), StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            fileChannel.read(buffer, 0, null, new CompletionHandler<Integer, Void>() {
                @Override
                public void completed(Integer result, Void attachment) {
                    if (result > 0) {
                        buffer.flip();
                        byte[] data = new byte[result];
                        buffer.get(data);
                        System.out.println(new String(data));
                    }
                }

                @Override
                public void failed(Throwable exc, Void attachment) {
                    exc.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        // Создание файлов, если они не существуют
        createFileIfNotExists("input.txt", "Hello, World!\nThis is a test.");
        createFileIfNotExists("largefile.txt", "This is a large file content.\n".repeat(1000000));

        // Задание 1: Работа с потоками ввода-вывода
        FileProcessor.processFile("input.txt", "output.txt");

        // Задание 2: Реализация паттерна Декоратор
        TextProcessor processor = new ReplaceDecorator(
                new UpperCaseDecorator(
                        new TrimDecorator(new SimpleTextProcessor())
                )
        );
        String result = processor.process(" Hello world ");
        System.out.println(result); // Вывод: HELLO_WORLD

        // Задание 3: Сравнение производительности IO и NIO
        FilePerformanceTest.testIO("largefile.txt", "largefile_io_copy.txt");
        FilePerformanceTest.testNIO("largefile.txt", "largefile_nio_copy.txt");

        // Задание 4: Программа с использованием Java NIO
        FileCopier.copyFile("largefile.txt", "largefile_nio_copy.txt");

        // Задание 5: Асинхронное чтение файла с использованием NIO.2
        AsyncFileReader.readFileAsync("largefile.txt");
    }

    private static void createFileIfNotExists(String filePath, String content) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            try {
                Files.write(path, content.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

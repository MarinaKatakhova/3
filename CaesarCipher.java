import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class CaesarCipher {

    private static final String ALPHABET_E = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ALPHABET_E_SIZE = ALPHABET_E.length();
    private static final String ALPHABET_R = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    private static final int ALPHABET_R_SIZE = ALPHABET_R.length();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите режим работы:");
        System.out.println("1. Шифрование текста");
        System.out.println("2. Расшифровка текста с известным ключом");
        System.out.println("3. Расшифровка методом brute force");
        System.out.println("4. Расшифровка методом статистического анализа");

        int mode = scanner.nextInt();
        scanner.nextLine();

        switch (mode) {
            case 1:
                encryptFile(scanner);
                break;
            case 2:
                decryptFile(scanner);
                break;
            case 3:
                bruteForceDecrypt(scanner);
                break;
            case 4:
                statisticalAnalysisDecrypt(scanner);
                break;
            default:
                System.out.println("Неверный режим работы.");
        }
    }

    private static void encryptFile(Scanner scanner) {
        System.out.println("Введите путь к файлу с исходным текстом:");
        String inputFilePath = scanner.nextLine();
        System.out.println("Введите путь к файлу для зашифрованного текста:");
        String outputFilePath = scanner.nextLine();
        System.out.println("Введите ключ (сдвиг):");
        int key = scanner.nextInt();

        if (!Files.exists(Paths.get(inputFilePath))) {
            System.out.println("Файл не существует.");
            return;
        }

        encryptOrDecryptFile(inputFilePath, outputFilePath, key, true);
    }

    private static void decryptFile(Scanner scanner) {
        System.out.println("Введите путь к файлу с зашифрованным текстом:");
        String inputFilePath = scanner.nextLine();
        System.out.println("Введите путь к файлу для расшифрованного текста:");
        String outputFilePath = scanner.nextLine();
        System.out.println("Введите ключ (сдвиг):");
        int key = scanner.nextInt();

        if (!Files.exists(Paths.get(inputFilePath))) {
            System.out.println("Файл не существует.");
            return;
        }

        encryptOrDecryptFile(inputFilePath, outputFilePath, key, false);
    }

    private static void bruteForceDecrypt(Scanner scanner) {
        System.out.println("Введите путь к файлу с зашифрованным текстом:");
        String inputFilePath = scanner.nextLine();
        System.out.println("Введите путь к файлу для расшифрованного текста:");
        String outputFilePath = scanner.nextLine();

        if (!Files.exists(Paths.get(inputFilePath))) {
            System.out.println("Файл не существует.");
            return;
        }

        for (int key = 0; key < Math.max(ALPHABET_E_SIZE, ALPHABET_R_SIZE); key++) {
            String tempOutputFilePath = outputFilePath + "_key_" + key;
            encryptOrDecryptFile(inputFilePath, tempOutputFilePath, key, false);
            System.out.println("Попробуйте ключ: " + key + ", результат сохранен в " + tempOutputFilePath);
        }
    }

    private static void statisticalAnalysisDecrypt(Scanner scanner) {
        System.out.println("Введите путь к файлу с зашифрованным текстом:");
        String inputFilePath = scanner.nextLine();
        System.out.println("Введите путь к файлу для расшифрованного текста:");
        String outputFilePath = scanner.nextLine();

        if (!Files.exists(Paths.get(inputFilePath))) {
            System.out.println("Файл не существует.");
            return;
        }

        int mostFrequentCharIndex = findMostFrequentCharIndex(inputFilePath);
        int key = (mostFrequentCharIndex - ALPHABET_E.indexOf('e')) % ALPHABET_E_SIZE;
        if (key < 0) key += ALPHABET_E_SIZE;

        encryptOrDecryptFile(inputFilePath, outputFilePath, key, false);
        System.out.println("Расшифрованный текст сохранен в " + outputFilePath + " с ключом " + key);
    }

    private static int findMostFrequentCharIndex(String filePath) {
        int[] charCount = new int[Math.max(ALPHABET_E_SIZE, ALPHABET_R_SIZE)];
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int ch;
            while ((ch = reader.read()) != -1) {
                int indexE = ALPHABET_E.indexOf(Character.toUpperCase((char) ch));
                int indexR = ALPHABET_R.indexOf(Character.toUpperCase((char) ch));
                if (indexE != -1) {
                    charCount[indexE]++;
                } else if (indexR != -1) {
                    charCount[ALPHABET_E_SIZE + indexR]++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int maxCount = 0;
        int mostFrequentIndex = 0;
        for (int i = 0; i < charCount.length; i++) {
            if (charCount[i] > maxCount) {
                maxCount = charCount[i];
                mostFrequentIndex = i;
            }
        }
        return mostFrequentIndex;
    }

    private static void encryptOrDecryptFile(String inputFilePath, String outputFilePath, int key, boolean encrypt) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String processedLine = encryptOrDecryptLine(line, key, encrypt);
                writer.write(processedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String encryptOrDecryptLine(String line, int key, boolean encrypt) {
        StringBuilder result = new StringBuilder();
        for (char ch : line.toCharArray()) {
            int indexE = ALPHABET_E.indexOf(Character.toUpperCase(ch));
            int indexR = ALPHABET_R.indexOf(Character.toUpperCase(ch));
            if (indexE != -1) {
                int newIndex = (indexE + (encrypt ? key : -key) + ALPHABET_E_SIZE) % ALPHABET_E_SIZE;
                char newChar = ALPHABET_E.charAt(newIndex);
                result.append(Character.isUpperCase(ch) ? newChar : Character.toLowerCase(newChar));
            } else if (indexR != -1) {
                int newIndex = (indexR + (encrypt ? key : -key) + ALPHABET_R_SIZE) % ALPHABET_R_SIZE;
                char newChar = ALPHABET_R.charAt(newIndex);
                result.append(Character.isUpperCase(ch) ? newChar : Character.toLowerCase(newChar));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }
}

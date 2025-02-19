import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final Scanner scanner = new Scanner(System.in);

    // Задача 1: Счётчик с использованием ReentrantLock
    static class Counter {
        private int count = 0;
        private final ReentrantLock lock = new ReentrantLock();

        public void increment() {
            lock.lock();
            try {
                count++;
            } finally {
                lock.unlock();
            }
        }

        public int getCount() {
            return count;
        }

        public static void main(String[] args) throws InterruptedException {
            Counter counter = new Counter();
            Thread[] threads = new Thread[5];

            for (int i = 0; i < 5; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 1000; j++) {
                        counter.increment();
                    }
                    System.out.println("Поток " + Thread.currentThread().getName() + " завершил инкрементирование.");
                });
                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            System.out.println("Итоговый счётчик: " + counter.getCount());
        }
    }

    // Задача 2: Генерация последовательности чисел с использованием CopyOnWriteArrayList
    static class NumberGenerator {
        public static void main(String[] args) throws InterruptedException {
            List<Integer> numbers = new CopyOnWriteArrayList<>();
            Thread[] threads = new Thread[10];

            for (int i = 0; i < 10; i++) {
                final int start = i * 10 + 1;
                threads[i] = new Thread(() -> {
                    for (int j = start; j < start + 10; j++) {
                        numbers.add(j);
                    }
                    System.out.println("Поток " + Thread.currentThread().getName() + " завершил добавление чисел.");
                });
                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            System.out.println("Сгенерированные числа: " + numbers);
        }
    }

    // Задача 3: Распределение задач с использованием пула потоков
    static class TaskExecutor {
        public static void main(String[] args) {
            ExecutorService executor = Executors.newFixedThreadPool(4);

            for (int i = 0; i < 20; i++) {
                final int taskNum = i;
                executor.submit(() -> {
                    System.out.println("Задача " + taskNum + " выполнена потоком " + Thread.currentThread().getName());
                });
            }

            executor.shutdown();
            System.out.println("Все задачи отправлены на выполнение.");
        }
    }

    // Задача 4: Симуляция работы банка с использованием ReentrantLock
    static class Bank {
        private final ReentrantLock lock = new ReentrantLock();
        private final int[] accounts;

        public Bank(int[] initialBalances) {
            this.accounts = initialBalances;
        }

        public void transfer(int from, int to, int amount) {
            lock.lock();
            try {
                if (accounts[from] >= amount) {
                    accounts[from] -= amount;
                    accounts[to] += amount;
                    System.out.println("Переведено " + amount + " с аккаунта " + from + " на аккаунт " + to);
                }
            } finally {
                lock.unlock();
            }
        }

        public static void main(String[] args) throws InterruptedException {
            int[] initialBalances = {1000, 2000, 3000};
            Bank bank = new Bank(initialBalances);
            Thread[] threads = new Thread[5];

            for (int i = 0; i < 5; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 100; j++) {
                        int from = (int) (Math.random() * 3);
                        int to = (int) (Math.random() * 3);
                        int amount = (int) (Math.random() * 100);
                        bank.transfer(from, to, amount);
                    }
                    System.out.println("Поток " + Thread.currentThread().getName() + " завершил переводы.");
                });
                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            System.out.println("Итоговые балансы: ");
            for (int balance : bank.accounts) {
                System.out.println(balance);
            }
        }
    }

    // Задача 5: Барьер синхронизации с использованием CyclicBarrier
    static class BarrierExample {
        public static void main(String[] args) {
            CyclicBarrier barrier = new CyclicBarrier(5);
            Thread[] threads = new Thread[5];

            for (int i = 0; i < 5; i++) {
                threads[i] = new Thread(() -> {
                    try {
                        System.out.println(Thread.currentThread().getName() + " ожидает на барьере");
                        barrier.await();
                        System.out.println(Thread.currentThread().getName() + " прошёл барьер");
                    } catch (InterruptedException | BrokenBarrierException e) {
                        LOGGER.log(Level.SEVERE, "Исключение в BarrierExample", e);
                    }
                });
                threads[i].start();
            }
        }
    }

    // Задача 6: Ограниченный доступ к ресурсу с использованием Semaphore
    static class LimitedAccess {
        private static final Semaphore semaphore = new Semaphore(2);

        public static void main(String[] args) {
            Thread[] threads = new Thread[5];

            for (int i = 0; i < 5; i++) {
                threads[i] = new Thread(() -> {
                    try {
                        semaphore.acquire();
                        System.out.println(Thread.currentThread().getName() + " захватил ресурс");
                        sleep(2000);
                    } catch (InterruptedException e) {
                        LOGGER.log(Level.SEVERE, "Исключение в LimitedAccess", e);
                    } finally {
                        semaphore.release();
                        System.out.println(Thread.currentThread().getName() + " освободил ресурс");
                    }
                });
                threads[i].start();
            }
        }
    }

    // Задача 7: Обработка результатов задач с использованием Callable и Future
    static class FactorialCalculator {
        public static void main(String[] args) throws InterruptedException, ExecutionException {
            ExecutorService executor = Executors.newFixedThreadPool(10);
            List<Future<Long>> results = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                final int number = i + 1;
                results.add(executor.submit(() -> factorial(number)));
            }

            for (int i = 0; i < 10; i++) {
                System.out.println("Факториал числа " + (i + 1) + " равен " + results.get(i).get());
            }

            executor.shutdown();
        }

        public static long factorial(int n) {
            long result = 1;
            for (int i = 1; i <= n; i++) {
                result *= i;
            }
            return result;
        }
    }

    // Задача 8: Симуляция производственной линии с использованием BlockingQueue
    static class ProductionLine {
        public static void main(String[] args) {
            BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

            Thread producer = new Thread(() -> {
                try {
                    for (int i = 1; i <= 10; i++) {
                        queue.put(i);
                        System.out.println("Произведено " + i);
                        sleep(1000);
                    }
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, "Исключение в ProductionLine producer", e);
                }
            });

            Thread consumer = new Thread(() -> {
                try {
                    for (int i = 1; i <= 10; i++) {
                        int item = queue.take();
                        System.out.println("Потреблено " + item);
                        sleep(1500);
                    }
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, "Исключение в ProductionLine consumer", e);
                }
            });

            producer.start();
            consumer.start();
        }
    }

    // Задача 9: Многопоточная сортировка
    static class ParallelSort {
        public static void main(String[] args) throws InterruptedException {
            int[] array = {10, 2, 3, 5, 1, 8, 7, 6, 4, 9};
            int numThreads = 4;
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);

            int chunkSize = array.length / numThreads;
            Future<?>[] futures = new Future[numThreads];

            for (int i = 0; i < numThreads; i++) {
                final int start = i * chunkSize;
                final int end = (i == numThreads - 1) ? array.length : start + chunkSize;
                futures[i] = executor.submit(() -> Arrays.sort(array, start, end));
            }

            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    LOGGER.log(Level.SEVERE, "Исключение в ParallelSort", e);
                }
            }

            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                LOGGER.log(Level.SEVERE, "Executor не завершил выполнение в указанное время.");
            }

            Arrays.sort(array); // Финальная сортировка
            System.out.println("Отсортированный массив: " + Arrays.toString(array));
        }
    }

    // Задача 10: Обед философов с использованием ReentrantLock
    static class DiningPhilosophers {
        private static final int NUM_PHILOSOPHERS = 5;
        private static final ReentrantLock[] forks = new ReentrantLock[NUM_PHILOSOPHERS];

        static {
            for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
                forks[i] = new ReentrantLock();
            }
        }

        public static void main(String[] args) {
            Thread[] philosophers = new Thread[NUM_PHILOSOPHERS];

            for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
                final int id = i;
                philosophers[i] = new Thread(() -> {
                    try {
                        while (true) {
                            think(id);
                            eat(id);
                        }
                    } catch (InterruptedException e) {
                        LOGGER.log(Level.SEVERE, "Исключение в DiningPhilosophers", e);
                    }
                });
                philosophers[i].start();
            }
        }

        private static void think(int id) throws InterruptedException {
            System.out.println("Философ " + id + " думает.");
            sleep((int) (Math.random() * 1000));
        }

        private static void eat(int id) throws InterruptedException {
            ReentrantLock leftFork = forks[id];
            ReentrantLock rightFork = forks[(id + 1) % NUM_PHILOSOPHERS];

            leftFork.lock();
            rightFork.lock();

            try {
                System.out.println("Философ " + id + " ест.");
                sleep((int) (Math.random() * 1000));
            } finally {
                rightFork.unlock();
                leftFork.unlock();
            }
        }
    }

    // Задача 11: Расчёт матрицы в параллельных потоках
    static class MatrixMultiplication {
        public static void main(String[] args) throws InterruptedException {
            int[][] A = {
                    {1, 2, 3},
                    {4, 5, 6},
                    {7, 8, 9}
            };
            int[][] B = {
                    {9, 8, 7},
                    {6, 5, 4},
                    {3, 2, 1}
            };
            int[][] C = new int[3][3];

            ExecutorService executor = Executors.newFixedThreadPool(3);

            for (int i = 0; i < 3; i++) {
                final int row = i;
                executor.submit(() -> {
                    for (int j = 0; j < 3; j++) {
                        C[row][j] = 0;
                        for (int k = 0; k < 3; k++) {
                            C[row][j] += A[row][k] * B[k][j];
                        }
                        System.out.println("Вычислено C[" + row + "][" + j + "] = " + C[row][j]);
                    }
                });
            }

            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                LOGGER.log(Level.SEVERE, "Executor не завершил выполнение в указанное время.");
            }

            System.out.println("Результирующая матрица C:");
            for (int j = 0; j < 3; j++) {
                System.out.print(C[0][j] + " ");
            }
            System.out.println();
            for (int j = 0; j < 3; j++) {
                System.out.print(C[1][j] + " ");
            }
            System.out.println();
            for (int j = 0; j < 3; j++) {
                System.out.print(C[2][j] + " ");
            }
            System.out.println();
        }
    }

    // Задача 12: Таймер с многопоточностью
    static class Timer {
        private static volatile boolean stop = false;

        public static void main(String[] args) throws InterruptedException {
            Thread timerThread = new Thread(() -> {
                while (!stop) {
                    System.out.println("Текущее время: " + System.currentTimeMillis());
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        LOGGER.log(Level.SEVERE, "Исключение в Timer", e);
                    }
                }
            });

            Thread stopperThread = new Thread(() -> {
                try {
                    sleep(10000);
                    stop = true;
                    System.out.println("Таймер остановлен.");
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, "Исключение в Timer stopper", e);
                }
            });

            timerThread.start();
            stopperThread.start();

            timerThread.join();
            stopperThread.join();
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println("Выберите задачу для выполнения:");
        System.out.println("1. Счётчик");
        System.out.println("2. Генерация последовательности чисел");
        System.out.println("3. Распределение задач");
        System.out.println("4. Симуляция работы банка");
        System.out.println("5. Барьер синхронизации");
        System.out.println("6. Ограниченный доступ к ресурсу");
        System.out.println("7. Обработка результатов задач");
        System.out.println("8. Симуляция производственной линии");
        System.out.println("9. Многопоточная сортировка");
        System.out.println("10. Обед философов");
        System.out.println("11. Расчёт матрицы");
        System.out.println("12. Таймер");
        System.out.print("Введите номер задачи: ");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                Counter.main(args);
                break;
            case 2:
                NumberGenerator.main(args);
                break;
            case 3:
                TaskExecutor.main(args);
                break;
            case 4:
                Bank.main(args);
                break;
            case 5:
                BarrierExample.main(args);
                break;
            case 6:
                LimitedAccess.main(args);
                break;
            case 7:
                FactorialCalculator.main(args);
                break;
            case 8:
                ProductionLine.main(args);
                break;
            case 9:
                ParallelSort.main(args);
                break;
            case 10:
                DiningPhilosophers.main(args);
                break;
            case 11:
                MatrixMultiplication.main(args);
                break;
            case 12:
                Timer.main(args);
                break;
            default:
                System.out.println("Неверный выбор.");
        }
    }
}
import java.util.ArrayList;
import java.util.List;

// Задача 1: Класс для подключения к базе данных (Singleton)
class DatabaseConnection {
    private static DatabaseConnection instance;

    private DatabaseConnection() {
        System.out.println("Создано подключение к базе данных.");
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
}

// Задача 2: Класс для системы логирования (Singleton)
class Logger {
    private static Logger instance;
    private List<String> logs;

    private Logger() {
        logs = new ArrayList<>();
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(String message) {
        logs.add(message);
    }

    public void printLogs() {
        for (String log : logs) {
            System.out.println(log);
        }
    }
}

// Задача 3: Enum для статусов заказа и класс Order
enum OrderStatus {
    NEW, IN_PROGRESS, DELIVERED, CANCELLED
}

class Order {
    private OrderStatus status;

    public Order() {
        this.status = OrderStatus.NEW;
    }

    public void changeStatus(OrderStatus newStatus) {
        if (status == OrderStatus.DELIVERED && newStatus == OrderStatus.CANCELLED) {
            System.out.println("Нельзя отменить доставленный заказ.");
            return;
        }
        status = newStatus;
        System.out.println("Статус заказа изменен на: " + status);
    }

    public void displayStatus() {
        System.out.println("Текущий статус заказа: " + status);
    }
}

// Задача 4: Enum для сезонов и метод для получения названия сезона
enum Season {
    WINTER, SPRING, SUMMER, AUTUMN
}

class SeasonUtils {
    public static String getSeasonName(Season season) {
        switch (season) {
            case WINTER:
                return "Зима";
            case SPRING:
                return "Весна";
            case SUMMER:
                return "Лето";
            case AUTUMN:
                return "Осень";
            default:
                throw new IllegalArgumentException("Неверное значение сезона: " + season);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        // Задача 1: Проверка Singleton для подключения к базе данных
        DatabaseConnection db1 = DatabaseConnection.getInstance();
        DatabaseConnection db2 = DatabaseConnection.getInstance();
        System.out.println(db1 == db2); // Должно вывести true

        // Задача 2: Проверка системы логирования
        Logger logger = Logger.getInstance();
        logger.log("Первое сообщение");
        logger.log("Второе сообщение");
        logger.printLogs();

        // Задача 3: Проверка класса Order
        Order order = new Order();
        order.displayStatus();
        order.changeStatus(OrderStatus.IN_PROGRESS);
        order.changeStatus(OrderStatus.DELIVERED);
        order.changeStatus(OrderStatus.CANCELLED); 

        // Задача 4: Проверка метода для получения названия сезона
        System.out.println(SeasonUtils.getSeasonName(Season.WINTER)); 
        System.out.println(SeasonUtils.getSeasonName(Season.SPRING)); 
        System.out.println(SeasonUtils.getSeasonName(Season.SUMMER)); 
        System.out.println(SeasonUtils.getSeasonName(Season.AUTUMN)); 
    }
}

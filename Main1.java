import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        // Задача 1
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        System.out.println("Текущая дата и время: " + currentDate.format(dateFormatter) + " " + currentTime.format(timeFormatter));

        // Задача 2
        LocalDate date1 = LocalDate.of(2024, 10, 1);
        LocalDate date2 = LocalDate.of(2024, 10, 5);
        System.out.println(compareDates(date1, date2));

        // Задача 3
        System.out.println("Дней до Нового года: " + daysUntilNewYear());

        // Задача 4
        int year = 2024;
        System.out.println("Год " + year + " високосный: " + isLeapYear(year));

        // Задача 5
        int month = 10;
        int year2 = 2024;
        System.out.println("Выходных в месяце: " + countWeekendsInMonth(month, year2));

        // Задача 6
        System.out.println("Время выполнения метода: " + measureExecutionTime(() -> {
            for (int i = 0; i < 1_000_000; i++) {
                // Пустой цикл
            }
        }) + " миллисекунд");

        // Задача 7
        String dateStr = "01-10-2023";
        System.out.println("Дата через 10 дней: " + addDaysToDate(dateStr, 10));

        // Задача 8
        ZonedDateTime utcDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC"));
        System.out.println("Время в Москве: " + convertToTimeZone(utcDateTime, "Europe/Moscow"));

        // Задача 9
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        System.out.println("Возраст: " + calculateAge(birthDate) + " лет");

        // Задача 10
        printMonthCalendar(month, year2);

        // Задача 11
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        System.out.println("Случайная дата: " + generateRandomDate(startDate, endDate));

        // Задача 12
        LocalDateTime eventDateTime = LocalDateTime.of(2024, 12, 31, 23, 59);
        System.out.println("Время до события: " + timeUntilEvent(eventDateTime));

        // Задача 13
        LocalDateTime workStart = LocalDateTime.of(2024, 10, 1, 9, 0);
        LocalDateTime workEnd = LocalDateTime.of(2024, 10, 1, 17, 0);
        System.out.println("Рабочих часов: " + calculateWorkHours(workStart, workEnd));

        // Задача 14
        LocalDate dateToFormat = LocalDate.now();
        System.out.println("Дата на русском: " + formatDateWithLocale(dateToFormat, new Locale("ru")));

        // Задача 15
        LocalDate dateToCheck = LocalDate.of(2024, 10, 1);
        System.out.println("День недели: " + getDayOfWeekInRussian(dateToCheck));
    }

    // Задача 2
    public static String compareDates(LocalDate date1, LocalDate date2) {
        if (date1.isBefore(date2)) {
            return "Первая дата меньше второй";
        } else if (date1.isAfter(date2)) {
            return "Первая дата больше второй";
        } else {
            return "Даты равны";
        }
    }

    // Задача 3
    public static long daysUntilNewYear() {
        LocalDate currentDate = LocalDate.now();
        LocalDate newYear = LocalDate.of(currentDate.getYear(), 12, 31);
        return ChronoUnit.DAYS.between(currentDate, newYear);
    }

    // Задача 4
    public static boolean isLeapYear(int year) {
        return Year.of(year).isLeap();
    }

    // Задача 5
    public static int countWeekendsInMonth(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        int weekends = 0;
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                weekends++;
            }
        }
        return weekends;
    }

    // Задача 6
    public static long measureExecutionTime(Runnable task) {
        long start = System.currentTimeMillis();
        task.run();
        long end = System.currentTimeMillis();
        return end - start;
    }

    // Задача 7
    public static String addDaysToDate(String dateStr, int days) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.parse(dateStr, formatter);
        LocalDate newDate = date.plusDays(days);
        return newDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    // Задача 8
    public static ZonedDateTime convertToTimeZone(ZonedDateTime utcDateTime, String timeZone) {
        return utcDateTime.withZoneSameInstant(ZoneId.of(timeZone));
    }

    // Задача 9
    public static int calculateAge(LocalDate birthDate) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }

    // Задача 10
    public static void printMonthCalendar(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            String dayType = (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) ? "Выходной" : "Рабочий";
            System.out.println(date + " - " + dayType);
        }
    }

    // Задача 11
    public static LocalDate generateRandomDate(LocalDate startDate, LocalDate endDate) {
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = endDate.toEpochDay();
        long randomDay = startEpochDay + new Random().nextInt((int) (endEpochDay - startEpochDay + 1));
        return LocalDate.ofEpochDay(randomDay);
    }

    // Задача 12
    public static String timeUntilEvent(LocalDateTime eventDateTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Duration duration = Duration.between(currentDateTime, eventDateTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%d часов, %d минут, %d секунд", hours, minutes, seconds);
    }

    // Задача 13
    public static long calculateWorkHours(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        return duration.toHours();
    }

    // Задача 14
    public static String formatDateWithLocale(LocalDate date, Locale locale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", locale);
        return date.format(formatter);
    }

    // Задача 15
    public static String getDayOfWeekInRussian(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        switch (dayOfWeek) {
            case MONDAY:
                return "Понедельник";
            case TUESDAY:
                return "Вторник";
            case WEDNESDAY:
                return "Среда";
            case THURSDAY:
                return "Четверг";
            case FRIDAY:
                return "Пятница";
            case SATURDAY:
                return "Суббота";
            case SUNDAY:
                return "Воскресенье";
            default:
                throw new IllegalArgumentException("Неверный день недели: " + dayOfWeek);
        }
    }
}

package supermarket.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Класс для управления конфигурацией симуляции супермаркета.
 * Содержит статические поля с настройками и методы для загрузки параметров из файла,
 * а при его отсутствии — создания файла с значениями по умолчанию.
 */
public class Config {
    /**
     * Максимальное время выполнения симуляции в миллисекундах.
     * По умолчанию — 5 минут (5 * 60 * 1000 = 300 000 мс).
     */
    public static long SIMULATION_TIMEOUT_MS = 5 * 60 * 1000;

    /**
     * Начальное количество товаров на складе при запуске симуляции.
     * По умолчанию — 50 единиц.
     */
    public static int INITIAL_WAREHOUSE_ITEMS = 50;

    /**
     * Максимальное количество событий, которые могут быть обработаны в симуляции.
     * Используется для ограничения объёма логирования или моделирования.
     * По умолчанию — 1000 событий.
     */
    public static int MAX_EVENTS = 1000;

    /**
     * Загружает параметры конфигурации из указанного файла.
     * Если файл не существует, создаётся файл с настройками по умолчанию.
     * В случае ошибки чтения используются текущие значения полей.
     *
     * @param filename путь к файлу конфигурации (например, "config.properties")
     */
    public static void loadFromFile(String filename) {
        try {
            Path p = Path.of(filename);
            if (!Files.exists(p)) {
                System.out.println("Файл конфигурации '" + filename + "' не найден. Используются параметры по умолчанию.");
                saveDefault(filename);
                return;
            }
            Properties prop = new Properties();
            try (var is = Files.newInputStream(p)) {
                prop.load(is);
            }
            SIMULATION_TIMEOUT_MS = Long.parseLong(prop.getProperty("simulation.timeout.ms", String.valueOf(SIMULATION_TIMEOUT_MS)));
            INITIAL_WAREHOUSE_ITEMS = Integer.parseInt(prop.getProperty("initial.warehouse.items", String.valueOf(INITIAL_WAREHOUSE_ITEMS)));
            MAX_EVENTS = Integer.parseInt(prop.getProperty("max.events", String.valueOf(MAX_EVENTS)));
            System.out.println("Конфигурация загружена из " + filename);
        } catch (Exception e) {
            System.out.println("Ошибка загрузки конфигурации: " + e.getMessage());
        }
    }

    /**
     * Сохраняет файл конфигурации с текущими значениями по умолчанию.
     * Используется при первом запуске, если файл конфигурации отсутствует.
     *
     * @param filename путь, по которому будет создан файл
     */
    private static void saveDefault(String filename) {
        try {
            String content = "# SupermarketSimulation config\n" +
                    "simulation.timeout.ms=" + SIMULATION_TIMEOUT_MS + "\n" +
                    "initial.warehouse.items=" + INITIAL_WAREHOUSE_ITEMS + "\n" +
                    "max.events=" + MAX_EVENTS + "\n";
            Files.writeString(Path.of(filename), content);
            System.out.println("Создан файл конфигурации '" + filename + "' со значениями по умолчанию.");
        } catch (IOException e) {
            System.out.println("Невозможно создать файл конфигурации: " + e.getMessage());
        }
    }
}
package supermarket;

import supermarket.config.Config;
import supermarket.events.EventGenerator;
import supermarket.store.Supermarket;
import supermarket.ui.ObserverWorker;
import supermarket.ui.SwingUI;

import javax.swing.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Config.loadFromFile("config.properties");

        Scanner scanner = new Scanner(System.in, "UTF-8");
        System.out.println("Введите команду:");
        System.out.println("Хочу играть!");
        System.out.println("Я наблюдатель");

        String line = scanner.nextLine();

        Supermarket market =
                new Supermarket(Config.INITIAL_WAREHOUSE_ITEMS);
        EventGenerator generator = new EventGenerator(market);

        if (line.startsWith("Хочу играть!")) {
            SwingUtilities.invokeLater(() ->
                    new SwingUI(market, generator, true).setVisible(true)
            );
        }

        else if (line.equals("Я наблюдатель")) {
            SwingUtilities.invokeLater(() -> {
                SwingUI ui = new SwingUI(market, generator, false);
                ui.setVisible(true);
                new ObserverWorker(generator, ui).start();
            });
        }

        else {
            System.out.println("Неизвестная команда");
        }
    }
}

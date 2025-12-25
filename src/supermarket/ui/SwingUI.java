package supermarket.ui;

import supermarket.events.EventGenerator;
import supermarket.store.Supermarket;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Графический интерфейс симуляции супермаркета на основе Swing.
 * Поддерживает два режима работы:
 * <ul>
 *   <li><b>Интерактивный</b> — пользователь сам управляет действиями (кнопки активны).</li>
 *   <li><b>Наблюдатель</b> — автоматическая симуляция, кнопки отключены.</li>
 * </ul>
 * Интерфейс отображает таблицу товаров в торговом зале, лог событий и позволяет
 * управлять симуляцией через кнопки. Симуляция автоматически завершается по таймеру.
 */
public class SwingUI extends JFrame {

    /**
     * Экземпляр супермаркета, состояние которого отображается в интерфейсе.
     */
    private final Supermarket market;

    /**
     * Генератор событий, используемый для запуска случайных действий.
     */
    private final EventGenerator generator;

    /**
     * Флаг, определяющий, включён ли интерактивный режим.
     * Если {@code true} — пользователь может нажимать кнопки.
     */
    private final boolean interactive;

    /**
     * Текстовая область для вывода лога событий.
     * Обновляется в потоке Swing через {@link SwingUtilities#invokeLater(Runnable)}.
     */
    private final JTextArea logArea = new JTextArea();

    /**
     * Модель таблицы для отображения товаров в торговом зале.
     * Содержит столбцы: "Название", "Цена".
     */
    private final DefaultTableModel tableModel =
            new DefaultTableModel(new String[]{"Название", "Цена"}, 0);

    /**
     * Создаёт графический интерфейс симуляции.
     *
     * @param market      экземпляр супермаркета для отображения
     * @param generator   генератор событий для кнопки "Случайное событие"
     * @param interactive {@code true}, если пользователь может управлять симуляцией
     */
    public SwingUI(Supermarket market, EventGenerator generator, boolean interactive) {
        this.market = market;
        this.generator = generator;
        this.interactive = interactive;

        setTitle(interactive
                ? "Супермаркет — интерактивный режим"
                : "Супермаркет — режим наблюдателя");

        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
        refresh();
        startTimer();
    }


    /**
     * Добавляет новое сообщение в лог событий.
     * Выполняется в потоке графического интерфейса (EDT).
     *
     * @param msg текст сообщения для добавления в лог
     */
    public void addLog(String msg) {
        SwingUtilities.invokeLater(() ->
                logArea.append(msg + "\n")
        );
    }

    /**
     * Обновляет отображение данных: перерисовывает таблицу товаров.
     * Выполняется в потоке графического интерфейса (EDT).
     */
    public void refresh() {
        SwingUtilities.invokeLater(this::refreshTable);
    }


    /**
     * Инициализирует компоненты графического интерфейса:
     * <ul>
     *   <li>Таблица с товарами</li>
     *   <li>Лог событий</li>
     *   <li>Панель кнопок</li>
     *   <li>Расстановка элементов на форме</li>
     * </ul>
     * Также настраивает обработчики действий для каждой кнопки.
     */
    private void initUI() {
        JTable table = new JTable(tableModel);
        JScrollPane tablePane = new JScrollPane(table);

        logArea.setEditable(false);
        JScrollPane logPane = new JScrollPane(logArea);

        JButton moveBtn = new JButton("Со склада в зал");
        JButton discardBtn = new JButton("Удалить просрочку");
        JButton discountBtn = new JButton("Установить скидку");
        JButton randomBtn = new JButton("Случайное событие");

        moveBtn.addActionListener(e -> {
            market.moveFromWarehouseToShop(3);
            addLog("Перемещение товаров со склада в зал");
            refresh();
        });

        discardBtn.addActionListener(e -> {
            market.discardExpired();
            addLog("Удаление просроченных товаров");
            refresh();
        });

        discountBtn.addActionListener(e -> {
            if (interactive) {
                String input = JOptionPane.showInputDialog(
                        this,
                        "Введите процент скидки (0–100):",
                        "Скидка",
                        JOptionPane.QUESTION_MESSAGE
                );

                if (input == null) return; // отмена

                try {
                    double percent = Double.parseDouble(input.trim());
                    if (percent < 0 || percent > 100) {
                        JOptionPane.showMessageDialog(this, "Введите число от 0 до 100");
                        return;
                    }
                    double discount = percent / 100.0;
                    market.setDiscount("", discount);
                    addLog("Установлена скидка: " + percent + "%");
                    refresh();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Некорректное число");
                }
            } else {
                double discount = 0.05 + Math.random() * 0.45; // от 0.05 до 0.50
                int percent = (int) Math.round(discount * 100);
                market.setDiscount("", discount);
                addLog("Установлена скидка: " + percent + "%");
                refresh();
            }
        });

        randomBtn.addActionListener(e -> {
            String event = generator.generateOne();
            addLog(event);
            refresh();
        });

        if (!interactive) {
            moveBtn.setEnabled(false);
            discardBtn.setEnabled(false);
            discountBtn.setEnabled(false);
            randomBtn.setEnabled(false);
        }

        JPanel buttons = new JPanel();
        buttons.add(moveBtn);
        buttons.add(discardBtn);
        buttons.add(discountBtn);
        buttons.add(randomBtn);

        JSplitPane split =
                new JSplitPane(JSplitPane.VERTICAL_SPLIT, tablePane, logPane);
        split.setDividerLocation(250);

        add(split, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    /**
     * Обновляет содержимое таблицы товаров на основе текущего состояния торгового зала.
     * Очищает таблицу и добавляет все товары из {@code market.shop.list()}.
     * Цены отображаются с двумя знаками после запятой.
     */
    private void refreshTable() {
        tableModel.setRowCount(0);
        market.shop.list().forEach(p ->
                tableModel.addRow(new Object[]{
                        p.getName(),
                        String.format("%.2f", p.getPrice())
                })
        );
    }

    /**
     * Запускает таймер, по истечении которого симуляция будет автоматически завершена.
     * Таймер срабатывает один раз через значение из {@code Config.SIMULATION_TIMEOUT_MS}.
     * После срабатывания:
     * <ul>
     *   <li>Останавливается генератор событий</li>
     *   <li>Показывается сообщение</li>
     *   <li>Приложение завершается</li>
     * </ul>
     */
    private void startTimer() {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    stopSimulation();
                    JOptionPane.showMessageDialog(
                            SwingUI.this,
                            "Симуляция завершена по таймеру",
                            "Завершение",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    System.exit(0);
                });
            }
        }, supermarket.config.Config.SIMULATION_TIMEOUT_MS);
    }

    /**
     * Останавливает генератор событий и логирует завершение симуляции.
     * Вызывается по таймеру или вручную для корректного завершения.
     */
    private void stopSimulation() {
        generator.stop();
        addLog("Симуляция остановлена по таймеру.");
    }
}
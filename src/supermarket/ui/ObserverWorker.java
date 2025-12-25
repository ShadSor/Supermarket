package supermarket.ui;

import supermarket.events.EventGenerator;

/**
 * Поток, наблюдающий за генерацией событий и обновляющий пользовательский интерфейс.
 * Периодически опрашивает {@link EventGenerator}, получает новое событие и передаёт его в {@link SwingUI}
 * для отображения в логе и обновления состояния. Работает до тех пор, пока симуляция активна.
 */
public class ObserverWorker extends Thread {

    /**
     * Генератор событий, за которым ведётся наблюдение.
     */
    private final EventGenerator generator;

    /**
     * Графический интерфейс, который обновляется при наступлении событий.
     */
    private final SwingUI ui;

    /**
     * Создаёт рабочий поток-наблюдатель.
     *
     * @param generator генератор событий
     * @param ui        графический интерфейс для обновления
     */
    public ObserverWorker(EventGenerator generator, SwingUI ui) {
        this.generator = generator;
        this.ui = ui;
    }

    /**
     * Основной цикл потока.
     * Пока генератор активен, извлекает события, обновляет лог и интерфейс.
     * Приостанавливает выполнение на 2 секунды между итерациями.
     * В случае прерывания потока выбрасывается {@link InterruptedException},
     * поток корректно завершает работу.
     */
    @Override
    public void run() {
        try {
            while (generator.isRunning()) {
                String event = generator.generateOne();
                ui.addLog(event);
                ui.refresh();
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
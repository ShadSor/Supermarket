package supermarket.events;

import supermarket.store.Supermarket;

import java.util.Random;

/**
 * Генератор случайных событий для симуляции работы супермаркета.
 * Класс генерирует одно из нескольких типов событий, имитирующих реальные действия:
 * пополнение склада, перемещение товаров, списание просроченных, покупки и скидки.
 * Работа генератора может быть остановлена с помощью метода {@link #stop()}.
 */
public class EventGenerator {

    /**
     * Ссылка на экземпляр супермаркета, над которым выполняются действия.
     */
    private final Supermarket market;

    /**
     * Генератор случайных чисел для выбора типа события.
     */
    private final Random rnd = new Random();

    /**
     * Флаг, определяющий, активна ли симуляция.
     * При значении {@code false} генерация событий прекращается.
     */
    private volatile boolean running = true;

    /**
     * Создаёт генератор событий, привязанный к указанному супермаркету.
     *
     * @param market экземпляр супермаркета, в котором будут происходить события
     */
    public EventGenerator(Supermarket market) {
        this.market = market;
    }

    /**
     * Генерирует одно случайное событие и применяет его к супермаркету.
     * Событие выбирается случайным образом из пяти возможных действий.
     * Если симуляция остановлена, возвращается сообщение об этом.
     *
     * @return строковое описание произошедшего события
     */
    public String generateOne() {
        if (!running) return "Симуляция остановлена";

        int pick = rnd.nextInt(5);

        switch (pick) {
            case 0 -> {
                market.warehouse.populateRandom(5);
                return "Завоз товаров на склад";
            }
            case 1 -> {
                market.moveFromWarehouseToShop(3);
                return "Перемещение товаров в торговый зал";
            }
            case 2 -> {
                market.discardExpired();
                return "Удаление просроченных товаров";
            }
            case 3 -> {
                if (market.shop.size() > 0) {
                    var p = market.shop.list().get(0);
                    market.buyProductByName(p.getName(), p.getPrice() + 50);
                    return "Покупатель купил товар";
                }
                return "Покупатель ушёл без покупки";
            }
            case 4 -> {
                double discount = 0.05 + rnd.nextDouble() * 0.45;
                int percent = (int) Math.round(discount * 100);
                market.setDiscount("", discount);
                return "Установлена скидка " + percent + "%";
            }
        }
        return "Неизвестное событие";
    }

    /**
     * Останавливает генерацию событий.
     * После вызова этого метода метод {@link #generateOne()} будет возвращать
     * сообщение "Симуляция остановлена".
     */
    public void stop() {
        running = false;
    }

    /**
     * Проверяет, активна ли симуляция.
     *
     * @return {@code true}, если генератор работает; {@code false}, если остановлен
     */
    public boolean isRunning() {
        return running;
    }
}
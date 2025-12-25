package supermarket.store;

import supermarket.model.Product;
import supermarket.store.Warehouse;

import java.time.LocalDate;
import java.util.List;

/**
 * Основной класс, моделирующий работу супермаркета.
 * Управляет складом и торговым залом: перемещает товары, списывает просроченные,
 * устанавливает скидки и обрабатывает покупки.
 * Используется как центральный объект симуляции.
 */
public class Supermarket {

    /**
     * Склад супермаркета, на котором хранятся товары до выкладки в торговый зал.
     */
    public final Warehouse warehouse = new Warehouse();

    /**
     * Торговый зал, где товары выставлены на продажу.
     */
    public final ShopFloor shop = new ShopFloor();

    /**
     * Создаёт экземпляр супермаркета и наполняет склад начальным количеством случайных товаров.
     *
     * @param initialItems начальное количество товаров на складе
     */
    public Supermarket(int initialItems) {
        warehouse.populateRandom(initialItems);
    }

    /**
     * Перемещает указанное количество товаров со склада в торговый зал.
     * Если на складе недостаточно товаров, перемещается всё доступное количество.
     *
     * @param count желаемое количество перемещаемых товаров
     */
    public void moveFromWarehouseToShop(int count) {
        List<Product> moved = warehouse.takeSome(count);
        moved.forEach(shop::add);
    }

    /**
     * Списывает все просроченные товары со склада и из торгового зала на текущую дату.
     * Использует текущую дату системы для проверки срока годности.
     */
    public void discardExpired() {
        LocalDate now = LocalDate.now();
        warehouse.removeExpired(now);
        shop.removeExpired(now);
    }

    /**
     * Устанавливает скидку на все товары в торговом зале, чьи названия содержат указанную подстроку.
     * Поиск выполняется без учёта регистра.
     *
     * @param nameLike подстрока для поиска в названиях товаров (пустая строка — ко всем товарам)
     * @param fraction скидка в долях единицы (например, 0.2 = 20%)
     */
    public void setDiscount(String nameLike, double fraction) {
        shop.list().forEach(p -> {
            if (p.getName().toLowerCase().contains(nameLike.toLowerCase())) {
                p.setDiscount(fraction);
            }
        });
    }

    /**
     * Обрабатывает покупку товара по имени.
     * Если товар найден на витрине и его цена со скидкой не превышает указанный бюджет — покупка успешна.
     * Товар удаляется из торгового зала.
     *
     * @param name   точное название товара (без учёта регистра)
     * @param budget максимальная сумма, которую покупатель готов потратить
     * @return {@code true}, если покупка прошла успешно; иначе — {@code false}
     */
    public boolean buyProductByName(String name, double budget) {
        for (Product p : shop.list()) {
            if (p.getName().equalsIgnoreCase(name) && p.getPrice() <= budget) {
                shop.remove(p);
                return true;
            }
        }
        return false;
    }
}
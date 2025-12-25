package supermarket.store;

import supermarket.model.Product;
import supermarket.model.UnitProduct;
import supermarket.model.WeightedProduct;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Класс, представляющий склад супермаркета.
 * Хранит товары до их перемещения в торговый зал.
 * Предоставляет операции добавления, извлечения, просмотра и автоматического удаления просроченных товаров.
 */
public class Warehouse {
    /**
     * Список товаров на складе.
     * Используется модификатор {@code final}, так как ссылка на список не меняется.
     */
    private final List<Product> products = new ArrayList<>();

    /**
     * Генератор случайных чисел для вспомогательных операций (например, при наполнении склада).
     */
    private final Random rnd = new Random();

    /**
     * Добавляет товар на склад.
     *
     * @param p добавляемый товар; не должен быть {@code null}
     */
    public void add(Product p) {
        products.add(p);
    }

    /**
     * Извлекает указанное количество товаров со склада (по одному, с начала списка).
     * Если товаров на складе меньше, чем запрошено, возвращаются все доступные.
     * Извлечённые товары удаляются из хранилища.
     *
     * @param n желаемое количество товаров
     * @return список извлечённых товаров (может быть пустым)
     */
    public List<Product> takeSome(int n) {
        List<Product> taken = new ArrayList<>();
        for (int i = 0; i < n && !products.isEmpty(); i++) {
            taken.add(products.remove(0));
        }
        return taken;
    }

    /**
     * Удаляет все просроченные товары на указанную дату.
     * Проверка осуществляется с помощью метода {@link Product#isExpired(LocalDate)}.
     *
     * @param at дата, на которую проверяется срок годности
     */
    public void removeExpired(LocalDate at) {
        products.removeIf(p -> p.isExpired(at));
    }

    /**
     * Возвращает текущее количество товаров на складе.
     *
     * @return число товаров
     */
    public int size() {
        return products.size();
    }

    /**
     * Наполняет склад случайными товарами заданного количества.
     * Случайным образом создаются товары типа {@link UnitProduct} или {@link WeightedProduct}
     * с случайными названиями и ценами. Часть товаров может иметь срок годности.
     *
     * @param count количество товаров для добавления
     */
    public void populateRandom(int count) {
        for (int i = 0; i < count; i++) {
            if (rnd.nextBoolean()) {
                UnitProduct u = new UnitProduct("U" + System.nanoTime(), randomName(), rnd.nextDouble() * 50 + 1);
                if (rnd.nextBoolean()) u.setExpiryDate(LocalDate.now().plusDays(rnd.nextInt(10) - 2));
                add(u);
            } else {
                WeightedProduct w = new WeightedProduct("W" + System.nanoTime(), randomName(), rnd.nextDouble() * 20 + 0.5);
                if (rnd.nextBoolean()) w.setExpiryDate(LocalDate.now().plusDays(rnd.nextInt(10) - 2));
                add(w);
            }
        }
    }

    /**
     * Возвращает случайное название товара из предустановленного списка.
     * Используется для генерации тестовых данных.
     *
     * @return одно из названий: "Хлеб", "Молоко", "Яблоко", "Колбаса" и т.д.
     */
    private String randomName() {
        String[] names = {"Хлеб", "Молоко", "Яблоко", "Колбаса", "Сыр", "Стиральный порошок", "Вино", "Рис"};
        return names[rnd.nextInt(names.length)];
    }
}
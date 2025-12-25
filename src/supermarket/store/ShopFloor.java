package supermarket.store;

import supermarket.model.Product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляющий торговый зал супермаркета.
 * Хранит товары, выставленные на продажу, и предоставляет операции по управлению ассортиментом:
 * добавление, удаление, получение списка и автоматическое списание просроченных товаров.
 */
public class ShopFloor {
    /**
     * Список товаров, выставленных на витрине.
     * Используется модификатор {@code final}, так как ссылка на список не меняется.
     */
    private final List<Product> display = new ArrayList<>();

    /**
     * Добавляет товар на витрину.
     * Дублирование не проверяется — товар может быть добавлен несколько раз.
     *
     * @param p добавляемый товар; не должен быть {@code null}
     */
    public void add(Product p) {
        display.add(p);
    }

    /**
     * Возвращает копию списка товаров, выставленных на витрине.
     * Изменения в возвращаемом списке не влияют на внутреннее состояние торгового зала.
     *
     * @return новый список товаров на витрине
     */
    public List<Product> list() {
        return new ArrayList<>(display);
    }

    /**
     * Удаляет указанный товар с витрины.
     * Удаление производится по ссылке (сравнение через {@code equals}).
     *
     * @param p удаляемый товар; если отсутствует — ничего не происходит
     */
    public void remove(Product p) {
        display.remove(p);
    }

    /**
     * Удаляет все просроченные товары на указанную дату.
     * Проверка выполняется с помощью метода {@link Product#isExpired(LocalDate)}.
     *
     * @param at дата, на которую проверяется просроченность товаров
     */
    public void removeExpired(LocalDate at) {
        display.removeIf(p -> p.isExpired(at));
    }

    /**
     * Возвращает количество товаров, выставленных на витрине.
     *
     * @return текущее число товаров на витрине
     */
    public int size() {
        return display.size();
    }
}
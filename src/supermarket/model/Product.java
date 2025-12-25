package supermarket.model;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Абстрактный класс, представляющий товар в супермаркете.
 * Содержит общие свойства товаров: название, цена, срок годности, скидка.
 * Поддерживает расчёты с учётом скидок и проверку на просроченность.
 */
public abstract class Product {
    /**
     * Уникальный идентификатор товара.
     * Не может быть изменён после создания.
     */
    protected final String id;

    /**
     * Название товара.
     * Не может быть изменено после создания.
     */
    protected final String name;

    /**
     * Базовая цена товара (за единицу или за килограмм).
     * Может быть изменена только через методы, устанавливающие скидки.
     */
    protected double price;

    /**
     * Опциональная дата окончания срока годности.
     * Если значение отсутствует, товар считается не имеющим срока годности.
     */
    protected Optional<LocalDate> expiryDate = Optional.empty();

    /**
     * Текущая скидка на товар в долях единицы (от 0.0 до 1.0).
     * Например, 0.2 = 20% скидка.
     */
    protected double discount = 0.0; // fraction 0.0 - 1.0

    /**
     * Создаёт экземпляр товара с указанными параметрами.
     *
     * @param id    уникальный идентификатор товара
     * @param name  название товара
     * @param price базовая цена товара
     */
    public Product(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    /**
     * Возвращает название товара.
     *
     * @return название товара
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает текущую цену товара с учётом скидки.
     * Формула: базовая цена × (1 - скидка)
     *
     * @return цена со скидкой
     */
    public double getPrice() {
        return price * (1 - discount);
    }

    /**
     * Устанавливает скидку на товар с ограничением в диапазоне от 0.0 до 1.0.
     * Если передано значение вне диапазона, оно будет автоматически обрезано.
     * Например: -0.1 → 0.0, 1.2 → 1.0
     *
     * @param d скидка в долях единицы (например, 0.15 = 15%)
     */
    public void setDiscount(double d) {
        discount = Math.max(0.0, Math.min(1.0, d));
    }

    /**
     * Возвращает текущую скидку на товар.
     *
     * @return скидка в долях единицы
     */
    public double getDiscount() {
        return discount;
    }

    /**
     * Возвращает базовую цену товара без учёта скидки.
     *
     * @return базовая цена
     */
    public double getBasePrice() {
        return price;
    }

    /**
     * Возвращает опциональную дату окончания срока годности.
     *
     * @return {@link Optional} с датой, если срок годности задан; иначе — пустой Optional
     */
    public Optional<LocalDate> getExpiryDate() {
        return expiryDate;
    }

    /**
     * Устанавливает дату окончания срока годности.
     * Если передано значение {@code null}, дата будет сброшена (товар не имеет срока годности).
     *
     * @param date дата окончания срока годности или {@code null}
     */
    public void setExpiryDate(LocalDate date) {
        expiryDate = Optional.ofNullable(date);
    }

    /**
     * Проверяет, истёк ли срок годности товара на указанную дату.
     * Если дата окончания срока годности не установлена, товар считается не просроченным.
     *
     * @param at дата, на которую проверяется просроченность
     * @return {@code true}, если товар просрочен; иначе — {@code false}
     */
    public boolean isExpired(LocalDate at) {
        return expiryDate.map(d -> !d.isAfter(at)).orElse(false);
    }
}
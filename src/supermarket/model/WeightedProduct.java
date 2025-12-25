package supermarket.model;

/**
 * Класс, представляющий товар, продающийся по весу (на вес).
 * Наследуется от {@link Product} и используется для товаров, цена которых указана за килограмм.
 * Примеры: колбаса, сыр, овощи и т.д.
 * Фактическая цена рассчитывается исходя из веса при продаже.
 */
public class WeightedProduct extends Product {

    /**
     * Создаёт товар, продающийся на вес.
     *
     * @param id         уникальный идентификатор товара
     * @param name       название товара
     * @param pricePerKg базовая цена за один килограмм товара
     */
    public WeightedProduct(String id, String name, double pricePerKg) {
        super(id, name, pricePerKg);
    }
}
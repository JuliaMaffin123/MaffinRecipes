package com.maffin.recipes.adapter;

/**
 * интерфейс, обеспечивающий активностям получить позицию записи в
 * адаптере на основании ID записи.
 */
public interface IGetItemPosition {
    /**
     * Возвращает позицию элемента по его id.
     *
     * @param id
     * 		идентификатор записи
     * @return
     * 		позиция элемента
     */
    int getItemPosition(final long id);
}

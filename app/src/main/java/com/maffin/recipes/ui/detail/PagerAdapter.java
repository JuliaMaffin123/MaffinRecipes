package com.maffin.recipes.ui.detail;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.Fragment;

import com.maffin.recipes.ui.detail.components.TabComponents;
import com.maffin.recipes.ui.detail.steps.TabSteps;

/**
 * Адаптер для наполнения контентом вкладок с фрагмента детализации рецепта.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    /** Количество вкладок. */
    private int tabCount;

    /**
     * Конструктор.
     * @param fm        менеджер фрагментов
     * @param tabCount  число вкладок
     */
    public PagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount= tabCount;
    }

    /**
     * Возвращает фрагмент по позиции в адаптере
     * @param position  позиция
     * @return  фрагмент
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                // Вкладка со списком ингредиентов
                TabComponents tabComponents = new TabComponents();
                return tabComponents;
            case 1:
                // Вкладка со списком шагов
                TabSteps tabSteps = new TabSteps();
                return tabSteps;
            default:
                return null;
        }
    }

    /**
     * Возвращает число вкладок.
     * @return  число вкладок
     */
    @Override
    public int getCount() {
        return tabCount;
    }
}

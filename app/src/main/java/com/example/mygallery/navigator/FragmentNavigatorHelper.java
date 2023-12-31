package com.example.mygallery.navigator;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentNavigatorHelper {
    private final FragmentManager fragmentManager;
    private final int containerId;
    private Fragment currentFragment;

    public FragmentNavigatorHelper(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public boolean isCurrentFragmentInstanceOf(Class<? extends Fragment> fragmentClass) {
        return fragmentClass.isInstance(getCurrentFragment());
    }

    public void switchToFragment(Class<? extends Fragment> fragmentClass) {
        try {
            // Создаем экземпляр фрагмента
            Fragment fragment = fragmentClass.getConstructor().newInstance();

            // Проверяем, не переключаемся ли на текущий фрагмент
            if (currentFragment != null && fragmentClass.isInstance(currentFragment)) {
                return; // Не переключаемся на тот же самый фрагмент
            }

            // Начинаем транзакцию фрагментов
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Открепляем текущий фрагмент (если есть)
            if (currentFragment != null) {
                transaction.detach(currentFragment);
            }

            // Добавляем или прикрепляем новый фрагмент
            if (!fragment.isAdded()) {
                transaction.add(containerId, fragment);
            } else {
                transaction.attach(fragment);
            }

            // Завершаем транзакцию и коммитим ее
            transaction.commit();

            // Обновляем текущий фрагмент
            currentFragment = fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

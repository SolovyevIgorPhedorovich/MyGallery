package com.example.mygallery.navigator;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.mygallery.fragments.AlbumRecyclerViewFragment;

public class FragmentManagerHelper {
    private final FragmentManager fragmentManager;
    private final int containerId;
    private Fragment currentFragment;

    public FragmentManagerHelper(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public boolean isCurrentFragmentInstanceOf(Class<? extends Fragment> fragmentClass) {
        Fragment currentFragment = getCurrentFragment();
        return fragmentClass.isInstance(currentFragment);
    }

    public void switchToFragment(Class<? extends Fragment> fragmentClass) {
        try {
            // Создаем экземпляр фрагмента
            Fragment fragment = fragmentClass.newInstance();

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

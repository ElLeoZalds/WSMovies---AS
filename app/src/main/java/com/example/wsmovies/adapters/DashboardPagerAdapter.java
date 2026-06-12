package com.example.wsmovies.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.wsmovies.fragments.ListarFragment;
import com.example.wsmovies.fragments.BuscarFragment;
import com.example.wsmovies.fragments.RegistrarFragment;

public class DashboardPagerAdapter extends FragmentStateAdapter {

    public DashboardPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // En este punto se hace necesario el BACKEND (JAVA) de cada fragmento
        switch (position) {
            case 0: return new ListarFragment();
            case 1: return new BuscarFragment();
            default: return new RegistrarFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

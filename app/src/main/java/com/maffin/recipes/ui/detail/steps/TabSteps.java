package com.maffin.recipes.ui.detail.steps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.maffin.recipes.R;

/**
 * Фрагмент для вкладки ПРИГОТОВЛЕНИЕ.
 */
public class TabSteps extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        return inflater.inflate(R.layout.tab_steps, container, false);
    }
}

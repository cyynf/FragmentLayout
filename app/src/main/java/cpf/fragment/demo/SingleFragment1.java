package cpf.fragment.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cpf.fragment.layout.FragmentLayout;

/**
 * @author : cpf
 * @date : 12/22/20
 * email       : cpf4263@gmail.com
 * description :
 */
public class SingleFragment1 extends Fragment {

    public SingleFragment1() {
        super(R.layout.fragment_single1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentLayout fragmentLayout = view.findViewById(R.id.fragmentLayout1);
        fragmentLayout.loadFragmentDelayed(this, 500);
    }
}

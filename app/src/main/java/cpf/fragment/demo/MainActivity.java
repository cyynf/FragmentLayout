package cpf.fragment.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import cpf.fragment.layout.FragmentLayout;
import cpf.fragment.layout.FragmentLoadListener;

public class MainActivity extends AppCompatActivity {

    FragmentLayout fragmentLayout, lazyFragmentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentLayout = findViewById(R.id.fragmentLayout);
        lazyFragmentLayout = findViewById(R.id.lazyFragmentLayout);
        lazyFragmentLayout.setListener(new FragmentLoadListener() {
            @Override
            public void onFragmentLoad(FragmentLayout view, Fragment fragment) {
                Toast.makeText(MainActivity.this, "loaded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void lazyLoad(View view) {
        lazyFragmentLayout.loadFragment(getSupportFragmentManager());
    }
}
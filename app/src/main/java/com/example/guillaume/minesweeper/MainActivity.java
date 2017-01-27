package com.example.guillaume.minesweeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tv_marked_mines;
    private Button btn_change_mode;
    private Button btn_reset;
    private CustomView cView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_marked_mines = (TextView) findViewById(R.id.tv_marked_mines);
        btn_change_mode = (Button) findViewById(R.id.btn_mode);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        cView = (CustomView) findViewById(R.id.custom_view);
        cView.setObserver(new Observer() {
            @Override
            public void callback() {
                tv_marked_mines.setText(String.format("Marked mines: %d", cView.getMarkedMines()));
            }
        });
        // Change mode button listener
        btn_change_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cView.isMarkingModeActive()) {
                    btn_change_mode.setText(R.string.btn_marking_mode);
                    cView.setMarkingMode(false);
                } else {
                    btn_change_mode.setText(R.string.btn_uncover_mode);
                    cView.setMarkingMode(true);
                }
            }
        });
        // Reset button listener
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_marked_mines.setText(R.string.tv_marked_mines_default);
                btn_change_mode.setText(R.string.btn_marking_mode);
                cView.reset();
            }
        });
    }
}

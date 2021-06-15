package com.example.classicbluetoothmaster;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class LineView extends View {

    private Paint paint;
    private Path path;

    public LineView(Context context, Paint paint, Path path) {
        super(context);

        this.paint = paint;
        this.path = path;
    }

    @Override
    protected void onDraw(Canvas canvas) {




    }
}

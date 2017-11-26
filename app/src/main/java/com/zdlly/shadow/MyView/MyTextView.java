package com.zdlly.shadow.MyView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.Selection;
import android.text.Spanned;
import android.text.method.MovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MotionEvent;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static android.content.ContentValues.TAG;

/**
 * Created by zdlly on 2017.11.24.
 */

public class MyTextView extends android.support.v7.widget.AppCompatEditText {
    private int offset;
    private ForegroundColorSpan mSelectionForegroundColorSpan;
    private ForegroundColorSpan oldmSelectionForegroundColorSpan;
    private HashMap<Integer, Integer> selectText = new LinkedHashMap<>();
    private boolean isNewText = false;
    private boolean isReverse = false;

    public MyTextView(Context context) {
        super(context);
        initLook();
    }


    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLook();
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLook();
    }

    private void initLook() {
        setGravity(Gravity.TOP);
        setBackgroundColor(Color.WHITE);

    }

    @Override
    protected boolean getDefaultEditable() {
        return super.getDefaultEditable();
    }

    @Override
    protected MovementMethod getDefaultMovementMethod() {
        return super.getDefaultMovementMethod();
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {

    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int begin = Selection.getSelectionStart(getText());
        int end = Selection.getSelectionEnd(getText());
        if (begin > end) {
            isReverse = true;
            int swap = begin;
            begin = end;
            end = swap;
        } else isReverse = false;
        int action = event.getAction();
        Layout layout = getLayout();
        int line = 0;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                line = layout.getLineForVertical(getScrollY() + (int) event.getY());
                offset = layout.getOffsetForHorizontal(line, (int) event.getX());
                Selection.setSelection(getEditableText(), offset);
                break;
            case MotionEvent.ACTION_MOVE:
                if (oldmSelectionForegroundColorSpan != null && !isNewText)
                    getText().removeSpan(oldmSelectionForegroundColorSpan);
                mSelectionForegroundColorSpan = new ForegroundColorSpan(isReverse ? Color.BLACK : Color.RED);
                oldmSelectionForegroundColorSpan = mSelectionForegroundColorSpan;
                isNewText = false;

                line = layout.getLineForVertical(getScrollY() + (int) event.getY());
                int curMoveOffset = layout.getOffsetForHorizontal(line, (int) event.getX());
                Selection.setSelection(getEditableText(), offset, curMoveOffset);
                getText().setSpan(mSelectionForegroundColorSpan, begin, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                break;

            case MotionEvent.ACTION_UP:
                line = layout.getLineForVertical(getScrollY() + (int) event.getY());
                int curOffset = layout.getOffsetForHorizontal(line, (int) event.getX());
                Selection.setSelection(getEditableText(), offset, curOffset);
                isNewText = true;
                break;
        }

        return true;
    }

    public void changeText() {
        int next;
        for (int i = 0; i < getText().length(); i = next) {
            next = getText().nextSpanTransition(i, getText().length(), ForegroundColorSpan.class);
            ForegroundColorSpan[] spans = getText().getSpans(i, next, ForegroundColorSpan.class);
            if (spans.length!=0){
                ForegroundColorSpan span = spans[spans.length - 1];
                if (span.getForegroundColor() == Color.RED) {
                    Log.d(TAG, "changeText: " + i + "::" + next);
                    String replacedString=getText().subSequence(i,next).toString().replaceAll("[^\\p{P}]" ,"__");
                    this.setText(getText().replace(i,next,replacedString));
                }
            }

        }
    }
}

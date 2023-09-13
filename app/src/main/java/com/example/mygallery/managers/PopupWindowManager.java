package com.example.mygallery.managers;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import androidx.core.content.ContextCompat;
import com.example.mygallery.R;
import com.example.mygallery.activities.ImageViewActivity;

public class PopupWindowManager {
    PopupWindow popupWindow;
    Context context;
    int x,y;

    public PopupWindowManager(Context context){
        popupWindow = new PopupWindow(context);
        this.context = context;
        this.x = 0;
        this.y = 0;
    }
    //Установка параметров меню
    private void setConfigurationContextPopupWindow(){
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.popupbg));
    }

    private void setConfigurationInputNamePopupWindow(){
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.popupbg));
    }

    private void setAnimationPopupWindow(){
        popupWindow.setAnimationStyle(R.style.popupWindowAnimation);
    }

    //Установка содержимого
    public void setContentViewPopupWindow(View view){
        popupWindow.setContentView(view);
        int viewId = view.getRootView().getId();
        if (viewId == R.id.contextMenu){
            setConfigurationContextPopupWindow();
        }
        else if (viewId == R.id.renameView){
            setConfigurationInputNamePopupWindow();
        }
        setAnimationPopupWindow();
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void showPopupWindow(View view, View viewMenu){
        popupWindow.showAtLocation(view, Gravity.BOTTOM, x, y);
        getIdButtonView(viewMenu);
    }

    private void getIdButtonView(View view){
        if (view instanceof ViewGroup){
            foreachId(view);
        }
    }

    private void foreachId(View view){
        ViewGroup viewGroup = (ViewGroup) view;

        for (int i = 0; i < viewGroup.getChildCount(); i++){
            View childView = viewGroup.getChildAt(i);

            if (childView instanceof Button){
                Button button = (Button) childView;
                //Получение ID кнопки
                int buttonId = button.getId();
                setListenerButton(button, buttonId);
            }
            if (childView instanceof LinearLayout){
                foreachId(childView);
            }
        }
    }

    //Назначение обработчка нажатия
    private void setListenerButton(Button button, int buttonId){
        if (context instanceof ImageViewActivity) {
            ImageViewActivity imageViewActivity = (ImageViewActivity) context;
            button.setOnClickListener(view -> {
                popupWindow.dismiss();
                if (R.id.moveFile == buttonId){
                    imageViewActivity.moveFile();
                }
                else if (R.id.copyFile == buttonId){
                    imageViewActivity.copyFile();
                }
                else if (R.id.slide_show == buttonId){
                    imageViewActivity.startSlaidShow();
                }
                else if (R.id.print == buttonId) {
                    imageViewActivity.print();
                }
                else if (R.id.rename_file == buttonId){
                    imageViewActivity.rename(view);
                }
                else if (R.id.set == buttonId){
                    imageViewActivity.setAs();
                }
                else if (R.id.rotation == buttonId){
                    imageViewActivity.rotation();
                }
                else if (R.id.choose == buttonId){
                    imageViewActivity.choose();
                }
                else if (R.id.OK == buttonId) {
                    EditText name = popupWindow.getContentView().findViewById(R.id.newName);
                    imageViewActivity.rename(name.getText().toString());
                }
            });
        }
    }

}

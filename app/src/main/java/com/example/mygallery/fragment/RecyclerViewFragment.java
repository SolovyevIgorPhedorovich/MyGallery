package com.example.mygallery.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.R;
import com.example.mygallery.activities.AlbumActivity;
import com.example.mygallery.activities.ImageViewActivity;
import com.example.mygallery.activities.MainActivity;
import com.example.mygallery.adapters.AlbumAdapter;
import com.example.mygallery.adapters.ImageAdapter;
import com.example.mygallery.managers.DataManager;
import com.example.mygallery.managers.DatabaseManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewFragment extends Fragment {
    
    public static final int FAVORITES = 0;
    public static final int ALBUM = 1;
    public static final int CART = 2;
    public static final int IMAGES = 4;
    private List<File> imagePaths;

    private int idFragment;
    private Context context;
    private RecyclerView recyclerView;
    private DataManager dataManager;

    public RecyclerViewFragment(Context context, int idFragment){
        this.context= context;
        this.idFragment = idFragment;
        this.dataManager = DataManager.getInstance(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        setConfigRecycleView(view);

        return view;
    }

    // Насторйка RecycleView с использование GridLayoutManager
    private void setConfigRecycleView(View view) {
        recyclerView = (RecyclerView) view;
        switch (idFragment){
            case FAVORITES:
                setConfigRecycleView(recyclerView, 4);
                DatabaseManager db = new DatabaseManager(context);
                dataManager.setPathsFiles(db.getFavorites());
                if (!dataManager.getPathsFiles().isEmpty())
                    createImagePreviewAdapter();
                else {

                }
                 break;
            case CART:
                setConfigRecycleView(recyclerView, 4);
                dataManager.setPathsFiles((List<File>) FileUtils.listFiles(new File(context.getFilesDir(),"Корзина"), null, false));
                if (!dataManager.getPathsFiles().isEmpty()){
                    createImagePreviewAdapter();
                }
                break;
            case ALBUM:
                setConfigRecycleView(recyclerView, 2);
                createAlbumAdapter();
                break;
            case IMAGES:
                setConfigRecycleView(recyclerView, 4);
                createImagePreviewAdapter();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + idFragment);
        }
    }

    private void setConfigRecycleView(RecyclerView recyclerView,int spanCount){
        recyclerView.setLayoutManager(new GridLayoutManager(context, spanCount));
    }

    //Создание адаптера
    private void createAlbumAdapter() {
        int imageWidth = getSizeViewImage();
        AlbumAdapter adapter = new AlbumAdapter(context, imageWidth, new AlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String nameFolder, int position) {
                ((AlbumActivity) context).openDirectoryImage(nameFolder, position);
            }
        });
        recyclerView.setAdapter(adapter);
        //recyclerView.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        if (isRun && !isFirstStart) {
        //            progressBar.setVisibility(View.GONE);
        //            task = new FileScannerAsyncTask(AlbumActivity.this);
        //            task.execute();
        //        }
        //    }
       // });
    }

    private void createImagePreviewAdapter(){
        ImageAdapter adapter = new ImageAdapter(context, dataManager.getPathsFiles(), new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openImageViewer(position);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private int getSizeViewImage() {
        //Получение ширины экрна
        Display display = ((AlbumActivity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        //Получение ширины отступа
        int imageMargin = getResources().getDimensionPixelSize(R.dimen.image_preview_margin_horizontal);

        //Вычисление ширины для отображения изображения в альбома
        return (screenWidth - (2 * imageMargin)) / 2;
    }

    private void openImageViewer(int position) {
        Intent intent = new Intent(context, ImageViewActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    public void updateAdapter(){
        if (idFragment == ALBUM){
            AlbumAdapter adapter;
            adapter =(AlbumAdapter) recyclerView.getAdapter();
            adapter.updateDataAdapter();
            adapter.notifyDataSetChanged();}
        else{
            ImageAdapter adapter;
            adapter = (ImageAdapter) recyclerView.getAdapter();
            adapter.notifyDataSetChanged();}
        Log.d("updateAdapter","updateComplite");
    }
}

package com.example.mygallery;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ImageViewHolder> {

    private Context context;
    private List<String> imageNameAlbum;
    private List<String> imagePreviewPaths;
    private List<Integer> countItemDirectory;
    private TextView directoryName;
    private TextView countItem;
    private int imageWidth;
    private AlbumAdapter.OnItemClickListener listener;

    public AlbumAdapter(Context context, List<String> imageNameAlbum, List<String> imagePreviewPaths, List<Integer> countItemDirectory, int imageWidth, AlbumAdapter.OnItemClickListener listener){
        this.context = context;
        this.imageNameAlbum = imageNameAlbum;
        this.imagePreviewPaths = imagePreviewPaths;
        this.countItemDirectory = countItemDirectory;
        this.imageWidth = imageWidth;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlbumAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_image_view, parent, false);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        countItem = itemView.findViewById(R.id.countImages);
        directoryName = itemView.findViewById(R.id.albumDirectoryName);
        layoutParams.width = imageWidth;
        layoutParams.height = imageWidth * 2;
        itemView.setLayoutParams(layoutParams);
        return new AlbumAdapter.ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imagePath = imagePreviewPaths.get(position);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        options.inSampleSize = calculetSize(options);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

        directoryName.setText(imageNameAlbum.get(position));
        countItem.setText(String.valueOf(countItemDirectory.get(position)));

        Bitmap roundedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundedBitmap);
        //Кисть для рисования и создание прозрачного фона
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xFFFFFFFF);
        //Создание прямоугольника
        RectF rectF = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawRoundRect(rectF, 20, 20, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);


        holder.imageView.setImageBitmap(roundedBitmap);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageNameAlbum.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.albumImagePreview);
        }
    }

    private int calculetSize(BitmapFactory.Options options){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > imageWidth * 2 || width > imageWidth){
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > imageWidth * 2  && (halfWidth / inSampleSize) > imageWidth){
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}

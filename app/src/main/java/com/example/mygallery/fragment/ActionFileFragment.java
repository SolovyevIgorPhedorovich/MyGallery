package com.example.mygallery.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mygallery.R;
import com.example.mygallery.activities.ImageViewActivity;
import com.example.mygallery.adapters.MyItemRecyclerViewAdapter;
import com.example.mygallery.managers.DatabaseManager;

import java.io.File;

/**
 * A fragment representing a list of Items.
 */
public class ActionFileFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    // 0 - move, 1- copy
    private boolean typeAction;
    private Context context;
    private ImageButton closeFragment;
    TextView textView;

    private RecyclerView recyclerView;
    private FolderSelectionListener folderSelectionListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ActionFileFragment(Context context, boolean typeAction) {
        this.typeAction = typeAction;
        this.context = context;
    }

    public interface FolderSelectionListener{
        void onFolderSelected(File folderPath);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_action_file, container, false);

        if (context instanceof ImageViewActivity){
            view.setPadding(0, ((ImageViewActivity) context).getStatusBarHeight(), 0, 0);
        }

        textView =  view.findViewById(R.id.typeAction);
        recyclerView = view.findViewById(R.id.list);
        closeFragment = view.findViewById(R.id.closeFragment);

        setTextView();

        // Set the adapter
        createAdapter(view);

        setListener();
        return view;
    }

    private void setListener(){
        closeFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFragment();
            }
        });
    }

    private void setTextView(){
        if (typeAction){
            textView.setText(R.string.copy_file);
        }
        else{
            textView.setText(R.string.move_file_in);
        }
    }

    private void createAdapter(View view){
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        MyItemRecyclerViewAdapter adapter = new MyItemRecyclerViewAdapter(context);
        adapter.setOnItemClickListener(new MyItemRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String nameFolder) {
                DatabaseManager DBManager = new DatabaseManager(context);
                File selectedFolderPath = new File(DBManager.getFolderPath(nameFolder));
                DBManager.close();

                if (folderSelectionListener != null){
                    folderSelectionListener.onFolderSelected(selectedFolderPath);
                }
                closeFragment();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void closeFragment(){
        if (context instanceof ImageViewActivity){
            ImageViewActivity activity = (ImageViewActivity) context;
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager.popBackStack();
        }
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        try{
            folderSelectionListener = (FolderSelectionListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+" must implement FolderSelectionListener");
        }
    }
}
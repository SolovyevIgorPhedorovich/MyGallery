package com.example.mygallery;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    public static final int NAME = 0;
    public static final int PATH = 1;
    public static final int COUNT = 2;
    public static final int COVERS = 3;
    private static DataManager instance;
    private List<String> namesFolders;
    private List<String> pathsFiles;
    private List<Integer> countFiles;
    private List<String> coversFolders;

    public List<String> pathsFolders;
    public int position;
    public DataManager(){
        namesFolders = new ArrayList<>();
        pathsFiles = new ArrayList<>();
        coversFolders = new ArrayList<>();
        countFiles = new ArrayList<>();
        pathsFolders = new ArrayList<>();
        instance = this;
    }

    public static DataManager getInstance(){
        if (instance == null){
            instance = new DataManager();
        }
        return instance;
    }

    public void addDataPathsFolders(String path){
        pathsFolders.add(path);
    }

    public void setData(List<String> namesFolders, List<String> pathsFiles, List<Integer> countFiles, List<String> coversFolders){
        this.namesFolders = namesFolders;
        this.pathsFiles = pathsFiles;
        this.countFiles = countFiles;
        this.coversFolders = coversFolders;
    }



    public void  setNamesFolders(List<String> namesFolders){
        this.namesFolders = namesFolders;
    }

    public void setPathsFiles(List<String> pathsFiles){
        this.pathsFiles = pathsFiles;
    }

    public void setCountFiles(List<Integer> countFiles){
        this.countFiles = countFiles;
    }

    public void setCoversFolders(List<String> coversFolders){
        this.coversFolders = coversFolders;
    }

    public List<String> getNamesFolders(){
        return namesFolders;
    }

    public List<String> getPathsFiles(){
        return pathsFiles;
    }

    public List<Integer> getCountFiles(){
        return countFiles;
    }

    public List<String> getCoversFolders(){
        return  coversFolders;
    }

    public void addDataInNamesFolders(String name){
        namesFolders.add(name);
    }

    public void addDataInNamesFolders(List<String> names){
        for (String name:names) {
            namesFolders.add(name);
        }
    }

    public void addDataInPathsFiles(String path){
        pathsFiles.add(path);
    }

    public void addDataInPathsFiles(List<String> paths){
        for (String path:paths) {
            pathsFiles.add(path);
        }
    }

    public void addDataInCountFiles(int count){
        countFiles.add(count);
    }

    public void addDataInCountFiles(List<Integer> counts){
        for (int count:counts) {
            countFiles.add(count);
        }
    }

    public void addDataInCoversFolders(String coverPath){
        coversFolders.add(coverPath);
    }

    public void addDataInCoversFolders(List <String> coverPaths){
        for (String coverPath:coverPaths) {
            coversFolders.add(coverPath);
        }
    }

    public boolean isData(){
        if (isDataNamesFolders() && isDataCountFiles() && isDataCoversFolders() && isDataPathsFiles()) {
            return true;
        }
        return false;
    }

    public boolean isDataNamesFolders() {
        return !this.namesFolders.isEmpty();
    }

    public boolean isDataPathsFiles(){
        return !this.pathsFiles.isEmpty();
    }

    public boolean isDataCountFiles(){
        return !this.countFiles.isEmpty();
    }

    public  boolean isDataCoversFolders(){
        return !this.coversFolders.isEmpty();
    }

    public void clearData(){
        namesFolders.clear();
        pathsFiles.clear();
        countFiles.clear();
        coversFolders.clear();
    }

    public void clearData(int id){
        if (id == NAME){
            namesFolders.clear();
        }
        else if (id == PATH) {
            pathsFiles.clear();
        }
        else if (id == COUNT){
            countFiles.clear();
        }
        else if (id == COVERS)
        {
            coversFolders.clear();
        }
    }

}

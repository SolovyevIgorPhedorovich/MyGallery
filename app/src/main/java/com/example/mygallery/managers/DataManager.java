package com.example.mygallery.managers;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    public static final int NAME = 0;
    public static final int PATH = 1;
    public static final int COUNT = 2;
    public static final int COVERS = 3;
    public static final int INVALIDS = 4;
    public static final int PATH_FOLDERS = 5;
    private static DataManager instance;
    private List<String> namesFolders;
    private List<File> pathsFiles;
    private List<Integer> countFiles;
    private List<String> coversFolders;
    private List<String> invalidsPathFile;

    private List<String> pathsFolders;
    public int position;
    public DataManager(){
        namesFolders = new ArrayList<>();
        pathsFiles = new ArrayList<>();
        pathsFolders = new ArrayList<>();
        coversFolders = new ArrayList<>();
        countFiles = new ArrayList<>();
        pathsFolders = new ArrayList<>();
        invalidsPathFile = new ArrayList<>();
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

    public void setData(List<String> namesFolders, List<Integer> countFiles, List<String> coversFolders){
        this.namesFolders = namesFolders;
        this.countFiles = countFiles;
        this.coversFolders = coversFolders;
    }

    public void setPathsFolders(List<String> pathsFolders){this.pathsFolders = pathsFolders;}

    public void  setNamesFolders(List<String> namesFolders){
        this.namesFolders = namesFolders;
    }

    public void setPathsFiles(File pathsFolder){
        this.pathsFiles = (List<File>) FileUtils.listFiles(pathsFolder, new String[]{"png", "jpg", "jpeg"}, false);
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

    public List<File> getPathsFiles(){
        return pathsFiles;
    }

    public List<Integer> getCountFiles(){
        return countFiles;
    }

    public List<String> getCoversFolders(){
        return  coversFolders;
    }

    public List<String> getInvalidsPathFile() {return invalidsPathFile;}

    public List<String> getPathsFolders(){return pathsFolders;}

    public void addDataInNamesFolders(String name){
        namesFolders.add(name);
    }

    public void addDataInvalidsPathFile(String path){
        invalidsPathFile.add(path);
    }
    public void addDataInNamesFolders(List<String> names){
        for (String name:names) {
            namesFolders.add(name);
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
        if (pathsFiles != null){
            return true;
        }
        else{
            return false;
        }

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
        pathsFolders.clear();
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
        } else if (id == INVALIDS) {
            invalidsPathFile.clear();
        }
        else if (id == PATH_FOLDERS){
            pathsFolders.clear();
        }
    }

}

package com.example.myapplication.info;

import java.util.HashMap;
import java.util.Map;

public class chatInfo {
    public Map<String, String> users = new HashMap<>() ;
    public Map<String, String> messages = new HashMap<>() ;

    public static class FileInfo {
        public String filename;
        public String filesize;
    }
}

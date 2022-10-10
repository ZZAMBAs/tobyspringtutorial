package com.example.tobyspringtutorial.modules.repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TextSqlReader implements SqlReader{
    private String filePath;

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void read(SqlRegistry sqlRegistry) {
        String read;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));

            while ((read = reader.readLine()) != null){
                String[] sqls = read.split("\t");
                sqlRegistry.registerSql(sqls[0], sqls[1]);
            }
        }catch (IOException e){
            throw new RuntimeException(e); // 예제여서 여기선 그냥 예외를 포장하였다.
        }finally {
            try { if (reader != null) reader.close();}
            catch (IOException e) {throw new RuntimeException(e);}
        }
    }
}

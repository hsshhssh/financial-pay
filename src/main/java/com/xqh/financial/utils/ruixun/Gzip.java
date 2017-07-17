package com.xqh.financial.utils.ruixun;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;



public class Gzip{

    public byte[] zip(byte[] datas) throws Exception { 
        List<ZipItem>item = new ArrayList<ZipItem>();
        item.add(new ZipItem("", datas));
        return zip(item);
    }

    public byte[] unzip(byte[] datas) throws Exception {
        ByteArrayInputStream inp = new ByteArrayInputStream(datas);
        GZIPInputStream zipInput = new GZIPInputStream(inp);
        byte[] buf = new byte[1024];  
        int num = -1;  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        while ((num = zipInput.read(buf, 0, buf.length)) != -1) {  
            baos.write(buf, 0, num);    
        }
        zipInput.close();
        return baos.toByteArray();
    }



    public static byte[] zip(List<ZipItem>zipItems) {  
        byte[] b = null;  
        ByteArrayOutputStream bos = new ByteArrayOutputStream();  
        GZIPOutputStream zip;  
        try {  
            zip = new GZIPOutputStream(bos); 
            for (ZipItem item : zipItems) {            
                zip.write(item.zipBody);
            }
            zip.close();  
            b = bos.toByteArray();  
            bos.close();  
        } catch (Exception ex) {  
            ex.printStackTrace();  
        } finally{
            try {
                bos.close();
            } catch (IOException e) {}
        }
        return b;  
    }  


    private static class ZipItem{

        private byte[] zipBody;

        ZipItem(String zipName,byte[]zipBody){
            this.zipBody = zipBody;
        }
    }
}

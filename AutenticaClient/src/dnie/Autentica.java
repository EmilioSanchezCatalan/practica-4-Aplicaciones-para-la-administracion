/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnie;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;
/**
 *
 * @author Juan Carlos
 */
public class Autentica {

    public Autentica() {
    }  
     /**
     * 
     * @param urlpost url del recurso que va a recibir las credenciales por POST
     * @param user
     * @param dni
     * @param fecha
     * @param firma
     * @param clavepublica 
     * @return 
     */
    public String enviarCredencialesPost(String urlpost, String user,String dni,String fecha, String firma, String clavepublica) {
        
        String postparam = "user="+user+"&dni="+dni+"&fecha="+fecha+"&firma="+firma+"&clavepublica="+clavepublica;
        InputStream is = null;
        String result="";


        HttpURLConnection conn = null;
        try {
            String contentAsString = "";
            String tempString = "";
            URL url = new URL(urlpost);
            
            System.out.println("Abriendo conexión: " + url.getHost()
                    + " puerto=" + url.getPort());
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            //Send request
            OutputStream os = conn.getOutputStream();
            BufferedWriter wr = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            wr.write(postparam);
            wr.flush();
            wr.close();
            // Starts the query
            conn.connect();
            final int response = conn.getResponseCode();
            final int contentLength = conn.getHeaderFieldInt("Content-length", 1000);

            System.out.println("Cod Respuesta del servidor: "+response);
            is = conn.getInputStream();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            while ((tempString = br.readLine()) != null) {
                contentAsString = contentAsString + tempString;

            }
            is.close();
            conn.disconnect();
			//Convert the InputStream into a string
            // contentAsString = readIt(is, len);
            return contentAsString;
        } catch (IOException e) {
            result = "Excepción: " + e.getMessage();
            System.out.println(result);

			// Makes sure that the InputStream is closed after the app is
            // finished using it.
        }
        return result;
    }
    /**
     * Devuelve el hash SHA-1 codificado en Base64
     * @param data
     * @return 
     */
    public static String toSHA1(byte[] data) {
    MessageDigest md = null;
    try {
        md = MessageDigest.getInstance("SHA-1");
        System.out.println("Hash SHA-1: "+md.toString());
        String base64=DatatypeConverter.printBase64Binary(md.digest(data));
        System.out.append("Cadena BASE 64: "+base64);
        return base64;
    }
    catch(NoSuchAlgorithmException e) {
        e.printStackTrace();
    } 
    return null;
    
}
}

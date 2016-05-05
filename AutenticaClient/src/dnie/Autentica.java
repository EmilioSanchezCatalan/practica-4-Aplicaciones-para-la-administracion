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
     * @param password
     * @return 
     */
    public String enviarCredencialesGet(String urlpost, String user,String dni,String password) {
        
        String getParam = "user="+user+"&dni="+dni+"&password="+password;
        InputStream is = null;
        String result="";


        HttpURLConnection conn = null;
        try {
            String contentAsString = "";
            String tempString = "";
            URL url = new URL(urlpost+"?"+getParam);
            
            System.out.println("Abriendo conexión: " + url.getHost()
                    + " puerto=" + url.getPort());
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            //Send request
            OutputStream os = conn.getOutputStream();
            BufferedWriter wr = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

           
            wr.flush();
            wr.close();
            // Starts the query
            conn.connect();
            final int response = conn.getResponseCode();
            final int contentLength = conn.getHeaderFieldInt("Content-length", 1000);

            System.out.println("Respuesta del servidor: "+response);
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
     * El código MAC se genera con el SHA-1 de la clave+usuario+dni
     * @param urlpost
     * @param user
     * @param dni
     * @param password
     * @return 
     */
     public String enviarCredencialesGetMAC(String urlpost, String user,String dni,String password) {
        
        String getParam;
        String hash=password+user+dni;//Formato del MAC 
        String mac = toSHA1(hash.getBytes());
        mac=mac.replaceAll("=", "%3D");
        getParam = "user="+user+"&dni="+dni+"&mac="+mac;
        InputStream is = null;
        String result="";


        HttpURLConnection conn = null;
        try {
            String contentAsString = "";
            String tempString = "";
            URL url = new URL(urlpost+"?"+getParam);
            
            System.out.println("Abriendo conexión: " + url.getHost()
                    + " puerto=" + url.getPort());
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            //Send request
            OutputStream os = conn.getOutputStream();
            BufferedWriter wr = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

           
            wr.flush();
            wr.close();
            // Starts the query
            conn.connect();
            final int response = conn.getResponseCode();
            final int contentLength = conn.getHeaderFieldInt("Content-length", 1000);

            System.out.println("Respuesta del servidor: "+response);
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
     * 
     * @param urlpost url del recurso que va a recibir las credenciales por POST
     * @param user
     * @param dni
     * @param password
     * @return 
     */
    public String enviarCredencialesPost(String urlpost, String user,String dni,String password) {
        
        String postparam = "user="+user+"&dni="+dni+"&password="+password;
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

            System.out.println("Respuesta del servidor: "+response);
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attpa.dnie;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;


/**
 *
 * @author Juan Carlos
 */
public class autentica extends HttpServlet {

    private byte clavepublica[] = null;
    private String user = null;
    private String dni = null;
    private String date = null;
    private String clave = null;
    private byte firma[] = null;
    private final DniDatabase db = new DniDatabase();
    
    String firmab64 = "";
    String clavepublicab64 = "";

    /**
     * Envia el mensaje de autentificación correcta
     * @param request recepción del servidor
     * @param response respuesta del servidor
     * @throws ServletException
     * @throws IOException 
     */
    protected void processRequestOK(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("Autentificacion correcta");
        } finally {
            out.close();
        }
    }
    
    /**
     * Envia el mensaje de autentificación incorrecta
     * @param request recepción del servidor
     * @param response respuesta del servidor
     * @throws ServletException
     * @throws IOException 
     */
    protected void processRequestER(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("Error en la autentificacion");
        } finally {
            out.close();
        }
    }
    /**
     * Metodo que recibe los datos por el metodo post
     * @param request recepcion del servidor.
     * @param response respuesta del servidor.
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        //declaracion de variables
        String datos;
        
        
        // recepcion de datos
        user = request.getParameter("user");
        dni = request.getParameter("dni");
        date = request.getParameter("fecha");
        clave = db.cogerClave(dni);
        firmab64 = request.getParameter("firma");
        clavepublicab64 = request.getParameter("clavepublica");
        
        // datos a que se han firmado
        datos = user+dni+ date + clave;
        
        // decodificación de base64
        firma = DatatypeConverter.parseBase64Binary(firmab64);
        clavepublica = DatatypeConverter.parseBase64Binary(clavepublicab64);

        // funcion de comprobación de la firma
        if(compruebaFirma(datos, firma, clavepublica)){
            processRequestOK(request, response);
        }else{
            processRequestER(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    
    /**
     * 
     * @param datos datos en claro que se han firmado
     * @param signRead firma de los datos
     * @param keyRead clave publica
     * @return true en caso de firma correcta, false en caso de firma incorrecta.
     */
     public boolean compruebaFirma(String datos, byte[] signRead,byte[]keyRead) {

        try {
            if (datos == null) {
                return false;
            }
            byte[] data = datos.getBytes();

            //Se genera la clave RSA a partir del array de bytes
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(keyRead);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
            
            //Se prepara el objeto Signature para comprobar la firma
            Signature sigver2 = Signature.getInstance("SHA1withRSA");
            //Se añade la clave pública
            sigver2.initVerify(pubKey);
            //Se le aportan los datos originales
            sigver2.update(data);
            //Se realiza la comprobación, si es correcta devolverá TRUE
            return sigver2.verify(signRead);
            
        } catch (NoSuchAlgorithmException ex) {
           
        } catch (InvalidKeySpecException ex) {
            
        } catch (InvalidKeyException ex) {
           
        } catch (SignatureException ex) {
            
         
        }
        return false;
    }
}

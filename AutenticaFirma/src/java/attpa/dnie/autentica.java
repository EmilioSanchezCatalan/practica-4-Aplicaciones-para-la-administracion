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
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private byte sign[] = null;
    private String user = null;
    private String dni = null;
    private String date = null;
    private byte key[] = null;

    String signBase64 = "";
    String keyBase64 = "";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Autenticación de Firma</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Autenticación de Firma</h1>");
            out.println("<p>");
            out.println("Datos: "+user+" "+dni+" "+date);
            out.println("</p>");
            out.println("<p>");
            out.println("Firma: "+signBase64);
            out.println("</p>");
            out.println("<p>");
            out.println("Key: "+keyBase64);
            out.println("</p>");
            
            DniDatabase db;
            
            db = new DniDatabase();
            out.println("<p>Leído Base datos "+db.connectToAndQueryDatabase("root", "")+"</p>");
            out.println("<p>FIN</p>");
            

            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        user = request.getParameter("user");
        dni = request.getParameter("dni");
        date = request.getParameter("date");
        
        signBase64 = request.getParameter("signature");
        keyBase64 = request.getParameter("key");

        
        sign = DatatypeConverter.parseBase64Binary(signBase64);
        key = DatatypeConverter.parseBase64Binary(keyBase64);

        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        user = request.getParameter("user");
        dni = request.getParameter("dni");
        date = request.getParameter("date");
        
        signBase64 = request.getParameter("signature");
        keyBase64 = request.getParameter("key");

        
        sign = DatatypeConverter.parseBase64Binary(signBase64);
        key = DatatypeConverter.parseBase64Binary(keyBase64);

        
        processRequest(request, response);
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

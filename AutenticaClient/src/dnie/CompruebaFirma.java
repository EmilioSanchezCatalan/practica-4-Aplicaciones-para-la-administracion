/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnie;

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

/**
 *
 * @author Juan Carlos
 */
public class CompruebaFirma {

    /**
     * Constructor por defecto
     *
     */
    public CompruebaFirma() {
    }

    /**
     * Comprueba la validez de la firma aportada en signRead con los datos en
     * claro y la clave pública usada
     *
     * @param signRead firma RSA + SHA-1
     * @param datos Datos firmados
     * @param keyRead Clave púlica RSA
     * @return Verdadero si la firma es correcta, falso en otro caso.
     */
    public boolean compruebaFirma(byte[] signRead, String datos, byte[] keyRead) {

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
            Logger.getLogger(FirmarDatos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(FirmarDatos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(FirmarDatos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(FirmarDatos.class.getName()).log(Level.SEVERE, null, ex);

        }
        return false;
    }
}

package dnie;

import java.io.FileOutputStream;
import java.security.*;
import java.util.*;
import javax.smartcardio.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FirmarDatos {
    

    public FirmarDatos(){

    }

    public String firmarDatos(String PIN, String datos) throws Exception {

        byte[] data = null;

        String alias = "";
        boolean found = false;

        Card c = conexionTarjeta();
        if (c == null) {
            throw new Exception("No se ha encontrado ninguna tarjeta");
        }

        c.disconnect(true);

        if (datos != null) {
            data=datos.getBytes();
            System.out.println("Datos a firmar: " + datos);
        } else {
            System.err.println("No hay datos que firmar");
            return null;
        }

        try {

            //Se busca el sistema en el que corre la aplicación para elegir
            //el fichero de configuración de PKCS11
            String configName = "";
            String osName = System.getProperty("os.name").toLowerCase();
            String osArch = System.getProperty("os.arch").toLowerCase();
            if (osName.substring(0, 7).compareTo("windows") == 0) {
                if (osArch.contains("64")) {
                    configName = "dnie_windows64.cfg";//Estamos en un Sistema Windows
                } else {
                    configName = "dnie_windows.cfg";//Estamos en un Sistema Windows
                }
            } else if (osName.substring(0, 5).compareTo("linux") == 0) {
                configName = "dnie_linux.cfg";//Estamos en un Sistema Linux
            } else if (osName.substring(0, 3).compareTo("mac") == 0) {
                configName = "dnie_mac.cfg";//Estamos en un Sistema Mac
            }

            //Se añade el proveedor de seguridad de PKCS11
            Provider p = new sun.security.pkcs11.SunPKCS11(configName);
            Security.addProvider(p);
            
            //Inic iamos el acceso a los certificados almacenados en la tarjeta.
            KeyStore keyStore = KeyStore.getInstance("PKCS11", "SunPKCS11-dnie");
            char[] pin = PIN.toCharArray();

            keyStore.load(null, pin);

            //Obtenemos la clave privada para realizar la firma del documento.
            Enumeration enumeration = keyStore.aliases();
            //java.security.cert.Certificate certificado;
            X509Certificate certificado;
            do {
                alias = enumeration.nextElement().toString();
                if (alias.compareTo("CertAutenticacion") == 0) {
                    found = true;
                } else {
                    found = false;
                }

            } while (enumeration.hasMoreElements() && found == false);

            if (found == true) {
                certificado = (X509Certificate) keyStore.getCertificate(alias);
                Key key = keyStore.getKey(alias, pin);

                System.out.println("Datos a firmar por "+certificado.getSubjectDN());
                if (key instanceof PrivateKey) {

                    boolean verSig;
                    byte[] realSig;

                    byte[] keyE = null;

                    keyE = key.getEncoded();
                    if (keyE != null) {
                        String keyEncoded = new String();
                        System.out.println("Clave " + keyEncoded);

                    }
                    
                    //TODO //////////////////////////////////////////////////////77
                    //Firmamos el reto
                    Signature sig = Signature.getInstance("SHA1withRSA");
                    sig.initSign((PrivateKey) key);

                    //Se firman los datos
                    sig.update(data);
                    
                    //Se guarda en realSig los bytes de la firma
                    realSig = sig.sign();

                    //Validamos la firma del reto con los datos en memoria
                    Signature sigver = Signature.getInstance("SHA1withRSA");
                    sigver.initVerify(certificado.getPublicKey());
                    sigver.update(data);

                    //Se verifica la firma
                    verSig = sigver.verify(realSig);

                    System.out.println("Resultado de la verificación inicial de la firma: " + verSig);

                    //Generamos dos ficheros
                    
                    //TODO: Se guarda los datos firmados
                    FileOutputStream signedFile = new FileOutputStream("firma.sig");
                    signedFile.write(realSig);
                    signedFile.close();

                    //TODO: Se guarda la clave pública
                    FileOutputStream keyfos = new FileOutputStream("public.key");
                    RSAPublicKey rsa = (RSAPublicKey) certificado.getPublicKey();
                    byte encodedKey[] = rsa.getEncoded();

                    String rsakey = rsa.getFormat() + " " + rsa.getAlgorithm() + rsa.toString();
                    System.out.println(rsakey);
                    keyfos.write(encodedKey);
                    keyfos.close();
                }
            }

        } catch (CertificateException e) {
            System.err.println("Caught exception " + e.toString() + ". Compruebe que no ha introducido un pin.");

        } catch (SignatureException e) {
            System.err.println("Caught exception " + e.toString());
        }

        return alias;
    }

    private Card conexionTarjeta() throws Exception {

        Card card = null;
        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = factory.terminals().list();
        //System.out.println("Terminals: " + terminals);

        for (int i = 0; i < terminals.size(); i++) {

            
            // get terminal
            CardTerminal terminal = terminals.get(i);
            // establish a connection with the card
            //System.out.println("Trying connect card");

            try {
                if (terminal.isCardPresent()) {
                    card = terminal.connect("*"); //T=0, T=1 or T=CL(not needed)
                    /*System.out.println("Connected");
                     System.out.println("");*/

                }
            } catch (Exception e) {

                System.out.println("Exception catched: " + e.getMessage());
                card = null;
            }
        }
        return card;
    }

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

}//Fin de la clase Firmar datos

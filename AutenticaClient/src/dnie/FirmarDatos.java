package dnie;

import java.io.FileOutputStream;
import java.security.*;
import java.util.*;
import javax.smartcardio.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
public class FirmarDatos {
    

    public FirmarDatos(){

    }
    /**
     * 
     * @param PIN pin del DNIe
     * @param datos Clave + Fecha
     * @return devuelve el userio y el dni.
     * @throws Exception 
     */
    public String [] firmarDatos(String PIN, String datos) throws Exception {

        byte[] data;
        String [] datosOut = new String[2];
        String alias = "";
        boolean found = false;
        
        // comprueba la conexion con la tarjeta
        Card c = conexionTarjeta();
        if (c == null) {
            throw new Exception("No se ha encontrado ninguna tarjeta");
        }

        c.disconnect(true);

        

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
                found = alias.compareTo("CertAutenticacion") == 0;

            } while (enumeration.hasMoreElements() && found == false);

            if (found == true) {
                certificado = (X509Certificate) keyStore.getCertificate(alias);
                Key key = keyStore.getKey(alias, pin);
                // extraemos el usuario el dni..
                String user = certificado.getSubjectDN().getName().substring(certificado.getSubjectDN().getName().indexOf("GIVENNAME=")+10,
                            certificado.getSubjectDN().getName().indexOf(",",certificado.getSubjectDN().getName().indexOf("GIVENNAME=")))
                            + certificado.getSubjectDN().getName().substring(certificado.getSubjectDN().getName().indexOf("SURNAME=")+8,
                            certificado.getSubjectDN().getName().indexOf(",",certificado.getSubjectDN().getName().indexOf("SURNAME=")));
                String dni = certificado.getSubjectDN().getName().substring(certificado.getSubjectDN().getName().indexOf("SERIALNUMBER=")
                        +13, certificado.getSubjectDN().getName().indexOf(",",certificado.getSubjectDN().getName().indexOf("SERIALNUMBER=")));
                
                // pasamos a minuscula el usuario y el dni;
                user = user.toLowerCase();
                dni = dni.toLowerCase();
                
                // eliminamos los espacios del nombre de usuario.
                if (user.contains(" ")){
                    user = user.replace(" ", "");
                }
                //los añadimos al los datos que va a devolver el metodo.
                datosOut[0] = user;
                datosOut[1] = dni;
                
                //añadimos el usuario y el dni a los datos a firmar.
                datos = user + dni + datos;
                if (datos != null) {
                    data=datos.getBytes();
                } else {
                    System.err.println("No hay datos que firmar");
                    return null;
                }
                if (key instanceof PrivateKey) {
                    
                    boolean verSig;
                    byte[] realSig;

                    byte[] keyE;

                    keyE = key.getEncoded();
                    if (keyE != null) {
                        String keyEncoded = new String();

                    }
                    
                    //Firmamos el resto
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

                    try ( //Generamos dos ficheros
                        FileOutputStream signedFile = new FileOutputStream("firma.sig")) {
                        signedFile.write(realSig);
                    }

                    try (FileOutputStream keyfos = new FileOutputStream("public.key")) {
                        RSAPublicKey rsa = (RSAPublicKey) certificado.getPublicKey();
                        byte encodedKey[] = rsa.getEncoded();
                        
                        String rsakey = rsa.getFormat() + " " + rsa.getAlgorithm() + rsa.toString();
                        keyfos.write(encodedKey);
                        
                    }
                }
            }

        } catch (CertificateException e) {
            System.err.println("Caught exception " + e.toString() + ". Compruebe que no ha introducido un pin.");

        } catch (SignatureException e) {
            System.err.println("Caught exception " + e.toString());
        }
        return datosOut;
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
}//Fin de la clase Firmar datos

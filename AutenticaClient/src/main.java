
import dnie.Autentica;
import dnie.FirmarDatos;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.CardException;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.xml.bind.DatatypeConverter;

/**
 * Programa para realización de firmas con DNIe ATENCIÓN: Para que funcione
 * correctamente se debe tener instalada una versión de java de 32 bits (aunque
 * el SO sea de 64) y la dll de Pkcs11 puede usarse la de 64 bits
 * (C:\Windows\SysWOW64\UsrPkcs11.dll) o la de 32 bits
 * (C:\WINDOWS\system32\UsrPkcs11.dll) La comporbación de la versión del SO
 * depende del Java activo en el proyecto y no del del SO
 *
 * @author toni
 *
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        FileInputStream signIn = null;
        Date fecha = new Date();
        String clave = null;
        String username = null;
        String dni = null;
        String firma = null;
        String clavepublica = null;
        //Sin PIN
        FirmarDatos od = new FirmarDatos();
        Autentica auth = new Autentica();
        Base64.Encoder encoder = Base64.getEncoder();
        /*
         IMPORTANTE: Introducir aquí el PIN del DNIe que se vaya a utilizar
         */
        String PIN = "";
        int salir = 0;
        do {

            JPasswordField passwordField = new JPasswordField();
            passwordField.setEchoChar('*');
            Object[] obj = {"Por favor introduzca su PIN:\n\n", passwordField};
            Object stringArray[] = {"Aceptar", "Cancelar"};
            if (JOptionPane.showOptionDialog(null, obj, "PIN del DNIe",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, stringArray, obj) == JOptionPane.YES_OPTION) {
                PIN = new String(passwordField.getPassword());
            }

            //Se ha introducido un PIN
            if ((PIN != null) && (PIN.length() > 0)) {

                do {
                    //Se pide la cadena a firmar
                    clave = (String) JOptionPane.showInputDialog(
                            null,
                            "Clave",
                            "Firmar",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            null);

                    //If a string was returned, say so.
                    if ((clave != null) && (clave.length() > 0)) {
                        
                        try {
                            String datos [] = od.firmarDatos(PIN, clave);
                            username = datos[0];
                            dni = datos[1];
                            
                        } catch (Exception ex) {
                            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    } else {
                        //No se han indroducido datos.
                        salir = JOptionPane.showConfirmDialog(
                                null,
                                "No han introducido datos",
                                "¿Desea salir?",
                                JOptionPane.YES_NO_OPTION);
                    }
                } while (salir != 0);
            } else {
                //No se ha indroducido el PIN.
                salir = JOptionPane.showConfirmDialog(
                        null,
                        "No ha introducido su PIN",
                        "¿Desea salir?",
                        JOptionPane.YES_NO_OPTION);
            }

        } while (salir != 0);

        try {
            
            String datosf = username + dni + clave;
            
            //Se lee la firma de fichero
            signIn = new FileInputStream("firma.sig");
            byte signRead[] = new byte[signIn.available()];
            signIn.read(signRead);
            signIn.close();
            firma = DatatypeConverter.printBase64Binary(signRead);
            firma = firma.replace("+", "%2B");
            //Se lee la clave pública
            FileInputStream keyIn = new FileInputStream("public.key");
            byte keyRead[] = new byte[keyIn.available()];
            keyIn.read(keyRead);
            keyIn.close();
            clavepublica = DatatypeConverter.printBase64Binary(keyRead);
            clavepublica = clavepublica.replace("+", "%2B");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(auth.enviarCredencialesPost("http://localhost:8080/AutenticaFirma/autentica", username, dni, fecha.toString() , firma, clavepublica));
    }
}

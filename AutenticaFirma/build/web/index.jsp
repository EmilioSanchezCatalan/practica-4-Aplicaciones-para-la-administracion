<%-- 
    Document   : index
    Created on : 10-abr-2014, 11:23:57
    Author     : Juan Carlos
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Autenticaci&oacute;n con DNIe</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width">
        <link rel="stylesheet" type="text/css" href="css/style.css">
    </head>

    <body>
        <div id='banner'>
            <h2>Pr&aacute;ctica 4. Implementaci&oacute;n de un servicio b&aacute;sico de autenticaci&oacute;n basado en datos firmados con DNIe</h2>
            <h3>Autenticaci&oacute;n con datos p&uacute;blicos del DNIe</h3>
        </div>
        <div id='main'>
            <div id="formright">
                <h3>Autenticar con GET</h3>
                <table>
                    <form action="autentica" method="get">
                        <tr><td><label for="user">Usuario:</label></td><td><input type="text" name="user"/></td>
                        <tr><td><label for="dni">DNI:</label></td><td><input type="text" name="dni"/></td>
                        <tr><td><label for="sign">Firma:</label></td><td><input type="text" name="sign"/></td>
                        <tr><td><label for="key">Clave p&uacute;blica:</label></td><td><input type="text" name="key"/></td>
                        <tr><td><input type="submit" value="Enviar"/></td>
                    </form>
                </table>
            </div>
            <div id="formright">
                <h3>Autenticar con POST</h3>
                <table>
                    <form action="autentica" method="post">
                        <tr><td><label for="user">Usuario:</label></td><td><input type="text" name="user"/></td>
                        <tr><td><label for="dni">DNI:</label></td><td><input type="text" name="dni"/></td>
                        <tr><td><label for="sign">Firma:</label></td><td><input type="text" name="sign"/></td>
                        <tr><td><label for="key">Clave p&uacute;blica:</label></td><td><input type="text" name="key"/></td>
                        <tr><td><input type="submit" value="Enviar"/></td>
                    </form>
                </table>
            </div>
        </div>

        <div id="foot">
            <h2>Aplicaciones Telemáticas para la Administración</h2>
            <p>Grado en Ingenier&iacute;a Telem&aacute;tica y Grado en Ingenier&iacute;a de Tecnolog&iacute;as de Telecomunicaci&oacute;n</p>
            <p>DEPARTAMENTO DE INGENIERÍA DE TELECOMUNICACIÓN</p>


        </div>
    </body>
</html>

package siac.adaptador;

import java.io.IOException;
import java.io.PrintWriter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

import javax.servlet.*;
import javax.servlet.http.*;

public class AdaptadorSIACServlet extends HttpServlet {

    private static final String CONTENT_TYPE =
        "text/html; charset=windows-1252";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException,
                                                            IOException {

        String datos = request.getParameter("datos");

        // Validar MAC
        String macRecibido = GetValue(datos, "<mac>", "</mac>");

        String datosConexion =
            GetValue(datos, "<datosConexion>", "</datosConexion>");
        String datosOperacion =
            GetValue(datos, "<datosOperacion>", "</datosOperacion>");

        datosConexion = "<datosConexion>" + datosConexion + "</datosConexion>";
        datosOperacion =
                "<datosOperacion>" + datosOperacion + "</datosOperacion>";

        String sistemaOrigen =
            GetValue(datos, "<sistemaOrigen>", "</sistemaOrigen>");
        String sistemaDestino =
            GetValue(datos, "<sistemaDestino>", "</sistemaDestino>");

        Boolean validarMAC = ConsultarBD.DebeValidar(sistemaOrigen, "", "MAC");

        if (validarMAC) {
            Resultado rpta = Seguridad.ValidarMAC(macRecibido, datosConexion + datosOperacion);
            if (rpta.getCodRpta() != 0) {
                MostrarMensaje(response,rpta.getMsgRpta());
                return;
            }
        }

        // Validar TOKEN
        String tokenRecibido = GetValue(datos, "<token>", "</token>");

        Boolean validarToken =
            ConsultarBD.DebeValidar(sistemaOrigen, "", "TOKEN");

        if (validarToken) {
            
            Resultado rpta = Seguridad.ValidarToken(tokenRecibido, Parametro.GetParametro("LLAVEENC_VITAL"),sistemaOrigen);

            if (rpta.getCodRpta() != 0) {
                MostrarMensaje(response,rpta.getMsgRpta());
                return;
            }
        }
        
        // Obtener usaurio destino
        String usuarioOrigen =
            GetValue(datos, "<aliasUsuarioOrigen>", "</aliasUsuarioOrigen>");
        String usuarioDestino =
            ConsultarBD.DarUsuarioDestino(usuarioOrigen, sistemaOrigen,
                                          sistemaDestino);

        if (usuarioDestino.startsWith("ERROR")) {
            MostrarMensaje(response, "USUARIO INCORRECTO:" + usuarioDestino);
            return;
        }

        // Hacer redirect a adaptador de sistema destino
        String autoridadAmbiental =
            GetValue(datos, "<autoridadAmbiental>", "</autoridadAmbiental>");
        String codigoOperacion =
            GetValue(datos, "<codigoOperacion>", "</codigoOperacion>");

        String datosConexionRedirect =
            "<aliasUsuarioOrigen>&aliasUsuarioOrigen</aliasUsuarioOrigen>" +
            "<aliasUsuarioDestino>&aliasUsuarioDestino</aliasUsuarioDestino>" +
            "<autoridadAmbiental>&autoridadAmbiental</autoridadAmbiental>" +
            "<sistemaOrigen>&sistemaOrigen</sistemaOrigen>" +
            "<sistemaDestino>&sistemaDestino</sistemaDestino>" +
            "<codigoOperacion>&codigoOperacion</codigoOperacion>";

        datosConexionRedirect =
                datosConexionRedirect.replaceAll("&aliasUsuarioOrigen",
                                                 usuarioOrigen);
        datosConexionRedirect =
                datosConexionRedirect.replaceAll("&aliasUsuarioDestino",
                                                 usuarioDestino);
        datosConexionRedirect =
                datosConexionRedirect.replaceAll("&autoridadAmbiental",
                                                 autoridadAmbiental);
        datosConexionRedirect =
                datosConexionRedirect.replaceAll("&sistemaOrigen",
                                                 sistemaOrigen);
        datosConexionRedirect =
                datosConexionRedirect.replaceAll("&sistemaDestino",
                                                 sistemaDestino);
        datosConexionRedirect =
                datosConexionRedirect.replaceAll("&codigoOperacion",
                                                 codigoOperacion);

        datosConexionRedirect =
                "<datosConexion>" + datosConexionRedirect + "</datosConexion>";

        String datosOperacionRedirect =
            GetValue(datos, "<datosOperacion>", "</datosOperacion>");
        datosOperacionRedirect =
                "<datosOperacion>" + datosOperacion + "</datosOperacion>";

        String dataPostRedirect =
            "<root> " + "<datosSeguridad>" + "<token>&token</token>" +
            "<mac>&mac</mac>" + "</datosSeguridad>" + datosConexionRedirect +
            datosOperacion + "</root>";

        DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaSistemaStr = formatoFecha.format(new Date());

        String tokenRedirect =
            Seguridad.Encriptar("SIAC " + fechaSistemaStr, Parametro.GetParametro("LLAVEENC_SIAC"));
        String macRedirect =
            Seguridad.getMD5(datosConexionRedirect + datosOperacion);

        dataPostRedirect =
                dataPostRedirect.replaceAll("&token", tokenRedirect);
        dataPostRedirect = dataPostRedirect.replaceAll("&mac", macRedirect);

        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<body onload='document.form.submit()';>");

        String urlSistemaDestino =
            ConsultarBD.DarUrlSistema(sistemaDestino, autoridadAmbiental);

        if (urlSistemaDestino.startsWith("ERROR")) {
            MostrarMensaje(response, "ERROR:" + urlSistemaDestino);
            return;
        }

        out.println("<form name='form' action='" + urlSistemaDestino + "' method='post'>");
        out.println("<p>Por favor dar click en Continuar:" + ":</p>");
        out.println("<input type='hidden' name='datos' value='" + dataPostRedirect + "' />");
        out.println(" <input id='Submit1' type='submit' value='Continuar' /> ");
        out.println("</body></html>");

        out.close();
    }


    public void MostrarMensaje(HttpServletResponse response,
                               String mensaje) throws ServletException,
                                                      IOException {
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>Adaptador SIAC Servlet</title></head>");
        out.println("<body>");
        out.println("<p>" + mensaje + "</p>");
        out.println("</body></html>");
        out.close();
    }


    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException,
                                                           IOException {
        String pruebaBD = ConsultarBD.ProbarBD();
        
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>Adaptador SIAC Servlet</title></head>");
        out.println("<body>");
        out.println("<p>Version del adaptador SIAC V 2018.04.11</p>");
        out.println("<p>" + pruebaBD + "</p>");
        out.println("</body></html>");
        out.close();
    }

    public String GetValue(String cadena, String sTextoBuscado,
                           String sTextoBuscado1) {

        while (cadena.indexOf(sTextoBuscado) > -1) {
            cadena =
                    cadena.substring(cadena.indexOf(sTextoBuscado) + sTextoBuscado.length(),
                                     cadena.length());
        }
        while (cadena.indexOf(sTextoBuscado1) > -1) {
            cadena = cadena.substring(0, cadena.indexOf(sTextoBuscado1));
        }
        return cadena;
    }

}

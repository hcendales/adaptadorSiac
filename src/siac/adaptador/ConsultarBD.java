package siac.adaptador;

import java.sql.CallableStatement;
import java.sql.Connection;

import java.sql.SQLException;
import java.sql.Types;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.sql.DataSource;

public class ConsultarBD {
    public ConsultarBD() {
        super();
    }

    public static String DarUsuarioDestino(String usuarioOrigen,
                                           String sistemaOrigen,
                                           String sistemaDestino) {
        Context ctx = null;
        Connection conn = null;
        String usuarioDestino = "";

        try {
            ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("jdbc/SiacDS");
            conn = ds.getConnection();

            CallableStatement cstmt =
                conn.prepareCall("{call PK_ADM_USUARIO.Pr_DarUsuarioDestino(?,?,?,?,?,?)}");
            cstmt.setString("p_usuarioOrigen", usuarioOrigen);
            cstmt.setString("p_sistemaOrigen", sistemaOrigen);
            cstmt.setString("p_sistemaDestino", sistemaDestino);

            cstmt.registerOutParameter("p_codError", Types.INTEGER);
            cstmt.registerOutParameter("p_msjError", Types.VARCHAR);
            cstmt.registerOutParameter("p_usuarioDestino", Types.VARCHAR);

            cstmt.execute();

            if (cstmt.getInt("p_codError") != 0)
                throw new Exception("Se genero error " +
                                    cstmt.getString("p_msjError"));


            usuarioDestino = cstmt.getString("p_usuarioDestino");

        } catch (Exception e) {
            usuarioDestino =  "ERROR " + e.getMessage();

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                }
            }
        }
        
        return usuarioDestino;

    }

    public static String DarUrlSistema(String sistema, String autoridad) {
        
        Context ctx = null;
        Connection conn = null;
        String urlSistema = "";

        try {
            ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("jdbc/SiacDS");
            conn = ds.getConnection();

            CallableStatement cstmt =
                conn.prepareCall("{call PK_ADM_PARAMETRO.Pr_DarParametroSistema(?,?,?,?,?)}");
            
            cstmt.setString("p_sistema", sistema);
            
            String parametro = "Adaptador" + sistema;
            if (sistema.equals("SIRH"))
                parametro += "_" + autoridad;
            
            cstmt.setString("p_parametro", parametro);

            cstmt.registerOutParameter("p_codError", Types.INTEGER);
            cstmt.registerOutParameter("p_msjError", Types.VARCHAR);
            cstmt.registerOutParameter("p_valor", Types.VARCHAR);

            cstmt.execute();

            if (cstmt.getInt("p_codError") != 0)
                throw new Exception("Se genero error " +
                                    cstmt.getString("p_msjError"));


            urlSistema = cstmt.getString("p_valor");

        } catch (Exception e) {
            urlSistema =  "ERROR " + e.getMessage();

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                }
            }
        }
        
        return urlSistema;
        
    }
    
    public static Boolean DebeValidar(String sistema, String autoridad, String parametro) {
        
        Context ctx = null;
        Connection conn = null;
        Boolean validar = true;

        try {
            ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("jdbc/SiacDS");
            conn = ds.getConnection();

            CallableStatement cstmt =
                conn.prepareCall("{call PK_ADM_PARAMETRO.Pr_DebeValidar(?,?,?,?,?)}");
            
            cstmt.setString("p_sistema", sistema);
            
            cstmt.setString("p_parametro", parametro);

            cstmt.registerOutParameter("p_codError", Types.INTEGER);
            cstmt.registerOutParameter("p_msjError", Types.VARCHAR);
            cstmt.registerOutParameter("p_valor", Types.INTEGER);

            cstmt.execute();

            if (cstmt.getInt("p_codError") != 0)
                throw new Exception("Se genero error " +
                                    cstmt.getString("p_msjError"));


            if (cstmt.getInt("p_valor") == 0)
                validar = false;
            
        } catch (Exception e) {

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                }
            }
        }
        
        return validar;
        
    }
    
    public static String DarParametro(String sistema, String parametro) {
        
        Context ctx = null;
        Connection conn = null;
        String rpta = "";

        try {
            ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("jdbc/SiacDS");
            conn = ds.getConnection();

            CallableStatement cstmt =
                conn.prepareCall("{call PK_ADM_PARAMETRO.Pr_DarParametro(?,?,?,?,?)}");
            
            cstmt.setString("p_sistema", sistema);
            
            cstmt.setString("p_parametro", parametro);

            cstmt.registerOutParameter("p_codError", Types.INTEGER);
            cstmt.registerOutParameter("p_msjError", Types.VARCHAR);
            cstmt.registerOutParameter("p_valor", Types.VARCHAR);

            cstmt.execute();

            if (cstmt.getInt("p_codError") != 0)
                throw new Exception("Se genero error " +
                                    cstmt.getString("p_msjError"));

            rpta =cstmt.getString("p_valor");
            
        } catch (Exception e) {

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                }
            }
        }
        
        return rpta;
        
    }
    
    public static String ProbarBD() {
        
        Context ctx = null;
        Connection conn = null;
        String rpta = "";

        try {
            ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("jdbc/SiacDS");
            conn = ds.getConnection();

            CallableStatement cstmt =
                conn.prepareCall("{call PK_ADM_PARAMETRO.Pr_ProbarBD(?,?)}");
            
            cstmt.registerOutParameter("p_codError", Types.INTEGER);
            cstmt.registerOutParameter("p_msjError", Types.VARCHAR);

            cstmt.execute();

            rpta =cstmt.getString("p_msjError");
            
        } catch (Exception e) {
            rpta = "PROBLEMAS CON BD."  + e.getMessage();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                }
            }
        }
        
        return rpta;
        
    }
}

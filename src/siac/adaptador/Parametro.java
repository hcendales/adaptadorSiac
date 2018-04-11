package siac.adaptador;

public class Parametro {
    public Parametro() {
        super();
    }

    private static String LLAVEENC_VITAL = "SIAC1234"; // debe ser de 8 caracteres
    private static String LLAVEENC_SIAC = "1234SIAC"; // debe ser de 8 caracteres

    private static long TIEMPO_VENCIMIENTO = 18000 + 30; // VALOR EN SEGUNDOS (tenemos diferencia de 5 horas con servidor OSB
    
    public static String GetParametro(String nomParametro) {

        if (nomParametro.toUpperCase().equals("LLAVEENC_VITAL") )
            return LLAVEENC_VITAL;

        if (nomParametro.toUpperCase().equals("LLAVEENC_SIAC") )
            return LLAVEENC_SIAC;

        if (nomParametro.toUpperCase().equals("TIEMPO_VENCIMIENTO") )
            return (new Long(TIEMPO_VENCIMIENTO)).toString();

        return null;
    }
    
}


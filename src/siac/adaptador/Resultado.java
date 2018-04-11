package siac.adaptador;

public class Resultado {
    public Resultado() {
        super();
    }
    
    private int codRpta;
    private String msgRpta;

    public void setCodRpta(int codRpta) {
        this.codRpta = codRpta;
    }

    public int getCodRpta() {
        return codRpta;
    }

    public void setMsgRpta(String msgRpta) {
        this.msgRpta = msgRpta;
    }

    public String getMsgRpta() {
        return msgRpta;
    }
}

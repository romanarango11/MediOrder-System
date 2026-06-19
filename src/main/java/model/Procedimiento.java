package model;

public class    Procedimiento {

    private String cups;
    private String cie;
    private String descripcionCups;
    private String descripcionCie;
    private int cantidad;
    private double valor;

    public Procedimiento(
            String cups,
            String cie,
            String descripcionCups,
            String descripcionCie,
            int cantidad,
            double valor) {

        this.cups = cups;
        this.cie = cie;
        this.descripcionCups = descripcionCups;
        this.descripcionCie = descripcionCie;
        this.cantidad = cantidad;
        this.valor = valor;
    }

    public double getSubtotal() {

        return cantidad * valor;
    }

    public String getCups() {
        return cups;
    }

    public String getCie() {
        return cie;
    }

    public String getDescripcionCups() {
        return descripcionCups;
    }

    public String getDescripcionCie() {
        return descripcionCie;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {

        this.valor = valor;
    }
}

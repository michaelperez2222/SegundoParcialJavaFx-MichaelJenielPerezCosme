
public class Vehiculo {

    private String placa;
    private String modelo;
    private String tipo;
    private int velocidadMax;
    private int precio;

    // Constructor de la clase vehiculo
    public Vehiculo(String placa, String modelo, String tipo, int velocidadMax, int precio) {
        this.placa = placa;
        this.modelo = modelo;
        this.tipo = tipo;
        this.velocidadMax = velocidadMax;
        this.precio = precio;
    }

    // Getters y Setters de la clase vehiculo y sus atributos

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getVelocidadMax() {
        return velocidadMax;
    }

    public void setVelocidadMax(int velocidadMax) {
        this.velocidadMax = velocidadMax;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    // SobreEscritura con el metodo toString
    @Override
    public String toString() {
        return "Vehiculo{" +
                "placa='" + placa + '\'' +
                ", modelo='" + modelo + '\'' +
                ", tipo='" + tipo + '\'' +
                ", velocidadMax=" + velocidadMax +
                ", precio=" + precio +
                '}';
    }

}

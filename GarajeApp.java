
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GarajeApp extends Application {

    // Variable principal requerida por especificacion
    public ObservableList<Vehiculo> listaGarajeVC77 = FXCollections.observableArrayList();

    private TextField txtPlaca = new TextField();
    private TextField txtModelo = new TextField();
    private ComboBox<String> cmbTipo = new ComboBox<>();
    private TextField txtVelocidad = new TextField();
    private TextField txtPrecio = new TextField();

    private TableView<Vehiculo> tablaVehiculos = new TableView<>();

    private static final String ARCHIVO_DAT = "garaje.dat";
    private static final String ARCHIVO_BITACORA = "bitacora.txt";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Garaje de Vice City - Crew Management");

        // Configurar ComboBox con los tipos que se permitan
        cmbTipo.getItems().addAll("Deportivo", "Moto", "SUV", "Lancha", "Avioneta");
        cmbTipo.setPromptText("Seleccione tipo");

        // Configurar la tabla y sus columnas
        TableColumn<Vehiculo, String> colPlaca = new TableColumn<>("Placa");
        colPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));

        TableColumn<Vehiculo, String> colModelo = new TableColumn<>("Modelo");
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));

        TableColumn<Vehiculo, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        TableColumn<Vehiculo, Integer> colVelocidad = new TableColumn<>("Velocidad Max (km/h)");
        colVelocidad.setCellValueFactory(new PropertyValueFactory<>("velocidadMax"));

        TableColumn<Vehiculo, Integer> colPrecio = new TableColumn<>("Precio ($)");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        tablaVehiculos.getColumns().addAll(colPlaca, colModelo, colTipo, colVelocidad, colPrecio);
        tablaVehiculos.setItems(listaGarajeVC77);
        tablaVehiculos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Cargar datos serializados al iniciar solo si existen
        cargarPartida();

        // Panel de Formulario
        GridPane gridForm = new GridPane();
        gridForm.setHgap(10);
        gridForm.setVgap(10);
        gridForm.setPadding(new Insets(15));

        gridForm.add(new Label("Placa:"), 0, 0);
        gridForm.add(txtPlaca, 1, 0);

        gridForm.add(new Label("Modelo:"), 0, 1);
        gridForm.add(txtModelo, 1, 1);

        gridForm.add(new Label("Tipo:"), 0, 2);
        gridForm.add(cmbTipo, 1, 2);

        gridForm.add(new Label("Velocidad Max (km/h):"), 0, 3);
        gridForm.add(txtVelocidad, 1, 3);

        gridForm.add(new Label("Precio ($):"), 0, 4);
        gridForm.add(txtPrecio, 1, 4);

        // Botones de accion
        Button btnAgregar = new Button("Agregar");
        Button btnEliminar = new Button("Eliminar seleccionado");
        Button btnLimpiar = new Button("Limpiar formulario");
        Button btnExportarCSV = new Button("Exportar CSV");
        Button btnImportarCSV = new Button("Importar CSV");
        Button btnGuardar = new Button("Guardar partida");

        HBox hboxBotones = new HBox(10, btnAgregar, btnEliminar, btnLimpiar, btnExportarCSV, btnImportarCSV,
                btnGuardar);
        hboxBotones.setPadding(new Insets(10));

        // Acciones de los botones
        btnAgregar.setOnAction(e -> agregarVehiculo());
        btnEliminar.setOnAction(e -> eliminarVehiculo());
        btnLimpiar.setOnAction(e -> limpiarFormulario());
        btnExportarCSV.setOnAction(e -> exportarCSV(primaryStage));
        btnImportarCSV.setOnAction(e -> importarCSV(primaryStage));
        btnGuardar.setOnAction(e -> guardarPartida());

        // Layout Principal
        BorderPane root = new BorderPane();
        root.setTop(gridForm);
        root.setCenter(tablaVehiculos);
        root.setBottom(hboxBotones);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * 
     */
    private void agregarVehiculo() {
        try {
            // Validar campos vacios
            if (txtPlaca.getText().trim().isEmpty() ||
                    txtModelo.getText().trim().isEmpty() ||
                    cmbTipo.getValue() == null ||
                    txtVelocidad.getText().trim().isEmpty() ||
                    txtPrecio.getText().trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de validacion", "Ningun campo puede estar vacio.");
                return;
            }

            String placa = txtPlaca.getText().trim();
            String modelo = txtModelo.getText().trim();
            String tipo = cmbTipo.getValue();

            // Validar formato numerico y rangos
            int velocidad;
            int precio;
            try {
                velocidad = Integer.parseInt(txtVelocidad.getText().trim());
                precio = Integer.parseInt(txtPrecio.getText().trim());
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de formato",
                        "Velocidad maxima y precio deben ser numeros enteros validos.");
                return;
            }

            if (velocidad <= 0) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de validacion",
                        "La velocidad maxima debe ser mayor que 0.");
                return;
            }

            if (precio < 0) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de validacion", "El precio no puede ser negativo.");
                return;
            }

            // Validar placa unica
            for (Vehiculo v : listaGarajeVC77) {
                if (v.getPlaca().equalsIgnoreCase(placa)) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de duplicidad",
                            "Ya existe un vehiculo registrado con la placa: " + placa);
                    return;
                }
            }

            // Crear y agregar vehiculo
            Vehiculo nuevoVehiculo = new Vehiculo(placa, modelo, tipo, velocidad, precio);
            listaGarajeVC77.add(nuevoVehiculo);
            for (Vehiculo v : listaGarajeVC77) {
                PrintWriter writer;
                writer.println(String.join(",",
                        v.getPlaca(),
                        v.getModelo(),
                        v.getTipo(),
                        String.valueOf(v.getVelocidadMax()),
                        String.valueOf(v.getPrecio())));
            }
            mostrarAlerta(Alert.AlertType.INFORMATION, "Exito", "Vehiculos exportados correctamente a CSV.");
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de E/S",
                    "No se pudo exportar el archivo CSV: " + e.getMessage());
        }

        // Registrar en bitácora
        Vehiculo nuevoVehiculo;
        registrarBitacora("AGREGAR", nuevoVehiculo);

        limpiarFormulario();

    }catch(

    Exception e)
    {
        mostrarAlerta(Alert.AlertType.ERROR, "Error inesperado",
                "Ocurrio un error al agregar el vehiculo: " + e.getMessage());
    }

    private void eliminarVehiculo() {
        Vehiculo seleccionado = tablaVehiculos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia",
                    "Por favor seleccione un vehiculo de la tabla para eliminar.");
            return;
        }

        listaGarajeVC77.remove(seleccionado);
        registrarBitacora("ELIMINAR", seleccionado);
    }

    private void limpiarFormulario() {
        txtPlaca.clear();
        txtModelo.clear();
        cmbTipo.setValue(null);
        txtVelocidad.clear();
        txtPrecio.clear();
        tablaVehiculos.getSelectionModel().clearSelection();
    }

    private void exportarCSV(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv"));
        fileChooser.setInitialFileName("garaje.csv");
        File archivo = fileChooser.showSaveDialog(stage);

        if (archivo == null) return;

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(archivo)))) {
            writer.println("placa,modelo,tipo,velocidadMax,precio");
    }

    private void importarCSV(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir archivo CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv"));
        File archivo = fileChooser.showOpenDialog(stage);

        if (archivo == null)
            return;

        int cargados = 0;
        int descartados = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine(); // Leer encabezado
            if (linea == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "El archivo CSV esta vacio.");
                return;
            }

            while ((linea = reader.readLine()) != null) {
                try {
                    String[] partes = linea.split(",");
                    if (partes.length != 5) {
                        descartados++;
                        continue;
                    }

                    String placa = partes[0].trim();
                    String modelo = partes[1].trim();
                    String tipo = partes[2].trim();
                    int velocidad = Integer.parseInt(partes[3].trim());
                    int precio = Integer.parseInt(partes[4].trim());

                    // Validar duplicados en la lista actual antes de importar
                    boolean duplicado = false;
                    for (Vehiculo v : listaGarajeVC77) {
                        if (v.getPlaca().equalsIgnoreCase(placa)) {
                            duplicado = true;
                            break;
                        }
                    }

                    if (!duplicado && velocidad > 0 && precio >= 0) {
                        Vehiculo v = new Vehiculo(placa, modelo, tipo, velocidad, precio);
                        listaGarajeVC77.add(v);
                        cargados++;
                    } else {
                        descartados++;
                    }
                } catch (Exception ex) {
                    descartados++;
                }
            }

            mostrarAlerta(Alert.AlertType.INFORMATION, "Resultado de Importacion",
                    "Vehiculos cargados exitosamente: " + cargados
                            + "\nLineas descartadas (formato invalido o duplicadas): " + descartados);

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de E/S", "No se pudo leer el archivo CSV: " + e.getMessage());
        }
    }

    private void guardarPartida() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_DAT))) {
            oos.writeObject(new java.util.ArrayList<>(listaGarajeVC77));
            mostrarAlerta(Alert.AlertType.INFORMATION, "Partida Guardada",
                    "Los datos del garaje se han guardado correctamente.");
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar la partida: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarPartida() {
        File archivo = new File(ARCHIVO_DAT);
        if (!archivo.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            java.util.List<Vehiculo> vehiculosCargados = (java.util.List<Vehiculo>) ois.readObject();
            listaGarajeVC77.setAll(vehiculosCargados);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Aviso: No se pudo cargar la partida anterior: " + e.getMessage());
        }
    }

    private void registrarBitacora(String accion, Vehiculo v) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String registro = String.format("[%s] %s - %s - %s%n", timestamp, accion, v.getPlaca(), v.getModelo());

        try (FileWriter fw = new FileWriter(ARCHIVO_BITACORA, true)) {
            fw.write(registro);
        } catch (IOException e) {
            System.err.println("Error al escribir en la bitacora: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package lab.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import lab.excepciones.*;
import lab.modelo.*;
import lab.prestamos.Prestamo;
import lab.servicios.*;

public class UI {

    final int X = 750, Y = 650;
    GestorLaboratorio gestor = new GestorLaboratorio();

    final String V_PRINCIPAL = "principal";
    final String V_RESUMEN_PRES = "resumenPrestamos";
    final String V_RESUMEN = "resumen";

    final String REG_PORTATIL = "Portatil";
    final String REG_MULTIMETRO = "Multimetro";
    final String REG_PROYECTOR = "Proyector";

    final String P_PRESTAR = "Prestar";
    final String P_DEVOLVER = "Devolver";

    private CardLayout cardLayout;
    private JPanel cards;

    private JPanel registroTipoPanel;
    private CardLayout registroTipoCard;

    private JPanel prestamoDevPanel;
    private CardLayout prestamoDevCard;

    private JButton registrarEquipoB;
    private JButton prestamoDevolucionB;
    private JButton mantenimientoB;
    private JButton buscarB;
    private JButton listarB;
    private JButton resumenB;
    private JButton pActivosB;

    private JTextField codigoF;
    private JTextField idF;
    private JTextField codigoRegistroF;
    private JTextField ubicacionF;
    private JTextField codigoPrestamoF;
    private JTextField nombrePrestamoF;
    private JTextField cuentaPrestamoF;

    JTextField codigoMantF;
    JTextField motivoF;

    // Para los campos de ingresar item
    private JTextField marcaF, numeroSerieF, almacenamientoF;
    private JTextField rangoF, precisionF, tipoF;
    private JTextField resXF, resYF, lumensF, horasLamparaF;

    private JComboBox<ESTADO> estadoL;
    private JComboBox<String> tipoL;
    private JComboBox<String> tipoRegistroL;

    private JCheckBox calibradoC, analogoC;

    private JRadioButton regCalibracionRB, operativoRB, enviarMantenimientoRB;
    private JRadioButton prestarRB, devolverRB;

    private ButtonGroup prestadoBG;
    private ButtonGroup calibracionBG;

    private JTextArea resumen;
    private JTextArea resumenPrestamos;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JFrame frame;

    public UI() {
        frame = new JFrame("Sistema de Gestión de Laboratorio");
        frame.setSize(X, Y);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        cards.add(ventanaPrincipalPanel(), V_PRINCIPAL);
        cards.add(generarResumenPanel(), V_RESUMEN);
        cards.add(generarPrestamosRPanel(), V_RESUMEN_PRES);

        JLabel titulo = new JLabel("Inventario de Equipos - Laboratorio UNAH", SwingConstants.CENTER);
        titulo.setFont(new Font("Comic Sans MS", Font.BOLD, 22));
        
        frame.add(titulo, BorderLayout.NORTH);
        frame.add(cards, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public JPanel ventanaPrincipalPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new BorderLayout());
        form.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JPanel botonesPanel = new JPanel(new GridLayout(1, 5, 5, 5));

        registrarEquipoB = new JButton("Registrar Equipo");
        prestamoDevolucionB = new JButton("Prestamo/Devolucion");
        mantenimientoB = new JButton("Mantenimiento");
        resumenB = new JButton("Resumen");
        pActivosB = new JButton("Prestamos Activos");

        botonesPanel.add(registrarEquipoB);
        botonesPanel.add(prestamoDevolucionB);
        botonesPanel.add(mantenimientoB);
        botonesPanel.add(resumenB);
        botonesPanel.add(pActivosB);

        form.add(botonesPanel, BorderLayout.SOUTH);

        //PARA LOS DIALOGOS LISTENERS
        registrarEquipoB.addActionListener((ae) -> abrirDialogoRegistrarEquipo());
        prestamoDevolucionB.addActionListener((ae) -> abrirDialogoRealizarPrestamo());
        mantenimientoB.addActionListener((ae) -> abrirDialogoMantenimiento());

        //VENTANAS LISTENERS
        resumenB.addActionListener((ae) -> {
            actualizarResumen();
            cardLayout.show(cards, V_RESUMEN);
        });
        pActivosB.addActionListener((ae) -> {
            actualizarPrestamos();
            cardLayout.show(cards, V_RESUMEN_PRES);
        });

        JPanel buscarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        codigoF = new JTextField(8);
        
        ESTADO[] estadosConTodos = new ESTADO[ESTADO.values().length + 1];
        estadosConTodos[0] = null;

        System.arraycopy(ESTADO.values(), 0, estadosConTodos, 1, ESTADO.values().length);
        
        estadoL = new JComboBox<>(estadosConTodos);
        tipoL = new JComboBox<>(new String[] { "Todos", "Portatil", "Proyector", "Multimetro" });

        buscarPanel.add(new JLabel("Código:"));
        buscarPanel.add(codigoF);
        buscarPanel.add(new JLabel("Estado:"));
        buscarPanel.add(estadoL);
        buscarPanel.add(new JLabel("Tipo:"));
        buscarPanel.add(tipoL);

        buscarB = new JButton("Buscar");
        listarB = new JButton("Listar Todo");

        buscarPanel.add(buscarB);
        buscarPanel.add(listarB);

        form.add(buscarPanel, BorderLayout.NORTH);

        String[] columnas = { "Codigo", "Tipo", "Ubicacion", "Estado", "Resumen Técnico" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        JScrollPane tablaScroll = new JScrollPane(tabla);
        form.add(tablaScroll, BorderLayout.CENTER);

        //FILTRAR Y BUSCAR
        buscarB.addActionListener(ae -> buscarYFiltrar());
        listarB.addActionListener(ae -> {
            codigoF.setText("");
            estadoL.setSelectedIndex(0);
            tipoL.setSelectedIndex(0);
            actualizarTablaInventario(gestor.mostrarInventario());
        });

        actualizarTablaInventario(gestor.mostrarInventario());

        root.add(form, BorderLayout.CENTER);
        return root;
    }

    //METODOS PARA LA TABLA
    private void actualizarTablaInventario(ArrayList<Equipo> lista) {
        modeloTabla.setRowCount(0);
        for (Equipo eq : lista) {
            if (eq != null) {
                Object[] fila = new Object[] {
                    eq.CODIGOINV,
                    eq.nombre,
                    eq.ubicacion,
                    eq.estado,
                    eq.getResumenTecnico()
                };
                modeloTabla.addRow(fila);
            }
        }
    }

    private void buscarYFiltrar() {
        String codBusqueda = codigoF.getText().trim();
        ESTADO estadoSel = (ESTADO) estadoL.getSelectedItem();
        String tipoSel = (String) tipoL.getSelectedItem();

        ArrayList<Equipo> todo = gestor.mostrarInventario();
        ArrayList<Equipo> filtrado = new ArrayList<>();

        for (Equipo eq : todo) {
            boolean coincideCod = codBusqueda.isEmpty() || eq.CODIGOINV.equalsIgnoreCase(codBusqueda);
            boolean coincideEstado = (estadoSel == null) || eq.estado == estadoSel;
            boolean coincideTipo = tipoSel.equals("Todos") || eq.nombre.equalsIgnoreCase(tipoSel);

            if (coincideCod && coincideEstado && coincideTipo) {
                filtrado.add(eq);
            }
        }

        actualizarTablaInventario(filtrado);
    }


    private void abrirDialogoRegistrarEquipo() {
        JDialog dialogo = new JDialog(frame, "Registrar Nuevo Equipo", true);
        dialogo.setSize(520, 420);
        dialogo.setLocationRelativeTo(frame);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new BorderLayout());
        form.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));

        registroTipoCard = new CardLayout();
        registroTipoPanel = new JPanel(registroTipoCard);

        registroTipoPanel.add(generarRegPortatil(), REG_PORTATIL);
        registroTipoPanel.add(generarRegMultimetro(), REG_MULTIMETRO);
        registroTipoPanel.add(generarRegProyector(), REG_PROYECTOR);

        JPanel contenido = new JPanel(new GridLayout(0, 2, 5, 5));
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        idF = new JTextField(10);
        codigoRegistroF = new JTextField(10);
        ubicacionF = new JTextField(10);
        tipoRegistroL = new JComboBox<>(new String[] { "Portatil", "Proyector", "Multimetro" });

        contenido.add(new JLabel("Tipo de Equipo: "));
        contenido.add(tipoRegistroL);
        contenido.add(new JLabel("ID (Numérico): "));
        contenido.add(idF);
        contenido.add(new JLabel("Código Inventario: "));
        contenido.add(codigoRegistroF);
        contenido.add(new JLabel("Ubicación: "));
        contenido.add(ubicacionF);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(contenido, BorderLayout.NORTH);
        centro.add(registroTipoPanel, BorderLayout.CENTER);

        tipoRegistroL.addActionListener(ae -> {
            String op = (String) tipoRegistroL.getSelectedItem();
            registroTipoCard.show(registroTipoPanel, op);
        });

        JPanel botonesPanel = new JPanel();
        JButton guardar = new JButton("Guardar");
        JButton limpiar = new JButton("Limpiar");
        JButton cancelar = new JButton("Cancelar");

        botonesPanel.add(guardar);
        botonesPanel.add(limpiar);
        botonesPanel.add(cancelar);

        cancelar.addActionListener((ae) -> dialogo.dispose());

        limpiar.addActionListener((ae) -> {
            codigoRegistroF.setText("");
            ubicacionF.setText("");
            idF.setText("");
            tipoRegistroL.setSelectedIndex(0);
            marcaF.setText("");
            numeroSerieF.setText("");
            almacenamientoF.setText("");
            rangoF.setText("");
            precisionF.setText("");
            tipoF.setText("");
            calibradoC.setSelected(false);
            analogoC.setSelected(false);
            resXF.setText("");
            resYF.setText("");
            lumensF.setText("");
            horasLamparaF.setText("");
        });

        guardar.addActionListener((ae) -> {
            try {
                int id = Integer.parseInt(idF.getText().trim());
                String cod = codigoRegistroF.getText().trim();
                String ubic = ubicacionF.getText().trim();
                String tipo = (String) tipoRegistroL.getSelectedItem();

                if (cod.isEmpty() || ubic.isEmpty()) {
                    JOptionPane.showMessageDialog(dialogo, "Por favor complete los campos generales.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (gestor.ValidarDato(id) != null || gestor.ValidarDato(cod) != null) {
                    JOptionPane.showMessageDialog(dialogo, "Codigo o ID repetido.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (id <0) {
                    JOptionPane.showMessageDialog(dialogo, "Ingrese valores positivos", "Atencion", JOptionPane.WARNING_MESSAGE);
                    return;
                }


                if (tipo.equals("Portatil")) {
                    String marca = marcaF.getText().trim();
                    int numSerie = Integer.parseInt(numeroSerieF.getText().trim());
                    int alm = Integer.parseInt(almacenamientoF.getText().trim());

                    if (numSerie <0 || alm <0) {
                        JOptionPane.showMessageDialog(dialogo, "Ingrese valores positivos", "Atencion", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    gestor.registrarEquipo(id, cod, ubic, marca, numSerie, alm);
                } else if (tipo.equals("Proyector")) {
                    int x = Integer.parseInt(resXF.getText().trim());
                    int y = Integer.parseInt(resYF.getText().trim());
                    int lu = Integer.parseInt(lumensF.getText().trim());
                    int hLamp = Integer.parseInt(horasLamparaF.getText().trim());

                    if (x <0 || y<0 || lu <0|| hLamp <0) {
                        JOptionPane.showMessageDialog(dialogo, "Ingrese valores positivos", "Atencion", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    gestor.registrarEquipo(id, cod, ubic, x, y, hLamp, lu);
                } else if (tipo.equals("Multimetro")) {
                    String tipoM = tipoF.getText().trim();
                    int rango = Integer.parseInt(rangoF.getText().trim());
                    int prec = Integer.parseInt(precisionF.getText().trim());
                    boolean cal = calibradoC.isSelected();
                    boolean ana = analogoC.isSelected();

                    if (rango <0 || prec <0) {
                        JOptionPane.showMessageDialog(dialogo, "Ingrese valores positivos", "Atencion", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    

                    gestor.registrarEquipo(id, cod, ubic, tipoM, cal, rango, prec, ana);
                }

                JOptionPane.showMessageDialog(dialogo, "Equipo registrado con exito.");
                actualizarTablaInventario(gestor.mostrarInventario());
                dialogo.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo, "Verifique que los campos numéricos contengan valores válidos.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
            } catch (EquipoNotFoundException ex) {
                JOptionPane.showMessageDialog(dialogo, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }catch(ArrayIndexOutOfBoundsException e){
                JOptionPane.showMessageDialog(dialogo, "LIMITE DE OBJETOS ALCANZADO.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        });

        form.add(centro, BorderLayout.CENTER);
        form.add(botonesPanel, BorderLayout.SOUTH);
        root.add(form, BorderLayout.CENTER);

        dialogo.add(root);
        dialogo.setVisible(true);
    }

    //PRESTAMO

    private void abrirDialogoRealizarPrestamo() {
        JDialog dialogo = new JDialog(frame, "Realizar Prestamo / Devolución", true);
        dialogo.setSize(480, 320);
        dialogo.setLocationRelativeTo(frame);

        prestamoDevCard = new CardLayout();
        prestamoDevPanel = new JPanel(prestamoDevCard);
        prestamoDevPanel.add(generarPPrestamo(), P_PRESTAR);
        prestamoDevPanel.add(generarPDevolver(), P_DEVOLVER);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new BorderLayout());
        form.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));

        JPanel contenido = new JPanel(new FlowLayout());
        codigoPrestamoF = new JTextField(10);
        prestadoBG = new ButtonGroup();
        prestarRB = new JRadioButton("Prestar", true);
        devolverRB = new JRadioButton("Devolver");

        JButton aceptar = new JButton("Aceptar");
        JButton cancelar = new JButton("Cancelar");

        prestadoBG.add(prestarRB);
        prestadoBG.add(devolverRB);

        prestarRB.addActionListener(ae -> prestamoDevCard.show(prestamoDevPanel, P_PRESTAR));
        devolverRB.addActionListener(ae -> prestamoDevCard.show(prestamoDevPanel, P_DEVOLVER));

        contenido.add(new JLabel("Código del Equipo: "));
        contenido.add(codigoPrestamoF);
        contenido.add(prestarRB);
        contenido.add(devolverRB);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(contenido, BorderLayout.NORTH);
        centro.add(prestamoDevPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel();
        footer.add(aceptar);
        footer.add(cancelar);

        aceptar.addActionListener(ae -> {
            String cod = codigoPrestamoF.getText().trim();
            if (cod.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "Ingrese el código del equipo.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                if (prestarRB.isSelected()) {
                    long cuenta = Long.parseLong(cuentaPrestamoF.getText().trim());
                    String nombre = nombrePrestamoF.getText().trim();
                    if (nombre.isEmpty()) {
                        JOptionPane.showMessageDialog(dialogo, "Ingrese el nombre del estudiante.", "Atencion", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (cuenta <0) {
                        JOptionPane.showMessageDialog(dialogo, "Ingrese valores positivos", "Atencion", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    gestor.CrearPrestamo(cod, cuenta, nombre);
                    JOptionPane.showMessageDialog(dialogo, "Prestamo registrado exitosamente.");
                } else {
                    gestor.devolverEquipo(cod);
                    JOptionPane.showMessageDialog(dialogo, "Equipo devuelto exitosamente.");
                }
                actualizarTablaInventario(gestor.mostrarInventario());
                dialogo.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo, "El numero de cuenta debe ser numerico.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (EquipoNotFoundException | ReglaPrestamoException | PrestamoActivoException | EstadoOperativoInvalidoException ex) {
                JOptionPane.showMessageDialog(dialogo, ex.getMessage(), "Error en Operacion", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelar.addActionListener(ae -> dialogo.dispose());

        form.add(centro, BorderLayout.CENTER);
        form.add(footer, BorderLayout.SOUTH);
        root.add(form, BorderLayout.CENTER);

        dialogo.add(root);
        dialogo.setVisible(true);
    }


    private void abrirDialogoMantenimiento() {
        JDialog dialogo = new JDialog(frame, "Estado Operativo / Calibración", true);
        dialogo.setSize(480, 320);
        dialogo.setLocationRelativeTo(frame);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new BorderLayout());
        form.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        JPanel contenido = new JPanel(new GridLayout(0, 1, 5, 5));
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        codigoMantF = new JTextField(10);
        motivoF = new JTextField(10);
        calibracionBG = new ButtonGroup();

        enviarMantenimientoRB = new JRadioButton("Enviar a Mantenimiento", true);
        operativoRB = new JRadioButton("Marcar Operativo");
        regCalibracionRB = new JRadioButton("Registrar Calibración");

        JButton aceptar = new JButton("Aceptar");
        JButton cancelar = new JButton("Cancelar");

        calibracionBG.add(enviarMantenimientoRB);
        calibracionBG.add(operativoRB);
        calibracionBG.add(regCalibracionRB);

        JPanel codPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
        codPan.add(new JLabel("Código del Equipo: "));
        codPan.add(codigoMantF);

        contenido.add(codPan);
        contenido.add(enviarMantenimientoRB);
        contenido.add(operativoRB);
        contenido.add(regCalibracionRB);

        JPanel panelMotivo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelMotivo.add(new JLabel("Motivo: "));
        panelMotivo.add(motivoF);

        ActionListener chequeo = ae -> panelMotivo.setVisible(enviarMantenimientoRB.isSelected());

        enviarMantenimientoRB.addActionListener(chequeo);
        operativoRB.addActionListener(chequeo);
        regCalibracionRB.addActionListener(chequeo);

        contenido.add(panelMotivo);

        JPanel footer = new JPanel();
        footer.add(aceptar);
        footer.add(cancelar);

        aceptar.addActionListener(ae -> {
            String cod = codigoMantF.getText().trim();
            if (cod.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "Ingrese el codigo del equipo.", "Atencion", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                if (enviarMantenimientoRB.isSelected()) {
                    gestor.cambiarEquipo(cod, ESTADO.EN_MANTENIMIENTO);
                    JOptionPane.showMessageDialog(dialogo, "Equipo enviado a mantenimiento.");
                } else if (operativoRB.isSelected()) {
                    gestor.cambiarEquipo(cod, ESTADO.DISPONIBLE);
                    JOptionPane.showMessageDialog(dialogo, "Equipo marcado como operativo.");
                } else if (regCalibracionRB.isSelected()) {
                    gestor.registrarCalibracion(cod);
                    JOptionPane.showMessageDialog(dialogo, "Calibracion registrada con exito.");
                }
                actualizarTablaInventario(gestor.mostrarInventario());
                dialogo.dispose();

            } catch (EquipoNotFoundException | EstadoOperativoInvalidoException ex) {
                JOptionPane.showMessageDialog(dialogo, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelar.addActionListener(ae -> dialogo.dispose());

        form.add(contenido, BorderLayout.CENTER);
        form.add(footer, BorderLayout.SOUTH);
        root.add(form, BorderLayout.CENTER);

        dialogo.add(root);
        dialogo.setVisible(true);
    }


    public JPanel generarRegPortatil() {
        JPanel root = new JPanel(new GridLayout(0, 2, 5, 5));
        root.setBorder(BorderFactory.createTitledBorder("Datos Portatil"));
        marcaF = new JTextField(10);
        almacenamientoF = new JTextField(10);
        numeroSerieF = new JTextField(10);

        root.add(new JLabel("Marca: "));
        root.add(marcaF);
        root.add(new JLabel("Almacenamiento (GB): "));
        root.add(almacenamientoF);
        root.add(new JLabel("Numero de Serie: "));
        root.add(numeroSerieF);
        return root;
    }

    public JPanel generarRegMultimetro() {
        JPanel root = new JPanel(new GridLayout(0, 2, 5, 5));
        root.setBorder(BorderFactory.createTitledBorder("Datos Multimetro"));
        rangoF = new JTextField(10);
        precisionF = new JTextField(10);
        tipoF = new JTextField(10);

        root.add(new JLabel("Rango Voltaje: "));
        root.add(rangoF);
        root.add(new JLabel("Precision: "));
        root.add(precisionF);
        root.add(new JLabel("Tipo Multimetro: "));
        root.add(tipoF);

        calibradoC = new JCheckBox("Calibrado");
        analogoC = new JCheckBox("Analogo");

        root.add(calibradoC);
        root.add(analogoC);
        return root;
    }

    public JPanel generarRegProyector() {
        JPanel root = new JPanel(new GridLayout(0, 2, 5, 5));
        root.setBorder(BorderFactory.createTitledBorder("Datos Proyector"));
        resXF = new JTextField(5);
        resYF = new JTextField(5);
        lumensF = new JTextField(5);
        horasLamparaF = new JTextField(10);

        JPanel resPan = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        resPan.add(resXF);
        resPan.add(new JLabel("x"));
        resPan.add(resYF);

        root.add(new JLabel("Resolucion: "));
        root.add(resPan);
        root.add(new JLabel("Lumens: "));
        root.add(lumensF);
        root.add(new JLabel("Horas Lámpara: "));
        root.add(horasLamparaF);
        return root;
    }

    public JPanel generarPPrestamo() {
        JPanel root = new JPanel(new GridLayout(2, 2, 5, 5));
        root.setBorder(BorderFactory.createTitledBorder("Datos del Estudiante"));
        nombrePrestamoF = new JTextField(15);
        cuentaPrestamoF = new JTextField(15);

        root.add(new JLabel("Nombre: "));
        root.add(nombrePrestamoF);
        root.add(new JLabel("Numero de Cuenta: "));
        root.add(cuentaPrestamoF);
        return root;
    }

    public JPanel generarPDevolver() {
        JPanel root = new JPanel();
        root.setBorder(BorderFactory.createTitledBorder("Devolución"));
        root.add(new JLabel("Presione Aceptar para registrar la devolución del equipo."));
        return root;
    }


    public JPanel generarResumenPanel() {
        JPanel root = new JPanel(new BorderLayout());
        JLabel t = new JLabel("Resumen del Laboratorio", SwingConstants.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 16));
        root.add(t, BorderLayout.NORTH);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new BorderLayout());
        form.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));

        resumen = new JTextArea(15, 20);
        resumen.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resumen.setEditable(false);

        JPanel pan = new JPanel();
        JButton regresar = new JButton("Regresar");
        pan.add(regresar);

        regresar.addActionListener(ae -> cardLayout.show(cards, V_PRINCIPAL));

        form.add(new JScrollPane(resumen), BorderLayout.CENTER);
        root.add(form, BorderLayout.CENTER);
        root.add(pan, BorderLayout.SOUTH);
        return root;
    }

    public JPanel generarPrestamosRPanel() {
        JPanel root = new JPanel(new BorderLayout());
        JLabel t = new JLabel("Préstamos Activos", SwingConstants.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 16));
        root.add(t, BorderLayout.NORTH);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new BorderLayout());
        form.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        resumenPrestamos = new JTextArea(15, 20);
        resumenPrestamos.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resumenPrestamos.setEditable(false);

        JPanel pan = new JPanel();
        JButton regresar = new JButton("Regresar");
        pan.add(regresar);

        regresar.addActionListener(ae -> cardLayout.show(cards, V_PRINCIPAL));

        form.add(new JScrollPane(resumenPrestamos), BorderLayout.CENTER);
        root.add(form, BorderLayout.CENTER);
        root.add(pan, BorderLayout.SOUTH);
        return root;
    }

    public void actualizarPrestamos() {
        StringBuilder DATA = new StringBuilder();
        ArrayList<Prestamo> lista = gestor.MostrarPrestamosActivos();
        if (!lista.isEmpty()) {
            for (Prestamo eq : lista) {
                DATA.append(eq.mostrarDatos()).append("\n-------------------------------------\n");
            }
        } else {
            DATA.append("No hay préstamos activos actualmente.");
        }
        resumenPrestamos.setText(DATA.toString());
    }

    public void actualizarResumen() {
        resumen.setText(gestor.getResumenLab());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UI());
    }
}
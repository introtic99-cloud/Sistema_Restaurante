package gui;

import decorator.ExtraQueso;
import decorator.ExtraTocino;
import facade.RestauranteFacade;
import model.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Sistema de gestión para pollería (JFrame - Swing).
 * Funciona completamente en memoria, sin conexión a base de datos.
 *
 * @author USER
 */
public class PolleriaApp extends JFrame {

    private final RestauranteFacade restaurante = new RestauranteFacade();

    // --- Estado del pedido en construcción ---
    private Pedido pedidoActual;
    private Plato platoSeleccionadoParaAgregar;
    private static final double IGV = 0.18;

    // --- Navegación superior (icono + CardLayout) ---
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private final java.util.List<Theme.NavButton> navButtons = new java.util.ArrayList<>();

    // --- Componentes compartidos entre métodos ---
    private JComboBox<Mesa> comboMesas;
    private JComboBox<Cliente> comboClientes;
    private JComboBox<Mesero> comboMeseros;
    private JLabel lblPedidoActivo;
    private DefaultTableModel modeloCarta;
    private JTable tablaCarta;
    private final java.util.List<Plato> platosMostrados = new java.util.ArrayList<>();
    private String filtroCategoriaCarta = "TODOS";
    private JTextField campoBusquedaCarta;
    private final java.util.List<AbstractButton> botonesCategoriaCarta = new java.util.ArrayList<>();
    private JPanel panelImagenPlatoSel;
    private JLabel lblNombrePlatoSel, lblCategoriaPlatoSel, lblPrecioPlatoSel, lblDescripcionPlatoSel;
    private DefaultTableModel modeloDetalle;
    private JTable tablaDetalle;
    private JLabel lblTotalPedido;
    private JLabel lblSubtotalDesglose, lblIgvDesglose, lblTotalDesglose;
    private JCheckBox chkExtraQueso;
    private JCheckBox chkExtraTocino;
    private JCheckBox chkAjiExtra;
    private JSpinner spinnerCantidad;

    private JPanel panelGridMesas;
    private Integer mesaSeleccionada;
    private final java.util.Map<Integer, JPanel> tarjetaPorMesa = new java.util.HashMap<>();
    private final java.util.Map<Integer, JLabel> lblTiempoPorMesa = new java.util.HashMap<>();
    private JLabel lblStatLibres, lblStatOcupadas, lblStatCapacidad, lblStatClientes;
    private javax.swing.Timer timerMesas;

    private DefaultTableModel modeloPedidos;
    private JTable tablaPedidos;
    private JLabel lblStatPedidosActivos, lblStatPreparacion, lblStatListos, lblStatPendientes;
    private javax.swing.Timer timerPedidosActivos;

    private JComboBox<Pedido> comboPedidosPago;
    private JComboBox<String> comboMetodoPago;
    private JTextArea areaFactura;
    private JPanel panelCentroPago;

    private DefaultTableModel modeloReservas;
    private JTable tablaReservas;
    private JComboBox<Cliente> comboClientesReserva;
    private JComboBox<Mesa> comboMesasReserva;
    private JSpinner spinnerFechaReserva;
    private JSpinner spinnerPersonasReserva;
    private JPanel panelCentroReservas;

    private DefaultTableModel modeloInventario;
    private JTable tablaInventario;

    private DefaultTableModel modeloCartaAdmin;
    private JTable tablaCartaAdmin;
    private JLabel lblHistorialMemento;

    private DefaultTableModel modeloPersonal;
    private JTable tablaPersonal;
    private JPanel panelCentroPersonal;
    private JTextArea areaFuncionPersonal;

    private JPanel panelCentroReporte;
    private JLabel lblRepFecha;
    private JLabel lblRepTotalPedidos, lblRepEntregados, lblRepEnCurso, lblRepCancelados, lblRepVentas;
    private DefaultTableModel modeloReporte;
    private JTable tablaReporte;
    private JLabel lblRepVentasFooter;

    public PolleriaApp() {
        setTitle("El Sabroso Pollo - Sistema de Gestión de Pollería");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1680, 900);
        setMinimumSize(new Dimension(1560, 780));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        initHeader();
        initTabs();
    }

    // ======================================================================
    // CABECERA
    // ======================================================================
    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(Theme.PRIMARY);
        header.setBorder(new EmptyBorder(8, 20, 8, 20));
        header.setPreferredSize(new Dimension(1680, 80));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        izquierda.setOpaque(false);
        JLabel logo = Theme.logoRedondo(46);
        izquierda.add(logo);
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("EL SABROSO POLLO");
        titulo.setFont(Theme.FONT_TITLE);
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitulo = new JLabel("Sistema de Gestión de Pollería");
        subtitulo.setFont(Theme.FONT_SUBHEADING);
        subtitulo.setForeground(new Color(255, 224, 200));
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(subtitulo);
        izquierda.add(textos);
        header.add(izquierda, BorderLayout.WEST);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        nav.setOpaque(false);
        String[][] items = {
            {"mesas", "🍽️", "Mesas"},
            {"pedido", "🛒", "Nuevo Pedido"},
            {"activos", "📋", "Pedidos Activos"},
            {"pago", "🧾", "Pagar / Facturar"},
            {"reservas", "📅", "Reservas"},
            {"inventario", "📦", "Inventario"},
            {"carta", "📖", "Carta (Admin)"},
            {"personal", "👥", "Personal"},
            {"reporte", "📊", "Reporte"},
        };
        for (String[] it : items) {
            Theme.NavButton nb = Theme.navButton(it[1], it[2]);
            String key = it[0];
            nb.addActionListener(e -> mostrarPanel(key));
            nav.add(nb);
            navButtons.add(nb);
        }
        header.add(nav, BorderLayout.CENTER);

        JPanel derecha = new JPanel();
        derecha.setOpaque(false);
        derecha.setLayout(new BoxLayout(derecha, BoxLayout.Y_AXIS));
        JCheckBox chkAdmin = new JCheckBox("Modo Administrador");
        chkAdmin.setOpaque(false);
        chkAdmin.setForeground(Color.WHITE);
        chkAdmin.setFont(Theme.FONT_BUTTON);
        chkAdmin.setFocusPainted(false);
        chkAdmin.setAlignmentX(Component.RIGHT_ALIGNMENT);
        chkAdmin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chkAdmin.addActionListener(e -> restaurante.setModoAdministrador(chkAdmin.isSelected()));
        derecha.add(chkAdmin);
        JLabel badgeLocal = new JLabel("● Modo local (sin base de datos)");
        badgeLocal.setFont(Theme.FONT_SMALL);
        badgeLocal.setForeground(new Color(255, 224, 200));
        badgeLocal.setAlignmentX(Component.RIGHT_ALIGNMENT);
        derecha.add(badgeLocal);
        header.add(derecha, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    private void mostrarPanel(String key) {
        cardLayout.show(contentPanel, key);
        for (Theme.NavButton nb : navButtons) nb.setSelected(false);
        int idx = -1;
        String[] keys = {"mesas", "pedido", "activos", "pago", "reservas", "inventario", "carta", "personal", "reporte"};
        for (int i = 0; i < keys.length; i++) if (keys[i].equals(key)) idx = i;
        if (idx >= 0) navButtons.get(idx).setSelected(true);
        refrescarTodo();
    }

    private void initTabs() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Theme.BG);
        contentPanel.add(crearPanelMesas(), "mesas");
        contentPanel.add(crearPanelPedido(), "pedido");
        contentPanel.add(crearPanelPedidosActivos(), "activos");
        contentPanel.add(crearPanelPago(), "pago");
        contentPanel.add(crearPanelReservas(), "reservas");
        contentPanel.add(crearPanelInventario(), "inventario");
        contentPanel.add(crearPanelCarta(), "carta");
        contentPanel.add(crearPanelPersonal(), "personal");
        contentPanel.add(crearPanelReporte(), "reporte");
        add(contentPanel, BorderLayout.CENTER);
        mostrarPanel("pedido");
    }

    private void refrescarTodo() {
        refrescarMesas();
        refrescarPedidosActivos();
        refrescarComboPago();
        refrescarReservas();
        refrescarInventario();
        refrescarCarta();
    }

    // ======================================================================
    // PANEL: MESAS
    // ======================================================================
    private JPanel crearPanelMesas() {
        JPanel panel = fondoPanel();
        JPanel encabezado = panelEncabezado("Estado de mesas del local",
                "Consulta la disponibilidad y libera u ocupa mesas manualmente.");
        panel.add(encabezado, BorderLayout.NORTH);

        panelGridMesas = new JPanel(new GridLayout(0, 4, 18, 18));
        panelGridMesas.setOpaque(false);
        panelGridMesas.setBorder(new EmptyBorder(4, 2, 4, 2));

        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setOpaque(false);
        envoltorio.add(panelGridMesas, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(envoltorio);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel pie = new JPanel();
        pie.setOpaque(false);
        pie.setLayout(new BoxLayout(pie, BoxLayout.Y_AXIS));

        JPanel filaStats = new JPanel(new BorderLayout());
        filaStats.setOpaque(false);
        JPanel statsIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statsIzq.setOpaque(false);
        lblStatLibres = new JLabel();
        lblStatOcupadas = new JLabel();
        lblStatCapacidad = new JLabel();
        lblStatClientes = new JLabel();
        statsIzq.add(tarjetaEstadistica("🟢", lblStatLibres, "Mesas Libres", Theme.SUCCESS, Theme.SUCCESS_BG));
        statsIzq.add(tarjetaEstadistica("🔴", lblStatOcupadas, "Mesas Ocupadas", Theme.DANGER, Theme.DANGER_BG));
        statsIzq.add(tarjetaEstadistica("👥", lblStatCapacidad, "Capacidad Total", Theme.INFO, Theme.INFO_BG));
        statsIzq.add(tarjetaEstadistica("👪", lblStatClientes, "Clientes Actuales", Theme.WARNING, Theme.WARNING_BG));
        filaStats.add(statsIzq, BorderLayout.WEST);
        filaStats.add(crearLeyendaMesas(), BorderLayout.EAST);
        pie.add(filaStats);
        pie.add(Box.createVerticalStrut(14));

        JPanel botones = barraBotones();
        JButton btnToggle = Theme.primaryButton("Liberar / Ocupar mesa seleccionada");
        btnToggle.addActionListener(e -> {
            if (mesaSeleccionada == null) { mensaje("Selecciona una mesa primero."); return; }
            for (Mesa m : restaurante.getDatosService().getMesas()) {
                if (m.getNumero() == mesaSeleccionada) { m.setOcupada(!m.isOcupada()); }
            }
            refrescarMesas();
        });
        botones.add(btnToggle);
        JButton btnRefrescar = Theme.outlineButton("↻ Actualizar");
        btnRefrescar.addActionListener(e -> refrescarMesas());
        botones.add(btnRefrescar);
        pie.add(botones);
        panel.add(pie, BorderLayout.SOUTH);

        refrescarMesas();

        if (timerMesas == null) {
            timerMesas = new javax.swing.Timer(1000, e -> actualizarTiemposMesas());
            timerMesas.start();
        }
        return panel;
    }

    private JPanel crearLeyendaMesas() {
        JPanel leyenda = new JPanel();
        leyenda.setOpaque(false);
        leyenda.setLayout(new BoxLayout(leyenda, BoxLayout.Y_AXIS));
        leyenda.add(itemLeyenda(Theme.SUCCESS, "LIBRE", "Mesa disponible"));
        leyenda.add(Box.createVerticalStrut(4));
        leyenda.add(itemLeyenda(Theme.DANGER, "OCUPADA", "Mesa en uso"));
        return leyenda;
    }

    private JPanel itemLeyenda(Color color, String titulo, String detalle) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        fila.setOpaque(false);
        JLabel punto = new JLabel("●");
        punto.setForeground(color);
        JLabel texto = new JLabel(titulo + "   " + detalle);
        texto.setFont(Theme.FONT_SMALL);
        texto.setForeground(Theme.TEXT_MUTED);
        JLabel tituloNegrita = new JLabel(titulo);
        tituloNegrita.setFont(Theme.FONT_BODY_BOLD);
        tituloNegrita.setForeground(Theme.TEXT);
        fila.add(punto);
        fila.add(tituloNegrita);
        JLabel detalleLbl = new JLabel(detalle);
        detalleLbl.setFont(Theme.FONT_SMALL);
        detalleLbl.setForeground(Theme.TEXT_MUTED);
        fila.add(detalleLbl);
        return fila;
    }

    private JPanel tarjetaEstadistica(String icono, JLabel lblValor, String etiqueta, Color color, Color colorFondo) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.X_AXIS));
        tarjeta.setBackground(colorFondo);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new Theme.RoundedLineBorder(color, 10),
                new EmptyBorder(8, 14, 8, 14)));
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(Theme.iconFont(18));
        tarjeta.add(lblIcono);
        tarjeta.add(Box.createHorizontalStrut(8));
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        lblValor.setFont(Theme.FONT_HEADING);
        lblValor.setForeground(color);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(Theme.FONT_SMALL);
        lblEtiqueta.setForeground(Theme.TEXT_MUTED);
        lblEtiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        textos.add(lblValor);
        textos.add(lblEtiqueta);
        tarjeta.add(textos);
        return tarjeta;
    }

    private JPanel crearTarjetaMesa(Mesa m) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.SURFACE);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setBorder(bordeTarjetaMesa(m.getNumero() == (mesaSeleccionada == null ? -1 : mesaSeleccionada), m.isOcupada()));

        JLabel titulo = new JLabel("Mesa " + m.getNumero());
        titulo.setFont(Theme.FONT_HEADING);
        titulo.setForeground(Theme.TEXT);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createVerticalStrut(8));

        Theme.TableIcon icono = Theme.tableIcon(m.isOcupada(), m.getCapacidad());
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);
        icono.setPreferredSize(new Dimension(150, 90));
        icono.setMaximumSize(new Dimension(150, 90));
        card.add(icono);
        card.add(Box.createVerticalStrut(8));

        JLabel badge = badgeEstadoMesa(m.isOcupada());
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(badge);
        card.add(Box.createVerticalStrut(10));

        card.add(filaIconoTexto("👤", "Capacidad: " + m.getCapacidad() + " personas"));
        card.add(Box.createVerticalStrut(2));
        card.add(filaIconoTexto("📍", "Ubicación: " + m.getUbicacion()));

        if (m.isOcupada()) {
            card.add(Box.createVerticalStrut(2));
            JLabel lblTiempo = new JLabel();
            lblTiempo.setFont(Theme.FONT_SMALL);
            lblTiempo.setForeground(Theme.DANGER);
            lblTiempo.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblTiempo.setText("🕐 Tiempo: " + formatoTiempo(m.getSegundosOcupada()));
            lblTiempoPorMesa.put(m.getNumero(), lblTiempo);
            JPanel filaTiempo = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            filaTiempo.setOpaque(false);
            filaTiempo.add(lblTiempo);
            card.add(filaTiempo);
        }

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                mesaSeleccionada = m.getNumero();
                aplicarSeleccionMesas();
            }
        });
        tarjetaPorMesa.put(m.getNumero(), card);
        return card;
    }

    private Border bordeTarjetaMesa(boolean seleccionada, boolean ocupada) {
        Color colorBorde = seleccionada ? Theme.PRIMARY : Theme.BORDER;
        int grosorArco = 14;
        return BorderFactory.createCompoundBorder(
                new Theme.RoundedLineBorder(colorBorde, grosorArco),
                new EmptyBorder(seleccionada ? 13 : 14, 14, 14, 14));
    }

    private void aplicarSeleccionMesas() {
        for (Mesa m : restaurante.getDatosService().getMesas()) {
            JPanel card = tarjetaPorMesa.get(m.getNumero());
            if (card == null) continue;
            boolean seleccionada = mesaSeleccionada != null && mesaSeleccionada == m.getNumero();
            card.setBorder(bordeTarjetaMesa(seleccionada, m.isOcupada()));
        }
    }

    private JLabel badgeEstadoMesa(boolean ocupada) {
        JLabel badge = new JLabel(ocupada ? "OCUPADA" : "LIBRE", SwingConstants.CENTER);
        badge.setFont(Theme.FONT_BADGE);
        badge.setOpaque(true);
        badge.setForeground(ocupada ? Theme.DANGER : Theme.SUCCESS);
        badge.setBackground(ocupada ? Theme.DANGER_BG : Theme.SUCCESS_BG);
        badge.setBorder(new EmptyBorder(4, 14, 4, 14));
        return badge;
    }

    private JPanel filaIconoTexto(String icono, String texto) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        fila.setOpaque(false);
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(Theme.iconFont(13));
        JLabel lblTexto = new JLabel(texto);
        lblTexto.setFont(Theme.FONT_SMALL);
        lblTexto.setForeground(Theme.TEXT_MUTED);
        fila.add(lblIcono);
        fila.add(lblTexto);
        return fila;
    }

    private String formatoTiempo(long totalSegundos) {
        long h = totalSegundos / 3600;
        long min = (totalSegundos % 3600) / 60;
        long s = totalSegundos % 60;
        return String.format("%02d:%02d:%02d", h, min, s);
    }

    private void actualizarTiemposMesas() {
        if (lblTiempoPorMesa.isEmpty()) return;
        for (Mesa m : restaurante.getDatosService().getMesas()) {
            JLabel lbl = lblTiempoPorMesa.get(m.getNumero());
            if (lbl != null && m.isOcupada()) {
                lbl.setText("🕐 Tiempo: " + formatoTiempo(m.getSegundosOcupada()));
            }
        }
    }

    private void refrescarMesas() {
        if (panelGridMesas == null) return;
        if (mesaSeleccionada != null) {
            boolean existe = false;
            for (Mesa m : restaurante.getDatosService().getMesas()) {
                if (m.getNumero() == mesaSeleccionada) { existe = true; break; }
            }
            if (!existe) mesaSeleccionada = null;
        }
        panelGridMesas.removeAll();
        tarjetaPorMesa.clear();
        lblTiempoPorMesa.clear();

        int libres = 0, ocupadas = 0, capacidadTotal = 0, clientesActuales = 0;
        for (Mesa m : restaurante.getDatosService().getMesas()) {
            panelGridMesas.add(crearTarjetaMesa(m));
            capacidadTotal += m.getCapacidad();
            if (m.isOcupada()) { ocupadas++; clientesActuales += m.getCapacidad(); } else { libres++; }
        }
        panelGridMesas.revalidate();
        panelGridMesas.repaint();

        lblStatLibres.setText(String.valueOf(libres));
        lblStatOcupadas.setText(String.valueOf(ocupadas));
        lblStatCapacidad.setText(String.valueOf(capacidadTotal));
        lblStatClientes.setText(String.valueOf(clientesActuales));

        if (comboMesas != null) {
            comboMesas.removeAllItems();
            for (Mesa m : restaurante.getDatosService().getMesasLibres()) comboMesas.addItem(m);
        }
        if (comboMesasReserva != null) {
            comboMesasReserva.removeAllItems();
            for (Mesa m : restaurante.getDatosService().getMesas()) comboMesasReserva.addItem(m);
        }
    }

    // ======================================================================
    // PANEL: NUEVO PEDIDO
    // ======================================================================
    private JPanel crearPanelPedido() {
        // -------- Contenedor raíz: fila fija (Datos del pedido) + fila que ocupa el resto (3 columnas) --------
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setBackground(Theme.BG);

        // -------- Fila 1 (130 px): Card "Datos del pedido" --------
        JPanel top = card(new BorderLayout(16, 0));
        top.setPreferredSize(new Dimension(10, 130));
        top.setMinimumSize(new Dimension(10, 130));
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JPanel formDatos = new JPanel();
        formDatos.setOpaque(false);
        formDatos.setBorder(new EmptyBorder(0, 0, 0, 8));
        formDatos.setLayout(new BoxLayout(formDatos, BoxLayout.Y_AXIS));

        JLabel tituloPedido = Theme.formLabel("Datos del pedido");
        tituloPedido.setFont(Theme.FONT_HEADING.deriveFont(16f));
        tituloPedido.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboClientes = new JComboBox<>();
        for (Cliente cl : restaurante.getDatosService().getClientes()) comboClientes.addItem(cl);
        comboMeseros = new JComboBox<>();
        for (Mesero ms : restaurante.getDatosService().getMeseros()) comboMeseros.addItem(ms);
        comboMesas = new JComboBox<>();
        estilizarCombo(comboClientes);
        estilizarCombo(comboMeseros);
        estilizarCombo(comboMesas);
        comboClientes.setPreferredSize(new Dimension(230, 36));
        comboMeseros.setPreferredSize(new Dimension(160, 36));
        comboMesas.setPreferredSize(new Dimension(200, 36));

        JButton btnNuevoCliente = Theme.outlineButton("+ Cliente nuevo");
        JButton btnIniciar = Theme.primaryButton("Iniciar Pedido");

        // Una sola fila con todos los controles alineados y con separación uniforme de 8 px
        JPanel filaControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaControles.setOpaque(false);
        filaControles.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaControles.add(campoConLabel("Cliente:", comboClientes));
        filaControles.add(btnNuevoCliente);
        filaControles.add(campoConLabel("Mesero:", comboMeseros));
        filaControles.add(campoConLabel("Mesa:", comboMesas));
        filaControles.add(btnIniciar);

        lblPedidoActivo = new JLabel("No hay ningún pedido iniciado.");
        lblPedidoActivo.setFont(Theme.FONT_SMALL);
        lblPedidoActivo.setForeground(Theme.PRIMARY);
        lblPedidoActivo.setAlignmentX(Component.LEFT_ALIGNMENT);

        formDatos.add(Box.createVerticalGlue());
        formDatos.add(tituloPedido);
        formDatos.add(Box.createVerticalStrut(8));
        formDatos.add(filaControles);
        formDatos.add(Box.createVerticalStrut(6));
        formDatos.add(lblPedidoActivo);
        formDatos.add(Box.createVerticalGlue());

        top.add(formDatos, BorderLayout.CENTER);

        JPanel estado = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        estado.setOpaque(false);
        estado.setPreferredSize(new Dimension(210, 130));
        JLabel iconoEstado = Theme.logoRedondo(48);
        JLabel textoEstado = new JLabel("¡Listo para atender!");
        textoEstado.setFont(Theme.FONT_BODY_BOLD);
        textoEstado.setForeground(Theme.PRIMARY);
        estado.add(iconoEstado);
        estado.add(textoEstado);
        top.add(estado, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);

        // -------- Fila 2 (ocupa el resto): 3 columnas Card — Carta 30% / Detalle plato 42% / Detalle pedido 28% --------
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1.0;
        gc.gridy = 0;
                 //aca era para cambiar la carta las dimesiones
        JPanel colCarta = crearColumnaCartaMenu();
        JPanel colDetallePlato = crearColumnaDetallePlato();
        JPanel colDetallePedido = crearColumnaDetallePedido();

       gc.gridx = 0; gc.weightx = 0.07; gc.insets = new Insets(0, 0, 0, 12);
centro.add(colCarta, gc);
        gc.gridx = 1; gc.weightx = 1.62; gc.insets = new Insets(0, 0, 0, 12);
        centro.add(colDetallePlato, gc);
        gc.gridx = 2; gc.weightx = 0.28; gc.insets = new Insets(0, 0, 0, 0);
        centro.add(colDetallePedido, gc);

        panel.add(centro, BorderLayout.CENTER);

        btnNuevoCliente.addActionListener(e -> dialogoNuevoCliente());
        btnIniciar.addActionListener(e -> {
            Cliente cliente = (Cliente) comboClientes.getSelectedItem();
            Mesero mesero = (Mesero) comboMeseros.getSelectedItem();
            Mesa mesa = (Mesa) comboMesas.getSelectedItem();
            if (cliente == null || mesero == null || mesa == null) {
                mensaje("Selecciona cliente, mesero y una mesa libre.");
                return;
            }
            pedidoActual = restaurante.crearPedido(cliente, mesero, mesa);
            mesa.setOcupada(true);
            lblPedidoActivo.setText("Pedido activo #" + pedidoActual.getId()
                    + "  |  Cliente: " + cliente.getNombre()
                    + "  |  Mesa: " + mesa.getNumero());
            actualizarDetallePedido();
            refrescarMesas();
        });

        poblarTablaCarta();
        return panel;
    }

    /**
     * Construye una "Card": panel contenedor con fondo blanco, esquinas
     * redondeadas, borde gris claro, sombra suave (varias capas
     * translúcidas desplazadas) y padding interno de 16 px. Es el bloque
     * base del nuevo layout del formulario "Nuevo Pedido".
     */
    private JPanel card(LayoutManager layout) {
        final int arc = 18;
        final int sombra = 6;
        JPanel c = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cardW = getWidth() - sombra;
                int cardH = getHeight() - sombra;
                for (int i = sombra; i >= 1; i--) {
                    g2.setColor(new Color(0x2A, 0x1A, 0x0C, 6));
                    g2.fillRoundRect(sombra - i, sombra - i + 2, cardW, cardH, arc, arc);
                }
                g2.setColor(Theme.SURFACE);
                g2.fillRoundRect(0, 0, cardW, cardH, arc, arc);
                g2.setColor(Theme.BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, cardW - 1, cardH - 1, arc, arc);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        c.setOpaque(false);
        c.setBorder(new EmptyBorder(16, 16, 16 + sombra, 16 + sombra));
        return c;
    }

    /** Combina una etiqueta corta y un control en una sola fila compacta, alineados verticalmente. */
    private JPanel campoConLabel(String etiqueta, JComponent control) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        fila.setOpaque(false);
        JLabel lbl = Theme.formLabel(etiqueta);
        fila.add(lbl);
        fila.add(control);
        return fila;
    }

    /** Columna 1: Carta del menú (Card) */
    private JPanel crearColumnaCartaMenu() {
        JPanel col = card(new BorderLayout(0, 8));

        JPanel norte = new JPanel();
        norte.setOpaque(false);
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        JLabel heading = Theme.heading("Carta del menú");
        heading.setFont(Theme.FONT_TITLE);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        norte.add(heading);
        norte.add(Box.createVerticalStrut(8));

        campoBusquedaCarta = new JTextField();
        Theme.styleField(campoBusquedaCarta);
        campoBusquedaCarta.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoBusquedaCarta.setPreferredSize(new Dimension(10, 36));
        campoBusquedaCarta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        Theme.placeholder(campoBusquedaCarta, "Buscar plato...");
        campoBusquedaCarta.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { poblarTablaCarta(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { poblarTablaCarta(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { poblarTablaCarta(); }
        });
        norte.add(campoBusquedaCarta);
        norte.add(Box.createVerticalStrut(6));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        filtros.setOpaque(false);
        filtros.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] categorias = {"TODOS", "POLLOS", "COMBOS", "BROASTERS", "ACOMPAÑAMIENTOS", "BEBIDAS"};
        botonesCategoriaCarta.clear();
        for (String cat : categorias) {
            JToggleButton btnCat = new JToggleButton(cat);
            btnCat.setFont(Theme.FONT_BODY);
            btnCat.setFocusPainted(false);
            btnCat.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnCat.setSelected(cat.equals("TODOS"));
            btnCat.setPreferredSize(new Dimension(btnCat.getPreferredSize().width, 36));
            estilizarBotonCategoria(btnCat);
            btnCat.addActionListener(e -> {
                filtroCategoriaCarta = cat;
                for (AbstractButton b : botonesCategoriaCarta) {
                    b.setSelected(b == btnCat);
                    estilizarBotonCategoria((JToggleButton) b);
                }
                poblarTablaCarta();
            });
            filtros.add(btnCat);
            botonesCategoriaCarta.add(btnCat);
        }
        norte.add(filtros);
        col.add(norte, BorderLayout.NORTH);

        modeloCarta = new DefaultTableModel(new Object[]{"Plato", "Precio S/."}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaCarta = new JTable(modeloCarta);
        Theme.styleTable(tablaCarta);
        tablaCarta.setRowHeight(28);
        tablaCarta.getColumnModel().getColumn(0).setPreferredWidth(200);
        tablaCarta.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaCarta.getColumnModel().getColumn(1).setMinWidth(85);
        tablaCarta.getColumnModel().getColumn(1).setMaxWidth(120);
        tablaCarta.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int fila = tablaCarta.getSelectedRow();
            if (fila >= 0 && fila < platosMostrados.size()) {
                mostrarDetallePlato(platosMostrados.get(fila));
            }
        });

        // La tabla crece automáticamente para ocupar toda la altura disponible de la columna.
        JScrollPane scroll = new JScrollPane(tablaCarta);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                new Theme.RoundedLineBorder(Theme.BORDER, 12),
                new EmptyBorder(4, 4, 4, 4)));
        scroll.getViewport().setBackground(Theme.SURFACE);
        col.add(scroll, BorderLayout.CENTER);
        return col;
    }

    private void estilizarBotonCategoria(JToggleButton btn) {
        btn.setBorder(BorderFactory.createCompoundBorder(
                new Theme.RoundedLineBorder(btn.isSelected() ? Theme.PRIMARY : Theme.BORDER, 16),
                new EmptyBorder(8, 16, 8, 16)));
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBackground(btn.isSelected() ? Theme.PRIMARY : Theme.SURFACE);
        btn.setForeground(btn.isSelected() ? Color.WHITE : Theme.TEXT);
    }

    private void poblarTablaCarta() {
        if (modeloCarta == null) return;
        String texto = (campoBusquedaCarta == null || campoBusquedaCarta.getForeground().equals(Theme.TEXT_MUTED))
                ? "" : campoBusquedaCarta.getText().trim().toLowerCase();
        modeloCarta.setRowCount(0);
        platosMostrados.clear();
        for (Plato p : restaurante.getDatosService().getMenu()) {
            if (!coincideCategoria(p.getCategoria(), filtroCategoriaCarta)) continue;
            if (!texto.isEmpty() && !p.getNombre().toLowerCase().contains(texto)) continue;
            platosMostrados.add(p);
            modeloCarta.addRow(new Object[]{p.getNombre(), String.format("%.2f", p.getPrecio())});
        }
        if (!platosMostrados.isEmpty()) {
            tablaCarta.setRowSelectionInterval(0, 0);
            mostrarDetallePlato(platosMostrados.get(0));
        } else {
            limpiarDetallePlato();
        }
    }

    private boolean coincideCategoria(String categoriaPlato, String filtro) {
        if (filtro.equals("TODOS")) return true;
        switch (filtro) {
            case "POLLOS": return categoriaPlato.equals("POLLOS A LA BRASA");
            case "COMBOS": return categoriaPlato.equals("COMBOS");
            case "BROASTERS": return categoriaPlato.startsWith("BROASTER") || categoriaPlato.contains("ALITAS");
            case "ACOMPAÑAMIENTOS": return categoriaPlato.equals("ACOMPAÑAMIENTOS") || categoriaPlato.equals("ENSALADAS");
            case "BEBIDAS": return categoriaPlato.equals("BEBIDAS") || categoriaPlato.equals("POSTRES");
            default: return true;
        }
    }

    /** Columna 2: Detalle del plato seleccionado (Card, con JScrollPane interno por si el contenido no entra) */
    private JPanel crearColumnaDetallePlato() {
        JPanel col = card(new BorderLayout(0, 10));

        // Panel de contenido con BoxLayout
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        JLabel heading = Theme.heading("Detalle del plato seleccionado");
        heading.setFont(Theme.FONT_TITLE);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(heading);
        contenido.add(Box.createVerticalStrut(8));

        panelImagenPlatoSel = new JPanel(new BorderLayout());
        panelImagenPlatoSel.setOpaque(false);
        panelImagenPlatoSel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelImagenPlatoSel.setMaximumSize(new Dimension(380, 200));
        panelImagenPlatoSel.setPreferredSize(new Dimension(380, 200));
        contenido.add(panelImagenPlatoSel);
        contenido.add(Box.createVerticalStrut(10));

        lblNombrePlatoSel = new JLabel("Selecciona un plato");
        lblNombrePlatoSel.setFont(Theme.FONT_TITLE.deriveFont(22f));
        lblNombrePlatoSel.setForeground(Theme.PRIMARY_DARK);
        lblNombrePlatoSel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(lblNombrePlatoSel);
        contenido.add(Box.createVerticalStrut(8));

        lblCategoriaPlatoSel = filaEtiqueta("Categoría:");
        contenido.add(lblCategoriaPlatoSel.getParent());
        contenido.add(Box.createVerticalStrut(4));

        lblPrecioPlatoSel = filaEtiqueta("Precio:");
        lblPrecioPlatoSel.setFont(Theme.FONT_HEADING);
        lblPrecioPlatoSel.setForeground(Theme.PRIMARY);
        contenido.add(lblPrecioPlatoSel.getParent());
        contenido.add(Box.createVerticalStrut(8));

        JLabel lblDescripcionTitulo = Theme.formLabel("Descripción:");
        lblDescripcionTitulo.setFont(Theme.FONT_BODY_BOLD);
        lblDescripcionTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(lblDescripcionTitulo);
        lblDescripcionPlatoSel = new JLabel("—");
        lblDescripcionPlatoSel.setFont(Theme.FONT_BODY);
        lblDescripcionPlatoSel.setForeground(Theme.TEXT_MUTED);
        lblDescripcionPlatoSel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(lblDescripcionPlatoSel);
        contenido.add(Box.createVerticalStrut(10));

        JLabel lblOpcionesTitulo = Theme.formLabel("Opciones adicionales");
        lblOpcionesTitulo.setFont(Theme.FONT_BODY_BOLD);
        lblOpcionesTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(lblOpcionesTitulo);
        contenido.add(Box.createVerticalStrut(4));

        chkExtraQueso = new JCheckBox("Queso");
        chkExtraTocino = new JCheckBox("Tocino");
        chkAjiExtra = new JCheckBox("Aji Extra");
        chkExtraQueso.setFont(Theme.FONT_BODY);
        chkExtraTocino.setFont(Theme.FONT_BODY);
        chkAjiExtra.setFont(Theme.FONT_BODY);
        contenido.add(filaOpcion(chkExtraQueso, 3.0));
        contenido.add(filaOpcion(chkExtraTocino, 4.0));
        contenido.add(filaOpcion(chkAjiExtra, 1.0));
        contenido.add(Box.createVerticalStrut(10));

        JPanel filaCantidad = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaCantidad.setOpaque(false);
        filaCantidad.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblCantidad = Theme.formLabel("Cantidad:");
        lblCantidad.setFont(Theme.FONT_BODY_BOLD);
        filaCantidad.add(lblCantidad);
        spinnerCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        estilizarSpinner(spinnerCantidad, 70);
        spinnerCantidad.setPreferredSize(new Dimension(70, 36));
        filaCantidad.add(spinnerCantidad);
        contenido.add(filaCantidad);
        contenido.add(Box.createVerticalStrut(10));

        JButton btnAgregarPlato = Theme.secondaryButton("🛒 Agregar al pedido");
        btnAgregarPlato.setFont(Theme.FONT_BODY_BOLD);
        btnAgregarPlato.setPreferredSize(new Dimension(200, 36));
        btnAgregarPlato.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(btnAgregarPlato);

        // Glue para empujar el contenido hacia arriba cuando sí sobra espacio
        contenido.add(Box.createVerticalGlue());

        // Red de seguridad: si la ventana es pequeña y el contenido no entra,
        // aparece una barra de scroll en vez de recortar/ocultar controles.
        JScrollPane scrollContenido = new JScrollPane(contenido);
        scrollContenido.setBorder(BorderFactory.createEmptyBorder());
        scrollContenido.setOpaque(false);
        scrollContenido.getViewport().setOpaque(false);
        scrollContenido.getVerticalScrollBar().setUnitIncrement(16);
        scrollContenido.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollContenido.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        col.add(scrollContenido, BorderLayout.CENTER);

        // Listener del botón
        btnAgregarPlato.addActionListener(e -> {
            if (pedidoActual == null) {
                mensaje("Primero inicia un pedido (cliente, mesero y mesa).");
                return;
            }
            if (platoSeleccionadoParaAgregar == null) {
                mensaje("Selecciona un plato de la carta.");
                return;
            }
            Plato platoFinal = platoSeleccionadoParaAgregar;
            if (chkExtraQueso.isSelected()) platoFinal = new ExtraQueso(platoFinal);
            if (chkExtraTocino.isSelected()) platoFinal = new ExtraTocino(platoFinal);
            if (chkAjiExtra.isSelected()) platoFinal = new decorator.AjiExtra(platoFinal);
            int cantidad = (Integer) spinnerCantidad.getValue();
            restaurante.agregarPlatoAPedido(pedidoActual, platoFinal, cantidad);
            actualizarDetallePedido();
            chkExtraQueso.setSelected(false);
            chkExtraTocino.setSelected(false);
            chkAjiExtra.setSelected(false);
            spinnerCantidad.setValue(1);
        });

        limpiarDetallePlato();
        return col;
    }

    private JLabel filaEtiqueta(String textoEtiqueta) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = Theme.formLabel(textoEtiqueta);
        lbl.setFont(Theme.FONT_BODY_BOLD);
        fila.add(lbl);
        JLabel valor = new JLabel("—");
        valor.setFont(Theme.FONT_BODY.deriveFont(16f));
        fila.add(valor);
        return valor;
    }

    private JPanel filaOpcion(JCheckBox chk, double precioExtra) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(4000, 30));
        chk.setOpaque(false);
        chk.setFont(Theme.FONT_BODY);
        chk.setFocusPainted(false);
        fila.add(chk, BorderLayout.WEST);
        JLabel precio = new JLabel("+ S/ " + String.format("%.2f", precioExtra));
        precio.setFont(Theme.FONT_BODY);
        precio.setForeground(Theme.TEXT_MUTED);
        fila.add(precio, BorderLayout.EAST);
        return fila;
    }

    private void mostrarDetallePlato(Plato p) {
        platoSeleccionadoParaAgregar = p;
        panelImagenPlatoSel.removeAll();
        panelImagenPlatoSel.add(Theme.platoImagen(p.getId(), p.getNombre(), 380, 200), BorderLayout.CENTER);
        panelImagenPlatoSel.revalidate();
        panelImagenPlatoSel.repaint();
        lblNombrePlatoSel.setText(p.getNombre());
        lblCategoriaPlatoSel.setText(p.getCategoria());
        lblPrecioPlatoSel.setText("S/ " + String.format("%.2f", p.getPrecio()));
        lblDescripcionPlatoSel.setText("<html><div style='width:300px;'>" + p.getDescripcion() + "</div></html>");
    }

    private void limpiarDetallePlato() {
        platoSeleccionadoParaAgregar = null;
        if (panelImagenPlatoSel != null) {
            panelImagenPlatoSel.removeAll();
            panelImagenPlatoSel.add(new Theme.PlaceholderImageLabel("Sin selección", 380, 200), BorderLayout.CENTER);
            panelImagenPlatoSel.revalidate();
            panelImagenPlatoSel.repaint();
        }
        if (lblNombrePlatoSel != null) lblNombrePlatoSel.setText("Selecciona un plato");
        if (lblCategoriaPlatoSel != null) lblCategoriaPlatoSel.setText("—");
        if (lblPrecioPlatoSel != null) lblPrecioPlatoSel.setText("—");
        if (lblDescripcionPlatoSel != null) lblDescripcionPlatoSel.setText("—");
    }

    /** Columna 3: Detalle del pedido (Card) */
    private JPanel crearColumnaDetallePedido() {
        JPanel col = card(new BorderLayout(0, 10));

        JLabel heading = Theme.heading("Detalle del pedido");
        heading.setFont(Theme.FONT_TITLE);
        col.add(heading, BorderLayout.NORTH);

        modeloDetalle = new DefaultTableModel(
                new Object[]{"Plato", "Cant.", "P.Unit.", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaDetalle = new JTable(modeloDetalle);
        Theme.styleTable(tablaDetalle);
        tablaDetalle.setRowHeight(28);
        tablaDetalle.getColumnModel().getColumn(0).setPreferredWidth(130);
        tablaDetalle.getColumnModel().getColumn(1).setPreferredWidth(55);
        tablaDetalle.getColumnModel().getColumn(2).setPreferredWidth(75);
        tablaDetalle.getColumnModel().getColumn(3).setPreferredWidth(95);

        JScrollPane scrollTabla = new JScrollPane(tablaDetalle);
        scrollTabla.setBorder(BorderFactory.createCompoundBorder(
                new Theme.RoundedLineBorder(Theme.BORDER, 12),
                new EmptyBorder(4, 4, 4, 4)));
        scrollTabla.getViewport().setBackground(Theme.SURFACE);
        col.add(scrollTabla, BorderLayout.CENTER);

        JPanel sur = new JPanel();
        sur.setOpaque(false);
        sur.setLayout(new BoxLayout(sur, BoxLayout.Y_AXIS));

        lblSubtotalDesglose = filaDesglose("Subtotal:", false);
        lblIgvDesglose = filaDesglose("IGV (18%):", false);
        sur.add(lblSubtotalDesglose.getParent());
        sur.add(lblIgvDesglose.getParent());
        sur.add(Box.createVerticalStrut(6));

        JPanel filaTotal = new JPanel(new BorderLayout());
        filaTotal.setOpaque(false);
        filaTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblTotalTexto = new JLabel("TOTAL:");
        lblTotalTexto.setFont(Theme.FONT_HEADING);
        lblTotalTexto.setForeground(Theme.PRIMARY);
        filaTotal.add(lblTotalTexto, BorderLayout.WEST);
        lblTotalDesglose = new JLabel("S/ 0.00");
        lblTotalDesglose.setFont(Theme.FONT_TITLE.deriveFont(22f));
        lblTotalDesglose.setForeground(Theme.PRIMARY);
        lblTotalDesglose.setHorizontalAlignment(SwingConstants.RIGHT);
        filaTotal.add(lblTotalDesglose, BorderLayout.EAST);
        sur.add(filaTotal);
        sur.add(Box.createVerticalStrut(12));

        lblTotalPedido = new JLabel();
        lblTotalPedido.setVisible(false);
        sur.add(lblTotalPedido);

        JPanel botones = new JPanel(new GridLayout(2, 1, 0, 8));
        botones.setOpaque(false);
        botones.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnVaciar = Theme.dangerButton("🗑 Vaciar pedido");
        JButton btnFinalizar = Theme.successButton("<html><center>Finalizar pedido<br>Procesar pago</center></html>");
        btnVaciar.setFont(Theme.FONT_BODY_BOLD);
        btnFinalizar.setFont(Theme.FONT_BODY_BOLD);
        botones.add(btnVaciar);
        botones.add(btnFinalizar);
        sur.add(botones);

        col.add(sur, BorderLayout.SOUTH);

        btnVaciar.addActionListener(e -> {
            if (pedidoActual == null) { mensaje("No hay un pedido activo."); return; }
            pedidoActual.getDetalles().clear();
            actualizarDetallePedido();
        });

        btnFinalizar.addActionListener(e -> {
            if (pedidoActual == null) { mensaje("No hay un pedido activo."); return; }
            if (pedidoActual.getDetalles().isEmpty()) {
                mensaje("Agrega al menos un plato antes de finalizar.");
                return;
            }
            mensaje("Pedido #" + pedidoActual.getId() + " enviado a cocina (estado PENDIENTE).\n"
                    + "Puedes seguir su avance en la pestaña 'Pedidos Activos'.");
            pedidoActual = null;
            lblPedidoActivo.setText("No hay ningún pedido iniciado.");
            actualizarDetallePedido();
            refrescarTodo();
        });

        actualizarDetallePedido();
        return col;
    }

    private JLabel filaDesglose(String etiqueta, boolean destacado) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(Theme.FONT_BODY);
        lblEtiqueta.setForeground(Theme.TEXT_MUTED);
        fila.add(lblEtiqueta, BorderLayout.WEST);
        JLabel valor = new JLabel("S/ 0.00");
        valor.setFont(Theme.FONT_BODY_BOLD);
        valor.setHorizontalAlignment(SwingConstants.RIGHT);
        fila.add(valor, BorderLayout.EAST);
        return valor;
    }

    private void actualizarDetallePedido() {
        modeloDetalle.setRowCount(0);
        double subtotal = 0;
        if (pedidoActual != null) {
            for (DetallePedido d : pedidoActual.getDetalles()) {
                modeloDetalle.addRow(new Object[]{
                        d.getProducto().getNombre(),
                        d.getCantidad(),
                        String.format("%.2f", d.getPrecioUnitario()),
                        String.format("%.2f", d.calcularSubtotal())
                });
            }
            subtotal = pedidoActual.calcularTotal();
        }
        double igv = subtotal * IGV;
        double total = subtotal + igv;
        lblSubtotalDesglose.setText("S/ " + String.format("%.2f", subtotal));
        lblIgvDesglose.setText("S/ " + String.format("%.2f", igv));
        lblTotalDesglose.setText("S/ " + String.format("%.2f", total));
        lblTotalPedido.setText("Total: S/. " + String.format("%.2f", subtotal));
    }

    private void dialogoNuevoCliente() {
        JTextField nombre = new JTextField();
        JTextField dni = new JTextField();
        JTextField telefono = new JTextField();
        JTextField email = new JTextField();
        JPanel form = formularioDialogo(
                "Nombre completo:", nombre, "DNI:", dni, "Teléfono:", telefono, "Email:", email);
        int resultado = JOptionPane.showConfirmDialog(this, form, "Nuevo cliente",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultado == JOptionPane.OK_OPTION) {
            if (nombre.getText().trim().isEmpty()) { mensaje("El nombre es obligatorio."); return; }
            Cliente nuevo = restaurante.getDatosService().agregarCliente(
                    nombre.getText().trim(), dni.getText().trim(),
                    telefono.getText().trim(), email.getText().trim());
            comboClientes.addItem(nuevo);
            comboClientes.setSelectedItem(nuevo);
            if (comboClientesReserva != null) comboClientesReserva.addItem(nuevo);
        }
    }

    // ======================================================================
    // PANEL: PEDIDOS ACTIVOS
    // ======================================================================
    private JPanel crearPanelPedidosActivos() {
        JPanel panel = fondoPanel();
        panel.add(panelEncabezadoConBarra("Pedidos Activos", "Controla el avance de cada pedido en cocina."), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 14));
        centro.setOpaque(false);

        JPanel filaStats = new JPanel(new GridLayout(1, 4, 14, 0));
        filaStats.setOpaque(false);
        lblStatPedidosActivos = new JLabel("0");
        lblStatPreparacion = new JLabel("0");
        lblStatListos = new JLabel("0");
        lblStatPendientes = new JLabel("0");
        filaStats.add(tarjetaResumenPedido("📋", Theme.PRIMARY, Theme.PRIMARY_LIGHT,
                lblStatPedidosActivos, "Pedidos activos", "Total en curso"));
        filaStats.add(tarjetaResumenPedido("⏰", Theme.WARNING, Theme.WARNING_BG,
                lblStatPreparacion, "En preparación", "Cocina"));
        filaStats.add(tarjetaResumenPedido("🍽", Theme.SUCCESS, Theme.SUCCESS_BG,
                lblStatListos, "Listos para servir", "Esperando mozo"));
        filaStats.add(tarjetaResumenPedido("⏳", Theme.INFO, Theme.INFO_BG,
                lblStatPendientes, "Pendientes", "Por iniciar"));
        centro.add(filaStats, BorderLayout.NORTH);

        modeloPedidos = new DefaultTableModel(
                new Object[]{"N° Pedido", "Hora", "Cliente", "Mesa", "Mesero", "Estado",
                        "Tiempo transcurrido", "Total (S/.)", "Acciones"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col == 8; }
        };
        tablaPedidos = new JTable(modeloPedidos);
        Theme.styleTable(tablaPedidos);
        tablaPedidos.setRowHeight(42);
        tablaPedidos.getColumnModel().getColumn(0).setCellRenderer(new RendererIdPedido());
        tablaPedidos.getColumnModel().getColumn(5).setCellRenderer(new RendererEstadoPedido());
        tablaPedidos.getColumnModel().getColumn(6).setCellRenderer(new RendererTiempoPedido());
        tablaPedidos.getColumnModel().getColumn(7).setCellRenderer(new RendererTotalPedido());
        tablaPedidos.getColumnModel().getColumn(8).setCellRenderer(new RendererAccionesPedido());
        tablaPedidos.getColumnModel().getColumn(8).setCellEditor(new EditorAccionesPedido());
        tablaPedidos.getColumnModel().getColumn(8).setPreferredWidth(150);
        centro.add(Theme.scrollCard(tablaPedidos), BorderLayout.CENTER);
        panel.add(centro, BorderLayout.CENTER);

        JPanel botones = barraBotones();
        JButton btnAvanzar = Theme.primaryButton("Avanzar estado →");
        JButton btnCancelar = Theme.dangerButton("Cancelar pedido");
        JButton btnRefrescar = Theme.outlineButton("↻ Actualizar");
        botones.add(btnAvanzar);
        botones.add(btnCancelar);
        botones.add(btnRefrescar);
        panel.add(botones, BorderLayout.SOUTH);
        btnAvanzar.addActionListener(e -> {
            Pedido p = pedidoSeleccionadoEnTabla();
            if (p != null) { restaurante.avanzarEstadoPedido(p); refrescarPedidosActivos(); }
        });
        btnCancelar.addActionListener(e -> {
            Pedido p = pedidoSeleccionadoEnTabla();
            if (p != null) { restaurante.cancelarPedido(p); refrescarPedidosActivos(); }
        });
        btnRefrescar.addActionListener(e -> refrescarPedidosActivos());
        refrescarPedidosActivos();

        if (timerPedidosActivos == null) {
            timerPedidosActivos = new javax.swing.Timer(1000, e -> {
                if (tablaPedidos != null) tablaPedidos.repaint();
            });
            timerPedidosActivos.start();
        }
        return panel;
    }

    /** Encabezado con una barra vertical de acento, usado en secciones destacadas como "Pedidos Activos". */
    private JPanel panelEncabezadoConBarra(String titulo, String subtitulo) {
        JPanel contenedor = new JPanel();
        contenedor.setOpaque(false);
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));

        JPanel filaTitulo = new JPanel();
        filaTitulo.setOpaque(false);
        filaTitulo.setLayout(new BoxLayout(filaTitulo, BoxLayout.X_AXIS));
        filaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel barra = new JPanel();
        barra.setBackground(Theme.PRIMARY);
        barra.setPreferredSize(new Dimension(5, 26));
        barra.setMaximumSize(new Dimension(5, 26));
        filaTitulo.add(barra);
        filaTitulo.add(Box.createHorizontalStrut(10));
        JLabel lblTitulo = new JLabel(titulo.toUpperCase());
        lblTitulo.setFont(Theme.FONT_TITLE);
        lblTitulo.setForeground(Theme.TEXT);
        filaTitulo.add(lblTitulo);
        contenedor.add(filaTitulo);
        contenedor.add(Box.createVerticalStrut(4));
        JLabel lblSub = Theme.subtle(subtitulo);
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenedor.add(lblSub);
        return contenedor;
    }

    /** Tarjeta blanca con un ícono circular a la izquierda y un valor + dos líneas de texto a la derecha. */
    private JPanel tarjetaResumenPedido(String emoji, Color colorIcono, Color colorFondoIcono,
                                          JLabel lblValor, String tituloLinea, String subLinea) {
        JPanel tarjeta = new JPanel(new BorderLayout(14, 0));
        tarjeta.setBackground(Theme.SURFACE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new Theme.RoundedLineBorder(Theme.BORDER, 12),
                new EmptyBorder(14, 16, 14, 16)));

        JComponent circulo = Theme.circuloIcono(emoji, colorIcono, colorFondoIcono, 48);
        tarjeta.add(circulo, BorderLayout.WEST);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        lblValor.setFont(Theme.FONT_TITLE.deriveFont(22f));
        lblValor.setForeground(Theme.TEXT);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblTitulo = new JLabel(tituloLinea);
        lblTitulo.setFont(Theme.FONT_BODY_BOLD);
        lblTitulo.setForeground(Theme.TEXT);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblSub = new JLabel(subLinea);
        lblSub.setFont(Theme.FONT_SMALL);
        lblSub.setForeground(Theme.TEXT_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        textos.add(lblValor);
        textos.add(lblTitulo);
        textos.add(lblSub);
        tarjeta.add(textos, BorderLayout.CENTER);
        return tarjeta;
    }

    /** Color de fila alternado/seleccionado, coherente con el usado en Theme para las tablas. */
    private Color colorFilaTabla(int row, boolean seleccionada) {
        if (seleccionada) return new Color(0xF6, 0xDF, 0xC8);
        return row % 2 == 0 ? Theme.SURFACE : Theme.SURFACE_ALT;
    }

    private class RendererIdPedido implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int col) {
            int id = value instanceof Integer ? (Integer) value : 0;
            JLabel lbl = new JLabel(String.format("#%05d", id));
            lbl.setOpaque(true);
            lbl.setFont(Theme.FONT_BODY_BOLD);
            lbl.setForeground(Theme.TEXT);
            lbl.setBackground(colorFilaTabla(row, isSelected));
            lbl.setBorder(new EmptyBorder(0, 14, 0, 14));
            return lbl;
        }
    }

    private class RendererEstadoPedido implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int col) {
            String estado = value == null ? "" : value.toString();
            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setOpaque(true);
            wrapper.setBackground(colorFilaTabla(row, isSelected));

            String texto; Color fg, bg;
            switch (estado) {
                case "PENDIENTE": texto = "Pendiente"; fg = Theme.TEXT_MUTED; bg = Theme.BORDER_SOFT; break;
                case "PREPARANDO": texto = "En preparación"; fg = Theme.WARNING; bg = Theme.WARNING_BG; break;
                case "LISTO": texto = "Listo para servir"; fg = Theme.SUCCESS; bg = Theme.SUCCESS_BG; break;
                case "ENTREGADO": texto = "Entregado"; fg = Theme.INFO; bg = Theme.INFO_BG; break;
                case "CANCELADO": texto = "Cancelado"; fg = Theme.DANGER; bg = Theme.DANGER_BG; break;
                default: texto = estado; fg = Theme.TEXT; bg = null;
            }
            JLabel pill = new JLabel(texto);
            pill.setFont(Theme.FONT_BADGE);
            pill.setForeground(fg);
            if (bg != null) {
                pill.setOpaque(true);
                pill.setBackground(bg);
                pill.setBorder(new EmptyBorder(4, 12, 4, 12));
            }
            wrapper.add(pill);
            return wrapper;
        }
    }

    private class RendererTiempoPedido implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int col) {
            long segundos = 0L;
            Object idObj = table.getModel().getValueAt(row, 0);
            if (idObj instanceof Integer) {
                Pedido p = restaurante.getPedidoPorId((Integer) idObj);
                if (p != null) {
                    segundos = Math.max(0L, java.time.Duration.between(p.getFechaHora(), LocalDateTime.now()).getSeconds());
                }
            }
            JLabel lbl = new JLabel("🕐 " + formatoTiempo(segundos));
            lbl.setOpaque(true);
            lbl.setFont(Theme.FONT_BODY);
            lbl.setForeground(Theme.TEXT_MUTED);
            lbl.setBackground(colorFilaTabla(row, isSelected));
            lbl.setBorder(new EmptyBorder(0, 14, 0, 14));
            return lbl;
        }
    }

    private class RendererTotalPedido implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int col) {
            double total = value instanceof Double ? (Double) value : 0.0;
            JLabel lbl = new JLabel(String.format("S/ %.2f", total));
            lbl.setOpaque(true);
            lbl.setFont(Theme.FONT_BODY_BOLD);
            lbl.setForeground(Theme.TEXT);
            lbl.setBackground(colorFilaTabla(row, isSelected));
            lbl.setBorder(new EmptyBorder(0, 14, 0, 14));
            return lbl;
        }
    }

    private class RendererAccionesPedido implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int col) {
            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setOpaque(true);
            wrapper.setBackground(colorFilaTabla(row, isSelected));
            JButton boton = Theme.outlineButton("👁 Ver detalle");
            boton.setFont(Theme.FONT_SMALL);
            wrapper.add(boton);
            return wrapper;
        }
    }

    private class EditorAccionesPedido extends AbstractCellEditor implements TableCellEditor {
        private final JButton boton = Theme.outlineButton("👁 Ver detalle");
        private int filaActual;

        EditorAccionesPedido() {
            boton.setFont(Theme.FONT_SMALL);
            boton.addActionListener(e -> {
                fireEditingStopped();
                Object idObj = tablaPedidos.getModel().getValueAt(filaActual, 0);
                if (idObj instanceof Integer) {
                    Pedido p = restaurante.getPedidoPorId((Integer) idObj);
                    if (p != null) mostrarDetallePedido(p);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            filaActual = row;
            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setOpaque(true);
            wrapper.setBackground(colorFilaTabla(row, true));
            wrapper.add(boton);
            return wrapper;
        }

        @Override
        public Object getCellEditorValue() { return null; }
    }

    private void mostrarDetallePedido(Pedido p) {
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Theme.SURFACE);
        contenido.setBorder(new EmptyBorder(4, 4, 12, 4));

        JLabel titulo = new JLabel(String.format("Pedido #%05d", p.getId()));
        titulo.setFont(Theme.FONT_HEADING);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(titulo);
        contenido.add(Box.createVerticalStrut(8));

        contenido.add(lineaDetallePedido("Cliente: ", p.getCliente().getNombre()));
        contenido.add(lineaDetallePedido("Mesa: ", "Mesa " + String.format("%02d", p.getMesa().getNumero())));
        contenido.add(lineaDetallePedido("Mesero: ", p.getMesero().getNombre()));
        contenido.add(lineaDetallePedido("Estado: ", p.getEstado().getNombreEstado()));
        contenido.add(lineaDetallePedido("Hora: ", p.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm"))));
        contenido.add(Box.createVerticalStrut(12));

        JLabel subtitulo = new JLabel("Platos pedidos:");
        subtitulo.setFont(Theme.FONT_BODY_BOLD);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(subtitulo);
        contenido.add(Box.createVerticalStrut(4));

        if (p.getDetalles().isEmpty()) {
            JLabel vacio = new JLabel("Sin platos registrados.");
            vacio.setFont(Theme.FONT_BODY);
            vacio.setForeground(Theme.TEXT_MUTED);
            vacio.setAlignmentX(Component.LEFT_ALIGNMENT);
            contenido.add(vacio);
        } else {
            for (DetallePedido d : p.getDetalles()) {
                JLabel linea = new JLabel(String.format("• %dx %s — S/ %.2f",
                        d.getCantidad(), d.getProducto().getNombre(), d.calcularSubtotal()));
                linea.setFont(Theme.FONT_BODY);
                linea.setAlignmentX(Component.LEFT_ALIGNMENT);
                contenido.add(linea);
            }
        }
        contenido.add(Box.createVerticalStrut(12));
        JLabel total = new JLabel(String.format("Total: S/ %.2f", p.calcularTotal()));
        total.setFont(Theme.FONT_HEADING);
        total.setForeground(Theme.PRIMARY);
        total.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(total);

        contenido.setPreferredSize(new Dimension(340, contenido.getPreferredSize().height));
        JOptionPane.showMessageDialog(this, contenido, "Detalle del pedido", JOptionPane.PLAIN_MESSAGE);
    }

    private JLabel lineaDetallePedido(String etiqueta, String valor) {
        JLabel lbl = new JLabel(etiqueta + valor);
        lbl.setFont(Theme.FONT_BODY);
        lbl.setForeground(Theme.TEXT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private Pedido pedidoSeleccionadoEnTabla() {
        int fila = tablaPedidos.getSelectedRow();
        if (fila < 0) { mensaje("Selecciona un pedido de la lista."); return null; }
        int id = (int) modeloPedidos.getValueAt(fila, 0);
        return restaurante.getPedidoPorId(id);
    }

    private void refrescarPedidosActivos() {
        if (modeloPedidos == null) return;
        modeloPedidos.setRowCount(0);
        int activos = 0, preparando = 0, listos = 0, pendientes = 0;
        DateTimeFormatter horaFmt = DateTimeFormatter.ofPattern("HH:mm");
        for (Pedido p : restaurante.getPedidos()) {
            String estadoNombre = p.getEstado().getNombreEstado();
            modeloPedidos.addRow(new Object[]{
                    p.getId(),
                    p.getFechaHora().format(horaFmt),
                    p.getCliente().getNombre(),
                    "Mesa " + String.format("%02d", p.getMesa().getNumero()),
                    p.getMesero().getNombre(),
                    estadoNombre,
                    "",
                    p.calcularTotal(),
                    p.getId()
            });
            switch (estadoNombre) {
                case "PENDIENTE": pendientes++; activos++; break;
                case "PREPARANDO": preparando++; activos++; break;
                case "LISTO": listos++; activos++; break;
                default: break;
            }
        }
        if (lblStatPedidosActivos != null) {
            lblStatPedidosActivos.setText(String.valueOf(activos));
            lblStatPreparacion.setText(String.valueOf(preparando));
            lblStatListos.setText(String.valueOf(listos));
            lblStatPendientes.setText(String.valueOf(pendientes));
        }
    }

    // ======================================================================
    // PANEL: PAGAR / FACTURAR
    // ======================================================================
    private JPanel crearPanelPago() {
        JPanel panel = fondoPanel();

        JPanel envoltorioNorte = new JPanel();
        envoltorioNorte.setOpaque(false);
        envoltorioNorte.setLayout(new BoxLayout(envoltorioNorte, BoxLayout.Y_AXIS));

        // --- Encabezado con icono ---
        JPanel encabezado = new JPanel(new BorderLayout(14, 0));
        encabezado.setOpaque(false);
        encabezado.setAlignmentX(Component.LEFT_ALIGNMENT);
        JComponent iconoCabecera = Theme.circuloIcono("🧾", Theme.PRIMARY, Theme.PRIMARY_LIGHT, 46);
        encabezado.add(iconoCabecera, BorderLayout.WEST);
        JPanel textosCabecera = new JPanel();
        textosCabecera.setOpaque(false);
        textosCabecera.setLayout(new BoxLayout(textosCabecera, BoxLayout.Y_AXIS));
        JLabel lblTituloPago = new JLabel("PAGAR / FACTURAR");
        lblTituloPago.setFont(Theme.FONT_TITLE.deriveFont(22f));
        lblTituloPago.setForeground(Theme.TEXT);
        lblTituloPago.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblSubtituloPago = Theme.subtle("Selecciona un pedido y procesa el pago para emitir la boleta.");
        lblSubtituloPago.setFont(Theme.FONT_BODY);
        lblSubtituloPago.setAlignmentX(Component.LEFT_ALIGNMENT);
        textosCabecera.add(lblTituloPago);
        textosCabecera.add(Box.createVerticalStrut(2));
        textosCabecera.add(lblSubtituloPago);
        encabezado.add(textosCabecera, BorderLayout.CENTER);
        envoltorioNorte.add(encabezado);
        envoltorioNorte.add(Box.createVerticalStrut(14));

        // --- Formulario: pedido / método de pago / botón ---
        JPanel top = Theme.card(new FlowLayout(FlowLayout.LEFT, 12, 6));
        top.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(Theme.formLabel("Pedido:"));
        comboPedidosPago = new JComboBox<>();
        estilizarCombo(comboPedidosPago);
        comboPedidosPago.setRenderer(new PedidoComboRenderer());
        comboPedidosPago.setPreferredSize(new Dimension(220, 34));
        top.add(comboPedidosPago);
        top.add(Theme.formLabel("Método de pago:"));
        comboMetodoPago = new JComboBox<>(new String[]{"EFECTIVO", "TARJETA", "YAPE"});
        estilizarCombo(comboMetodoPago);
        comboMetodoPago.setRenderer(new MetodoPagoComboRenderer());
        comboMetodoPago.setPreferredSize(new Dimension(160, 34));
        top.add(comboMetodoPago);
        JButton btnCobrar = Theme.primaryButton("🖨  Procesar pago y emitir boleta  →");
        top.add(btnCobrar);
        envoltorioNorte.add(top);
        envoltorioNorte.add(Box.createVerticalStrut(14));

        // --- Franja "¿Cómo funciona?" ---
        JPanel infoBarra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        infoBarra.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoBarra.setBackground(Theme.PRIMARY_LIGHT);
        infoBarra.setBorder(BorderFactory.createCompoundBorder(
                new Theme.RoundedLineBorder(Theme.BORDER, 12),
                new EmptyBorder(6, 12, 6, 12)));
        JLabel lblInfoIcono = new JLabel("\u24D8");
        lblInfoIcono.setFont(Theme.FONT_BODY_BOLD);
        lblInfoIcono.setForeground(Theme.PRIMARY_DARK);
        infoBarra.add(lblInfoIcono);
        JLabel lblComoFunciona = new JLabel("¿Cómo funciona?");
        lblComoFunciona.setFont(Theme.FONT_BODY_BOLD);
        lblComoFunciona.setForeground(Theme.TEXT);
        infoBarra.add(lblComoFunciona);
        infoBarra.add(pasoInfo("1. Selecciona el pedido que deseas pagar"));
        infoBarra.add(puntoSeparador());
        infoBarra.add(pasoInfo("2. Elige el método de pago"));
        infoBarra.add(puntoSeparador());
        infoBarra.add(pasoInfo("3. Procesa el pago y genera la boleta"));
        envoltorioNorte.add(infoBarra);

        panel.add(envoltorioNorte, BorderLayout.NORTH);

        // --- Centro: alterna entre estado vacío y la boleta generada ---
        panelCentroPago = new JPanel(new CardLayout());
        panelCentroPago.setOpaque(false);

        JPanel estadoVacio = Theme.card(new BorderLayout());
        JPanel contenidoVacio = new JPanel();
        contenidoVacio.setOpaque(false);
        contenidoVacio.setLayout(new BoxLayout(contenidoVacio, BoxLayout.Y_AXIS));
        JComponent iconoVacio = Theme.circuloIcono("🧾", Theme.PRIMARY, Theme.PRIMARY_LIGHT, 64);
        iconoVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblVacioTitulo = new JLabel("Selecciona un pedido");
        lblVacioTitulo.setFont(Theme.FONT_HEADING);
        lblVacioTitulo.setForeground(Theme.TEXT);
        lblVacioTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblVacioTexto = new JLabel("<html><div style='text-align:center;width:280px;'>"
                + "Los detalles del pedido y el resumen de pago se mostrarán aquí.</div></html>");
        lblVacioTexto.setFont(Theme.FONT_BODY);
        lblVacioTexto.setForeground(Theme.TEXT_MUTED);
        lblVacioTexto.setAlignmentX(Component.CENTER_ALIGNMENT);
        contenidoVacio.add(Box.createVerticalGlue());
        contenidoVacio.add(iconoVacio);
        contenidoVacio.add(Box.createVerticalStrut(14));
        contenidoVacio.add(lblVacioTitulo);
        contenidoVacio.add(Box.createVerticalStrut(6));
        contenidoVacio.add(lblVacioTexto);
        contenidoVacio.add(Box.createVerticalGlue());
        estadoVacio.add(contenidoVacio, BorderLayout.CENTER);
        panelCentroPago.add(estadoVacio, "vacio");

        JPanel estadoFactura = Theme.card(new BorderLayout());
        areaFactura = new JTextArea();
        areaFactura.setEditable(false);
        areaFactura.setFont(Theme.FONT_MONO);
        areaFactura.setBackground(Theme.SURFACE);
        areaFactura.setForeground(Theme.TEXT);
        areaFactura.setBorder(new EmptyBorder(4, 4, 4, 4));
        JScrollPane spFactura = new JScrollPane(areaFactura);
        spFactura.setBorder(BorderFactory.createEmptyBorder());
        estadoFactura.add(spFactura, BorderLayout.CENTER);
        panelCentroPago.add(estadoFactura, "factura");

        panel.add(panelCentroPago, BorderLayout.CENTER);

        // --- Pie: mensaje de confianza ---
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER_SOFT),
                new EmptyBorder(0, 0, 0, 0)));
        JLabel lblPieIcono = new JLabel("🛡");
        lblPieIcono.setFont(Theme.iconFont(13));
        JLabel lblPieTexto = Theme.subtle("Sistema seguro y confiable para el control de tu negocio.");
        lblPieTexto.setFont(Theme.FONT_SMALL);
        pie.add(lblPieIcono);
        pie.add(lblPieTexto);
        panel.add(pie, BorderLayout.SOUTH);

        btnCobrar.addActionListener(e -> {
            Pedido p = (Pedido) comboPedidosPago.getSelectedItem();
            if (p == null) { mensaje("No hay pedidos disponibles para cobrar."); return; }
            if (p.getDetalles().isEmpty()) { mensaje("Ese pedido no tiene platos registrados."); return; }
            String metodo = (String) comboMetodoPago.getSelectedItem();
            Factura factura = restaurante.procesarPago(p, metodo);
            factura.setMetodoPago(metodo);
            mostrarFactura(factura);
            p.getMesa().setOcupada(false);
            refrescarTodo();
        });
        refrescarComboPago();
        return panel;
    }

    /** Texto pequeño usado dentro de la franja "¿Cómo funciona?". */
    private JLabel pasoInfo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT);
        return lbl;
    }

    /** Punto separador "•" usado entre los pasos de la franja informativa. */
    private JLabel puntoSeparador() {
        JLabel lbl = new JLabel("•");
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT_MUTED);
        return lbl;
    }

    /** Renderer con icono para el combo de selección de pedido. */
    private class PedidoComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                         boolean isSelected, boolean cellHasFocus) {
            String texto = (value == null) ? "Selecciona un pedido" : "📋  " + value.toString();
            JLabel lbl = (JLabel) super.getListCellRendererComponent(list, texto, index, isSelected, cellHasFocus);
            lbl.setFont(Theme.FONT_BODY);
            return lbl;
        }
    }

    /** Renderer con icono para el combo de método de pago. */
    private class MetodoPagoComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                         boolean isSelected, boolean cellHasFocus) {
            String metodo = String.valueOf(value);
            String icono;
            switch (metodo) {
                case "TARJETA": icono = "💳  "; break;
                case "YAPE": icono = "📱  "; break;
                default: icono = "📷  "; break;
            }
            JLabel lbl = (JLabel) super.getListCellRendererComponent(list, icono + metodo, index, isSelected, cellHasFocus);
            lbl.setFont(Theme.FONT_BODY);
            return lbl;
        }
    }

    private void mostrarFactura(Factura f) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("       EL SABROSO POLLO - BOLETA\n");
        sb.append("========================================\n");
        sb.append("Boleta N°   : ").append(f.getId()).append("\n");
        sb.append("Fecha       : ").append(f.getFechaEmision().format(fmt)).append("\n");
        sb.append("Cliente     : ").append(f.getPedido().getCliente().getNombre()).append("\n");
        sb.append("Mesa        : ").append(f.getPedido().getMesa().getNumero()).append("\n");
        sb.append("Método pago : ").append(f.getMetodoPago()).append("\n");
        sb.append("----------------------------------------\n");
        for (DetallePedido d : f.getPedido().getDetalles()) {
            sb.append(String.format("%-24s x%-3d S/.%7.2f%n",
                    d.getProducto().getNombre(), d.getCantidad(), d.calcularSubtotal()));
        }
        sb.append("----------------------------------------\n");
        sb.append(String.format("Subtotal: S/. %.2f%n", f.getSubtotal()));
        sb.append(String.format("IGV (%.0f%%): S/. %.2f%n", f.getImpuesto() * 100, f.getSubtotal() * f.getImpuesto()));
        sb.append(String.format("TOTAL: S/. %.2f%n", f.getTotal()));
        sb.append("========================================\n");
        sb.append("        ¡Gracias por su visita!\n");
        areaFactura.setText(sb.toString());
        areaFactura.setCaretPosition(0);
        if (panelCentroPago != null) {
            CardLayout cl = (CardLayout) panelCentroPago.getLayout();
            cl.show(panelCentroPago, "factura");
        }
    }

    private void refrescarComboPago() {
        if (comboPedidosPago == null) return;
        comboPedidosPago.removeAllItems();
        for (Pedido p : restaurante.getPedidos()) {
            if (!p.getEstado().getNombreEstado().equals("CANCELADO")) comboPedidosPago.addItem(p);
        }
    }

    // ======================================================================
    // PANEL: RESERVAS
    // ======================================================================
    private JPanel crearPanelReservas() {
        JPanel panel = fondoPanel();

        // --- Tarjeta del formulario: icono/título a la izquierda, campos a la derecha ---
        JPanel form = Theme.card(new BorderLayout(0, 0));

        JPanel izquierda = new JPanel();
        izquierda.setOpaque(false);
        izquierda.setLayout(new BoxLayout(izquierda, BoxLayout.Y_AXIS));
        izquierda.setBorder(new EmptyBorder(4, 0, 4, 24));
        izquierda.setPreferredSize(new Dimension(230, 0));
        JComponent iconoReserva = Theme.circuloIcono("📅", Theme.PRIMARY, Theme.PRIMARY_LIGHT, 56);
        iconoReserva.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblTituloReserva = new JLabel("Nueva reserva");
        lblTituloReserva.setFont(Theme.FONT_HEADING);
        lblTituloReserva.setForeground(Theme.TEXT);
        lblTituloReserva.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblSubReserva = new JLabel("<html><div style='width:190px;'>"
                + "Complete los datos para registrar una nueva reserva.</div></html>");
        lblSubReserva.setFont(Theme.FONT_BODY);
        lblSubReserva.setForeground(Theme.TEXT_MUTED);
        lblSubReserva.setAlignmentX(Component.LEFT_ALIGNMENT);
        izquierda.add(iconoReserva);
        izquierda.add(Box.createVerticalStrut(12));
        izquierda.add(lblTituloReserva);
        izquierda.add(Box.createVerticalStrut(6));
        izquierda.add(lblSubReserva);

        JPanel divisor = new JPanel();
        divisor.setBackground(Theme.BORDER_SOFT);
        divisor.setPreferredSize(new Dimension(1, 10));

        JPanel izquierdaConDivisor = new JPanel(new BorderLayout());
        izquierdaConDivisor.setOpaque(false);
        izquierdaConDivisor.add(izquierda, BorderLayout.CENTER);
        izquierdaConDivisor.add(divisor, BorderLayout.EAST);
        form.add(izquierdaConDivisor, BorderLayout.WEST);

        JPanel derecha = new JPanel(new BorderLayout(0, 18));
        derecha.setOpaque(false);
        derecha.setBorder(new EmptyBorder(0, 24, 0, 0));

        comboClientesReserva = new JComboBox<>();
        for (Cliente cl : restaurante.getDatosService().getClientes()) comboClientesReserva.addItem(cl);
        comboMesasReserva = new JComboBox<>();
        for (Mesa m : restaurante.getDatosService().getMesas()) comboMesasReserva.addItem(m);
        estilizarCombo(comboClientesReserva);
        estilizarCombo(comboMesasReserva);

        spinnerFechaReserva = new JSpinner(new SpinnerDateModel());
        spinnerFechaReserva.setEditor(new JSpinner.DateEditor(spinnerFechaReserva, "dd/MM/yyyy HH:mm"));
        spinnerPersonasReserva = new JSpinner(new SpinnerNumberModel(2, 1, 30, 1));
        estilizarSpinner(spinnerFechaReserva, 150);
        estilizarSpinner(spinnerPersonasReserva, 60);

        JPanel filaCampos1 = new JPanel(new GridLayout(1, 2, 24, 0));
        filaCampos1.setOpaque(false);
        filaCampos1.add(campoConIcono("👤", "Cliente", comboClientesReserva));
        filaCampos1.add(campoConIcono("🍽", "Mesa", comboMesasReserva));

        JPanel filaCampos2 = new JPanel(new GridLayout(1, 2, 24, 0));
        filaCampos2.setOpaque(false);
        filaCampos2.add(campoConIcono("📅", "Fecha y hora", spinnerFechaReserva));
        filaCampos2.add(campoConIcono("👥", "N° personas", spinnerPersonasReserva));

        JPanel filasCampos = new JPanel();
        filasCampos.setOpaque(false);
        filasCampos.setLayout(new BoxLayout(filasCampos, BoxLayout.Y_AXIS));
        filasCampos.add(filaCampos1);
        filasCampos.add(Box.createVerticalStrut(16));
        filasCampos.add(filaCampos2);
        derecha.add(filasCampos, BorderLayout.NORTH);

        JButton btnReservar = Theme.primaryButton("📅  Registrar reserva");
        JPanel filaBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        filaBoton.setOpaque(false);
        filaBoton.setBorder(new EmptyBorder(20, 0, 0, 0));
        filaBoton.add(btnReservar);
        derecha.add(filaBoton, BorderLayout.SOUTH);

        form.add(derecha, BorderLayout.CENTER);
        panel.add(form, BorderLayout.NORTH);

        // --- Sección inferior: encabezado + tabla / estado vacío ---
        JPanel inferior = new JPanel(new BorderLayout(0, 10));
        inferior.setOpaque(false);
        inferior.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel encabezadoTabla = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        encabezadoTabla.setOpaque(false);
        JLabel lblIconoTabla = new JLabel("📅");
        lblIconoTabla.setFont(Theme.iconFont(16));
        JLabel lblTituloTabla = new JLabel("Reservas registradas");
        lblTituloTabla.setFont(Theme.FONT_HEADING);
        lblTituloTabla.setForeground(Theme.TEXT);
        encabezadoTabla.add(lblIconoTabla);
        encabezadoTabla.add(lblTituloTabla);
        inferior.add(encabezadoTabla, BorderLayout.NORTH);

        modeloReservas = new DefaultTableModel(
                new Object[]{"N°", "Cliente", "Mesa", "Fecha/Hora", "Personas", "Confirmada"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaReservas = new JTable(modeloReservas);
        Theme.styleTable(tablaReservas);
        Theme.badgeColumn(tablaReservas, 5);

        panelCentroReservas = new JPanel(new CardLayout());
        panelCentroReservas.setOpaque(false);
        JScrollPane spReservas = Theme.scrollCard(tablaReservas);
        panelCentroReservas.add(spReservas, "tabla");

        JPanel estadoVacioReservas = Theme.card(new BorderLayout());
        JPanel contenidoVacioReservas = new JPanel();
        contenidoVacioReservas.setOpaque(false);
        contenidoVacioReservas.setLayout(new BoxLayout(contenidoVacioReservas, BoxLayout.Y_AXIS));
        JComponent iconoVacioReservas = Theme.circuloIcono("📅", Theme.PRIMARY, Theme.PRIMARY_LIGHT, 56);
        iconoVacioReservas.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblVacioTituloReservas = new JLabel("No hay reservas registradas.");
        lblVacioTituloReservas.setFont(Theme.FONT_HEADING);
        lblVacioTituloReservas.setForeground(Theme.TEXT);
        lblVacioTituloReservas.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblVacioTextoReservas = new JLabel("Las reservas que registres aparecerán aquí.");
        lblVacioTextoReservas.setFont(Theme.FONT_BODY);
        lblVacioTextoReservas.setForeground(Theme.TEXT_MUTED);
        lblVacioTextoReservas.setAlignmentX(Component.CENTER_ALIGNMENT);
        contenidoVacioReservas.add(Box.createVerticalGlue());
        contenidoVacioReservas.add(iconoVacioReservas);
        contenidoVacioReservas.add(Box.createVerticalStrut(14));
        contenidoVacioReservas.add(lblVacioTituloReservas);
        contenidoVacioReservas.add(Box.createVerticalStrut(4));
        contenidoVacioReservas.add(lblVacioTextoReservas);
        contenidoVacioReservas.add(Box.createVerticalGlue());
        estadoVacioReservas.add(contenidoVacioReservas, BorderLayout.CENTER);
        panelCentroReservas.add(estadoVacioReservas, "vacio");

        inferior.add(panelCentroReservas, BorderLayout.CENTER);
        panel.add(inferior, BorderLayout.CENTER);

        btnReservar.addActionListener(e -> {
            Cliente cliente = (Cliente) comboClientesReserva.getSelectedItem();
            Mesa mesa = (Mesa) comboMesasReserva.getSelectedItem();
            int personas = (Integer) spinnerPersonasReserva.getValue();
            java.util.Date fecha = (java.util.Date) spinnerFechaReserva.getValue();
            LocalDateTime fechaHora = fecha.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            if (cliente == null || mesa == null) { mensaje("Selecciona cliente y mesa."); return; }
            restaurante.hacerReserva(cliente, mesa, fechaHora, personas);
            refrescarReservas();
        });

        refrescarReservas();
        return panel;
    }

    /** Bloque de campo con etiqueta e icono, usado en el formulario de reservas. */
    private JPanel campoConIcono(String icono, String etiqueta, JComponent campo) {
        JPanel bloque = new JPanel();
        bloque.setOpaque(false);
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(Theme.FONT_BODY_BOLD);
        lblEtiqueta.setForeground(Theme.TEXT);
        lblEtiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel fila = new JPanel(new BorderLayout(8, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(Theme.iconFont(14));
        lblIcono.setForeground(Theme.TEXT_MUTED);
        fila.add(lblIcono, BorderLayout.WEST);
        fila.add(campo, BorderLayout.CENTER);
        bloque.add(lblEtiqueta);
        bloque.add(Box.createVerticalStrut(6));
        bloque.add(fila);
        return bloque;
    }

    private void refrescarReservas() {
        if (modeloReservas == null) return;
        modeloReservas.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Reserva r : restaurante.getReservas()) {
            modeloReservas.addRow(new Object[]{
                    r.getId(), r.getCliente().getNombre(), r.getMesa().getNumero(),
                    r.getFechaHora().format(fmt), r.getNumeroPersonas(),
                    r.isConfirmada() ? "Sí" : "No"
            });
        }
        if (panelCentroReservas != null) {
            CardLayout cl = (CardLayout) panelCentroReservas.getLayout();
            cl.show(panelCentroReservas, modeloReservas.getRowCount() == 0 ? "vacio" : "tabla");
        }
    }

    // ======================================================================
    // PANEL: INVENTARIO
    // ======================================================================
    private static final java.util.Map<String, String> ICONOS_INSUMO = new java.util.HashMap<>();
    static {
        ICONOS_INSUMO.put("pollo", "🍗");
        ICONOS_INSUMO.put("papa", "🥔");
        ICONOS_INSUMO.put("arroz", "🍚");
        ICONOS_INSUMO.put("aceite", "🫒");
        ICONOS_INSUMO.put("ají", "🧄");
        ICONOS_INSUMO.put("aji", "🧄");
        ICONOS_INSUMO.put("limón", "🍋");
        ICONOS_INSUMO.put("limon", "🍋");
        ICONOS_INSUMO.put("lechuga", "🥬");
        ICONOS_INSUMO.put("tomate", "🍅");
        ICONOS_INSUMO.put("cebolla", "🧅");
        ICONOS_INSUMO.put("gaseosa", "🥤");
    }

    private JPanel crearPanelInventario() {
        JPanel panel = fondoPanel();

        // --- Encabezado: icono + título/subtítulo a la izquierda, tarjeta de aviso a la derecha ---
        JPanel encabezado = new JPanel(new BorderLayout(16, 0));
        encabezado.setOpaque(false);

        JPanel izqEncabezado = new JPanel(new BorderLayout(14, 0));
        izqEncabezado.setOpaque(false);
        JComponent iconoInventario = Theme.circuloIcono("🧺", Theme.PRIMARY, Theme.PRIMARY_LIGHT, 56);
        izqEncabezado.add(iconoInventario, BorderLayout.WEST);
        JPanel textosInventario = new JPanel();
        textosInventario.setOpaque(false);
        textosInventario.setLayout(new BoxLayout(textosInventario, BoxLayout.Y_AXIS));
        JLabel lblTituloInventario = new JLabel("Inventario de insumos");
        lblTituloInventario.setFont(Theme.FONT_TITLE.deriveFont(22f));
        lblTituloInventario.setForeground(Theme.TEXT);
        lblTituloInventario.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblSubInventario = Theme.subtle("Consulta el stock disponible y reabastece cuando sea necesario.");
        lblSubInventario.setFont(Theme.FONT_BODY);
        lblSubInventario.setAlignmentX(Component.LEFT_ALIGNMENT);
        textosInventario.add(lblTituloInventario);
        textosInventario.add(Box.createVerticalStrut(2));
        textosInventario.add(lblSubInventario);
        izqEncabezado.add(textosInventario, BorderLayout.CENTER);
        encabezado.add(izqEncabezado, BorderLayout.WEST);

        JPanel avisoInventario = new JPanel(new BorderLayout(10, 0));
        avisoInventario.setBackground(Theme.PRIMARY_LIGHT);
        avisoInventario.setBorder(BorderFactory.createCompoundBorder(
                new Theme.RoundedLineBorder(Theme.BORDER, 12),
                new EmptyBorder(10, 14, 10, 14)));
        JLabel lblIconoAviso = new JLabel("📋");
        lblIconoAviso.setFont(Theme.iconFont(20));
        avisoInventario.add(lblIconoAviso, BorderLayout.WEST);
        JPanel textoAviso = new JPanel();
        textoAviso.setOpaque(false);
        textoAviso.setLayout(new BoxLayout(textoAviso, BoxLayout.Y_AXIS));
        JLabel lblAviso1 = new JLabel("Mantener el inventario actualizado");
        lblAviso1.setFont(Theme.FONT_BODY_BOLD);
        lblAviso1.setForeground(Theme.TEXT);
        JLabel lblAviso2 = new JLabel("te ayuda a controlar costos y evitar faltantes.");
        lblAviso2.setFont(Theme.FONT_SMALL);
        lblAviso2.setForeground(Theme.TEXT_MUTED);
        textoAviso.add(lblAviso1);
        textoAviso.add(lblAviso2);
        avisoInventario.add(textoAviso, BorderLayout.CENTER);
        encabezado.add(avisoInventario, BorderLayout.EAST);

        panel.add(encabezado, BorderLayout.NORTH);

        modeloInventario = new DefaultTableModel(
                new Object[]{"# ID", "🎁 Insumo", "📦 Cantidad", "🥤 Unidad", "$ P. Unit S/.", "💰 Valor total S/."}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaInventario = new JTable(modeloInventario);
        Theme.styleTable(tablaInventario);
        panel.add(Theme.scrollCard(tablaInventario), BorderLayout.CENTER);

        // --- Pie: botones a la izquierda, mascota/consejo a la derecha ---
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        JPanel botones = barraBotones();
        JButton btnReabastecer = Theme.primaryButton("🔄  Reabastecer seleccionado");
        botones.add(btnReabastecer);
        JButton btnRefrescar = Theme.outlineButton("🔄  Actualizar");
        botones.add(btnRefrescar);
        pie.add(botones, BorderLayout.WEST);

        JPanel mascota = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        mascota.setOpaque(false);
        JPanel burbuja = new JPanel();
        burbuja.setLayout(new BoxLayout(burbuja, BoxLayout.Y_AXIS));
        burbuja.setBackground(Theme.SURFACE);
        burbuja.setBorder(BorderFactory.createCompoundBorder(
                new Theme.RoundedLineBorder(Theme.BORDER, 12),
                new EmptyBorder(8, 12, 8, 12)));
        JLabel lblBurbuja1 = new JLabel("¡Todo en orden!");
        lblBurbuja1.setFont(Theme.FONT_BODY_BOLD);
        lblBurbuja1.setForeground(Theme.PRIMARY);
        JLabel lblBurbuja2 = new JLabel("Revisa tu inventario y");
        lblBurbuja2.setFont(Theme.FONT_SMALL);
        lblBurbuja2.setForeground(Theme.TEXT_MUTED);
        JLabel lblBurbuja3 = new JLabel("mantén tu cocina siempre lista.");
        lblBurbuja3.setFont(Theme.FONT_SMALL);
        lblBurbuja3.setForeground(Theme.TEXT_MUTED);
        burbuja.add(lblBurbuja1);
        burbuja.add(lblBurbuja2);
        burbuja.add(lblBurbuja3);
        JLabel lblMascota = new JLabel("🐔");
        lblMascota.setFont(Theme.iconFont(38));
        mascota.add(burbuja);
        mascota.add(lblMascota);
        pie.add(mascota, BorderLayout.EAST);

        panel.add(pie, BorderLayout.SOUTH);

        btnReabastecer.addActionListener(e -> {
            int fila = tablaInventario.getSelectedRow();
            if (fila < 0) { mensaje("Selecciona un insumo."); return; }
            int id = (int) modeloInventario.getValueAt(fila, 0);
            String texto = JOptionPane.showInputDialog(this, "Cantidad a agregar:", "10");
            if (texto == null) return;
            try {
                int cantidad = Integer.parseInt(texto.trim());
                restaurante.reabastecerIngrediente(id, cantidad);
                refrescarInventario();
            } catch (NumberFormatException ex) { mensaje("Ingresa un número válido."); }
        });
        btnRefrescar.addActionListener(e -> refrescarInventario());
        refrescarInventario();
        return panel;
    }

    private void refrescarInventario() {
        if (modeloInventario == null) return;
        modeloInventario.setRowCount(0);
        for (Ingrediente ing : restaurante.getIngredientes().values()) {
            String icono = ICONOS_INSUMO.getOrDefault(ing.getNombre().toLowerCase(), "🧺");
            modeloInventario.addRow(new Object[]{
                    ing.getId(), icono + "  " + ing.getNombre(), ing.getCantidad(), ing.getUnidadMedida(),
                    String.format("%.2f", ing.getPrecioUnitario()),
                    String.format("%.2f", ing.getCantidad() * ing.getPrecioUnitario())
            });
        }
    }

    // ======================================================================
    // PANEL: CARTA (ADMIN)
    // ======================================================================
    private static final java.util.Map<String, Color[]> COLORES_CATEGORIA = new java.util.HashMap<>();
    private static final java.util.Map<String, String> ICONOS_CATEGORIA = new java.util.HashMap<>();
    static {
        COLORES_CATEGORIA.put("POLLOS A LA BRASA", new Color[]{new Color(0xC2, 0x41, 0x1C), new Color(0xFB, 0xE3, 0xD8)});
        COLORES_CATEGORIA.put("COMBOS", new Color[]{new Color(0x24, 0x7A, 0x3D), new Color(0xE2, 0xF3, 0xE6)});
        COLORES_CATEGORIA.put("BROASTER", new Color[]{new Color(0xA8, 0x63, 0x00), new Color(0xFC, 0xEF, 0xD8)});
        COLORES_CATEGORIA.put("ALITAS Y CHICHARRÓN", new Color[]{new Color(0x6A, 0x3D, 0x9A), new Color(0xEC, 0xE3, 0xF7)});
        COLORES_CATEGORIA.put("ACOMPAÑAMIENTOS", new Color[]{new Color(0x1B, 0x5E, 0x8C), new Color(0xE1, 0xEE, 0xF6)});
        COLORES_CATEGORIA.put("ENSALADAS", new Color[]{new Color(0x24, 0x7A, 0x3D), new Color(0xE2, 0xF3, 0xE6)});
        COLORES_CATEGORIA.put("BEBIDAS", new Color[]{new Color(0x1B, 0x5E, 0x8C), new Color(0xE1, 0xEE, 0xF6)});
        COLORES_CATEGORIA.put("POSTRES", new Color[]{new Color(0xBF, 0x2E, 0x2E), new Color(0xFB, 0xE7, 0xE7)});

        ICONOS_CATEGORIA.put("POLLOS A LA BRASA", "🍗");
        ICONOS_CATEGORIA.put("COMBOS", "🍱");
        ICONOS_CATEGORIA.put("BROASTER", "🍢");
        ICONOS_CATEGORIA.put("ALITAS Y CHICHARRÓN", "🍢");
        ICONOS_CATEGORIA.put("ACOMPAÑAMIENTOS", "🥗");
        ICONOS_CATEGORIA.put("ENSALADAS", "🥬");
        ICONOS_CATEGORIA.put("BEBIDAS", "🥤");
        ICONOS_CATEGORIA.put("POSTRES", "🍮");
    }

    /** Renderer tipo "pill" de colores para la columna Categoría de la carta. */
    private class CategoriaBadgeRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int col) {
            String texto = value == null ? "" : value.toString();
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            wrapper.setOpaque(true);
            wrapper.setBackground(row % 2 == 0 ? Theme.SURFACE : Theme.SURFACE_ALT);
            Color[] colores = COLORES_CATEGORIA.get(texto.toUpperCase());
            Color fg = colores != null ? colores[0] : Theme.TEXT;
            Color bg = colores != null ? colores[1] : Theme.SURFACE_ALT;
            String icono = ICONOS_CATEGORIA.getOrDefault(texto.toUpperCase(), "🍽");
            JLabel pill = new JLabel(icono + "  " + texto);
            pill.setFont(Theme.FONT_BADGE);
            pill.setForeground(fg);
            pill.setOpaque(true);
            pill.setBackground(bg);
            pill.setBorder(new EmptyBorder(4, 10, 4, 10));
            wrapper.add(pill);
            wrapper.setBorder(new EmptyBorder(0, 14, 0, 14));
            return wrapper;
        }
    }

    /** Renderer del icono de menú "⋮" al final de cada fila de la carta. */
    private static class MenuKebabRenderer extends DefaultTableCellRenderer {
        MenuKebabRenderer() {
            setHorizontalAlignment(CENTER);
            setForeground(Theme.TEXT_MUTED);
            setFont(Theme.FONT_BODY_BOLD);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, "⋮", isSelected, hasFocus, row, col);
            setBackground(row % 2 == 0 ? Theme.SURFACE : Theme.SURFACE_ALT);
            return this;
        }
    }

    private JPanel crearPanelCarta() {
        JPanel panel = fondoPanel();
        panel.add(panelEncabezado("Gestión de la carta",
                "Cada cambio guarda un snapshot para poder deshacer (patrón Memento)."), BorderLayout.NORTH);
        modeloCartaAdmin = new DefaultTableModel(
                new Object[]{"ID", "Plato", "Categoría", "Precio S/.", ""}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaCartaAdmin = new JTable(modeloCartaAdmin);
        Theme.styleTable(tablaCartaAdmin);
        tablaCartaAdmin.getColumnModel().getColumn(2).setCellRenderer(new CategoriaBadgeRenderer());
        tablaCartaAdmin.getColumnModel().getColumn(4).setCellRenderer(new MenuKebabRenderer());
        tablaCartaAdmin.getColumnModel().getColumn(4).setMaxWidth(46);
        tablaCartaAdmin.getColumnModel().getColumn(4).setPreferredWidth(46);
        tablaCartaAdmin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int fila = tablaCartaAdmin.rowAtPoint(e.getPoint());
                int col = tablaCartaAdmin.columnAtPoint(e.getPoint());
                if (fila < 0) return;
                tablaCartaAdmin.setRowSelectionInterval(fila, fila);
                if (col == 4) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem quitar = new JMenuItem("🗑  Quitar plato");
                    quitar.addActionListener(ev -> accionQuitarPlatoSeleccionado());
                    menu.add(quitar);
                    menu.show(tablaCartaAdmin, e.getX(), e.getY());
                }
            }
        });
        panel.add(Theme.scrollCard(tablaCartaAdmin), BorderLayout.CENTER);
        JPanel botones = barraBotones();
        JButton btnAgregar = Theme.primaryButton("+  Agregar plato");
        JButton btnQuitar = Theme.dangerButton("🗑  Quitar plato seleccionado");
        JButton btnDeshacer = Theme.outlineButton("🕐  Deshacer último cambio");
        botones.add(btnAgregar);
        botones.add(btnQuitar);
        botones.add(btnDeshacer);
        lblHistorialMemento = new JLabel();
        lblHistorialMemento.setFont(Theme.FONT_SMALL);
        lblHistorialMemento.setForeground(Theme.TEXT_MUTED);
        lblHistorialMemento.setIcon(null);
        lblHistorialMemento.setBorder(new EmptyBorder(0, 4, 6, 0));
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setOpaque(false);
        JPanel filaHistorial = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filaHistorial.setOpaque(false);
        JLabel lblRelojHistorial = new JLabel("🕒");
        lblRelojHistorial.setFont(Theme.iconFont(14));
        lblRelojHistorial.setForeground(Theme.TEXT_MUTED);
        filaHistorial.add(lblRelojHistorial);
        filaHistorial.add(lblHistorialMemento);
        panelSur.add(filaHistorial, BorderLayout.WEST);
        panelSur.add(botones, BorderLayout.EAST);
        panel.add(panelSur, BorderLayout.SOUTH);
        btnAgregar.addActionListener(e -> dialogoNuevoPlato());
        btnQuitar.addActionListener(e -> accionQuitarPlatoSeleccionado());
        btnDeshacer.addActionListener(e -> {
            boolean ok = restaurante.getDatosService().deshacerCambioCarta();
            if (!ok) mensaje("No hay cambios anteriores guardados para deshacer.");
            refrescarTodo();
        });
        refrescarCarta();
        return panel;
    }

    private void accionQuitarPlatoSeleccionado() {
        int fila = tablaCartaAdmin.getSelectedRow();
        if (fila < 0) { mensaje("Selecciona un plato de la carta."); return; }
        int id = (int) modeloCartaAdmin.getValueAt(fila, 0);
        Plato objetivo = null;
        for (Plato p : restaurante.getDatosService().getMenu()) {
            if (p.getId() == id) { objetivo = p; break; }
        }
        if (objetivo != null) { restaurante.getDatosService().quitarPlatoDeCarta(objetivo); refrescarTodo(); }
    }

    private void dialogoNuevoPlato() {
        JTextField nombre = new JTextField();
        JTextField categoria = new JTextField();
        JTextField precio = new JTextField();
        JTextField descripcion = new JTextField();
        JPanel form = formularioDialogo(
                "Nombre del plato:", nombre, "Categoría:", categoria,
                "Precio (S/.):", precio, "Descripción:", descripcion);
        int resultado = JOptionPane.showConfirmDialog(this, form, "Nuevo plato",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultado == JOptionPane.OK_OPTION) {
            try {
                double precioVal = Double.parseDouble(precio.getText().trim().replace(",", "."));
                int nuevoId = restaurante.getDatosService().getMenu().size() + 1;
                Plato nuevo = new Plato(nuevoId, nombre.getText().trim(), precioVal,
                        descripcion.getText().trim(), categoria.getText().trim().toUpperCase());
                restaurante.getDatosService().agregarPlatoACarta(nuevo);
                refrescarTodo();
            } catch (NumberFormatException ex) { mensaje("Ingresa un precio válido."); }
        }
    }

    private void refrescarCarta() {
        if (modeloCartaAdmin != null) {
            modeloCartaAdmin.setRowCount(0);
            for (Plato p : restaurante.getDatosService().getMenu()) {
                modeloCartaAdmin.addRow(new Object[]{
                        p.getId(), p.getNombre(), p.getCategoria(),
                        String.format("%.2f", p.getPrecio()), ""
                });
            }
            lblHistorialMemento.setText("Snapshots guardados en el historial (Memento): "
                    + restaurante.getDatosService().getCantidadEstadosGuardados());
        }
        poblarTablaCarta();
    }

    // ======================================================================
    // PANEL: PERSONAL
    // ======================================================================
    private static final java.util.Map<String, Color[]> COLORES_ROL = new java.util.HashMap<>();
    private static final java.util.Map<String, String> ICONOS_ROL = new java.util.HashMap<>();
    static {
        COLORES_ROL.put("MESERO", new Color[]{new Color(0x1B, 0x5E, 0x8C), new Color(0xE1, 0xEE, 0xF6)});
        COLORES_ROL.put("COCINERO", new Color[]{new Color(0x24, 0x7A, 0x3D), new Color(0xE2, 0xF3, 0xE6)});
        COLORES_ROL.put("ADMINISTRADOR", new Color[]{new Color(0x6A, 0x3D, 0x9A), new Color(0xEC, 0xE3, 0xF7)});
        ICONOS_ROL.put("MESERO", "👤");
        ICONOS_ROL.put("COCINERO", "👨‍🍳");
        ICONOS_ROL.put("ADMINISTRADOR", "🛡");
    }

    /** Renderer tipo "pill" de colores para la columna Rol de personal. */
    private class RolBadgeRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int col) {
            String texto = value == null ? "" : value.toString();
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            wrapper.setOpaque(true);
            wrapper.setBackground(isSelected ? new Color(0xF6, 0xDF, 0xC8) : (row % 2 == 0 ? Theme.SURFACE : Theme.SURFACE_ALT));
            Color[] colores = COLORES_ROL.get(texto.toUpperCase());
            Color fg = colores != null ? colores[0] : Theme.TEXT;
            Color bg = colores != null ? colores[1] : Theme.SURFACE_ALT;
            String icono = ICONOS_ROL.getOrDefault(texto.toUpperCase(), "👤");
            JLabel pill = new JLabel(icono + "  " + texto);
            pill.setFont(Theme.FONT_BADGE);
            pill.setForeground(fg);
            pill.setOpaque(true);
            pill.setBackground(bg);
            pill.setBorder(new EmptyBorder(4, 10, 4, 10));
            wrapper.add(pill);
            wrapper.setBorder(new EmptyBorder(0, 14, 0, 14));
            return wrapper;
        }
    }

    private JPanel crearPanelPersonal() {
        JPanel panel = fondoPanel();
        panel.add(panelEncabezado("Personal del local",
                "Mesero, Cocinero y Administrador son subtipos de Empleado."), BorderLayout.NORTH);
        modeloPersonal = new DefaultTableModel(
                new Object[]{"ID", "NOMBRE", "ROL", "TURNO", "SALARIO S/."}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaPersonal = new JTable(modeloPersonal);
        Theme.styleTable(tablaPersonal);
        tablaPersonal.getColumnModel().getColumn(2).setCellRenderer(new RolBadgeRenderer());
        panel.add(Theme.scrollCard(tablaPersonal), BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout(0, 10));
        sur.setOpaque(false);
        sur.setBorder(new EmptyBorder(12, 0, 0, 0));
        JButton btnFuncion = Theme.primaryButton("👤  VER FUNCIÓN DEL EMPLEADO SELECCIONADO");
        JPanel filaBoton = barraBotones();
        filaBoton.add(btnFuncion);
        sur.add(filaBoton, BorderLayout.NORTH);

        panelCentroPersonal = new JPanel(new CardLayout());
        panelCentroPersonal.setOpaque(false);

        JPanel estadoVacioPersonal = Theme.card(new FlowLayout(FlowLayout.LEFT, 12, 14));
        JComponent iconoVacioPersonal = Theme.circuloIcono("ⓘ", Theme.TEXT_MUTED, Theme.SURFACE_ALT, 34);
        estadoVacioPersonal.add(iconoVacioPersonal);
        JLabel lblVacioPersonal = new JLabel("Seleccione un empleado para ver su función y responsabilidades.");
        lblVacioPersonal.setFont(Theme.FONT_BODY);
        lblVacioPersonal.setForeground(Theme.TEXT_MUTED);
        estadoVacioPersonal.add(lblVacioPersonal);
        panelCentroPersonal.add(estadoVacioPersonal, "vacio");

        areaFuncionPersonal = new JTextArea(4, 20);
        areaFuncionPersonal.setEditable(false);
        areaFuncionPersonal.setFont(Theme.FONT_MONO);
        areaFuncionPersonal.setBackground(Theme.SURFACE);
        areaFuncionPersonal.setForeground(Theme.TEXT);
        areaFuncionPersonal.setBorder(new EmptyBorder(10, 12, 10, 12));
        JScrollPane spFuncion = new JScrollPane(areaFuncionPersonal);
        spFuncion.setBorder(new Theme.RoundedLineBorder(Theme.BORDER, 12));
        panelCentroPersonal.add(spFuncion, "funcion");

        sur.add(panelCentroPersonal, BorderLayout.CENTER);
        panel.add(sur, BorderLayout.SOUTH);

        btnFuncion.addActionListener(e -> {
            int fila = tablaPersonal.getSelectedRow();
            if (fila < 0) { mensaje("Selecciona un empleado de la lista."); return; }
            int id = (int) modeloPersonal.getValueAt(fila, 0);
            Empleado empleado = null;
            for (Empleado emp : restaurante.getDatosService().getEmpleados()) {
                if (emp.getId() == id) { empleado = emp; break; }
            }
            if (empleado != null) {
                areaFuncionPersonal.append(empleado.getRolDescripcion() + "\n");
                CardLayout cl = (CardLayout) panelCentroPersonal.getLayout();
                cl.show(panelCentroPersonal, "funcion");
            }
        });

        for (Empleado emp : restaurante.getDatosService().getEmpleados()) {
            modeloPersonal.addRow(new Object[]{
                    emp.getId(), emp.getNombre(), emp.getClass().getSimpleName(),
                    emp.getTurno(), String.format("%.2f", emp.getSalario())
            });
        }
        return panel;
    }

    // ======================================================================
    // PANEL: REPORTE
    // ======================================================================
    private JPanel crearPanelReporte() {
        JPanel panel = fondoPanel();

        JPanel envoltorioNorte = new JPanel();
        envoltorioNorte.setOpaque(false);
        envoltorioNorte.setLayout(new BoxLayout(envoltorioNorte, BoxLayout.Y_AXIS));
        JPanel top = barraBotones();
        top.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnGenerar = Theme.primaryButton("\uD83D\uDCC4  Generar reporte de ventas del día");
        top.add(btnGenerar);
        envoltorioNorte.add(top);
        envoltorioNorte.add(Box.createVerticalStrut(14));
        panel.add(envoltorioNorte, BorderLayout.NORTH);

        panelCentroReporte = new JPanel(new CardLayout());
        panelCentroReporte.setOpaque(false);

        // --- Estado vacío (antes de generar el primer reporte) ---
        JPanel estadoVacio = Theme.card(new BorderLayout());
        JPanel contenidoVacio = new JPanel();
        contenidoVacio.setOpaque(false);
        contenidoVacio.setLayout(new BoxLayout(contenidoVacio, BoxLayout.Y_AXIS));
        JComponent iconoVacio = Theme.circuloIcono("📊", Theme.PRIMARY, Theme.PRIMARY_LIGHT, 64);
        iconoVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblVacioTitulo = new JLabel("Aún no se generó ningún reporte");
        lblVacioTitulo.setFont(Theme.FONT_HEADING);
        lblVacioTitulo.setForeground(Theme.TEXT);
        lblVacioTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblVacioTexto = new JLabel("<html><div style='text-align:center;width:320px;'>"
                + "Pulsa «Generar reporte de ventas del día» para ver el resumen de ventas y pedidos.</div></html>");
        lblVacioTexto.setFont(Theme.FONT_BODY);
        lblVacioTexto.setForeground(Theme.TEXT_MUTED);
        lblVacioTexto.setAlignmentX(Component.CENTER_ALIGNMENT);
        contenidoVacio.add(Box.createVerticalGlue());
        contenidoVacio.add(iconoVacio);
        contenidoVacio.add(Box.createVerticalStrut(14));
        contenidoVacio.add(lblVacioTitulo);
        contenidoVacio.add(Box.createVerticalStrut(6));
        contenidoVacio.add(lblVacioTexto);
        contenidoVacio.add(Box.createVerticalGlue());
        estadoVacio.add(contenidoVacio, BorderLayout.CENTER);
        panelCentroReporte.add(estadoVacio, "vacio");

        // --- Estado con el reporte generado ---
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        JPanel encabezado = Theme.card(new BorderLayout(14, 0));
        encabezado.setAlignmentX(Component.LEFT_ALIGNMENT);
        JComponent iconoCabecera = Theme.circuloIcono("📊", Theme.PRIMARY, Theme.PRIMARY_LIGHT, 52);
        encabezado.add(iconoCabecera, BorderLayout.WEST);
        JPanel textosCabecera = new JPanel();
        textosCabecera.setOpaque(false);
        textosCabecera.setLayout(new BoxLayout(textosCabecera, BoxLayout.Y_AXIS));
        JLabel lblTituloReporte = new JLabel("REPORTE DEL DÍA");
        lblTituloReporte.setFont(Theme.FONT_TITLE.deriveFont(22f));
        lblTituloReporte.setForeground(Theme.TEXT);
        lblTituloReporte.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblSubtituloReporte = Theme.subtle("Resumen de ventas y pedidos del día");
        lblSubtituloReporte.setFont(Theme.FONT_BODY);
        lblSubtituloReporte.setAlignmentX(Component.LEFT_ALIGNMENT);
        textosCabecera.add(lblTituloReporte);
        textosCabecera.add(Box.createVerticalStrut(2));
        textosCabecera.add(lblSubtituloReporte);
        encabezado.add(textosCabecera, BorderLayout.CENTER);

        JPanel chipFecha = new JPanel();
        chipFecha.setOpaque(true);
        chipFecha.setBackground(Theme.SURFACE_ALT);
        chipFecha.setLayout(new BoxLayout(chipFecha, BoxLayout.Y_AXIS));
        chipFecha.setBorder(BorderFactory.createCompoundBorder(
                new Theme.RoundedLineBorder(Theme.BORDER, 10),
                new EmptyBorder(8, 14, 8, 14)));
        JLabel lblChipTitulo = new JLabel("📅  Fecha de generación");
        lblChipTitulo.setFont(Theme.FONT_SMALL);
        lblChipTitulo.setForeground(Theme.TEXT_MUTED);
        lblChipTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblRepFecha = new JLabel("--/--/---- --:--");
        lblRepFecha.setFont(Theme.FONT_BODY_BOLD);
        lblRepFecha.setForeground(Theme.PRIMARY);
        lblRepFecha.setAlignmentX(Component.CENTER_ALIGNMENT);
        chipFecha.add(lblChipTitulo);
        chipFecha.add(lblRepFecha);
        encabezado.add(chipFecha, BorderLayout.EAST);

        contenido.add(encabezado);
        contenido.add(Box.createVerticalStrut(16));

        JPanel tituloResumen = tituloSeccionReporte("RESUMEN GENERAL");
        tituloResumen.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(tituloResumen);
        contenido.add(Box.createVerticalStrut(10));

        JPanel filaStatsReporte = new JPanel(new GridLayout(1, 5, 14, 0));
        filaStatsReporte.setOpaque(false);
        filaStatsReporte.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblRepTotalPedidos = new JLabel("0");
        lblRepEntregados = new JLabel("0");
        lblRepEnCurso = new JLabel("0");
        lblRepCancelados = new JLabel("0");
        lblRepVentas = new JLabel("S/. 0.00");
        filaStatsReporte.add(tarjetaResumenPedido("📋", Theme.INFO, Theme.INFO_BG,
                lblRepTotalPedidos, "Total de pedidos", "registrados"));
        filaStatsReporte.add(tarjetaResumenPedido("✅", Theme.SUCCESS, Theme.SUCCESS_BG,
                lblRepEntregados, "Pedidos", "entregados"));
        filaStatsReporte.add(tarjetaResumenPedido("⏰", Theme.WARNING, Theme.WARNING_BG,
                lblRepEnCurso, "Pedidos", "en curso"));
        filaStatsReporte.add(tarjetaResumenPedido("✖", Theme.DANGER, Theme.DANGER_BG,
                lblRepCancelados, "Pedidos", "cancelados"));
        filaStatsReporte.add(tarjetaResumenPedido("💰", Theme.PRIMARY, Theme.PRIMARY_LIGHT,
                lblRepVentas, "Ventas totales", "(entregados)"));
        contenido.add(filaStatsReporte);
        contenido.add(Box.createVerticalStrut(18));

        JPanel tituloDetalle = tituloSeccionReporte("DETALLE DE PEDIDOS");
        tituloDetalle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(tituloDetalle);
        contenido.add(Box.createVerticalStrut(10));

        modeloReporte = new DefaultTableModel(
                new Object[]{"#", "Cliente / Pedido", "Mesa", "Estado", "Total (S/.)"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaReporte = new JTable(modeloReporte);
        Theme.styleTable(tablaReporte);
        tablaReporte.setRowHeight(40);
        Theme.badgeColumn(tablaReporte, 3);
        JScrollPane spReporte = Theme.scrollCard(tablaReporte);
        spReporte.setAlignmentX(Component.LEFT_ALIGNMENT);
        spReporte.setPreferredSize(new Dimension(10, 240));
        spReporte.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        contenido.add(spReporte);
        contenido.add(Box.createVerticalStrut(18));

        JPanel tarjetaVentas = new JPanel(new BorderLayout(16, 0));
        tarjetaVentas.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjetaVentas.setOpaque(true);
        tarjetaVentas.setBackground(Theme.PRIMARY_LIGHT);
        tarjetaVentas.setBorder(BorderFactory.createCompoundBorder(
                new Theme.RoundedLineBorder(Theme.BORDER, 14),
                new EmptyBorder(20, 22, 20, 22)));
        JComponent iconoTrofeo = Theme.circuloIcono("🏆", Theme.ACCENT_DARK, Color.WHITE, 56);
        tarjetaVentas.add(iconoTrofeo, BorderLayout.WEST);
        JPanel textosVentas = new JPanel();
        textosVentas.setOpaque(false);
        textosVentas.setLayout(new BoxLayout(textosVentas, BoxLayout.Y_AXIS));
        JLabel lblVentasLinea1 = new JLabel("Ventas totales del día (entregados)");
        lblVentasLinea1.setFont(Theme.FONT_BODY_BOLD);
        lblVentasLinea1.setForeground(Theme.TEXT);
        lblVentasLinea1.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblRepVentasFooter = new JLabel("S/. 0.00");
        lblRepVentasFooter.setFont(Theme.FONT_TITLE.deriveFont(30f));
        lblRepVentasFooter.setForeground(Theme.PRIMARY_DARK);
        lblRepVentasFooter.setAlignmentX(Component.LEFT_ALIGNMENT);
        textosVentas.add(lblVentasLinea1);
        textosVentas.add(Box.createVerticalStrut(4));
        textosVentas.add(lblRepVentasFooter);
        tarjetaVentas.add(textosVentas, BorderLayout.CENTER);
        contenido.add(tarjetaVentas);

        JScrollPane spContenido = new JScrollPane(contenido);
        spContenido.setBorder(BorderFactory.createEmptyBorder());
        spContenido.getViewport().setOpaque(false);
        spContenido.setOpaque(false);
        spContenido.getVerticalScrollBar().setUnitIncrement(16);
        panelCentroReporte.add(spContenido, "contenido");

        panel.add(panelCentroReporte, BorderLayout.CENTER);
        btnGenerar.addActionListener(e -> generarReporte());
        return panel;
    }

    /** Título de sección pequeño con una barra de acento a la izquierda (usado en el panel de Reporte). */
    private JPanel tituloSeccionReporte(String texto) {
        JPanel fila = new JPanel();
        fila.setOpaque(false);
        fila.setLayout(new BoxLayout(fila, BoxLayout.X_AXIS));
        JPanel barra = new JPanel();
        barra.setBackground(Theme.PRIMARY);
        barra.setPreferredSize(new Dimension(4, 16));
        barra.setMaximumSize(new Dimension(4, 16));
        fila.add(barra);
        fila.add(Box.createHorizontalStrut(8));
        JLabel lbl = new JLabel(texto);
        lbl.setFont(Theme.FONT_BODY_BOLD);
        lbl.setForeground(Theme.TEXT_MUTED);
        fila.add(lbl);
        return fila;
    }

    private void generarReporte() {
        try {
            restaurante.generarReporteVentas();
        } catch (SecurityException ex) {
            modeloReporte.setRowCount(0);
            mensaje("Acceso denegado: activa 'Modo Administrador' (arriba a la derecha) "
                    + "para poder generar reportes.\n\n(Patrón Proxy protegiendo el acceso)");
            return;
        }
        List<Pedido> pedidos = restaurante.getPedidos();
        double totalVentas = 0;
        int completados = 0, cancelados = 0, enCurso = 0;
        for (Pedido p : pedidos) {
            String estado = p.getEstado().getNombreEstado();
            if (estado.equals("ENTREGADO")) { completados++; totalVentas += p.calcularTotal(); }
            else if (estado.equals("CANCELADO")) cancelados++;
            else enCurso++;
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        lblRepFecha.setText(LocalDateTime.now().format(fmt));
        lblRepTotalPedidos.setText(String.valueOf(pedidos.size()));
        lblRepEntregados.setText(String.valueOf(completados));
        lblRepEnCurso.setText(String.valueOf(enCurso));
        lblRepCancelados.setText(String.valueOf(cancelados));
        lblRepVentas.setText(String.format("S/. %.2f", totalVentas));
        lblRepVentasFooter.setText(String.format("S/. %.2f", totalVentas));

        modeloReporte.setRowCount(0);
        for (Pedido p : pedidos) {
            modeloReporte.addRow(new Object[]{
                    p.getId(), p.getCliente().getNombre(), "Mesa " + p.getMesa().getNumero(),
                    p.getEstado().getNombreEstado(), String.format("%.2f", p.calcularTotal())
            });
        }

        CardLayout cl = (CardLayout) panelCentroReporte.getLayout();
        cl.show(panelCentroReporte, "contenido");
    }

    // ======================================================================
    // UTILIDADES
    // ======================================================================
    private JPanel fondoPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBorder(new EmptyBorder(18, 24, 18, 24));
        panel.setBackground(Theme.BG);
        return panel;
    }

    private JPanel panelEncabezado(String titulo, String subtitulo) {
        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        JLabel lblTitulo = Theme.heading(titulo);
        lblTitulo.setFont(Theme.FONT_TITLE);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblSubtitulo = Theme.subtle(subtitulo);
        lblSubtitulo.setFont(Theme.FONT_BODY);
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        encabezado.add(lblTitulo);
        encabezado.add(Box.createVerticalStrut(3));
        encabezado.add(lblSubtitulo);
        return encabezado;
    }

    private JPanel barraBotones() {
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);
        return botones;
    }

    private JPanel formularioDialogo(Object... labelsAndFields) {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.SURFACE);
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        int row = 0;
        for (int i = 0; i < labelsAndFields.length; i += 2) {
            String label = (String) labelsAndFields[i];
            JComponent field = (JComponent) labelsAndFields[i + 1];
            estilizarField(field);
            c.gridx = 0; c.gridy = row; c.weightx = 0;
            JLabel lbl = Theme.formLabel(label);
            lbl.setFont(Theme.FONT_BODY_BOLD);
            form.add(lbl, c);
            c.gridx = 1; c.weightx = 1;
            form.add(field, c);
            row++;
        }
        return form;
    }

    private void estilizarField(JComponent field) {
        field.setFont(Theme.FONT_BODY);
        if (field instanceof JTextField) {
            ((JTextField) field).setColumns(20);
            ((JTextField) field).setPreferredSize(new Dimension(200, 32));
        }
        field.setBorder(BorderFactory.createCompoundBorder(
                new Theme.RoundedLineBorder(Theme.BORDER, 8),
                new EmptyBorder(6, 10, 6, 10)));
    }

    private void estilizarCombo(JComboBox<?> combo) {
        combo.setFont(Theme.FONT_BODY);
        combo.setBackground(Theme.SURFACE);
        combo.setBorder(new EmptyBorder(4, 8, 4, 8));
        combo.setFocusable(false);
        combo.setPreferredSize(new Dimension(180, 32));
    }

    private void estilizarSpinner(JSpinner spinner, int ancho) {
        spinner.setFont(Theme.FONT_BODY);
        spinner.setPreferredSize(new Dimension(ancho, 32));
        spinner.setBorder(BorderFactory.createCompoundBorder(
                new Theme.RoundedLineBorder(Theme.BORDER, 8),
                new EmptyBorder(2, 6, 2, 6)));
    }

    private void mensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }

    // =====================================================================
    // MAIN
    // ======================================================================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) { /* fallback */ }
        UIManager.put("ToolTip.background", Theme.SURFACE);
        UIManager.put("OptionPane.background", Theme.BG);
        UIManager.put("Panel.background", Theme.BG);
        EventQueue.invokeLater(() -> new PolleriaApp().setVisible(true));
    }
}
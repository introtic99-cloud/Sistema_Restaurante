package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * Paleta de colores, tipografías y componentes reutilizables para dar
 * una apariencia moderna y consistente a toda la aplicación.
 */
public final class Theme {

    private Theme() { }

    // ------------------------------------------------------------------
    // Paleta de colores
    // ------------------------------------------------------------------
    public static final Color BG            = new Color(0xF7, 0xEF, 0xE2);
    public static final Color SURFACE       = Color.WHITE;
    public static final Color SURFACE_ALT   = new Color(0xFC, 0xF6, 0xEC);
    public static final Color BORDER        = new Color(0xE6, 0xD7, 0xBF);
    public static final Color BORDER_SOFT   = new Color(0xEF, 0xE3, 0xD0);

    public static final Color PRIMARY       = new Color(0xC2, 0x41, 0x1C);
    public static final Color PRIMARY_DARK  = new Color(0x9C, 0x30, 0x12);
    public static final Color PRIMARY_LIGHT = new Color(0xF3, 0xE1, 0xD8);

    public static final Color ACCENT        = new Color(0xEF, 0xA5, 0x3C);
    public static final Color ACCENT_DARK   = new Color(0xD6, 0x8B, 0x1E);

    public static final Color HEADER_ROW    = new Color(0x5A, 0x2A, 0x14);

    public static final Color TEXT          = new Color(0x35, 0x24, 0x14);
    public static final Color TEXT_MUTED    = new Color(0x8F, 0x79, 0x63);
    public static final Color TEXT_ON_DARK  = new Color(0xFF, 0xEE, 0xDE);

    public static final Color SUCCESS       = new Color(0x24, 0x7A, 0x3D);
    public static final Color SUCCESS_BG    = new Color(0xE2, 0xF3, 0xE6);
    public static final Color DANGER        = new Color(0xBF, 0x2E, 0x2E);
    public static final Color DANGER_BG     = new Color(0xFB, 0xE7, 0xE7);
    public static final Color WARNING       = new Color(0xA8, 0x63, 0x00);
    public static final Color WARNING_BG    = new Color(0xFC, 0xEF, 0xD8);
    public static final Color INFO          = new Color(0x1B, 0x5E, 0x8C);
    public static final Color INFO_BG       = new Color(0xE1, 0xEE, 0xF6);

    // ------------------------------------------------------------------
    // Tipografía
    // ------------------------------------------------------------------
    private static final String FAMILY = "Segoe UI";

    /**
     * Familia tipográfica para los emojis/iconos (🛒 🍽️ 📋 🍗 ...). "Segoe UI"
     * normal NO incluye esos glifos: forzarla sobre un emoji hace que se
     * dibuje un cuadrado vacío ("tofu"). Aquí se busca en tiempo de
     * ejecución la primera fuente de emoji realmente instalada en el
     * sistema y, si no hay ninguna, se usa la fuente lógica "Dialog" (que
     * en Windows/Linux/Mac sí resuelve el glifo mediante su propio
     * mecanismo de sustitución de fuentes).
     */
    private static final String ICON_FAMILY = pickIconFamily();

    private static String pickIconFamily() {
        java.util.Set<String> disponibles = new java.util.HashSet<>(java.util.Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String candidata : new String[] {
                "Segoe UI Emoji", "Noto Color Emoji", "Segoe UI Symbol", "Apple Color Emoji"
        }) {
            if (disponibles.contains(candidata)) return candidata;
        }
        return "Dialog";
    }

    public static final Font FONT_TITLE      = safeFont(Font.BOLD, 28);
    public static final Font FONT_HEADING    = safeFont(Font.BOLD, 18);
    public static final Font FONT_SUBHEADING = safeFont(Font.PLAIN, 12);
    public static final Font FONT_BODY       = safeFont(Font.PLAIN, 14);
    public static final Font FONT_BODY_BOLD  = safeFont(Font.BOLD, 14);
    public static final Font FONT_SMALL      = safeFont(Font.PLAIN, 12);
    public static final Font FONT_BADGE      = safeFont(Font.BOLD, 12);
    public static final Font FONT_BUTTON     = safeFont(Font.BOLD, 14);
    public static final Font FONT_MONO       = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    private static Font safeFont(int style, int size) {
        return new Font(FAMILY, style, size);
    }

    /** Fuente segura para dibujar emojis/iconos (ver {@link #ICON_FAMILY}). */
    public static Font iconFont(int size) {
        return new Font(ICON_FAMILY, Font.PLAIN, size);
    }

    // ------------------------------------------------------------------
    // Botones redondeados
    // ------------------------------------------------------------------
    public enum ButtonKind { PRIMARY, SECONDARY, OUTLINE, DANGER, SUCCESS }

    public static JButton button(String text, ButtonKind kind) {
        return new RoundedButton(text, kind);
    }

    public static JButton primaryButton(String text)   { return button(text, ButtonKind.PRIMARY); }
    public static JButton secondaryButton(String text) { return button(text, ButtonKind.SECONDARY); }
    public static JButton outlineButton(String text)   { return button(text, ButtonKind.OUTLINE); }
    public static JButton dangerButton(String text)    { return button(text, ButtonKind.DANGER); }
    public static JButton successButton(String text)   { return button(text, ButtonKind.SUCCESS); }

    public static class RoundedButton extends JButton {
        private final ButtonKind kind;
        private boolean hovering = false;
        private final int arc = 10;

        RoundedButton(String text, ButtonKind kind) {
            super(text);
            this.kind = kind;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setFont(FONT_BUTTON);
            setForeground(foregroundFor(kind));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(0, 0, 0, 0));
            setBorder(new EmptyBorder(9, 16, 9, 16));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hovering = false; repaint(); }
            });
        }

        private static Color foregroundFor(ButtonKind kind) {
            switch (kind) {
                case PRIMARY:   return Color.WHITE;
                case SUCCESS:   return Color.WHITE;
                case SECONDARY: return TEXT;
                case DANGER:    return DANGER;
                default:        return PRIMARY;
            }
        }

        private Color fillFor() {
            boolean pressed = getModel().isPressed();
            switch (kind) {
                case PRIMARY:
                    return pressed ? PRIMARY_DARK : (hovering ? PRIMARY_DARK : PRIMARY);
                case SUCCESS:
                    return pressed ? new Color(0x1B, 0x5E, 0x2E) : (hovering ? new Color(0x1B, 0x5E, 0x2E) : SUCCESS);
                case SECONDARY:
                    return pressed ? ACCENT_DARK : (hovering ? ACCENT_DARK : ACCENT);
                case DANGER:
                    return pressed ? new Color(0xF6, 0xC9, 0xC9) : (hovering ? new Color(0xF9, 0xDA, 0xDA) : SURFACE);
                default:
                    return pressed ? PRIMARY_LIGHT : (hovering ? PRIMARY_LIGHT : SURFACE);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = isEnabled() ? fillFor() : new Color(0xE7, 0xDF, 0xD2);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            if (kind == ButtonKind.OUTLINE || kind == ButtonKind.DANGER) {
                g2.setStroke(new BasicStroke(1.3f));
                g2.setColor(isEnabled() ? (kind == ButtonKind.DANGER ? DANGER : PRIMARY) : TEXT_MUTED);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            }
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public boolean isOpaque() { return false; }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width, 44);
        }
    }

    // ------------------------------------------------------------------
    // Botón de navegación superior (icono + texto, estilo "pestaña")
    // ------------------------------------------------------------------
    public static class NavButton extends JToggleButton {
        private boolean hovering = false;

        public NavButton(String icono, String texto) {
            super("<html><div style='text-align:center;'>"
                    + "<span style='font-family:" + ICON_FAMILY + "; font-size:15px;'>" + icono + "</span>"
                    + "<br><span style='white-space:nowrap;'>" + lineaTexto(texto) + "</span></div></html>");
            setFont(FONT_SMALL.deriveFont(11f));
            setForeground(TEXT_ON_DARK);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(5, 4, 5, 4));
            setPreferredSize(new Dimension(98, 54));
            setMinimumSize(new Dimension(98, 54));
            setMaximumSize(new Dimension(98, 54));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hovering = false; repaint(); }
            });
        }

        /** Si el texto tiene varias palabras, corta en el último espacio (2 líneas); si es una sola palabra, la deja intacta (sin partirla a la mitad). */
        private static String lineaTexto(String texto) {
            int idx = texto.lastIndexOf(' ');
            if (idx < 0) return texto;
            return texto.substring(0, idx) + "<br>" + texto.substring(idx + 1);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (isSelected() || hovering) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected() ? new Color(255, 255, 255, 40) : new Color(255, 255, 255, 20));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
            super.paintComponent(g);
        }

        @Override
        public boolean isOpaque() { return false; }
    }

    public static NavButton navButton(String icono, String texto) {
        return new NavButton(icono, texto);
    }

    // ------------------------------------------------------------------
    // Tarjetas / secciones
    // ------------------------------------------------------------------
    public static JPanel card(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(BORDER, 12),
                new EmptyBorder(16, 18, 16, 18)));
        return panel;
    }

    public static JPanel section(String title, LayoutManager contentLayout, JComponent content) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);
        wrapper.add(heading(title), BorderLayout.NORTH);
        wrapper.add(content, BorderLayout.CENTER);
        return wrapper;
    }

    public static JLabel heading(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_HEADING);
        lbl.setForeground(TEXT);
        return lbl;
    }

    public static JLabel subtle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SUBHEADING);
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    /** Borde rectangular con esquinas redondeadas, dibujado con antialiasing. */
    public static class RoundedLineBorder implements Border {
        private final Color color;
        private final int arc;

        public RoundedLineBorder(Color color, int arc) {
            this.color = color;
            this.arc = arc;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(x, y, w - 1, h - 1, arc, arc);
            g2.dispose();
        }

        @Override public Insets getBorderInsets(Component c) { return new Insets(1, 1, 1, 1); }
        @Override public boolean isBorderOpaque() { return false; }
    }

    // ------------------------------------------------------------------
    // Tablas
    // ------------------------------------------------------------------
    public static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(FONT_BODY);
        table.setForeground(TEXT);
        table.setSelectionBackground(new Color(0xF6, 0xDF, 0xC8));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);
        table.setFocusable(false);
        table.setRowSelectionAllowed(true);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer());
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);

        table.setDefaultRenderer(Object.class, new StripedRenderer());
    }

    /** Aplica un renderer tipo "pill" (badge) a una columna concreta de la tabla. */
    public static void badgeColumn(JTable table, int columnIndex) {
        table.getColumnModel().getColumn(columnIndex).setCellRenderer(new BadgeRenderer());
    }

    public static JScrollPane scrollCard(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new RoundedLineBorder(BORDER, 12));
        sp.getViewport().setBackground(SURFACE);
        sp.setBackground(SURFACE);
        return sp;
    }

    private static Color rowColor(int row, boolean selected) {
        if (selected) return new Color(0xF6, 0xDF, 0xC8);
        return row % 2 == 0 ? SURFACE : SURFACE_ALT;
    }

    static class HeaderRenderer extends DefaultTableCellRenderer {
        HeaderRenderer() {
            setOpaque(true);
            setHorizontalAlignment(LEFT);
            setFont(FONT_BODY_BOLD);
            setForeground(TEXT_ON_DARK);
            setBackground(HEADER_ROW);
            setBorder(new EmptyBorder(0, 14, 0, 14));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int col) {
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    static class StripedRenderer extends DefaultTableCellRenderer {
        StripedRenderer() {
            setBorder(new EmptyBorder(0, 14, 0, 14));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            setBackground(rowColor(row, isSelected));
            setForeground(TEXT);
            setHorizontalAlignment(LEFT);
            return this;
        }
    }

    static class BadgeRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int col) {
            String text = value == null ? "" : value.toString();
            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setOpaque(true);
            wrapper.setBackground(rowColor(row, isSelected));

            Color fg, bg;
            switch (text.toUpperCase()) {
                case "LIBRE": case "ENTREGADO": case "SÍ": case "SI":
                    fg = SUCCESS; bg = SUCCESS_BG; break;
                case "OCUPADA": case "CANCELADO": case "NO":
                    fg = DANGER; bg = DANGER_BG; break;
                case "PENDIENTE":
                    fg = WARNING; bg = WARNING_BG; break;
                case "PREPARANDO": case "LISTO":
                    fg = INFO; bg = INFO_BG; break;
                default:
                    fg = TEXT; bg = null;
            }
            JLabel pill = new JLabel(text);
            pill.setFont(FONT_BADGE);
            pill.setForeground(fg);
            if (bg != null) {
                pill.setOpaque(true);
                pill.setBackground(bg);
                pill.setBorder(new EmptyBorder(4, 12, 4, 12));
            } else {
                pill.setBorder(new EmptyBorder(4, 4, 4, 4));
            }
            wrapper.add(pill);
            return wrapper;
        }
    }

    // ------------------------------------------------------------------
    // Campos de formulario
    // ------------------------------------------------------------------
    public static void styleField(JComponent field) {
        field.setFont(FONT_BODY);
        field.setBackground(SURFACE);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(BORDER, 8),
                new EmptyBorder(5, 8, 5, 8)));
    }

    /** Muestra un texto gris de ejemplo mientras el campo está vacío y sin foco (compatible con cualquier Look&Feel). */
    public static void placeholder(JTextField field, String texto) {
        final Color normal = TEXT;
        final Color placeholderColor = TEXT_MUTED;
        field.setForeground(field.getText().isEmpty() ? placeholderColor : normal);
        if (field.getText().isEmpty()) field.setText(texto);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(texto) && field.getForeground().equals(placeholderColor)) {
                    field.setText("");
                    field.setForeground(normal);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(placeholderColor);
                    field.setText(texto);
                }
            }
        });
    }

    public static JLabel formLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BODY_BOLD);
        lbl.setForeground(TEXT);
        return lbl;
    }

    // ------------------------------------------------------------------
    // Imágenes de platos / logo, con marcador de posición automático
    // ------------------------------------------------------------------
    /**
     * Carpeta (dentro de src/) donde el usuario debe colocar las imágenes.
     * Estructura esperada dentro del proyecto NetBeans:
     *
     *   JavaApplication4/src/images/logo.png            (logo redondo, 96x96 px, PNG)
     *   JavaApplication4/src/images/platos/<id>.png      (foto de cada plato, PNG o JPG, ideal 640x400 px)
     *
     * El id de cada plato es el mismo id numérico definido en DatosService
     * (por ejemplo: images/platos/1.png para "Pollo a la Brasa 1/4").
     * Si el archivo no existe, se dibuja automáticamente un marcador de
     * posición para que la aplicación nunca falle por falta de imágenes.
     */
    public static final String IMG_BASE = "/images/";

    /** Intenta cargar y escalar una imagen desde el classpath (src/images/...). Devuelve null si no existe. */
    public static ImageIcon loadImage(String relativePath, int w, int h) {
        java.net.URL url = Theme.class.getResource(IMG_BASE + relativePath);
        if (url == null) return null;
        try {
            ImageIcon raw = new ImageIcon(url);
            Image scaled = raw.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception ex) {
            return null;
        }
    }

    /** Devuelve la foto del plato (images/platos/{id}.png o .jpg) o un marcador de posición dibujado. */
    public static JComponent platoImagen(int idPlato, String nombrePlato, int w, int h) {
        ImageIcon icon = loadImage("platos/" + idPlato + ".png", w, h);
        if (icon == null) icon = loadImage("platos/" + idPlato + ".jpg", w, h);
        JLabel lbl;
        if (icon != null) {
            lbl = new JLabel(icon);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            lbl = new PlaceholderImageLabel(nombrePlato, w, h);
        }
        lbl.setPreferredSize(new Dimension(w, h));
        lbl.setOpaque(false);
        return lbl;
    }

    /** Logo circular de la cabecera (images/logo.png) o un marcador dibujado con un pollito. */
    public static JLabel logoRedondo(int diametro) {
        ImageIcon icon = loadImage("logo.png", diametro, diametro);
        if (icon != null) {
            return new JLabel(icon);
        }
        JLabel lbl = new JLabel("\uD83C\uDF57") { // 🍗
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setOpaque(false);
        lbl.setPreferredSize(new Dimension(diametro, diametro));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setFont(new Font(ICON_FAMILY, Font.PLAIN, (int) (diametro * 0.55)));
        return lbl;
    }

    /** Círculo de color con un emoji/ícono centrado (usado en tarjetas de resumen/estadísticas). */
    public static JComponent circuloIcono(String emoji, Color colorTexto, Color colorFondo, int diametro) {
        JLabel lbl = new JLabel(emoji) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(colorFondo);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setOpaque(false);
        lbl.setPreferredSize(new Dimension(diametro, diametro));
        lbl.setMinimumSize(new Dimension(diametro, diametro));
        lbl.setMaximumSize(new Dimension(diametro, diametro));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setVerticalAlignment(SwingConstants.CENTER);
        lbl.setForeground(colorTexto);
        lbl.setFont(new Font(ICON_FAMILY, Font.PLAIN, (int) (diametro * 0.42)));
        return lbl;
    }

    /** Panel/etiqueta dibujada a mano (plato + tenedor) usada mientras no exista la imagen real. */
    public static class PlaceholderImageLabel extends JLabel {
        private final String texto;

        public PlaceholderImageLabel(String texto, int w, int h) {
            this.texto = texto;
            setPreferredSize(new Dimension(w, h));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(SURFACE_ALT);
            g2.fillRoundRect(0, 0, w, h, 14, 14);
            g2.setColor(BORDER);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);

            int cx = w / 2, cy = h / 2 - 4;
            int r = Math.min(w, h) / 4;
            g2.setColor(ACCENT_DARK);
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            g2.setColor(ACCENT);
            g2.fillOval(cx - r + 6, cy - r + 6, r * 2 - 12, r * 2 - 12);
            g2.setColor(TEXT_MUTED);
            g2.setFont(new Font(FAMILY, Font.PLAIN, Math.max(10, h / 12)));
            String hint = "Imagen no disponible";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(hint, (w - fm.stringWidth(hint)) / 2, cy + r + fm.getHeight());
            g2.setColor(TEXT);
            g2.setFont(FONT_SMALL);
            FontMetrics fm2 = g2.getFontMetrics();
            String name = texto == null ? "" : texto;
            if (fm2.stringWidth(name) > w - 16) {
                while (name.length() > 3 && fm2.stringWidth(name + "…") > w - 16) {
                    name = name.substring(0, name.length() - 1);
                }
                name = name + "…";
            }
            g2.drawString(name, (w - fm2.stringWidth(name)) / 2, cy + r + fm.getHeight() + 16);
            g2.dispose();
        }
    }

    // ------------------------------------------------------------------
    // Icono de mesa con sillas (usado en las tarjetas de "Estado de mesas")
    // ------------------------------------------------------------------
    /** Dibuja una mesa vista desde arriba con sillas alrededor, coloreadas según el estado (libre/ocupada). */
    public static class TableIcon extends JPanel {
        private final boolean ocupada;
        private final int capacidad;

        public TableIcon(boolean ocupada, int capacidad) {
            this.ocupada = ocupada;
            this.capacidad = capacidad;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int chairSize = Math.max(14, h / 6);
            int tableW = (int) (w * 0.48);
            int tableH = (int) (h * 0.42);
            int tableX = (w - tableW) / 2;
            int tableY = (h - tableH) / 2;
            double cx = w / 2.0, cy = h / 2.0;

            Color chairFill = ocupada ? DANGER : SUCCESS;
            Color chairBorder = ocupada ? new Color(0x9C, 0x22, 0x22) : new Color(0x1B, 0x5E, 0x2E);

            int n = Math.max(1, Math.min(capacidad, 8));
            double rx = tableW * 0.62 + chairSize * 0.58;
            double ry = tableH * 0.62 + chairSize * 0.58;
            for (int i = 0; i < n; i++) {
                double angle = (2 * Math.PI * i / n) - Math.PI / 2;
                int chx = (int) Math.round(cx + rx * Math.cos(angle)) - chairSize / 2;
                int chy = (int) Math.round(cy + ry * Math.sin(angle)) - chairSize / 2;
                g2.setColor(chairFill);
                g2.fillRoundRect(chx, chy, chairSize, chairSize, 6, 6);
                g2.setColor(chairBorder);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(chx, chy, chairSize, chairSize, 6, 6);
            }

            // Tablero de la mesa (madera)
            g2.setColor(new Color(0xDD, 0xAC, 0x71));
            g2.fillRoundRect(tableX, tableY, tableW, tableH, 12, 12);
            g2.setColor(new Color(0xB8, 0x83, 0x48));
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(tableX, tableY, tableW, tableH, 12, 12);

            // Maceta decorativa en el centro
            int potR = Math.max(8, Math.min(tableW, tableH) / 3);
            int potX = (int) cx - potR / 2;
            int potY = (int) cy - potR / 2;
            g2.setColor(new Color(0xE0, 0x8A, 0x3A));
            g2.fillOval(potX, potY + potR / 4, potR, (int) (potR * 0.7));
            g2.setColor(new Color(0x5C, 0x8A, 0x3C));
            g2.fillOval(potX + potR / 6, potY - potR / 4, (int) (potR * 0.7), (int) (potR * 0.7));

            g2.dispose();
        }
    }

    public static TableIcon tableIcon(boolean ocupada, int capacidad) {
        return new TableIcon(ocupada, capacidad);
    }
}
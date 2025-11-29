package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.security.SessionManager;
import com.veterinaria.veterinariaapp.model.Usuario;

// Repos existentes
import com.veterinaria.veterinariaapp.repository.ICitaRepository;
import com.veterinaria.veterinariaapp.repository.CitaRepository;
import com.veterinaria.veterinariaapp.repository.IClienteRepository;
import com.veterinaria.veterinariaapp.repository.ClienteRepository;
import com.veterinaria.veterinariaapp.repository.IMascotaRepository;
import com.veterinaria.veterinariaapp.repository.MascotaRepository;
import com.veterinaria.veterinariaapp.repository.IUsuarioRepository;
import com.veterinaria.veterinariaapp.repository.UsuarioRepository;

import com.veterinaria.veterinariaapp.repository.IServiciosRepository;
import com.veterinaria.veterinariaapp.repository.ServicioRepository;

// Dashboard (nuevo)
import com.veterinaria.veterinariaapp.repository.IDashboardRepository;
import com.veterinaria.veterinariaapp.repository.DashboardRepository;
import com.veterinaria.veterinariaapp.service.DashboardService;

// Servicios
import com.veterinaria.veterinariaapp.service.AuthService;
import com.veterinaria.veterinariaapp.service.CitaService;
import com.veterinaria.veterinariaapp.service.ClienteService;
import com.veterinaria.veterinariaapp.service.MascotaService;
import com.veterinaria.veterinariaapp.service.UserService;
// Servicios (NUEVO)
import com.veterinaria.veterinariaapp.service.ServiciosService;

// OCP
import com.veterinaria.veterinariaapp.security.IPermisosRol;
import com.veterinaria.veterinariaapp.security.PermisosAdmin;
import com.veterinaria.veterinariaapp.security.PermisosRecepcionista;
import com.veterinaria.veterinariaapp.security.PermisosVeterinario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

// Fechas (solo por si quieres formatear distinto más adelante)
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MainWindow extends JFrame {

    // ===== Dashboard dinámico =====
    private JLabel lblKpiCitas    = new JLabel("0", SwingConstants.CENTER);
    private JLabel lblKpiMascotas = new JLabel("0", SwingConstants.CENTER);
    private JLabel lblKpiClientes = new JLabel("0", SwingConstants.CENTER);
    private JLabel lblUpdatedAt   = new JLabel("", SwingConstants.RIGHT);

    private final Map<String,Integer> dataCitasPorDia = new LinkedHashMap<>();
    private final Map<String,Integer> dataEspecies    = new LinkedHashMap<>();

    // NUEVO: en vez de usar BarChartPanel directo, usamos un pager
    private final CitasChartPager barCitasPager = new CitasChartPager(dataCitasPorDia);
    private final PieChartPanel pieEspecies  = new PieChartPanel(dataEspecies);

    // Card central
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);

    // Services/repos (existentes)
    private final ICitaRepository citaRepo = new CitaRepository();
    private final IClienteRepository clienteRepo = new ClienteRepository();
    private final IMascotaRepository mascotaRepo = new MascotaRepository();
    private final IUsuarioRepository usuarioRepo = new UsuarioRepository();

    // REPOSITORIOS DE SERVICIOS (NUEVO)
    private final IServiciosRepository serviciosRepo = new ServicioRepository();
    private final CitaService citaService = new CitaService(citaRepo, mascotaRepo);
    private final ClienteService clienteService = new ClienteService(clienteRepo);
    private final MascotaService mascotaService = new MascotaService(mascotaRepo, clienteRepo);
    private final UserService userService = new UserService(usuarioRepo);
    private final AuthService authService = new AuthService(usuarioRepo);

    private final ServiciosService serviciosService = new ServiciosService(serviciosRepo);
    // Dashboard service/repo (nuevo)
    private final IDashboardRepository dashboardRepo = new DashboardRepository();
    private final DashboardService dashboardService = new DashboardService(dashboardRepo);

    // Vistas
    private CitasViewForm citasView;
    private ClienteViewForm clienteView;
    private MascotaViewForm mascotaView;
    private UsuariosPanel usuariosPanel;
    
    private ServiciosViewForm serviciosView;

    // Menús/botones
    private JMenuItem miClientes, miMascotas, miCitas, miUsuarios;
    private JButton btnUsuarios, btnCitas, btnClientes, btnMascotas;

    private JMenuItem miServicios;
    private JButton btnServicios;
    
    public MainWindow() {
        setTitle("Veterinario — Panel Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setJMenuBar(buildMenuBar());
        add(buildSidebar(), BorderLayout.WEST);

        content.add(buildDashboard(), "dashboard");

        usuariosPanel = new UsuariosPanel(userService);
        clienteView   = new ClienteViewForm(clienteService);
        mascotaView   = new MascotaViewForm(mascotaService);
        citasView     = new CitasViewForm(citaService);
        
        serviciosView = new ServiciosViewForm(serviciosService);

        content.add(usuariosPanel, "usuarios");
        content.add(clienteView.getContentPane(), "clientes");
        content.add(mascotaView.getContentPane(), "mascotas");
        content.add(citasView.getContentPane(), "citas");
        
        content.add(serviciosView.getContentPane(), "servicios");
        add(content, BorderLayout.CENTER);

        aplicarPermisosSegunRol();

        cards.show(content, "dashboard");
        cargarDashboard();

        // Refrescar al recuperar foco (por si guardaste una cita y regresas)
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) { cargarDashboard(); }
        });
    }

    // ==== MENÚ SUPERIOR ====
    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu mArchivo = new JMenu("Archivo");
        JMenuItem miCerrarSesion = new JMenuItem("Cerrar Sesión");
        JMenuItem miSalir        = new JMenuItem("Salir");
        mArchivo.add(miCerrarSesion);
        mArchivo.add(miSalir);

        JMenu mModulos = new JMenu("Módulos");
        miClientes = new JMenuItem("Clientes");
        miMascotas = new JMenuItem("Mascotas");
        miCitas    = new JMenuItem("Citas");
        miUsuarios = new JMenuItem("Usuarios");
        
        miServicios = new JMenuItem("Servicios");
        
        mModulos.add(miClientes);
        mModulos.add(miMascotas);
        mModulos.add(miCitas);
        mModulos.add(miUsuarios);

        mModulos.add(miServicios);
        bar.add(mArchivo);
        bar.add(mModulos);

        miCerrarSesion.addActionListener(e -> {
            SessionManager.get().logout();
            JOptionPane.showMessageDialog(this, "Sesión cerrada.");
            new LoginFrame(authService).setVisible(true);
            dispose();
        });
        miSalir.addActionListener(e -> System.exit(0));

        miClientes.addActionListener(e -> { cards.show(content, "clientes"); if (clienteView != null) clienteView.recargarTabla(); });
        miMascotas.addActionListener(e -> { cards.show(content, "mascotas"); if (mascotaView != null) mascotaView.recargarTabla(); });
        miCitas.addActionListener(e -> { cards.show(content, "citas"); if (citasView != null) citasView.recargarTabla(); });
        miUsuarios.addActionListener(e -> { cards.show(content, "usuarios"); if (usuariosPanel != null) usuariosPanel.cargarUsuarios(); });

        miServicios.addActionListener(e -> { cards.show(content, "servicios"); if (serviciosView != null) serviciosView.recargarTabla(); });
        miCerrarSesion.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        miSalir.setAccelerator        (KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_DOWN_MASK));

        return bar;
    }

    // ==== MENÚ LATERAL ====
    private JComponent buildSidebar() {
        JPanel side = new JPanel(new BorderLayout());
        side.setPreferredSize(new Dimension(200, getHeight()));
        side.setBackground(new Color(225,239,255));

        JLabel hola = new JLabel("  Bienvenido, " +
                (SessionManager.get().isAuthenticated() ? SessionManager.get().getCurrentUser().getNombre() : "Usuario"),
                SwingConstants.LEFT);
        hola.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        hola.setFont(hola.getFont().deriveFont(Font.BOLD, 13f));
        side.add(hola, BorderLayout.NORTH);

        JPanel menu = new JPanel(new GridLayout(0,1,0,6));
        menu.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JButton btnInicio = mkNav("Inicio");
        btnUsuarios = mkNav("Usuarios");
        btnCitas    = mkNav("Citas");
        btnClientes = mkNav("Clientes");
        btnMascotas = mkNav("Mascotas");
        
        btnServicios = mkNav("Servicios");
        JButton btnCerrar = mkNav("Cerrar sesión");

        menu.add(btnInicio);
        menu.add(btnUsuarios);
        menu.add(btnCitas);
        menu.add(btnClientes);
        menu.add(btnMascotas);
        
        menu.add(btnServicios);

        side.add(menu, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        south.add(btnCerrar, BorderLayout.SOUTH);
        side.add(south, BorderLayout.SOUTH);

        btnInicio.addActionListener(e -> {
            cards.show(content, "dashboard");
            cargarDashboard();
        });
        btnUsuarios.addActionListener(e -> { cards.show(content, "usuarios"); if (usuariosPanel != null) usuariosPanel.cargarUsuarios(); });
        btnClientes.addActionListener(e -> { cards.show(content, "clientes"); if (clienteView != null) clienteView.recargarTabla(); });
        btnCitas.addActionListener(e -> { cards.show(content, "citas"); if (citasView != null) citasView.recargarTabla(); });
        btnMascotas.addActionListener(e -> { cards.show(content, "mascotas"); if (mascotaView != null) mascotaView.recargarTabla(); });
        
        btnServicios.addActionListener(e -> { cards.show(content, "servicios"); if (serviciosView != null) serviciosView.recargarTabla(); });
        btnCerrar.addActionListener(e -> {
            SessionManager.get().logout();
            JOptionPane.showMessageDialog(this, "Sesión cerrada.");
            new LoginFrame(authService).setVisible(true);
            dispose();
        });

        return side;
    }

    private JButton mkNav(String text){
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(Color.WHITE);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210,225,245)),
                BorderFactory.createEmptyBorder(10,10,10,10)
        ));
        return b;
    }

    // ==== Dashboard dinámico e interactivo ====
    private JComponent buildDashboard() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        JLabel title = new JLabel("Panel Administrativo");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));
        root.add(title, BorderLayout.NORTH);

        JPanel kpis = new JPanel(new GridLayout(1,3,12,12));
        kpis.add(kpiCard("Citas", lblKpiCitas));
        kpis.add(kpiCard("Mascotas", lblKpiMascotas));
        kpis.add(kpiCard("Clientes", lblKpiClientes));

        JPanel charts = new JPanel(new GridLayout(1,2,12,12));
        charts.setBorder(BorderFactory.createEmptyBorder(12,0,0,0));
        charts.add(chartPanel("Citas por día", barCitasPager));
        charts.add(chartPanel("Distribución de especies", pieEspecies));

        JPanel center = new JPanel(new BorderLayout());
        center.add(kpis, BorderLayout.NORTH);
        center.add(charts, BorderLayout.CENTER);

        lblUpdatedAt.setFont(lblUpdatedAt.getFont().deriveFont(11f));
        lblUpdatedAt.setForeground(new Color(120,120,120));
        center.add(lblUpdatedAt, BorderLayout.SOUTH);

        root.add(center, BorderLayout.CENTER);
        return root;
    }

    private JPanel kpiCard(String titulo, JLabel valorLabel){
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(16,16,16,16)
        ));
        JLabel t = new JLabel(titulo);
        t.setFont(t.getFont().deriveFont(14f));
        valorLabel.setFont(valorLabel.getFont().deriveFont(Font.BOLD, 28f));
        p.add(t, BorderLayout.NORTH);
        p.add(valorLabel, BorderLayout.CENTER);
        return p;
    }

    private JPanel chartPanel(String titulo, JComponent chart){
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(12,12,12,12)
        ));
        JLabel t = new JLabel(titulo);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 14f));
        t.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));
        panel.add(t, BorderLayout.NORTH);
        panel.add(chart, BorderLayout.CENTER);
        return panel;
    }

    /** Carga datos reales y refresca el dashboard. */
    private void cargarDashboard() {
        try {
            // KPIs
            int citas = dashboardService.totalCitas();
            int mascotas = dashboardService.totalMascotas();
            int clientes = dashboardService.totalClientes();
            lblKpiCitas.setText(String.valueOf(citas));
            lblKpiMascotas.setText(String.valueOf(mascotas));
            lblKpiClientes.setText(String.valueOf(clientes));

            // Barras: ventana más amplia para que el pager tenga material (p. ej., 3 meses hacia atrás y adelante)
            final int DAYS_BACK = 90;
            final int DAYS_FORWARD = 90;

            dataCitasPorDia.clear();
            dataCitasPorDia.putAll(dashboardService.citasPorDiaVentana(DAYS_BACK, DAYS_FORWARD));

            // el pager muestra SOLO los días con citas y paginados de 5 en 5
            barCitasPager.setData(dataCitasPorDia);


            // Pie: top 5 especies
            dataEspecies.clear();
            dataEspecies.putAll(dashboardService.distribucionEspecies(5));
            pieEspecies.setData(dataEspecies);

            lblUpdatedAt.setText("Última actualización: " + java.time.LocalDateTime.now().withNano(0));

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar el Dashboard: " + ex.getMessage(),
                    "Dashboard", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ====== CONTENEDOR CON PAGINACIÓN (solo días con citas) ======
    static class CitasChartPager extends JPanel {
        private final BarChartPanel chart;
        private final JButton btnPrev = new JButton("◀");
        private final JButton btnNext = new JButton("▶");
        private final JLabel lblInfo  = new JLabel(" ", SwingConstants.CENTER);

        private final java.util.List<Map.Entry<String,Integer>> nonZero = new ArrayList<>();
        private int PAGE_SIZE = 5;
        private int page = 0;

        CitasChartPager(Map<String,Integer> rawData){
            super(new BorderLayout());
            chart = new BarChartPanel(new LinkedHashMap<>());
            add(chart, BorderLayout.CENTER);

            JPanel nav = new JPanel(new BorderLayout());
            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            btnPrev.setFocusable(false);
            btnNext.setFocusable(false);
            left.add(btnPrev);
            right.add(btnNext);

            lblInfo.setFont(lblInfo.getFont().deriveFont(11f));
            lblInfo.setForeground(new Color(90,90,90));
            nav.add(left, BorderLayout.WEST);
            nav.add(lblInfo, BorderLayout.CENTER);
            nav.add(right, BorderLayout.EAST);

            nav.setBorder(BorderFactory.createEmptyBorder(6,0,0,0));
            add(nav, BorderLayout.SOUTH);

            btnPrev.addActionListener(e -> { if (page>0){ page--; refresh(); }});
            btnNext.addActionListener(e -> { if ((page+1)*PAGE_SIZE < nonZero.size()){ page++; refresh(); }});

            setData(rawData);
        }

        public void setData(Map<String,Integer> raw){
            nonZero.clear();
            for (Map.Entry<String,Integer> e: raw.entrySet()){
                if (e.getValue()!=null && e.getValue() > 0){
                    nonZero.add(e);
                }
            }
            // mantener el orden original del mapa (ya viene LinkedHashMap)
            page = 0;
            refresh();
        }

        private void refresh(){
            int total = nonZero.size();
            int from = Math.min(page*PAGE_SIZE, total);
            int to   = Math.min(from + PAGE_SIZE, total);

            Map<String,Integer> slice = new LinkedHashMap<>();
            for (int i=from; i<to; i++){
                var e = nonZero.get(i);
                slice.put(e.getKey(), e.getValue());
            }
            chart.setData(slice);

            btnPrev.setEnabled(page>0);
            btnNext.setEnabled(to < total);

            if (total==0){
                lblInfo.setText("No hay días con citas en el período.");
            } else {
                lblInfo.setText(String.format("Mostrando %d–%d de %d días con citas",
                        (from==to?0:from+1), to, total));
            }
        }
    }

    // ====== Gráfico de Barras (solo lo que le pasen) ======
    static class BarChartPanel extends JPanel {
        private Map<String,Integer> data;
        private String[] keys = new String[0];

        // Colores estilo pastel
        private final Color BAR_FILL   = new Color(186,235,231);
        private final Color BAR_STROKE = new Color(128,205,201);
        private final Color GRID       = new Color(224,234,240);
        private final Color AXIS       = new Color(200,205,210);
        private final Font  TITLE_FONT = getFont().deriveFont(Font.BOLD, 14f);

        // Mostrar números encima de las barras
        private final boolean SHOW_VALUES = true;

        BarChartPanel(Map<String,Integer> data){
            this.data = new LinkedHashMap<>(data);
            this.keys = this.data.keySet().toArray(new String[0]);
            setPreferredSize(new Dimension(420,260));
            setBackground(Color.WHITE);
            setToolTipText("");
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) { updateTooltip(e.getX(), e.getY()); }
            });
        }

        public void setData(Map<String,Integer> newData){
            this.data = new LinkedHashMap<>(newData);
            this.keys = this.data.keySet().toArray(new String[0]);
            repaint();
        }

        private void updateTooltip(int mx, int my){
            if (keys.length == 0) { setToolTipText(null); return; }
            int w=getWidth(), h=getHeight();
            int marginLeft=40, marginTop=40, marginRight=40, marginBottom=56;

            int plotX = marginLeft, plotY = marginTop + 20;
            int plotW = w - marginLeft - marginRight;
            int plotH = h - marginTop - marginBottom - 20;

            int n = keys.length;
            if (n == 0) { setToolTipText(null); return; }

            double slot = plotW / (double) n;
            int max = Math.max(1, data.values().stream().mapToInt(i->i).max().orElse(1));

            for (int i=0;i<n;i++){
                String k = keys[i];
                double slotX = plotX + i*slot;
                int bw = (int)Math.max(16, Math.min(40, Math.round(slot*0.6)));
                int x = (int)Math.round(slotX + (slot - bw)/2.0);

                int barH = (int)Math.round((data.get(k) / (double)max) * (plotH - 10));
                int y = plotY + plotH - barH;

                if (mx>=x && mx<=x+bw && my>=y && my<=plotY + plotH){
                    setToolTipText(k + " → " + data.get(k));
                    return;
                }
            }
            setToolTipText(null);
        }

        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight();

            int marginLeft=40, marginTop=40, marginRight=40, marginBottom=56;

            int plotX = marginLeft, plotY = marginTop + 20;
            int plotW = w - marginLeft - marginRight;
            int plotH = h - marginTop - marginBottom - 20;

            // Fondo
            g2.setColor(Color.WHITE);
            g2.fillRect(0,0,w,h);

            // Leyenda “Citas por día”
            g2.setFont(TITLE_FONT);
            int legendX = plotX + 10, legendY = marginTop + 14;
            g2.setColor(new Color(136,206,201));
            g2.fillRoundRect(legendX, legendY-10, 16, 10, 4,4);
            g2.setColor(new Color(100,100,100));
            g2.drawString("Citas por día", legendX + 24, legendY);

            // Rejilla horizontal (5 líneas)
            g2.setColor(GRID);
            for (int i=0;i<=5;i++){
                int y = plotY + (int)Math.round(i*(plotH/5.0));
                g2.drawLine(plotX, y, plotX+plotW, y);
            }

            // Ejes
            g2.setColor(AXIS);
            g2.drawLine(plotX, plotY, plotX, plotY+plotH);          // Y
            g2.drawLine(plotX, plotY+plotH, plotX+plotW, plotY+plotH); // X

            // Datos
            if (data.isEmpty()) { g2.dispose(); return; }
            int max = Math.max(1, data.values().stream().mapToInt(i->i).max().orElse(1));
            String[] labels = keys;
            int n = labels.length;
            if (n == 0) { g2.dispose(); return; }

            // slot = ancho por categoría; barra centrada
            double slot = plotW / (double) n;

            // Dibujar barras
            int i=0;
            int[] barTopX = new int[n];
            int[] barTopY = new int[n];
            for (String k : labels){
                double slotX = plotX + i*slot;
                int bw = (int)Math.max(16, Math.min(40, Math.round(slot*0.6)));
                int x = (int)Math.round(slotX + (slot - bw)/2.0);

                int barH = (int)Math.round((data.get(k) / (double)max) * (plotH - 10));
                int y = plotY + plotH - barH;

                g2.setColor(BAR_FILL);
                g2.fillRoundRect(x, y, bw, barH, 8, 8);
                g2.setColor(BAR_STROKE);
                g2.drawRoundRect(x, y, bw, barH, 8, 8);

                barTopX[i] = x + bw/2;
                barTopY[i] = y;
                i++;
            }

            // Valores encima de las barras
            if (SHOW_VALUES){
                g2.setFont(getFont().deriveFont(Font.BOLD, 11f));
                FontMetrics vfm = g2.getFontMetrics();
                for (int idx=0; idx<n; idx++){
                    String val = String.valueOf(data.get(labels[idx]));
                    int tx = barTopX[idx];
                    int ty = Math.max( barTopY[idx] - 4, marginTop + 24 );

                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(tx - vfm.stringWidth(val)/2 - 3, ty - vfm.getAscent(),
                                     vfm.stringWidth(val) + 6, vfm.getAscent()+2, 6, 6);

                    g2.setColor(new Color(60,60,60));
                    g2.drawString(val, tx - vfm.stringWidth(val)/2, ty);
                }
            }

            // Etiquetas X (fechas): sin salto, con rotación suave si hace falta
            g2.setFont(getFont().deriveFont(11f));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(new Color(80,80,80));

            boolean rotate = slot < (fm.charWidth('0')*6 + 8); // si hay poco espacio, inclinamos
            for (int idx=0; idx<n; idx++){
                String k = labels[idx];

                // Si recibimos d/M/yyyy, lo dejamos así; recortamos si hay muy poco ancho
                String txt = k;
                if (slot < 50 && k.length() >= 5){
                    // dd/MM
                    int p = k.lastIndexOf('/');
                    if (p>0) txt = k.substring(0, p);
                }

                double slotX = plotX + idx*slot;
                int centerX = (int)Math.round(slotX + slot/2.0);
                int baseY = plotY + plotH + 20;

                if (rotate){
                    g2.translate(centerX, baseY);
                    g2.rotate(Math.toRadians(-35));
                    g2.drawString(txt, -fm.stringWidth(txt)/2, 0);
                    g2.rotate(Math.toRadians(35));
                    g2.translate(-centerX, -baseY);
                } else {
                    g2.drawString(txt, centerX - fm.stringWidth(txt)/2, baseY);
                }
            }

            g2.dispose();
        }
    }

    // ====== Gráfico de Torta (leyenda superior + tooltip) ======
    static class PieChartPanel extends JPanel {
        private Map<String,Integer> data;
        private java.util.List<Slice> slices = new java.util.ArrayList<>();

        private final Color[] colors={ new Color(245,96,111), new Color(84,141,212), new Color(254,194,79),
                                       new Color(85,192,187), new Color(155,125,230), new Color(255,150,100) };

        static class Slice { String label; int value; int start; int extent; Color color; }

        PieChartPanel(Map<String,Integer> data){
            this.data = new LinkedHashMap<>(data);
            setPreferredSize(new Dimension(420,260));
            setBackground(Color.WHITE);
            setToolTipText("");
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) { updateTooltip(e.getX(), e.getY()); }
            });
        }

        public void setData(Map<String,Integer> newData){
            this.data = new LinkedHashMap<>(newData);
            rebuildSlices();
            repaint();
        }

        private void rebuildSlices(){
            slices.clear();
            int total=data.values().stream().mapToInt(i->i).sum();
            if(total==0) return;
            int start=0, idx=0;
            for(var e: data.entrySet()){
                Slice s = new Slice();
                s.label = e.getKey();
                s.value = e.getValue();
                s.start = start;
                s.extent = (int)Math.round(360.0*s.value/total);
                s.color = colors[idx++%colors.length];
                start += s.extent;
                slices.add(s);
            }
        }

        private void updateTooltip(int mx, int my){
            if (slices.isEmpty()){ setToolTipText(null); return; }
            int w=getWidth(), h=getHeight();

            int legendH = 24;
            int size=Math.min(w, h-legendH) - 40;
            int x=(w-size)/2, y=legendH + (h-legendH-size)/2;

            double cx=x+size/2.0, cy=y+size/2.0;
            double dx=mx-cx, dy=my-cy;
            double dist=Math.hypot(dx,dy);
            if (dist>size/2.0){ setToolTipText(null); return; }

            double ang = Math.toDegrees(Math.atan2(dy, dx));
            ang = (ang<0)?(360+ang):ang;

            int total=data.values().stream().mapToInt(i->i).sum();
            for (Slice s: slices){
                int end = s.start + s.extent;
                boolean inside = (ang>=s.start && ang<=end) || (end>360 && ang<=end-360);
                if (inside){
                    int pct = (int)Math.round(s.value*100.0/total);
                    setToolTipText(s.label + " → " + s.value + " (" + pct + "%)");
                    return;
                }
            }
            setToolTipText(null);
        }

        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w=getWidth(), h=getHeight();
            int legendH = 24;
            int size=Math.min(w, h-legendH) - 40;
            if(size<60){ g2.dispose(); return; }
            int x=(w-size)/2, y=legendH + (h-legendH-size)/2;

            int total=data.values().stream().mapToInt(i->i).sum();
            if(total==0){ g2.dispose(); return; }
            if (slices.isEmpty()) rebuildSlices();

            for (Slice s: slices){
                g2.setColor(s.color);
                g2.fillArc(x,y,size,size,s.start,s.extent);
            }

            int lx = 20, ly = 16;
            g2.setFont(getFont().deriveFont(Font.BOLD, 12f));
            for (Slice s : slices){
                g2.setColor(s.color);
                g2.fillRoundRect(lx, ly-10, 14, 10, 4,4);
                g2.setColor(new Color(60,60,60));
                g2.drawString(s.label, lx + 20, ly);
                lx += 20 + g2.getFontMetrics().stringWidth(s.label) + 24;
                if (lx > w-120) {
                    lx = 20; ly += 18;
                }
            }

            g2.dispose();
        }
    }

    // --- Permisos (OCP) ---
    private void aplicarPermisosSegunRol() {
        Usuario usuarioLogueado = SessionManager.get().getCurrentUser();
        if (usuarioLogueado == null) {
            JOptionPane.showMessageDialog(this, "Error de sesión. Por favor, inicie sesión de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
            new LoginFrame(authService).setVisible(true);
            dispose();
            return;
        }
        IPermisosRol permisos = obtenerEstrategiaPermisos(usuarioLogueado.getRolId());
        permisos.configurarPermisos(this);
    }

    private IPermisosRol obtenerEstrategiaPermisos(int rolId) {
        switch (rolId) {
            case 1: return new PermisosAdmin();
            case 2: return new PermisosVeterinario();
            case 3: return new PermisosRecepcionista();
            default: return new PermisosRecepcionista();
        }
    }

    // Getters para estrategias de permisos
    public JMenuItem getMiClientes() { return miClientes; }
    public JMenuItem getMiMascotas() { return miMascotas; }
    public JMenuItem getMiCitas() { return miCitas; }
    public JMenuItem getMiUsuarios() { return miUsuarios; }
    public JButton getBtnClientes() { return btnClientes; }
    public JButton getBtnMascotas() { return btnMascotas; }
    public JButton getBtnCitas() { return btnCitas; }
    public JButton getBtnUsuarios() { return btnUsuarios; }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) { e.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}

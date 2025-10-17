package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.security.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainWindow extends JFrame {

    // ===== Datos fijos por ahora (sin medicamentos) =====
    private int kpiCitas = 3, kpiMascotas = 15, kpiClientes = 10;
    private Map<String,Integer> citasPorDia = Map.of("26/3",1,"27/3",1,"28/3",1);
    private Map<String,Integer> especies = new LinkedHashMap<>() {{
        put("Perro",40); put("Gato",25); put("Conejo",15); put("Hamster",10); put("Pájaro",10);
    }};

    // Card central
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);

    public MainWindow() {
        setTitle("Veterinari — Panel Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);

        // Vistas
        content.add(buildDashboard(), "dashboard");
        content.add(new UsuariosPanel(), "usuarios");
        content.add(buildClientesWrapper(), "clientes"); // embebe ClienteViewForm
        add(content, BorderLayout.CENTER);

        cards.show(content, "dashboard");
    }

    // ==== MENÚ LATERAL ====
    private JComponent buildSidebar() {
        JPanel side = new JPanel(new BorderLayout());
        side.setPreferredSize(new Dimension(200, getHeight()));
        side.setBackground(new Color(225,239,255));

        JLabel hola = new JLabel("  Bienvenido, " +
                (SessionManager.get().isAuthenticated()
                        ? SessionManager.get().getCurrentUser().getNombre()
                        : "Usuario"),
                SwingConstants.LEFT);
        hola.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        hola.setFont(hola.getFont().deriveFont(Font.BOLD, 13f));
        side.add(hola, BorderLayout.NORTH);

        JPanel menu = new JPanel(new GridLayout(0,1,0,6));
        menu.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JButton btnInicio    = mkNav("Inicio");
        JButton btnUsuarios  = mkNav("Usuarios");
        JButton btnCitas     = mkNav("Citas");     // pendiente
        JButton btnClientes  = mkNav("Clientes");  // embebido
        JButton btnMascotas  = mkNav("Mascotas");  // pendiente
        JButton btnCerrar    = mkNav("Cerrar sesión");

        menu.add(btnInicio);
        menu.add(btnUsuarios);
        menu.add(btnCitas);
        menu.add(btnClientes);
        menu.add(btnMascotas);

        side.add(menu, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        south.add(btnCerrar, BorderLayout.SOUTH);
        side.add(south, BorderLayout.SOUTH);

        // acciones (cambiar vista en el mismo frame)
        btnInicio.addActionListener(e -> cards.show(content, "dashboard"));
        btnUsuarios.addActionListener(e -> cards.show(content, "usuarios"));
        btnClientes.addActionListener(e -> cards.show(content, "clientes"));
        btnCitas.addActionListener(e -> JOptionPane.showMessageDialog(this, "Módulo Citas (pendiente)"));
        btnMascotas.addActionListener(e -> JOptionPane.showMessageDialog(this, "Módulo Mascotas (pendiente)"));
        btnCerrar.addActionListener(e -> {
            SessionManager.get().logout();
            JOptionPane.showMessageDialog(this, "Sesión cerrada.");
            new LoginFrame().setVisible(true);
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

    // ==== Dashboard (KPIs + charts) ====
    private JComponent buildDashboard() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        JLabel title = new JLabel("Panel Administrativo");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setBorder(BorderFactory.createEmptyBorder(0,0,12,0));
        root.add(title, BorderLayout.NORTH);

        JPanel kpis = new JPanel(new GridLayout(1,3,12,12));
        kpis.add(kpiCard("Citas", kpiCitas));
        kpis.add(kpiCard("Mascotas", kpiMascotas));
        kpis.add(kpiCard("Clientes", kpiClientes));

        JPanel charts = new JPanel(new GridLayout(1,2,12,12));
        charts.setBorder(BorderFactory.createEmptyBorder(12,0,0,0));
        charts.add(chartPanel("Citas por día", new BarChartPanel(citasPorDia)));
        charts.add(chartPanel("Distribución de especies", new PieChartPanel(especies)));

        JPanel center = new JPanel(new BorderLayout());
        center.add(kpis, BorderLayout.NORTH);
        center.add(charts, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);
        return root;
    }

    private JPanel kpiCard(String titulo, int valor){
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(16,16,16,16)
        ));
        JLabel t = new JLabel(titulo);
        t.setFont(t.getFont().deriveFont(14f));
        JLabel v = new JLabel(String.valueOf(valor), SwingConstants.CENTER);
        v.setFont(v.getFont().deriveFont(Font.BOLD, 28f));
        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
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

    // ==== Wrapper para CLIENTES (embebe el contenido del JFrame existente) ====
    private JComponent buildClientesWrapper() {
        JPanel wrapper = new JPanel(new BorderLayout());
        // Instancia el JFrame, usa su contenido y NO lo muestres como ventana
        ClienteViewForm frame = new ClienteViewForm();
        wrapper.add(frame.getContentPane(), BorderLayout.CENTER);
        return wrapper;
    }

    // ==== Gráficos simples sin librerías ====
    static class BarChartPanel extends JPanel {
        private final Map<String,Integer> data;
        BarChartPanel(Map<String,Integer> data){ this.data=data; setPreferredSize(new Dimension(420,260)); }
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight(), m=40, axisY=h-m, axisX=m;
            g2.setColor(new Color(200,200,200));
            g2.drawLine(axisX, axisY, w-m, axisY); g2.drawLine(axisX, m/2, axisX, axisY);
            int max=data.values().stream().mapToInt(i->i).max().orElse(1);
            int n=Math.max(data.size(),1), bw=Math.max((w-m*2)/(n*2),20), i=0;
            for(var e: data.entrySet()){
                int x=axisX+10+i*(bw*2);
                int barH=(int)((h-m*1.5)*(e.getValue()/(double)max));
                int y=axisY-barH;
                g2.setColor(new Color(180,225,230));
                g2.fillRoundRect(x,y,bw,barH,8,8);
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(e.getKey(), x, axisY+15);
                i++;
            }
            g2.dispose();
        }
    }

    static class PieChartPanel extends JPanel {
        private final Map<String,Integer> data;
        PieChartPanel(Map<String,Integer> data){ this.data=data; setPreferredSize(new Dimension(420,260)); }
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight(), size=Math.min(w,h)-40, x=(w-size)/2, y=(h-size)/2;
            int total=data.values().stream().mapToInt(i->i).sum(); if(total==0){g2.dispose(); return;}
            Color[] colors={ new Color(242,99,123), new Color(90,155,212), new Color(255,191,71),
                             new Color(123,201,82), new Color(142,120,220), new Color(255,120,80) };
            int start=0, idx=0;
            for(var e: data.entrySet()){
                int angle=(int)Math.round(360.0*e.getValue()/total);
                g2.setColor(colors[idx++%colors.length]);
                g2.fillArc(x,y,size,size,start,angle);
                start+=angle;
            }
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}

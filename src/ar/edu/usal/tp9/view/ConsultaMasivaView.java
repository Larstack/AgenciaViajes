package ar.edu.usal.tp9.view;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import ar.edu.usal.tp9.controller.ConsultaMasivaController;
import ar.edu.usal.tp9.utils.Constants;
import ar.edu.usal.tp9.utils.GuiUtilities;

public class ConsultaMasivaView {

	private JFrame ventana = new JFrame("Consulta y Actualizacion Masiva");

	private JLabel lblPasajero = new JLabel("Pasajero: ");
	private JTextField txtPasajero = new JTextField(Constants.TEXTO_ANCHO);

	private JButton btnConsultar =  new JButton("Consultar");

	private JButton btnActualizar =  new JButton("Actualizar");

	private JScrollPane scrollPane;

	private JTable tablaResultado = new JTable();

	private JLabel lblContRegBusqueda = new JLabel("Cantidad registros encontrados: ");
	private JTextArea contadorRegBusqueda = new JTextArea();

	private JLabel lblContRegTotal = new JLabel("Cantidad registros total: ");
	private JTextArea contadorRegTotal = new JTextArea();

	private ConsultaMasivaController consultaMasivaController;

	private JTextArea[] componentesTextosArray = {contadorRegBusqueda, contadorRegTotal};
	
	private DefaultTableModel tableModel = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int col)
        {
            return false;
        }
    };

	public ConsultaMasivaView(ConsultaMasivaController consultaMasivaController) {

		consultaMasivaController.setView(this);
		this.consultaMasivaController = consultaMasivaController;

		this.tableModel = (DefaultTableModel)this.getTablaResultado().getModel();

		this.addColumnasTabla(tableModel);
		
		this.getTablaResultado().getColumnModel().getColumn(1).setPreferredWidth(400);
		this.getTablaResultado().getColumnModel().getColumn(2).setPreferredWidth(400);
		this.getTablaResultado().getColumnModel().getColumn(3).setPreferredWidth(150);
		this.getTablaResultado().getColumnModel().getColumn(4).setPreferredWidth(150);
		this.getTablaResultado().getColumnModel().getColumn(5).setPreferredWidth(150);
		this.getTablaResultado().getColumnModel().getColumn(6).setPreferredWidth(150);
		this.getTablaResultado().getColumnModel().getColumn(7).setPreferredWidth(150);
		this.getTablaResultado().getColumnModel().getColumn(8).setPreferredWidth(150);
		this.getTablaResultado().getColumnModel().getColumn(9).setPreferredWidth(150);
		this.getTablaResultado().getColumnModel().getColumn(10).setPreferredWidth(150);
		this.getTablaResultado().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		GuiUtilities.aplicarFormatoVentana(ventana);

		GuiUtilities.aplicarFormatoTextField(ventana, txtPasajero);

		GuiUtilities.aplicarFormatoComponentes(ventana, componentesTextosArray);

		GuiUtilities.setearComandoBoton(btnConsultar, "Consultar", consultaMasivaController);
		GuiUtilities.setearComandoBoton(btnActualizar, "Actualizar", consultaMasivaController);

		scrollPane = new JScrollPane(tablaResultado);

		Component[] componentesArray = {lblPasajero, txtPasajero, btnConsultar, scrollPane, lblContRegBusqueda, 
				contadorRegBusqueda, lblContRegTotal, contadorRegTotal, btnActualizar};

		GuiUtilities.agregarComponentesVentana(ventana, componentesArray);

		ventana.setVisible(true);

	}

	private void addColumnasTabla(DefaultTableModel tableModel) {

		tableModel.setColumnIdentifiers(
				new String[]{
						"Id",
						"Pasajero",
						"Localidad/es",
						"Fecha/Hora Salida",
						"Cantidad dias",
						"Seguro",
						"Abono Transporte",
						"Guia",
						"Hotel",
						"Pension completa",
						"Importe"
				});

	}

	public JTable getTablaResultado() {

		return tablaResultado;

	}

	public JTextField getPasajero() {

		return txtPasajero;

	}

	public JTextArea getContadorRegTotal() {

		return contadorRegTotal;

	}

	public JTextArea getContadorRegBusqueda() {

		return contadorRegBusqueda;

	}

	public void mostrarRegistros(ArrayList<String[]> registros,
			String cantidadRegistros, String cantidadRegistrosTotales) {

		for (int i = 0; i < this.tableModel.getRowCount(); i++) {
			
			this.tableModel.removeRow(i);	
		}
		
		for (int i = 0; i < registros.size(); i++) {

			this.tableModel.isCellEditable(i, 0);
			this.tableModel.addRow(registros.get(i));
		}

		this.contadorRegBusqueda.setText(cantidadRegistros);
		this.contadorRegTotal.setText(cantidadRegistrosTotales);
	}

	public void mostrarMessageDialog(String error, String titulo) {
		
		JOptionPane.showMessageDialog(null, error, titulo, 
				JOptionPane.INFORMATION_MESSAGE);
	}

	public void mostrarMensajeDialog(String mensajeBody, String titulo) {
		
		JOptionPane.showMessageDialog(null, mensajeBody, titulo, JOptionPane.INFORMATION_MESSAGE);
		this.cerrar();	
	}
	
	public void cerrar() {
		ventana.dispose();
	}
}

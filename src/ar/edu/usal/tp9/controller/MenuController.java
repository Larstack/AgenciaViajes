package ar.edu.usal.tp9.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import ar.edu.usal.tp9.utils.DbConnection;
import ar.edu.usal.tp9.view.AcercaDeView;
import ar.edu.usal.tp9.view.ConsultaActualizacionView;
import ar.edu.usal.tp9.view.ConsultaMasivaView;
import ar.edu.usal.tp9.view.IngresoView;
import ar.edu.usal.tp9.view.MenuView;

public class MenuController implements ActionListener {

	private MenuView menuView;

	@Override
	public void actionPerformed(ActionEvent e) {

		if ("Ingreso".equals(e.getActionCommand())) {
			IngresoController ingresoController = new IngresoController();
			IngresoView ingresoView = new IngresoView(ingresoController);
		} else if ("Modificacion y Consulta".equals(e.getActionCommand())) { 
			ConsultaActualizacionController consultaActualizacionController = new ConsultaActualizacionController();
			ConsultaActualizacionView consultaActualizacionView = new ConsultaActualizacionView(consultaActualizacionController);
		} else if ("Consulta y Actualizacion Masiva".equals(e.getActionCommand())) {
			ConsultaMasivaController consultaMasivaController = new ConsultaMasivaController();
			ConsultaMasivaView consultaMasivaView = new ConsultaMasivaView(consultaMasivaController);
		} else if ("Acerca de".equals(e.getActionCommand())) {
			AcercaDeView acercaDeView = new AcercaDeView();
		} else if ("Salir".equals(e.getActionCommand())){
			
			DbConnection db = DbConnection.getInstance();
			
			try {
				
				db.getConnection().close();
				
			} catch (SQLException e1) {
				
				e1.printStackTrace();
			}
			
			System.exit(0);
		}
	}

	public void setView(MenuView menuView) {

		this.menuView = menuView;

	}

}

package ar.edu.usal.tp9.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.ListModel;

import ar.edu.usal.tp9.exception.PaqueteNoEncontradoException;
import ar.edu.usal.tp9.model.dao.FacturasDao;
import ar.edu.usal.tp9.model.dao.HotelesDao;
import ar.edu.usal.tp9.model.dao.PaquetesDao;
import ar.edu.usal.tp9.model.dao.PasajerosDao;
import ar.edu.usal.tp9.model.dao.TablasMaestrasDao;
import ar.edu.usal.tp9.model.dto.Hoteles;
import ar.edu.usal.tp9.model.dto.Paquetes;
import ar.edu.usal.tp9.model.dto.PaquetesConEstadias;
import ar.edu.usal.tp9.model.dto.Pasajeros;
import ar.edu.usal.tp9.model.interfaces.ICalculoImporte;
import ar.edu.usal.tp9.utils.Validador;
import ar.edu.usal.tp9.view.ConsultaActualizacionView;

public class ConsultaActualizacionController implements ActionListener, ICalculoImporte {

	private ConsultaActualizacionView consultaActualizacionView;

	public void setView(ConsultaActualizacionView consultaActualizacionView) {

		this.consultaActualizacionView = consultaActualizacionView;

	}

	public ArrayList getPasajerosListasFromTxt() {

		PasajerosDao pasajerosDao = PasajerosDao.getInstance();
		Iterator it = pasajerosDao.getPasajeros().iterator();

		ArrayList<String> nombresPasajeros = new ArrayList<String>();
		ArrayList<Integer> documentosPasajeros = new ArrayList<Integer>();
		nombresPasajeros.add("Seleccionar");

		while (it.hasNext()) {

			Pasajeros pasajeroTmp = ((Pasajeros) it.next());

			nombresPasajeros.add(pasajeroTmp.getNombreApellido());
			documentosPasajeros.add(pasajeroTmp.getDni());			
		}

		ArrayList listaDatosPasajeros = new ArrayList<>();
		listaDatosPasajeros.add(nombresPasajeros);
		listaDatosPasajeros.add(documentosPasajeros);

		return listaDatosPasajeros;		
	}

	public Object[] getLocalidadesFromDb() {

		TablasMaestrasDao tablasMaestrasDao = TablasMaestrasDao.getInstance();
		tablasMaestrasDao.getLocalidades().add(0,"Seleccionar");

		return tablasMaestrasDao.getLocalidades().toArray();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if ("Consultar".equals(e.getActionCommand())) {

			PasajerosDao pasajerosDao = PasajerosDao.getInstance();
			Pasajeros pasajero = (Pasajeros) this.consultaActualizacionView.getCmbPasajeros().getSelectedItem();

			String localidadString = ((String) this.consultaActualizacionView.getCmbLocalidades().getSelectedItem()).trim();

			PaquetesDao paquetesDao = new PaquetesDao();

			try {

				Paquetes paqueteEncontrado = paquetesDao.getPaqueteByPasajeroLocalidad(pasajero, localidadString);

				if(paqueteEncontrado != null){
					
					String hotel = null;
					boolean pensionCompleta = false;

					if(paqueteEncontrado instanceof PaquetesConEstadias){

						hotel = ((PaquetesConEstadias)paqueteEncontrado).getHotel().getNombre();
						pensionCompleta = ((PaquetesConEstadias)paqueteEncontrado).isEsPensionCompleta();
					}

					ArrayList<String> nombresPasajerosPaquete = new ArrayList<String>(); 
					ArrayList<Integer> documentosPasajerosPaquete = new ArrayList<Integer>();

					ArrayList<Pasajeros> pasajerosTmp = paqueteEncontrado.getPasajeros();

					this.consultaActualizacionView.setIdPaqueteEncontrado(paqueteEncontrado.getId());
					this.consultaActualizacionView.fillForm(
							pasajerosTmp.toArray(),
							documentosPasajerosPaquete.toArray(),
							paqueteEncontrado.getLocalidades().toArray(),
							Validador.calendarToString(paqueteEncontrado.getFechaHoraSalida(), "dd/MM/yyyy"),
							Validador.HoraCalendarToString(paqueteEncontrado.getFechaHoraSalida()),
							paqueteEncontrado.isTieneSeguro(),
							paqueteEncontrado.isQuiereAbonoTransporteLocal(),
							paqueteEncontrado.isQuiereVisitasGuiadas(),
							hotel,
							pensionCompleta,
							String.valueOf(paqueteEncontrado.getImporte())
							);
				}else{
					
					throw new PaqueteNoEncontradoException();
				}
			} catch (PaqueteNoEncontradoException ex) {

				this.consultaActualizacionView.mostrarMensajeDialog(ex.getMessage(), "Paquete No Encontrado");
			}

		} else if ("Modificacion".equals(e.getActionCommand())) {

			this.consultaActualizacionView.ocultarVisibilizarComponentesVentana(
					this.consultaActualizacionView.getComponentesPaqueteEncontrado(), true, true);

		} else if ("Anulacion".equals(e.getActionCommand())) {

			int rta = JOptionPane.showConfirmDialog(null, "Quiere borrar el paquete?", 
					"Confirmacion", JOptionPane.OK_CANCEL_OPTION);

			if (rta == JOptionPane.YES_OPTION){

				PaquetesDao paquetesDao = new PaquetesDao();
				//				Paquetes paquete = paquetesDao.getPaqueteById(this.consultaActualizacionView.getIdPaqueteEncontrado());

				boolean persistenciaOk = paquetesDao.borrarPaquete(this.consultaActualizacionView.getIdPaqueteEncontrado());

				if(persistenciaOk) {

					this.consultaActualizacionView.mostrarMensajeDialog("Datos guardados con exito!", "Exito");
					this.consultaActualizacionView.limpiar();
					this.consultaActualizacionView.ocultarVisibilizarComponentesVentana(
							this.consultaActualizacionView.getComponentesPaqueteEncontrado(), false, false);
				}else{

					this.consultaActualizacionView.mostrarMensajeDialog("Se ha verificado un error de persistencia.", "ERROR");
				}
			}
		} else if ("Calcular".equals(e.getActionCommand())) {

			if(this.consultaActualizacionView.validar()){

				double importe = this.calcularImporte();

				this.consultaActualizacionView.mostrarImporte(importe);
			}

		}else if ("Aceptar".equals(e.getActionCommand())) {

			PaquetesDao paquetesDao = new PaquetesDao();
			Paquetes paquete = paquetesDao.getPaqueteById(this.consultaActualizacionView.getIdPaqueteEncontrado());

			boolean persistenciaOk = false;

			if(this.consultaActualizacionView.validar()){

				persistenciaOk = this.guardarPaquete(paquete);
			}

			if(persistenciaOk) {

				this.consultaActualizacionView.mostrarMensajeDialog("Datos guardados con exito!", "Exito");
			}else{

				this.consultaActualizacionView.mostrarMensajeDialog("Se ha verificado un error de persistencia.", "ERROR");
			}

		} else if ("Cancelar".equals(e.getActionCommand())) {


			int rta = JOptionPane.showConfirmDialog(null, "Los datos seran borrados. Confirma?", 
					"Confirmacion", JOptionPane.OK_CANCEL_OPTION);

			if (rta == JOptionPane.YES_OPTION)
		
				this.consultaActualizacionView.limpiar();
		
		}else if ("QuitarPasajero".equals(e.getActionCommand())) {
			
			int[] elementosSeleccionados = consultaActualizacionView.getListaPasajerosCopia().getSelectedIndices();
			
			for(int i = elementosSeleccionados.length-1; i>=0; i--)
				consultaActualizacionView.getListModelPasajeros().remove(elementosSeleccionados[i]);
		
		} else if ("QuitarLocalidad".equals(e.getActionCommand())) {
			
			int[] elementosSeleccionados = consultaActualizacionView.getListaLocalidadesCopia().getSelectedIndices();
			
			for(int i = elementosSeleccionados.length-1; i>=0; i--)
				consultaActualizacionView.getModelo().remove(elementosSeleccionados[i]);
		
		} else if ("AgregarPasajero".equals(e.getActionCommand())) {
			
			ArrayList<Object> elementosSeleccionados = (ArrayList<Object>) consultaActualizacionView.getListaPasajerosOriginal().getSelectedValuesList();
			for(Object elemSelec : elementosSeleccionados){
				
				if(!(consultaActualizacionView.getListModelPasajeros().contains(((Pasajeros) elemSelec))))
					consultaActualizacionView.getListModelPasajeros().addElement((Pasajeros) elemSelec);
			}
		
		} else if ("AgregarLocalidad".equals(e.getActionCommand())) {
			
			ArrayList<Object> elementosSeleccionados = (ArrayList<Object>) consultaActualizacionView.getListaLocalidadesOriginal().getSelectedValuesList();
			for(Object elemSelec : elementosSeleccionados){
				
				if(!(consultaActualizacionView.getModelo().contains(((String) elemSelec))))
					consultaActualizacionView.getModelo().addElement((String) elemSelec);
			}
		
		} else if ("Seleccionar".equals(e.getActionCommand())) {
			
			TablasMaestrasDao tablasMaestrasDao = TablasMaestrasDao.getInstance();
			
			if (consultaActualizacionView.getComboHorariosIndex() == 0) {
				consultaActualizacionView.getComboModel().removeAllElements();
				for (int j = 0; j < tablasMaestrasDao.getTurnoHorariosMap().get("MANANA").size(); j++) {
					consultaActualizacionView.getComboHoras().addItem(tablasMaestrasDao.getTurnoHorariosMap().get("MANANA").get(j));
				}

			} else if (consultaActualizacionView.getComboHorariosIndex() == 1) {
				consultaActualizacionView.getComboModel().removeAllElements();
				for (int j = 0; j < tablasMaestrasDao.getTurnoHorariosMap().get("TARDE").size(); j++) {
					consultaActualizacionView.getComboHoras().addItem(tablasMaestrasDao.getTurnoHorariosMap().get("TARDE").get(j));
				}
	
			} else if (consultaActualizacionView.getComboHorariosIndex() == 2) {
				consultaActualizacionView.getComboModel().removeAllElements();
				for (int j = 0; j < tablasMaestrasDao.getTurnoHorariosMap().get("NOCHE").size(); j++) {
					consultaActualizacionView.getComboHoras().addItem(tablasMaestrasDao.getTurnoHorariosMap().get("NOCHE").get(j));
				}
	
			} 
			
		}
	}

	private boolean guardarPaquete(Paquetes paquete) {

		int id = paquete.getId();
		int numeroFactura = paquete.getFacturas().getNumero(); 

		if(this.consultaActualizacionView.getCmbHoteles().getSelectedIndex() > 0){

			if(!(paquete instanceof PaquetesConEstadias)){

				paquete = new PaquetesConEstadias();
			}

			HotelesDao hotelesDao = HotelesDao.getInstance();

			((PaquetesConEstadias) paquete).setHotel(hotelesDao.getHotelByNombre(((String)this.consultaActualizacionView.getCmbHoteles().getSelectedItem()).trim()));
			((PaquetesConEstadias) paquete).setEsPensionCompleta(this.consultaActualizacionView.getEsPensionCompleta().isSelected());
		}

		paquete.setId(id);
		paquete.setCantidadDias(Integer.valueOf(this.consultaActualizacionView.getTxtCantidadDias().getText()));

		Calendar fechaHoraSalida = Validador.stringToCalendar(this.consultaActualizacionView.getTxtFechaSalida().getText().trim(), "dd/MM/yyyy");
		paquete.setFechaHoraSalida(fechaHoraSalida);

		String horaCombo = ((String)this.consultaActualizacionView.getComboHoras().getSelectedItem()).trim();
		int hora = Integer.valueOf(horaCombo.substring(0, 2));
		int minutos = Integer.valueOf(horaCombo.substring(3, 5));
		Validador.setearHora(hora, minutos, fechaHoraSalida);

		double importe = Double.parseDouble(this.consultaActualizacionView.getTxtImporte().getText().trim());
		paquete.setImporte(importe);
		
		ArrayList<String> localidades = new ArrayList<String>(); 
		ListModel model = this.consultaActualizacionView.getListaLocalidadesCopia().getModel();
		for (int i = 0; i < model.getSize(); i++) {

			localidades.add((String) model.getElementAt(i));
		}
		
		paquete.setLocalidades(localidades);

		//		PasajerosDao pasajerosDao = PasajerosDao.getInstance();

		ArrayList<Pasajeros> pasajeros = new ArrayList<Pasajeros>(); 
		ListModel modelPasajeros = this.consultaActualizacionView.getListaPasajerosCopia().getModel();
		for (int i = 0; i < modelPasajeros.getSize(); i++) {

			pasajeros.add((Pasajeros) modelPasajeros.getElementAt(i));
		}

		paquete.setPasajeros(pasajeros);

		paquete.setQuiereAbonoTransporteLocal(this.consultaActualizacionView.getQuiereAbonoTransporteLocal().isSelected());
		paquete.setQuiereVisitasGuiadas(this.consultaActualizacionView.getQuiereVisitasGuiadas().isSelected());
		paquete.setTieneSeguro(this.consultaActualizacionView.getGrpSeguro().getSelection().getActionCommand().trim().
				equals("Si") ? true : false);

		//Se genera la factura correspondiente.
		paquete.generarFactura();
		paquete.getFacturas().setNumero(numeroFactura);
		PaquetesDao paquetesDao = new PaquetesDao();

		return paquetesDao.updatePaquete(paquete);
	}

	public Object[] getHotelesFromDb() {

		HotelesDao hotelesDao = HotelesDao.getInstance();
		Iterator it = hotelesDao.getHoteles().iterator();

		ArrayList<String> nombresHoteles = new ArrayList<String>();
		nombresHoteles.add("Seleccionar");

		while (it.hasNext()) {

			nombresHoteles.add(((Hoteles) it.next()).getNombre());

		}

		return nombresHoteles.toArray();
	}

	public Object[] getTurnosFromDb() {

		TablasMaestrasDao tablasMaestrasDao = TablasMaestrasDao.getInstance();
		HashMap turnosHorariosMap = tablasMaestrasDao.getTurnoHorariosMap();

		return turnosHorariosMap.keySet().toArray();
	}

	@Override
	public double calcularImporte() {

		double importeTotal = 0;
		double importeHotel = 0;

		TablasMaestrasDao tablasMaestrasDao = TablasMaestrasDao.getInstance();
		HashMap<String, Double> localidadesImportesMap = tablasMaestrasDao.getLocalidadesImportesMap();
		ListModel modelo = this.consultaActualizacionView.getModelo();

		ArrayList<String> localidadesSeleccionadas = new ArrayList<String>();

		for (int i = 0; i < modelo.getSize(); i++) {

			localidadesSeleccionadas.add((String)modelo.getElementAt(i));
		}

		double totalImporteLocalidades = 0;

		for (int i = 0; i < localidadesSeleccionadas.size(); i++) {

			totalImporteLocalidades += localidadesImportesMap.get(localidadesSeleccionadas.get(i));
		}

		importeTotal = importeTotal + totalImporteLocalidades; 

		if (this.consultaActualizacionView.getRdbSi().isSelected()) {

			importeTotal += importeTotal * ICalculoImporte.PORCENTAJE_SEGURO;

		}

		if (this.consultaActualizacionView.getQuiereVisitasGuiadas().isSelected()) {

			importeTotal += importeTotal * ICalculoImporte.PORCENTAJE_GUIA;

		}

		if (this.consultaActualizacionView.getQuiereAbonoTransporteLocal().isSelected()) {

			importeTotal += importeTotal * ICalculoImporte.PORCENTAJE_ABONO_TRANSPORTE;

		} 	

		if (this.consultaActualizacionView.getCmbHoteles().getSelectedIndex() != 0) {

			HotelesDao hotelesDao = HotelesDao.getInstance();

			importeHotel = hotelesDao.getHotelByNombre(((String)this.consultaActualizacionView.getCmbHoteles().getSelectedItem()).trim()).getImporte();

			importeTotal += importeHotel * (Double.valueOf(consultaActualizacionView.getTxtCantidadDias().getText()));

			if (this.consultaActualizacionView.getEsPensionCompleta().isSelected()) {

				importeTotal += importeTotal * ICalculoImporte.PORCENTAJE_PENSION_COMPLETA;

			}

		}		

		return importeTotal;

	}
}

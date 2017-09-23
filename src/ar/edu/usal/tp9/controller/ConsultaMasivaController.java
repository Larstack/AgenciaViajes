package ar.edu.usal.tp9.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import ar.edu.usal.tp9.model.dao.HotelesDao;
import ar.edu.usal.tp9.model.dao.PaquetesDao;
import ar.edu.usal.tp9.model.dao.PasajerosDao;
import ar.edu.usal.tp9.model.dao.TablasMaestrasDao;
import ar.edu.usal.tp9.model.dto.Hoteles;
import ar.edu.usal.tp9.model.dto.Paquetes;
import ar.edu.usal.tp9.model.dto.PaquetesConEstadias;
import ar.edu.usal.tp9.model.dto.Pasajeros;
import ar.edu.usal.tp9.utils.Validador;
import ar.edu.usal.tp9.view.ConsultaMasivaView;

public class ConsultaMasivaController implements ActionListener {

	private ConsultaMasivaView consultaMasivaView;

	public void setView(ConsultaMasivaView consultaMasivaView) {

		this.consultaMasivaView = consultaMasivaView;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if ("Consultar".equals(e.getActionCommand())) {

			ArrayList<Paquetes> paquetesEncontrados = new ArrayList<Paquetes>();
			ArrayList<String[]> registros = new ArrayList<String[]>();
			String cantidadRegistros = "";
			String cantidadRegistrosTotales = "";

			String pasajeroTxt = consultaMasivaView.getPasajero().getText();
			PaquetesDao paquetesDao = new PaquetesDao();

			if(pasajeroTxt != null && !pasajeroTxt.trim().isEmpty()){

				PasajerosDao pasajerosDao = PasajerosDao.getInstance();
				Pasajeros pasajeroEncontrado = pasajerosDao.buscarPasajeroByNombre(pasajeroTxt.trim());

				paquetesEncontrados = paquetesDao.getPaqueteByPasajero(pasajeroEncontrado);

			}else{

				paquetesEncontrados = paquetesDao.loadTodosLosPaquetes();

			}
			if(paquetesEncontrados != null && !paquetesEncontrados.isEmpty()){

				for (int i = 0; i < paquetesEncontrados.size(); i++) {

					Paquetes paqueteIterado = paquetesEncontrados.get(i);

					String hotelString = "";
					String pensionCompleta = "";

					if(paqueteIterado instanceof PaquetesConEstadias){

						hotelString = ((PaquetesConEstadias) paqueteIterado).getHotel().getNombre();
						pensionCompleta = ((PaquetesConEstadias) paqueteIterado).isEsPensionCompleta() ? "SI" : "NO";
					}

					ArrayList<String> nombrePasajerosList = new ArrayList<>();
					for (int j = 0; j < paqueteIterado.getPasajeros().size(); j++) {

						nombrePasajerosList.add(paqueteIterado.getPasajeros().get(j).getNombreApellido().trim());
					}

					String[] registro = {
							String.valueOf(paqueteIterado.getId()),
							Validador.ListToString(nombrePasajerosList),					
							Validador.ListToString(paqueteIterado.getLocalidades()),
							Validador.calendarToString(paqueteIterado.getFechaHoraSalida(), "dd/MM/yyyy"),
							String.valueOf(paqueteIterado.getCantidadDias()),
							paqueteIterado.isTieneSeguro() ? "SI" : "NO",
									paqueteIterado.isQuiereAbonoTransporteLocal() ? "SI" : "NO",
											paqueteIterado.isQuiereVisitasGuiadas() ? "SI" : "NO",
													hotelString,
													pensionCompleta,
													String.valueOf(paqueteIterado.getImporte())
					};

					registros.add(registro);
				}
			}

			cantidadRegistros = String.valueOf(registros.size());
			cantidadRegistrosTotales = String.valueOf(paquetesDao.getCantidadTotalPaquetes());

			this.consultaMasivaView.mostrarRegistros(registros, cantidadRegistros, cantidadRegistrosTotales);

		}else if ("Actualizar".equals(e.getActionCommand())) {

			ArrayList<String> errores = new ArrayList<>();

			DefaultTableModel model = (DefaultTableModel)this.consultaMasivaView.getTablaResultado().getModel();

			for (int i = 0; i < model.getRowCount(); i++) {

				Paquetes paquete = new Paquetes();
				int idPaquete = Integer.valueOf((String) model.getValueAt(i, 0));

				if(model.getValueAt(i, 8) != null && !(((String) model.getValueAt(i, 8)).trim().isEmpty())){

					HotelesDao hotelesDao = HotelesDao.getInstance();

					Hoteles hotel = hotelesDao.getHotelByNombre((String)model.getValueAt(i, 8));

					if(hotel == null){

						errores.add("- El hotel ingresado es invalido. Si no se requiere hotel, dejar vacio el campo.");
					}else{

						paquete = new PaquetesConEstadias();

						((PaquetesConEstadias) paquete).setHotel(hotel);
						((PaquetesConEstadias) paquete).setEsPensionCompleta(
							Boolean.valueOf(((((String)model.getValueAt(i, 9)).trim().isEmpty() ||
							((String)model.getValueAt(i, 9)).trim().equalsIgnoreCase("NO")) ? false : true))
						);
					}
				}

				if(errores.isEmpty()){

					paquete.setId(idPaquete);
					ArrayList<Pasajeros> pasajeros = new ArrayList<>();

					PasajerosDao pasajerosDao = PasajerosDao.getInstance();
					boolean ok = false;
					if(model.getValueAt(i, 1) != null && !(((String) model.getValueAt(i, 1)).trim().isEmpty())){

						String pasajerosString = (String)model.getValueAt(i, 1);
						String[] pasajerosArray = pasajerosString.split(",");

						for (int j = 0; j < pasajerosArray.length; j++) {

							Pasajeros pasajero = pasajerosDao.buscarPasajeroByNombre(pasajerosArray[j].trim());

							if(pasajero != null){

								pasajeros.add(pasajero);
							}else{

								ok = false;
								break;
							}
						}

						ok = true;
					}

					if(!ok){

						errores.add("- Pasajeros no encontrados. Controlar los datos ingresados. Cada pasajero debe estar separado por coma.");
					}else{

						paquete.setPasajeros(pasajeros);

						TablasMaestrasDao tablaMaestrasDao = TablasMaestrasDao.getInstance();
						ArrayList<String> localidades = new ArrayList<>();
						ok = false;
						if(model.getValueAt(i, 2) != null && !(((String) model.getValueAt(i, 2)).trim().isEmpty())){

							String localidadesString = (String)model.getValueAt(i, 2);
							String[] localidadesArray = localidadesString.split(",");

							for (int j = 0; j < localidadesArray.length; j++) {

								String localidad = tablaMaestrasDao.getLocalidadByNombre(localidadesArray[j].trim());

								if(localidad != null && !localidad.isEmpty()){

									localidades.add(localidad);
								}else{

									ok = false;
									break;
								}
							}

							ok = true;
						}

						if(!ok){

							errores.add("- Localidades no encontradas. Controlar los datos ingresados. Cada localidad debe estar separada por coma.");
						}else{

							paquete.setLocalidades(localidades);

							Calendar fecha = Calendar.getInstance();
							ok = false;
							if(model.getValueAt(i, 3) != null && !(((String) model.getValueAt(i, 3)).trim().isEmpty())){

								String fechaString = (String)model.getValueAt(i, 3);

								try{
									int dia = Integer.valueOf(fechaString.trim().substring(0,2));
									int mes = Integer.valueOf(fechaString.trim().substring(3,5));
									int anio = Integer.valueOf(fechaString.trim().substring(6,10));

									if(!((dia > 0 && dia <=31) && (mes > 0 && mes <= 12) && (anio >= 2017 && anio <= 9999))){

										ok = false;
									}else{

										fecha = Validador.stringToCalendar(fechaString, "dd/MM/yyyy");

										ok = true;
									}
								}catch(NumberFormatException ex){

									ok = false;
								}
							}

							if(!ok){

								errores.add("- La fecha ingresada es invalida. El formato debe ser dd/mm/yyyy.");
							}else{

								paquete.setFechaHoraSalida(fecha);

								int dias = 0;
								ok = false;
								if(model.getValueAt(i, 4) != null && !(((String) model.getValueAt(i, 4)).trim().isEmpty())){

									String cantidadString = (String)model.getValueAt(i, 4);

									try{
										dias = Integer.valueOf(cantidadString.trim());

										if(dias > 0){

											ok = true;
										}
									}catch(NumberFormatException ex){

										ok = false;
									}
								}

								if(!ok){

									errores.add("- La cantidad de dias debe ser en formato numerico y mayor a 0.");
								}else{

									paquete.setCantidadDias(dias);

									paquete.setTieneSeguro(
											Boolean.valueOf(((((String)model.getValueAt(i, 5)).trim().isEmpty() ||
													((String)model.getValueAt(i, 5)).trim().equalsIgnoreCase("NO")) ? false : true))
											);

									paquete.setQuiereAbonoTransporteLocal(
											Boolean.valueOf(((((String)model.getValueAt(i, 6)).trim().isEmpty() ||
													((String)model.getValueAt(i, 6)).trim().equalsIgnoreCase("NO")) ? false : true)) 
											);

									paquete.setQuiereVisitasGuiadas(
											Boolean.valueOf(((((String)model.getValueAt(i, 7)).trim().isEmpty() ||
													((String)model.getValueAt(i, 7)).trim().equalsIgnoreCase("NO")) ? false : true)) 
											);

									double importe = 0;
									ok = false;
									if(model.getValueAt(i, 10) != null && !(((String) model.getValueAt(i, 10)).trim().isEmpty())){

										String importeString = (String)model.getValueAt(i, 10);

										try{
											importe = Double.valueOf(importeString.trim());

											if(importe >= 0){

												ok = true;

												paquete.setImporte(importe);

												paquete.generarFactura();
												
												PaquetesDao paquetesDao = new PaquetesDao();
												boolean persistenciaOk = paquetesDao.actualizarPaqueteConsultaMasiva(paquete);

												if(persistenciaOk) {

													this.consultaMasivaView.mostrarMensajeDialog("Datos guardados con exito!", "Exito");
												}else{

													this.consultaMasivaView.mostrarMensajeDialog("Se ha verificado un error de persistencia.", "ERROR");
												}
											}
										}catch(NumberFormatException ex){

											ok = false;
										}
									}

									if(!ok){

										errores.add("- El importe tiene que estar en formato numerico y mayor o igual a 0.");
									}
								}
							}
						}
					}
				}else{

						String error = "";

						for (int j = 0; j < errores.size(); j++) {

							error = error + "\n " + errores.get(j);
						}

						this.consultaMasivaView.mostrarMessageDialog(error, "Datos no validos");
				}
			}
		}
	}
}

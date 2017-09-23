package ar.edu.usal.tp9.model.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;

import ar.edu.usal.tp9.exception.PaqueteNoEncontradoException;
import ar.edu.usal.tp9.model.dto.Facturas;
import ar.edu.usal.tp9.model.dto.Hoteles;
import ar.edu.usal.tp9.model.dto.Paquetes;
import ar.edu.usal.tp9.model.dto.PaquetesConEstadias;
import ar.edu.usal.tp9.model.dto.Pasajeros;
import ar.edu.usal.tp9.utils.DbConnection;
import ar.edu.usal.tp9.utils.Validador;

public class PaquetesDao {

	private Connection conn;

	public PaquetesDao(){

		DbConnection dbConn = DbConnection.getInstance();
		this.conn = dbConn.getConnection();
	}

	public boolean persistirPaquete(Paquetes paquete) {

		boolean persistenciaOk = false;

		try {

			Facturas factura = paquete.getFacturas();

			/*
			 * El numero de la factura se genera automaticamente al insertar el registro.
			 * 
			 * CREATE SEQUENCE factura_numero_seq
			 * START WITH 1  
			 * INCREMENT BY 1;
			 */
			String insertFactura = "insert into Facturas(fecha,importe,tipo) " +
					"values(getDate()," + String.valueOf(factura.getImporte()) + ", '" + factura.getTipo() +"')";

			Statement stmt = this.conn.createStatement();
			stmt.execute(insertFactura);

			ResultSet rs = stmt.executeQuery("select max(id) facturaid from Facturas");
			rs.next();
			String facturaId = String.valueOf(rs.getInt("facturaid")); 

			String tieneSeguro = paquete.isTieneSeguro() ? "1" : "0";
			String quiereVisitaGuiada = paquete.isQuiereVisitasGuiadas() ? "1" : "0";
			String quiereAbonoTransporteLocal = paquete.isQuiereAbonoTransporteLocal() ? "1" : "0";
			String esPensionCompleta = "0"; 
			String hotelId = "null";

			if(paquete instanceof PaquetesConEstadias){

				esPensionCompleta = ((PaquetesConEstadias)paquete).isEsPensionCompleta() ? "1" : "0";
				hotelId = String.valueOf(((PaquetesConEstadias)paquete).getHotel().getId());
			}

			String insertPaquete = "insert into Paquetes( " +
					"		fecha_hora_salida, " +
					"		cantidad_dias, " +
					"		importe, " +
					"		tiene_seguro, " +
					"		factura_id, " +
					"		quiere_visita_guiada, " +
					"		quiere_abono_transporte_local, " +
					"		es_pension_completa, " +
					"		hotel_id) " +
					"values('" +
					Validador.calendarToString(paquete.getFechaHoraSalida(), "yyyyMMdd HH:mm:ss") + "' " +
					"	," + String.valueOf(paquete.getCantidadDias()) +
					"	," + String.valueOf(paquete.getImporte()) +
					"	," + tieneSeguro +
					"	," + facturaId +
					"	," + quiereVisitaGuiada +
					"	," + quiereAbonoTransporteLocal +
					"	," + esPensionCompleta +
					"	," + hotelId +
					")";

			stmt.execute(insertPaquete);

			rs = stmt.executeQuery("select max(id) paqueteid from Paquetes");
			rs.next();
			String paqueteId = String.valueOf(rs.getInt("paqueteid")); 

			String localidades = "";
			for (int j = 0; j < paquete.getLocalidades().size(); j++) {

				localidades = localidades + "'" + paquete.getLocalidades().get(j) + "',";
			}
			localidades = localidades.substring(0,localidades.length()-1);

			String insertLocalidadesPaquetes =
					"insert into LocalidadesPaquetes(paquete_id, localidad_id) " +
							"select " + paqueteId + ", l.id from Localidades l " +
							"where l.localidad in ("+ localidades +") ";

			stmt.execute(insertLocalidadesPaquetes);

			String pasajeros = "";

			for (int j = 0; j < paquete.getPasajeros().size(); j++) {

				pasajeros = pasajeros + "'" + String.valueOf(paquete.getPasajeros().get(j).getDni()) + "',";
			}
			pasajeros = pasajeros.substring(0,pasajeros.length()-1);

			String insertPasajerosPaquetes =
					"insert into PasajerosPaquetes(paquete_id, pasajero_id) " +
							"select " + paqueteId + ", p.id from Pasajeros p " +
							"where p.dni in ("+ pasajeros +") ";

			stmt.execute(insertPasajerosPaquetes);

			persistenciaOk = true;

		} catch (Exception e) {

			e.printStackTrace();
		}

		return persistenciaOk;
	}
	
	public boolean updatePaquete(Paquetes paquete) {

		boolean persistenciaOk = false;

		try {

			Facturas factura = paquete.getFacturas();

			String updateFactura = "update Facturas set " +
					" fecha = getDate(), importe = " + String.valueOf(factura.getImporte()) +
					" ,tipo = '" + factura.getTipo() + "' " +
					" where numero = " + String.valueOf(factura.getNumero());

			Statement stmt = this.conn.createStatement();
			stmt.execute(updateFactura);

			ResultSet rs = stmt.executeQuery("select id from Facturas where numero = " + String.valueOf(factura.getNumero()));
			rs.next();
			String facturaId = String.valueOf(rs.getInt("id")); 

			String tieneSeguro = paquete.isTieneSeguro() ? "1" : "0";
			String quiereVisitaGuiada = paquete.isQuiereVisitasGuiadas() ? "1" : "0";
			String quiereAbonoTransporteLocal = paquete.isQuiereAbonoTransporteLocal() ? "1" : "0";
			String esPensionCompleta = "0"; 
			String hotelId = "null";

			if(paquete instanceof PaquetesConEstadias){

				esPensionCompleta = ((PaquetesConEstadias)paquete).isEsPensionCompleta() ? "1" : "0";
				hotelId = String.valueOf(((PaquetesConEstadias)paquete).getHotel().getId());
			}

			String updatePaquete = "update Paquetes set " +
					"		fecha_hora_salida = '" + Validador.calendarToString(paquete.getFechaHoraSalida(), "yyyyMMdd HH:mm:ss") + "' " + 
					"		,cantidad_dias = " + String.valueOf(paquete.getCantidadDias()) +
					"		,importe = " + String.valueOf(paquete.getImporte()) +
					"		,tiene_seguro = " + tieneSeguro +
					"		,factura_id = " + facturaId +
					"		,quiere_visita_guiada = " + quiereVisitaGuiada +
					"		,quiere_abono_transporte_local = " + quiereAbonoTransporteLocal +
					"		,es_pension_completa = " + esPensionCompleta +
					"		, hotel_id = " + hotelId +
					" where id = " + paquete.getId();

			stmt.execute(updatePaquete);

			String localidades = "";
			for (int j = 0; j < paquete.getLocalidades().size(); j++) {

				localidades = localidades + "'" + paquete.getLocalidades().get(j) + "',";
			}
			localidades = localidades.substring(0,localidades.length()-1);

			String sqlDeleteLocalidades = "delete from LocalidadesPaquetes where paquete_id = " + paquete.getId(); 
			stmt.execute(sqlDeleteLocalidades);
			
			String insertLocalidadesPaquetes =
					"insert into LocalidadesPaquetes(paquete_id, localidad_id) " +
							"select " + paquete.getId() + ", l.id from Localidades l " +
							"where l.localidad in ("+ localidades +") ";

			stmt.execute(insertLocalidadesPaquetes);

			String pasajeros = "";

			for (int j = 0; j < paquete.getPasajeros().size(); j++) {

				pasajeros = pasajeros + "'" + String.valueOf(paquete.getPasajeros().get(j).getDni()) + "',";
			}
			pasajeros = pasajeros.substring(0,pasajeros.length()-1);

			String sqlDeletePasajeros = "delete from PasajerosPaquetes where paquete_id = " + paquete.getId(); 
			stmt.execute(sqlDeletePasajeros);
			
			String insertPasajerosPaquetes =
					"insert into PasajerosPaquetes(paquete_id, pasajero_id) " +
							"select " + paquete.getId() + ", p.id from Pasajeros p " +
							"where p.dni in ("+ pasajeros +") ";

			stmt.execute(insertPasajerosPaquetes);

			persistenciaOk = true;

		} catch (Exception e) {

			e.printStackTrace();
		}

		return persistenciaOk;
	}

	public boolean borrarPaquete(int id){

		try {

			String deleteLocalidades = "delete from LocalidadesPaquetes where paquete_id = " + id;

			Statement stmt = this.conn.createStatement();
			stmt.execute(deleteLocalidades);
			
			String deletePasajeros = "delete from PasajerosPaquetes where paquete_id = " + id;

			stmt = this.conn.createStatement();
			stmt.execute(deletePasajeros);
			
			String deletePaquete = "delete from Paquetes where id = " + id;

			stmt = this.conn.createStatement();
			stmt.execute(deletePaquete);

			return true;
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean persistirPaqueteSP(Paquetes paquete) {

		try{
			double importe = paquete.getFacturas().getImporte();
			char tipoFactura = paquete.getFacturas().getTipo();

			boolean tieneSeguro = paquete.isTieneSeguro();
			boolean quiereVisitaGuiada = paquete.isQuiereVisitasGuiadas();
			boolean quiereAbonoTransporteLocal = paquete.isQuiereAbonoTransporteLocal();
			boolean esPensionCompleta = false;
			Integer hotelId = null;

			if(paquete instanceof PaquetesConEstadias){

				esPensionCompleta = ((PaquetesConEstadias)paquete).isEsPensionCompleta();
				hotelId = ((PaquetesConEstadias)paquete).getHotel().getId();
			}

			Date fechaHoraSalida = new Date(paquete.getFechaHoraSalida().getTimeInMillis());
			int cantidadDias = paquete.getCantidadDias();

			String localidades = "";
			for (int j = 0; j < paquete.getLocalidades().size(); j++) {

				localidades = localidades + paquete.getLocalidades().get(j).trim() + ",";
			}
			localidades = localidades.substring(0,localidades.length()-1);

			String pasajeros = "";

			for (int j = 0; j < paquete.getPasajeros().size(); j++) {

				pasajeros = pasajeros + String.valueOf(paquete.getPasajeros().get(j).getDni()) + ",";
			}
			pasajeros = pasajeros.substring(0,pasajeros.length()-1);

			CallableStatement callStmt = this.conn.prepareCall("{call sp_ingresar_nuevo_paquete(?,?,?,?,?,?,?,?,?,?,?)}");
			callStmt.setDouble(1, importe);
			callStmt.setString(2, String.valueOf(tipoFactura));
			callStmt.setBoolean(3, tieneSeguro);
			callStmt.setBoolean(4, quiereVisitaGuiada);
			callStmt.setBoolean(5, quiereAbonoTransporteLocal);
			callStmt.setBoolean(6, esPensionCompleta);
			callStmt.setInt(7, hotelId);
			callStmt.setDate(8, fechaHoraSalida);
			callStmt.setInt(9, cantidadDias);
			callStmt.setString(10, localidades);
			callStmt.setString(11, pasajeros);

			callStmt.execute();

			return true;
		}catch(Exception e){

			e.printStackTrace();
		}
		return false;
	}

	public Paquetes getPaqueteById(int id) {

		ArrayList<Paquetes> paquetes = loadPaquetes(null, null, id);
		
		return (paquetes != null && paquetes.size() > 0) ? paquetes.get(0) : null; 
	}

	public Paquetes getPaqueteByPasajeroLocalidad(Pasajeros pasajero,
			String localidadString) throws PaqueteNoEncontradoException {

		ArrayList<Paquetes> paquetes = loadPaquetes(pasajero, localidadString, null);
		
		return (paquetes != null && paquetes.size() > 0) ? paquetes.get(0) : null; 
	}

	public ArrayList<Paquetes> getPaqueteByPasajero(Pasajeros pasajero) {

		return loadPaquetes(pasajero, null, null);		
	}

	public ArrayList<Paquetes> loadTodosLosPaquetes() {
		
		return this.loadPaquetes(null, null, null);
	}
	
	public int getCantidadTotalPaquetes() {
		
		int cantidad = 0;
		
		try{

			String sql = "select count(paq.id) cantidad from Paquetes paq ";

			Statement stmt = this.conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()){
				
				cantidad = rs.getInt("cantidad");
			}
			
		}catch(Exception e){

			e.printStackTrace();
		}

		return cantidad;
	}

	
	private ArrayList<Paquetes> loadPaquetes(Pasajeros pasajero, String localidad, Integer id) {
		
		ArrayList<Paquetes> paquetesArray = new ArrayList<Paquetes>();
		PasajerosDao pasajerosDao = PasajerosDao.getInstance();
		TablasMaestrasDao tablasMaestrasDao = TablasMaestrasDao.getInstance();
		
		try{

			String sql = "select distinct" +
					"	paq.id id, " +
					"	paq.fecha_hora_salida fecha_hora_salida, " +
					"	paq.cantidad_dias cantidad_dias, " +
					"	paq.importe importe, " +
					"	paq.tiene_seguro tiene_seguro, " +
					"	paq.factura_id factura_id, " +
					"	paq.quiere_visita_guiada quiere_visita_guiada, " +
					"	paq.quiere_abono_transporte_local quiere_abono_transporte_local, " +
					"	paq.hotel_id hotel_id, " +
					"	paq.es_pension_completa es_pension_completa " +
					"from Paquetes paq ";
	
			String where = "where 1=1 ";

			if(id != null){
				
				where = where + "and paq.id = " + String.valueOf(id);
			}

			if(localidad != null && !localidad.trim().isEmpty()){

				sql +=  "	inner join LocalidadesPaquetes lp on paq.id = lp.paquete_id " +
						"		inner join Localidades l on lp.localidad_id = l.id ";
				
				where += "and l.localidad = '" + localidad.trim() + "' ";
			}
			
			if(pasajero != null){

				sql += " inner join PasajerosPaquetes pp on paq.id = pp.paquete_id " +
					" inner join Pasajeros pas on pp.pasajero_id = pas.id ";
				
				where += "and pas.dni = '" + String.valueOf(pasajero.getDni()) + "' ";
			}
			
			sql += where;
			
			Statement stmt = this.conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()){

				Paquetes paquete = new Paquetes();

				HotelesDao hotelesDao = HotelesDao.getInstance();
				int idHotel = rs.getInt("hotel_id");

				if(idHotel != 0){

					paquete = new PaquetesConEstadias();

					((PaquetesConEstadias)paquete).setHotel(hotelesDao.getHotelById(idHotel));
					((PaquetesConEstadias)paquete).setEsPensionCompleta(rs.getBoolean("es_pension_completa"));
				}
				
				paquete.setId(rs.getInt("id"));

				Calendar fechaHoraSalida = Calendar.getInstance();
				fechaHoraSalida.setTime(rs.getDate("fecha_hora_salida"));
				paquete.setFechaHoraSalida(fechaHoraSalida);

				paquete.setCantidadDias(rs.getInt("cantidad_dias"));
				paquete.setImporte(rs.getDouble("importe"));
				paquete.setTieneSeguro(rs.getBoolean("tiene_seguro"));
				paquete.setQuiereVisitasGuiadas(rs.getBoolean("quiere_visita_guiada"));
				paquete.setQuiereAbonoTransporteLocal(rs.getBoolean("quiere_abono_transporte_local"));

				FacturasDao facturasDao = new FacturasDao();
				facturasDao.loadFacturaById(rs.getInt("factura_id"), paquete);

				paquete.setPasajeros(pasajerosDao.loadPasajerosByPaquete(paquete));
				
				paquete.setLocalidades(tablasMaestrasDao.loadLocalidadesStringByPaquete(paquete));
				
				paquetesArray.add(paquete);
			}
		}catch(Exception e){

			e.printStackTrace();
		}

		return paquetesArray;
	}

	public boolean actualizarPaqueteConsultaMasiva(Paquetes paquete) {
		
		boolean persistenciaOk = false;

		try {

			Facturas factura = paquete.getFacturas();

			PreparedStatement stmt = this.conn.prepareStatement(			
					"update Facturas set fecha = getDate(), importe = ? ,tipo = ? " +
					"where id in (select factura_id from Paquetes where id = ?)");

					stmt.setDouble(1, factura.getImporte());
					stmt.setString(2, String.valueOf(factura.getTipo()));
					stmt.setInt(3, paquete.getId());
					
			stmt.execute();

			stmt = this.conn.prepareStatement(
				"update Paquetes set " +
				"		fecha_hora_salida = ? " + 
				"		,cantidad_dias = ? " +
				"		,importe = ? " +
				"		,tiene_seguro = ? " +
				"		,quiere_visita_guiada = ? " + 
				"		,quiere_abono_transporte_local = ? " +
				"		,es_pension_completa = ? " +
				"		,hotel_id = ? " +
				" where id = ? "
			);
			
			Date fecha = new Date(paquete.getFechaHoraSalida().getTimeInMillis());
			stmt.setDate(1, fecha);
			stmt.setInt(2, paquete.getCantidadDias());
			stmt.setDouble(3, paquete.getImporte());
			stmt.setBoolean(4, paquete.isTieneSeguro());
			stmt.setBoolean(5, paquete.isQuiereVisitasGuiadas());
			stmt.setBoolean(6, paquete.isQuiereAbonoTransporteLocal());
			stmt.setInt(9, paquete.getId());
			
			if(paquete instanceof PaquetesConEstadias){

				stmt.setBoolean(7, ((PaquetesConEstadias)paquete).isEsPensionCompleta());
				stmt.setInt(8, ((PaquetesConEstadias)paquete).getHotel().getId());
			}else{
				
				stmt.setBoolean(7, false); 
				stmt.setNull(8, java.sql.Types.INTEGER);
			}

			stmt.execute();

			stmt = this.conn.prepareStatement("delete from LocalidadesPaquetes where paquete_id = ? ");
			
			stmt.setInt(1,paquete.getId()); 
			
			stmt.execute();

			String sql = "insert into LocalidadesPaquetes(paquete_id, localidad_id) " +
					"select ? , l.id from Localidades l " +
					"where l.localidad in (";
			
			for (int j = 0; j < paquete.getLocalidades().size(); j++) {
				
				sql += "?,";
			}
			sql = sql.substring(0,sql.length()-1);
		
			sql += ")";
			
			stmt = this.conn.prepareStatement(sql);	
			
			stmt.setInt(1, paquete.getId());
			
			for (int j = 2; j <= (paquete.getLocalidades().size() + 1); j++) {
				
				stmt.setString(j, paquete.getLocalidades().get(j-2));
			}
			
			stmt.execute();
			
			stmt = this.conn.prepareStatement("delete from PasajerosPaquetes where paquete_id = ? ");
			
			stmt.setInt(1,paquete.getId()); 
			
			stmt.execute();

			sql = "insert into PasajerosPaquetes(paquete_id, pasajero_id) " +
			"select ? , p.id from Pasajeros p " +
			"where p.dni in (";

			for (int j = 0; j < paquete.getPasajeros().size(); j++) {

				sql += "?,";
			}

			sql = sql.substring(0,sql.length()-1);
		
			sql += ")";
			
			stmt = this.conn.prepareStatement(sql);	
			
			stmt.setInt(1, paquete.getId());
			
			for (int j = 2; j <= (paquete.getPasajeros().size()+1); j++) {
				
				stmt.setInt(j, paquete.getPasajeros().get(j-2).getDni());
			}
			
			stmt.execute();

			persistenciaOk = true;

		} catch (Exception e) {

			e.printStackTrace();
		}

		return persistenciaOk;
	}
}

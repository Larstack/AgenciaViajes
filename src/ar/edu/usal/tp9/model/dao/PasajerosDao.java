package ar.edu.usal.tp9.model.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import ar.edu.usal.tp9.model.dto.Pasajeros;
import ar.edu.usal.tp9.utils.DbConnection;

public class PasajerosDao {

	private static PasajerosDao pasajerosDaoInstance = null;

	private Connection conn;
	
	private ArrayList<Pasajeros> pasajeros;

	private PasajerosDao(){
		
		this.pasajeros = new ArrayList<Pasajeros>();
		
		DbConnection dbConn = DbConnection.getInstance();
		this.conn = dbConn.getConnection();
		
		this.loadPasajeros();
	}

	public static PasajerosDao getInstance(){
		
		if(pasajerosDaoInstance==null){
			
			pasajerosDaoInstance = new PasajerosDao();
		}
		
		return pasajerosDaoInstance;
	}
	
	private void loadPasajeros() {

		try {

			String sql = "SELECT nombre_apellido, fecha_nacimiento, dni, email FROM pasajeros";

			Statement stmt = this.conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()){

				String nombreApellido = rs.getString("nombre_apellido");

				Calendar fechaNacimiento = Calendar.getInstance();
				fechaNacimiento.setTime(rs.getDate("fecha_nacimiento"));

				int dni = rs.getInt("dni");
				String email = rs.getString("email");

				this.pasajeros.add(new Pasajeros(nombreApellido, fechaNacimiento, dni, email));
			}
		}catch(Exception e){

			System.out.println("Error al cargar los pasajeros.");
		}
	}
	
	public ArrayList<Pasajeros> getPasajeros() {
		return pasajeros;
	}

	public Pasajeros getPasajeroByNombre(String pasajeroString) {

		Iterator pasajerosIterator = this.pasajeros.iterator();
		
		while (pasajerosIterator.hasNext()) {
			
			Pasajeros pasajero = (Pasajeros) pasajerosIterator.next();
			
			if(pasajero.getNombreApellido().trim().equals(pasajeroString.trim())){
				
				return pasajero;
			}
		}
		
		return null;
	}
	
	public Pasajeros getPasajeroByDocumento(int pasajeroDocumento) {

		Iterator pasajerosIterator = this.pasajeros.iterator();
		
		while (pasajerosIterator.hasNext()) {
			
			Pasajeros pasajero = (Pasajeros) pasajerosIterator.next();
			
			if(pasajero.getDni() == pasajeroDocumento){
				
				return pasajero;
			}
		}
		
		return null;
	}

	public Pasajeros buscarPasajeroByNombre(String pasajeroBuscado) {

		for (int i = 0; i < this.pasajeros.size(); i++) {
			
			String pasajeroTmp = new String(this.pasajeros.get(i).getNombreApellido().trim());
			
			int indice = pasajeroTmp.toLowerCase().indexOf(pasajeroBuscado.toLowerCase());
			
			if(indice != -1){
				
				return this.pasajeros.get(i);
			}
			
		}
		
		return null;
	}
}

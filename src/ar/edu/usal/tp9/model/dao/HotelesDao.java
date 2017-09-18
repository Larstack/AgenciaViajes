package ar.edu.usal.tp9.model.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import ar.edu.usal.tp9.model.dto.Hoteles;
import ar.edu.usal.tp9.utils.DbConnection;

public class HotelesDao {
	
	private static HotelesDao hotelesDaoInstance = null;

	private Connection conn;
	
	private ArrayList<Hoteles> hoteles;

	private HotelesDao(){
		
		this.hoteles = new ArrayList<Hoteles>();
		
		DbConnection dbConn = DbConnection.getInstance();
		this.conn = dbConn.getConnection();

		this.loadHoteles();
	}

	public static HotelesDao getInstance(){
		
		if(hotelesDaoInstance==null){
			
			hotelesDaoInstance = new HotelesDao();
		}
		
		return hotelesDaoInstance;
	}
	
	private void loadHoteles() {
		
		try {
			
			String sql = "SELECT id, nombre, importe, estrellas FROM Hoteles";
			Statement stmt = this.conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				
				Hoteles hotel = new Hoteles();
				
				hotel.setId(rs.getInt("id"));
				hotel.setNombre(rs.getString("nombre"));
				hotel.setEstrellas(rs.getInt("estrellas"));				
				hotel.setImporte(rs.getDouble("importe"));
				
				this.hoteles.add(hotel);
			}
			
		}catch(Exception e){
			
			System.out.println("Error al cargar los hoteles.");
		}
	}
	
	public Hoteles getHotelByNombre(String hotelString) {

		Iterator hotelesIterator = this.hoteles.iterator();
		
		while (hotelesIterator.hasNext()) {
			
			Hoteles hotel = (Hoteles) hotelesIterator.next();
			
			if(hotel.getNombre().trim().equals(hotelString.trim())){
				
				return hotel;
			}
		}
		
		return null;
	}
	
	public Hoteles getHotelById(int idHotel) {

		Iterator hotelesIterator = this.hoteles.iterator();
		
		while (hotelesIterator.hasNext()) {
			
			Hoteles hotel = (Hoteles) hotelesIterator.next();
			
			if(hotel.getId() == idHotel){
				
				return hotel;
			}
		}
		
		return null;
	}

	public ArrayList<Hoteles> getHoteles() {
		return hoteles;
	}
}

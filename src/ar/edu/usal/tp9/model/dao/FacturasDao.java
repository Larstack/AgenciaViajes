package ar.edu.usal.tp9.model.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

import ar.edu.usal.tp9.model.dto.Paquetes;
import ar.edu.usal.tp9.utils.DbConnection;

public class FacturasDao {

//	private static int nextIdFactura = 0;

//	private static FacturasDao facturasDaoInstance = null;

	private Connection conn;

	public FacturasDao(){

		DbConnection dbConn = DbConnection.getInstance();
		this.conn = dbConn.getConnection();
		
//		this.loadFacturas();
	}

//	public static FacturasDao getInstance(){
//		
//		if(facturasDaoInstance==null){
//			
//			facturasDaoInstance = new FacturasDao();
//		}
//		
//		return facturasDaoInstance;
//	}
	
//	public void loadFacturas() {
//
//		try{
//			String sql = 
//					"select f.fecha fecha, f.importe importe, f.numero numero, f.tipo tipo, paq.id paqueteid" +
//					"from Facturas f " +
//					"	inner join Paquetes paq on f.id = paq.factura_id ";
////					"		inner join PasajerosPaquetes pp on paq.id = pp.paquete_id ";
//
//			Statement stmt = this.conn.createStatement();
//			ResultSet rs = stmt.executeQuery(sql);
//
//			while(rs.next()){
//
//				int numeroFactura = rs.getInt("numero");
//				int idPaquete = rs.getInt("paqueteid");
//
//				Calendar fecha = Calendar.getInstance();
//				fecha.setTime(rs.getDate("fecha"));
//
//				char tipo = rs.getString("tipo").charAt(0);
//				double importe = rs.getDouble("importe");
//
//				PaquetesDao paquetesDao = new PaquetesDao();
//
//				Paquetes paquete = paquetesDao.getPaqueteById(idPaquete);
//				paquete.generarFactura(numeroFactura, fecha, tipo, importe);
//			}
//			
//		}catch(Exception e){
//
//			System.out.println("Error al cargar las facturas.");
//
//		}
//	}
	
	public void loadFacturaById(int id, Paquetes paquete) {

		try{
			String sql = 
					"select f.fecha fecha, f.importe importe, f.numero numero, f.tipo tipo " +
					"from Facturas f " +
					"where f.id = " + id;

			Statement stmt = this.conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()){

				int numeroFactura = rs.getInt("numero");
				
				Calendar fecha = Calendar.getInstance();
				fecha.setTime(rs.getDate("fecha"));

				char tipo = rs.getString("tipo").charAt(0);
				double importe = rs.getDouble("importe");

				paquete.generarFactura(numeroFactura, fecha, tipo, importe);
			}
			
		}catch(Exception e){

			System.out.println("Error al cargar la factura.");

		}
	}
	
}

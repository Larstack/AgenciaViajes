package ar.edu.usal.tp9.model.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ar.edu.usal.tp9.model.dto.Paquetes;
import ar.edu.usal.tp9.utils.DbConnection;

public class TablasMaestrasDao {

	private static TablasMaestrasDao tablasMaestrasDaoInstance = null;
	
	private HashMap<String,Double> localidadesImportesMap;
	private HashMap<String,ArrayList<String>> turnoHorariosMap;

	private Connection conn;
	
	private TablasMaestrasDao(){
		
		this.turnoHorariosMap = new HashMap<String, ArrayList<String>>();
		this.localidadesImportesMap = new HashMap<String, Double>();
		
		DbConnection dbConn = DbConnection.getInstance();
		this.conn = dbConn.getConnection();
		
		this.loadLocalidades();
		this.loadHorariosViajes();
	}

	public static TablasMaestrasDao getInstance(){
		
		if(tablasMaestrasDaoInstance==null){
			
			tablasMaestrasDaoInstance = new TablasMaestrasDao();
		}
		
		return tablasMaestrasDaoInstance;
	}
	
	private void loadLocalidades() {

		try {
			String sql = "SELECT localidad, importe FROM Localidades";

			Statement stmt = this.conn.createStatement();		
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {

				String localidad = rs.getString("localidad");
				Double importe = rs.getDouble("importe");

				this.localidadesImportesMap.put(localidad, importe);
			}
		}catch (Exception e) {

			System.out.println("Se ha verificado un error al cargar las localidades.");
		}
	}	
	
	public void loadHorariosViajes(){

		try {

			String sql = "SELECT turno, horario FROM Horarios";
			Statement stmt = this.conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()){

				String turno = rs.getString("turno");
				Time horario = rs.getTime("horario");

				if(this.turnoHorariosMap.get(turno) != null){

					this.turnoHorariosMap.get(turno).add(horario.toString());
				}else{

					this.turnoHorariosMap.put(turno, new ArrayList<String>());
					this.turnoHorariosMap.get(turno).add(horario.toString());
				}
			}
		}catch (Exception e) {

			System.out.println("Error al cargar los horarios.");
		}
	}

	public ArrayList<String> getLocalidades() {
		
		Object[] localidadesArray = this.localidadesImportesMap.keySet().toArray();
		ArrayList<String> localidadesList = new ArrayList<String>();
		
		for (int i = 0; i < localidadesArray.length; i++) {
			localidadesList.add((String)localidadesArray[i]);
		}
		
		return localidadesList;
	}

	public HashMap<String, ArrayList<String>> getTurnoHorariosMap() {
		
		return turnoHorariosMap;
	}

	public HashMap<String, Double> getLocalidadesImportesMap() {
		return localidadesImportesMap;
	}

	public ArrayList<String> loadLocalidadesStringByPaquete(Paquetes paquete) {

		HashMap<String,Double> localidadesImportesMapTmp = this.loadLocalidadesByPaquete(paquete);
		ArrayList<String> localidades = new ArrayList<>();
		
		Iterator it = localidadesImportesMapTmp.keySet().iterator();
		
		while (it.hasNext()) {
			
			localidades.add((String) it.next());
		}
		
		return localidades;
	}
	
	public HashMap<String,Double> loadLocalidadesByPaquete(Paquetes paquete) {
		
		HashMap<String,Double> localidadesImportesMapTmp = new HashMap<String, Double>();
		
		try {
			String sql = "SELECT localidad, importe FROM Localidades l " +
					"inner join LocalidadesPaquetes lp on l.id = lp.localidad_id " +
					"where lp.paquete_id =" + paquete.getId();

			Statement stmt = this.conn.createStatement();		
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {

				String localidad = rs.getString("localidad");
				Double importe = rs.getDouble("importe");

				localidadesImportesMapTmp.put(localidad, importe);
			}
		}catch (Exception e) {

			System.out.println("Se ha verificado un error al cargar las localidades.");
		}
		
		return localidadesImportesMapTmp;
	}
	
	public String getLocalidadByNombre(String localidadBuscada) {
		
		Iterator it = this.localidadesImportesMap.keySet().iterator();
		
		while(it.hasNext()){
			
			String localidad = (String) it.next();
			
			if(localidadBuscada.trim().toUpperCase().equals(localidad.trim().toUpperCase())){
				
				return localidad;
			}
		}
		
		return null;
	}
}

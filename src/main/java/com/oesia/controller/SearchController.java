package com.oesia.controller;


import com.jcraft.jsch.JSchException;
import com.oesia.model.Canal;
import com.oesia.model.Cliente;
import com.oesia.model.ClienteRepository;
import com.oesia.model.Compositor;
import com.oesia.model.MyRepository;
import com.oesia.model.Pe;
import com.oesia.model.PeRepository;
import com.oesia.model.PortFR;
import com.oesia.model.PortRadius;
import com.oesia.model.SedeRepository;
import com.oesia.model.Sedes;
import com.oesia.model.Servicio;
import com.oesia.model.ServiciosRepository;
import com.oesia.model.CiudadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
public class SearchController {
	
	@Autowired
	private SedeRepository sedeRepository; 
	@Autowired
	private ClienteRepository clienteRepository; 
	@Autowired
	private MyRepository myRepository; 
	@Autowired
	private PeRepository peRepository;
	@Autowired
	private CiudadRepository ciudadRepository;
	@Autowired
	private ServiciosRepository serviciosRepository;

	
	
    @PostMapping(path = "/api/clientes")

    public @ResponseBody List<Cliente> getClientes() {

    	List<Cliente> clientes = clienteRepository.findById();
        return clientes;

    }

    @PostMapping(path = "/api/ciudades")
    public @ResponseBody List<?> getCiudades(@RequestBody Cliente cliente){
		
    	List<?> ciudades = ciudadRepository.findCiudadesCliente(cliente.getCliente_nit());
    	return ciudades;
    }
    
    @PostMapping(path = "/api/sedes")
    public @ResponseBody List<Sedes> findSedes(@RequestBody Sedes sede){
		
    	List<Sedes> sedes = sedeRepository.findSedesCliente(sede.getSede_cliente(), sede.getSede_ciudad());
    	return sedes;
    }
     
    @PostMapping(path = "/api/canales")
    public @ResponseBody List<?> findCanal(@RequestBody Servicio servicio){   // @RequestBody Servicio_vpnip servicio
		
  
    	List<?> servicios = serviciosRepository.findServicioSede(servicio.getServicio_sede());  //
    	return servicios;
    }
    
    @PostMapping(path = "/api/vias")
    public  @ResponseBody List<Object> findVias(@RequestBody Canal canal){
		
    	List<Object> respuesta =  new ArrayList<Object>();
    	Canal vias = myRepository.findVias(canal.getId());
    	Pe pe = peRepository.findPe(vias.getPe());


        	respuesta.add(vias);
        	respuesta.add(pe);
    		return respuesta;	
    }
    
    
    
    //@Param("idCanal") String idCanal, @Param("pe_ip") String pe_ip
    @PostMapping(path = "/api/pruebasmpls")
    public List<String> pruebasMpls(@RequestBody String[] vias1) throws IOException, JSchException, InterruptedException{
    	
   // 	String ic = vias[0]; 
    //	String ipe = vias[1];
    	

    	Canal vias = myRepository.findViasS(vias1[0]);
    	Pe pe = peRepository.findByIP(vias1[1]);
    	
    	/*
    	List<String> respuesta =  new ArrayList<String>();    	
    	respuesta.add(pe.getIp_pe());
    	respuesta.add(vias.getVprn());
    	respuesta.add(vias1[0]);
    	respuesta.add(vias1[1]);
    	return respuesta;
    	*/
    	String nodo = pe.getIp_pe();
    	PortFR man = new PortFR(nodo); 
    	Compositor myComposer = new Compositor(); 
    	man.conectar();
    	
    	List<String> comandos = myComposer.crearComandos(pe.getNombre_pe(), vias.getVprn(), vias.getIpwan_pe(), vias.getIpwan_router(), vias.getPuertope(), vias.getEnrutamiento());
        List<String> respuesta = man.execute(comandos);
    	man.close();
    	
    	return respuesta;
    	
    	
    	
    	
    	//@Param("pruebampls") Canal pruebampls, @Param("pruebape") Pe pruebape
    	/*
   /* 	Pe pe_nodo = peRepository.findPe(pe.getPe_id());
    	String ippe = pe_nodo.getIp_pe();
    	String penombre = pe_nodo.getNombre_pe(); */
    	
  //  	PortFR man = new PortFR(pe.getIp_pe()); // no está mapeado a la tabla
   //     Compositor myComposer = new Compositor();  
   //     man.conectar();
   /* *    List<String> respuestape =  new ArrayList<String>();
        respuestape.add(pruebaspe.getIpwan_router());
        return respuestape;
   **/     
   	//    List<String> comandos = myComposer.crearComandos(pruebape.getNombre_pe(), pruebampls.getVprn(), pruebampls.getIpwan_pe(), pruebampls.getIpwan_router(), pruebampls.getPuertope(), pruebampls.getEnrutamiento());
   //	    List<String> respuestape =  new ArrayList<String>();
   	//    respuestape.addAll(comandos);
   	//    return respuestape;
   	//    List<String> respuestape = man.execute(comandos);	
    	//man.close();
    //	return respuestape; */
		
    }
    
    @PostMapping(path = "/api/pruebasrouter")
    public List<String> pruebasRouter(@RequestBody Canal canal) throws IOException, JSchException, InterruptedException{
    	
    	PortRadius man = new PortRadius(canal.getLoopback()); 
    	Compositor myComposer = new Compositor(); 
    	man.conectar();
    	
    	List<String> comandos = myComposer.comandosRouter(canal.getEnrutamiento());
        List<String> respuestarouter = man.execute(comandos);
    	man.close();
    	return respuestarouter; 
    }
}
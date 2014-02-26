package com.biblicon.control.springmvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.biblicon.modelo.bean.CampoTipoFicha;
import com.biblicon.modelo.bean.Ficha;
import com.biblicon.modelo.bean.TipoFicha;
import com.biblicon.modelo.bean.Usuario;
import com.biblicon.modelo.dao.CampoTipoFichaDAO;
import com.biblicon.modelo.dao.FichaDAO;
import com.biblicon.modelo.dao.TipoFichaDAO;
import com.biblicon.modelo.dao.UsuarioCompartidoDAO;
import com.biblicon.modelo.dao.UsuarioDAO;
import com.biblicon.util.Constantes;
import com.google.gson.Gson;

@Controller
public class FichaController {

	 final Logger logger = Logger.getLogger(FichaController.class);
	
	 @Autowired
	 private TipoFichaDAO tipofichaDAO;
	 @Autowired
	 private CampoTipoFichaDAO camposDAO;
	 @Autowired
	 private FichaDAO fichaDAO;
	 @Autowired
	 private UsuarioCompartidoDAO usuarioCompartidoDAO;
	 @Autowired
	 private UsuarioDAO usuarioDAO;
	 
	 @RequestMapping("ficha.htm")
	 public String principal(HttpServletRequest request, HttpServletResponse response) throws IOException {
		 Usuario usuario = (Usuario)request.getSession().getAttribute("usuario");
		 ArrayList<TipoFicha> listaTipos = tipofichaDAO.consultarPorUsuario(usuario.getId_usuario());
		 Gson gson = new Gson();
		 String tipos = gson.toJson(listaTipos);
		 request.setAttribute("tipos", tipos);
		 String campos = gson.toJson(camposDAO.consultarPorTipo(listaTipos.get(0).getId_tipo_ficha()));
		 request.setAttribute("campos", campos);
		 return "ficha";
	 }
	 
	@RequestMapping(value={"/agregarFicha.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	@ResponseBody
	public String agregarFicha(HttpServletRequest request)
	{
		
		Ficha ficha = null;
		String respuesta = "";
		try{
			
			Usuario usuario = (Usuario)request.getSession().getAttribute("usuario");
			Gson json= new Gson();			
			HashMap<String,String> camposMap = (HashMap<String,String>)json.fromJson(request.getParameter("campos"),HashMap.class);			
			camposMap.put(Constantes.usuario, usuario.getId_usuario());
			
			ficha = fichaDAO.llenarFicha(camposMap);			
			int id= fichaDAO.insertar(ficha);
			
			if(id != 0){
				respuesta = "{ \"respuesta\" : \"1\", \"id\" : \"" + id + "\"}";
			}else {
				respuesta = "{ \"respuesta\" : \"0\" , \"error\" : \"Error al insertar\"}";
			}
			
		}catch(Exception e){
			respuesta = "{ \"respuesta\" : \"0\" , \"error\" : \"Error al insertar catch\"}";
		}
		return respuesta;
	}
	
	@RequestMapping("actualizarFicha.htm")
	public String actualizarFicha(HttpServletRequest request)
	{
		
		Ficha ficha = fichaDAO.consultaFicha(new Integer(request.getParameter("idFicha")));
		Gson gson = new Gson();
		ArrayList<CampoTipoFicha> lstCampos = fichaDAO.llenarCampos(ficha);
		String campos = gson.toJson(lstCampos);
		request.setAttribute("tipos", tipos);
		request.setAttribute("campos", campos);
		return "actualizarficha";
	}
	
	@RequestMapping(value={"/eliminarFicha.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	@ResponseBody
	public String eliminarFicha(HttpServletRequest request)
	{	
		
		String respuesta = "";
		try{
			String idFicha = request.getParameter("id");
			Ficha ficha = new Ficha();
			ficha.setId_ficha(Integer.parseInt(idFicha));
			
			boolean result = fichaDAO.delete(ficha);
			
			if(result){
				respuesta = "{ \"respuesta\" : \"1\" }";
			}else {
				respuesta = "{ \"respuesta\" : \"0\" , \"error\" : \"Error al borrar\"}";
			}
		}catch(Exception e){
			respuesta = "{ \"respuesta\" : \"0\" , \"error\" : \"Error al borrar catch\"}";
		}
		
		return respuesta;
		
	}	
	
}
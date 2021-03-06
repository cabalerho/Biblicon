package com.biblicon.control.springmvc;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.biblicon.modelo.bean.ContenidoFicha;
import com.biblicon.modelo.bean.Ficha;
import com.biblicon.modelo.bean.TipoFicha;
import com.biblicon.modelo.bean.Usuario;
import com.biblicon.modelo.bean.UsuarioCompartido;
import com.biblicon.modelo.dao.ContenidoFichaDAO;
import com.biblicon.modelo.dao.FichaDAO;
import com.biblicon.modelo.dao.TipoFichaDAO;
import com.biblicon.modelo.dao.UsuarioCompartidoDAO;
import com.biblicon.modelo.dao.UsuarioDAO;
import com.google.gson.Gson;

@Controller
public class FichaCompartidaController {
	
	 @Autowired
	 private TipoFichaDAO tipofichaDAO;
	 @Autowired
	 private FichaDAO fichaDAO; 
	 @Autowired
	 private ContenidoFichaDAO contenidoFichaAO;
	 @Autowired
	 private UsuarioCompartidoDAO usuarioCompartidoDAO;
	 @Autowired
	 private UsuarioDAO usuarioDAO;
	 
	
	 @RequestMapping("fichasCompartidas.htm")
	 public String principal(HttpServletRequest request, HttpServletResponse response) throws IOException {
		 if (request.getSession().getAttribute("usuario") == null) return "login";
		 Usuario usuario = (Usuario)request.getSession().getAttribute("usuario");
		 Gson gson = new Gson();
		 
		 ArrayList<Ficha> listafichas = fichaDAO.consultaFichasCompartidasUsuario(usuario.getId_usuario());
		 //Consultar las fichas con la tabla usuarioCompartido
		 
		 for (Ficha ficha : listafichas) {
			 
			 ficha.setCantidadContenido(contenidoFichaAO.cantidadContenidoFicha(ficha.getId_ficha()));
			 ficha.setCampos(fichaDAO.llenarCampos(ficha));
			 
		}
		 
		 ArrayList<String> listaCategorias = fichaDAO.consultarCategoriasUsuario(usuario.getId_usuario());
		 //Consultar las categorias de las fichas compartidas
		 
		 ArrayList<TipoFicha> listaTipos = tipofichaDAO.consultarPorUsuario(usuario.getId_usuario()); 
		 // Consultar los tipos fichas que estan en las fihas compartidas
		 
		 
		 String categorias = gson.toJson(listaCategorias);
		 String tipos = gson.toJson(listaTipos);
		 String fichas = gson.toJson(listafichas);
		 request.setAttribute("tipos", tipos);
		 request.setAttribute("fichas", fichas);
		 request.setAttribute("categorias", categorias);
		 
		 return "fichasCompartidas";
	 }
	 
	 
	 @RequestMapping(value={"/clonarFichaCompartida.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	 @ResponseBody
	 public String clonarFichaCompartida(HttpServletRequest request)
	 {		
		String respuesta = "";
		Ficha ficha = null;
		UsuarioCompartido usuariocompartido = null; 
		try{
			Usuario usuario = (Usuario)request.getSession().getAttribute("usuario");	
			String idficha = request.getParameter("id");
				
			ficha = fichaDAO.consultaFicha(Integer.parseInt(idficha));
			ficha.setUsuario(usuario);
			
			if(!tipofichaDAO.existeTipoFichaUsuario(ficha.getTipo_ficha().getId_tipo_ficha(),usuario.getId_usuario())){
				
				ficha.getTipo_ficha().setUsuario(usuario);				
				tipofichaDAO.insertar(ficha.getTipo_ficha());
			}
				
			int nuevaFicha = fichaDAO.insertar(ficha);
				
			ArrayList<ContenidoFicha> fichasContenido = contenidoFichaAO.consultarContenidoFicha(new Integer(idficha));
			for(ContenidoFicha contenidoFicha: fichasContenido){
					
				contenidoFicha.getFicha().setId_ficha(nuevaFicha);
				contenidoFichaAO.insertar(contenidoFicha);
			}
				
				
			usuariocompartido = new UsuarioCompartido();
			usuariocompartido.setFicha(ficha);
			usuariocompartido.setUsuario(usuario);
				
			usuarioCompartidoDAO.delete(usuariocompartido);
				
			respuesta = "{ \"respuesta\" : \"1\"}";
				
		}catch(Exception e){
			respuesta = "{ \"respuesta\" : \"0\" , \"error\" : \"Error al clonar catch\"}";
		}
			
			return respuesta;
		}

	
	@RequestMapping(value={"/compartirFicha.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	@ResponseBody
	public String compartirFicha(HttpServletRequest request)
	{
		
		String respuesta = "";
		try{
			String idusuario = request.getParameter("usuariocompartir");	
			String idficha = request.getParameter("id");				
			
			if(usuarioDAO.consultarPorId(idusuario)!=null){
			
				UsuarioCompartido usuarioCompartido = new UsuarioCompartido();			
				usuarioCompartido.getFicha().setId_ficha(Integer.parseInt(idficha));			
				usuarioCompartido.getUsuario().setId_usuario(idusuario);
												
				if(usuarioCompartidoDAO.insertar(usuarioCompartido)){
					respuesta = "{ \"respuesta\" : \"1\", \"id\" : \"" + idficha + "\"}";
				}else {
					respuesta = "{ \"respuesta\" : \"0\" , \"error\" : \"Error al compartir\"}";
				}
			}else{
				respuesta = "{ \"respuesta\" : \"0\" , \"error\" : \"El usuario no existe\"}";
			}
		}catch(Exception e){
			respuesta = "{ \"respuesta\" : \"0\" , \"error\" : \"Error al compartir catch\"}";
		}
		
		return respuesta;
	}
	
	@RequestMapping(value={"/busquedaCompartidas.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	@ResponseBody
	 public String busqueda(HttpServletRequest request, HttpServletResponse response) throws IOException {
		 
		 Usuario usuario = (Usuario)request.getSession().getAttribute("usuario");
		 Gson gson = new Gson();
		 
		 String categoria = request.getParameter("categoria");
		 String tipo_ficha = request.getParameter("tipo_ficha");
		 String busqueda = request.getParameter("busqueda");
		 
		 categoria = categoria.replace("[", " ");
		 categoria = categoria.replace("]", " ");
		 categoria = categoria.replace("\"", "'");
		 
		 tipo_ficha = tipo_ficha.replace("[", " ");
		 tipo_ficha = tipo_ficha.replace("]", " ");
		 tipo_ficha = tipo_ficha.replace("\"", "'");
		 
		 ArrayList<Ficha> listafichas = fichaDAO.consultarFichasUsuarioCategoriaTipoFicha(usuario.getId_usuario(), categoria, tipo_ficha, busqueda);
		 
		 for (Ficha ficha : listafichas) {
			 
			 ficha.setCantidadCompartida(usuarioCompartidoDAO.cantidadFichaCompartida(ficha.getId_ficha()));
			 ficha.setCantidadContenido(contenidoFichaAO.cantidadContenidoFicha(ficha.getId_ficha()));
			 ficha.setCampos(fichaDAO.llenarCampos(ficha));
			 
		}
		 
		 String fichas = gson.toJson(listafichas);
		 
		 String respuesta = "{ \"respuesta\" : \"1\", \"fichas\" : " + fichas + "}";
		 return respuesta;
	 }

}

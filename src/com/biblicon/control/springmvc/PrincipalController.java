package com.biblicon.control.springmvc;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.biblicon.modelo.bean.Ficha;
import com.biblicon.modelo.bean.TipoFicha;
import com.biblicon.modelo.bean.Usuario;
import com.biblicon.modelo.dao.FichaDAO;
import com.biblicon.modelo.dao.TipoFichaDAO;
import com.google.gson.Gson;

@Controller
public class PrincipalController {

	 final Logger logger = Logger.getLogger(PrincipalController.class);
	
 @Autowired
 private TipoFichaDAO tipofichaDAO;
 @Autowired
 private FichaDAO fichaDAO;
 
 @RequestMapping("principal.htm")
 public String principal(HttpServletRequest request, HttpServletResponse response) throws IOException {
	 Usuario usuario = (Usuario)request.getSession().getAttribute("usuario");
	 Gson gson = new Gson();
	 ArrayList<TipoFicha> listaTipos = tipofichaDAO.consultarPorUsuario(usuario.getId_usuario());
	 ArrayList<Ficha> listafichas = fichaDAO.consultarFichasUsuarioBusqueda(usuario.getId_usuario(), "");
	 for (Ficha ficha : listafichas) {
		 ficha.setCampos(fichaDAO.llenarCampos(ficha));
	}
	 
	 ArrayList<String> listaCategorias = fichaDAO.consultarCategoriasUsuario(usuario.getId_usuario());
	 
	 String categorias = gson.toJson(listaCategorias);
	 String tipos = gson.toJson(listaTipos);
	 String fichas = gson.toJson(listafichas);
	 request.setAttribute("tipos", tipos);
	 request.setAttribute("fichas", fichas);
	 request.setAttribute("categorias", categorias);
	 
	 System.out.println(fichas);
	 System.out.println("Entrando en el pincipal");
	 
	 return "principal";
 }
}
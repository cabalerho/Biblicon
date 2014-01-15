package control.springmvc;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import modelo.Calculadora;

@Controller
public class CalculadoraController {

	 final Logger logger = Logger.getLogger(CalculadoraController.class);
	
 @Autowired
 private Calculadora servicioCalculadora;
 
 @RequestMapping("calcula.htm")
 public ModelAndView calcula(HttpServletResponse response) throws IOException {
	 logger.info("Log4j Cargado!!!!!!!!! :D");
	 return new ModelAndView("hola");
 }
}
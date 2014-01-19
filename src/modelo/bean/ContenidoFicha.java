package modelo.bean;

public class ContenidoFicha {
	
	private Integer id_contenido;
	private Ficha ficha;
	private String palabra_clave;
	private Integer tipo_contenido;
	private String contenido;
	private String paginas;
	private String notas;

	public Integer getId_contenido() {
		return id_contenido;
	}

	public void setId_contenido(Integer id_contenido) {
		this.id_contenido = id_contenido;
	}

	public String getPalabra_clave() {
		return palabra_clave;
	}

	public void setPalabra_clave(String palabra_clave) {
		this.palabra_clave = palabra_clave;
	}

	public Integer getTipo_contenido() {
		return tipo_contenido;
	}

	public void setTipo_contenido(Integer tipo_contenido) {
		this.tipo_contenido = tipo_contenido;
	}

	public String getContenido() {
		return contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	public String getPaginas() {
		return paginas;
	}

	public void setPaginas(String paginas) {
		this.paginas = paginas;
	}

	public String getNotas() {
		return notas;
	}

	public void setNotas(String notas) {
		this.notas = notas;
	}

	public Ficha getFicha() {
		return ficha;
	}

	public void setFicha(Ficha ficha) {
		this.ficha = ficha;
	}
}

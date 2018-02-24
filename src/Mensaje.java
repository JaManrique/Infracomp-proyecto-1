
public class Mensaje 
{
	private String consulta;
	private String respuesta;
	
	public Mensaje() {
		super();
		this.consulta = consultaAleatoria();
	}
	
	public String getConsulta() {
		return consulta;
	}
	
	public String getRespuesta() {
		return respuesta;
	}
	
	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}
	
	private String consultaAleatoria() {
		return "";
	}
}

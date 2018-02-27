
public class Mensaje 
{
	private String consulta;
	private String respuesta;
	
	public Mensaje(String consulta) {
		super();
		this.consulta = consulta;
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
}

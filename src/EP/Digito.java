package EP;

public class Digito {

	public double[] linha;
	public double[] linhaNormalizada;
	public int classeEsperada;
	public double classeEsperadaNormalizada;
	public int classeObtida;
	public double classeObtidaNormalizada;
	
	public Digito() {
		this.linha = new double[64];
		this.linhaNormalizada = new double[64];
		this.classeEsperada = -1;
		this.classeObtida = -1;
	}
	
	public double[] classeEsperadaToVector() {
		double[] vector = new double[1];
		vector[0] = (double) classeEsperada;
		
		return vector;
	}
}

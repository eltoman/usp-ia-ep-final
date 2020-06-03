package EP;

import java.text.DecimalFormat;
import java.util.List;

public class Avaliacao {

	public static int[][] gerarMatrizDeConfusao(List<Digito> digitos) {
		int[][] matrizDeConfusao = new int[10][10];

		for (int i = 0; i < digitos.size(); i++) {
			Digito digito = digitos.get(i);
			matrizDeConfusao[digito.classeEsperada][digito.classeObtida]++;
		}

		return matrizDeConfusao;
	}
	
	public static int gerarTP(int[][] matrizDeConfusao, int index) {
		int tp = matrizDeConfusao[index][index];
		
		return tp;
	}
	
	public static int gerarTN(int[][] matrizDeConfusao, int index) {
		int tn = 0;
		
		for(int i = 0; i < matrizDeConfusao.length; i++) {
			if(i != index)
				tn = tn + matrizDeConfusao[i][i];
		}
		
		return tn;
	}
	
	public static int gerarFP(int[][] matrizDeConfusao, int index) {
		int fp = 0;
		
		for(int i = 0; i < matrizDeConfusao.length; i++) {
			if(i != index)
				fp = fp + matrizDeConfusao[i][index];
		}
		
		return fp;
	}
	
	public static int gerarFN(int[][] matrizDeConfusao, int index) {
		int fn = 0;
		
		for(int i = 0; i < matrizDeConfusao.length; i++) {
			if(i != index)
				fn = fn + matrizDeConfusao[index][i];
		}
		
		return fn;
	}
	
	public static double gerarTPR(int[][] matrizDeConfusao, int index) {
		double tpr = 0;
		
		int tp = gerarTP(matrizDeConfusao, index);
		int fn = gerarFN(matrizDeConfusao, index);
		
		tpr = (double) tp / (tp + fn);
		if(Double.isNaN(tpr))
			tpr = 0;
		tpr = arredondarNumero(tpr);
		
		return tpr;
	}
	
	public static double gerarFPR(int[][] matrizDeConfusao, int index) {
		double fpr = 0;
		
		int tn = gerarTN(matrizDeConfusao, index);
		int fp = gerarFP(matrizDeConfusao, index);
		
		fpr = (double) fp / (tn + fp);
		if(Double.isNaN(fpr))
			fpr = 0;
		fpr = arredondarNumero(fpr);
		
		return fpr;
	}
	
	public static double gerarSPC(int[][] matrizDeConfusao, int index) {
		double spc = 0;
		
		int tn = gerarTN(matrizDeConfusao, index);
		int fp = gerarFP(matrizDeConfusao, index);
		
		spc = (double) tn / (fp + tn);
		if(Double.isNaN(spc))
			spc = 0;
		spc = arredondarNumero(spc);
		
		return spc;
	}
	
	public static double gerarPPV(int[][] matrizDeConfusao, int index) {
		double ppv = 0;
		
		int tp = gerarTP(matrizDeConfusao, index);
		int fp = gerarFP(matrizDeConfusao, index);
		
		ppv = (double) tp / (tp + fp);
		if(Double.isNaN(ppv))
			ppv = 0;
		ppv = arredondarNumero(ppv);
		
		return ppv;
	}
	
	public static double gerarNPV(int[][] matrizDeConfusao, int index) {
		double npv = 0;
		
		int tn = gerarTN(matrizDeConfusao, index);
		int fn = gerarFN(matrizDeConfusao, index);
		
		npv = (double) tn / (tn + fn);
		if(Double.isNaN(npv))
			npv = 0;
		npv = arredondarNumero(npv);
		
		return npv;
	}
	
	public static double gerarFDR(int[][] matrizDeConfusao, int index) {
		double fdr = 0;
		
		int tp = gerarTP(matrizDeConfusao, index);
		int fp = gerarFP(matrizDeConfusao, index);
		
		fdr = (double) fp / (tp + fp);
		if(Double.isNaN(fdr))
			fdr = 0;
		fdr = arredondarNumero(fdr);
		
		return fdr;
	}
	
	public static double gerarFScore(int[][] matrizDeConfusao, int index) {
		double fScore = 0;
		
		double tpr = gerarTPR(matrizDeConfusao, index);
		double ppv = gerarPPV(matrizDeConfusao, index);
		
		fScore = (double) (tpr*ppv) / ((tpr + ppv)/2);
		if(Double.isNaN(fScore))
			fScore = 0;
		fScore = arredondarNumero(fScore);
		
		return fScore;
	}
	
	public static double gerarTaxaAcuracia(int[][] matrizDeConfusao) {
		int numAcertos = gerarNumAcertos(matrizDeConfusao);
		int numErros = gerarNumErros(matrizDeConfusao);
		
		int total = numAcertos + numErros;
		
		double acuracia = (double) numAcertos/total;
		acuracia = acuracia*100;
		if(Double.isNaN(acuracia))
			acuracia = 0;
		acuracia = arredondarNumero(acuracia);

		return acuracia;
	}
	
	public static double gerarTaxaErro(int[][] matrizDeConfusao) {
		int numAcertos = gerarNumAcertos(matrizDeConfusao);
		int numErros = gerarNumErros(matrizDeConfusao);
		
		int total = numAcertos + numErros;
		
		double taxaErro = (double) numErros/total;
		taxaErro = taxaErro*100;
		if(Double.isNaN(taxaErro))
			taxaErro = 0;
		taxaErro = arredondarNumero(taxaErro);
		
		return taxaErro;
	}

	private static int gerarNumAcertos(int[][] matrizDeConfusao) {
		int numAcertos = 0;

		for (int i = 0; i < 10; i++) {
			int acertos = matrizDeConfusao[i][i];
			numAcertos = numAcertos + acertos;
		}

		return numAcertos;
	}
	
	private static int gerarNumErros(int[][] matrizDeConfusao) {
		int numErros = 0;

		for (int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(i != j){
					int erros = matrizDeConfusao[i][j];
					numErros = numErros + erros;
				}
			}
		}

		return numErros;
	}
	
	public static double arredondarNumero(double numOriginal) {
		
		DecimalFormat df = new DecimalFormat("#.00");
        String round = df.format(numOriginal).replace(",", ".");
        double novoNumero = Double.parseDouble(round);
        
        return novoNumero;
	}
	

    /**
     * Calculates the mean value of <code>array</code>
     *
     * @param array the array of values
     * @return the mean value of <code>array</code>
     */
    public static double media(double[] array) {
        double sum = 0.0;

        for(int i=0; i<array.length; i++)
            sum += array[i];

        return (sum/array.length);
    }

    /**
     * Calculates the standar deviation of values in <code>array</code>
     *
     * @param array the array of values
     * @return the standard deviation
     */
    public static double desvioPadrao(double[] serie) {
        double sd = 0.0;
        double mean = media(serie);

        for(int i=0; i<serie.length; i++)
            sd += (serie[i]-mean) * (serie[i]-mean);

        return Math.sqrt(sd/serie.length);
    }

}

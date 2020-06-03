package EP;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class Processamento {
	
	private static ArrayList<String> mLinhas;
	private static ArrayList<Digito> mDigitos;
	
//	inicializa as estruturas globais
	private static void inicializarEstruturas() {
		mLinhas = new ArrayList<String>();
		mDigitos = new ArrayList<Digito>();
	}

//	faz o pre processamento dos dados
	public static ArrayList<Digito> preProcessar(ArrayList<String> linhas) {
		
		inicializarEstruturas();
		
		mLinhas = linhas;
		
//		faz a leitura dos registros do conjunto de dados de entrada
		for(int i = mLinhas.size() - 1; i >= 0; i--) {
			String linha = mLinhas.get(i);
			String[] info = linha.split(","); // divide o registro por ',' para obter cada atributo descritivo
			
			String novaLinha = "";
			boolean linhaNumerica = true;
			
			for(int j = 0; j < 64; j++) {
				String numberStr = info[j];
				
				//remover registros com atributos nao numericos
				if(!(linhaNumerica = isNumeric(numberStr))) {
					mLinhas.remove(i);
					break;
				}
				
				//trata casos de numeros fora do intervalo 0-16
				double number = Double.parseDouble(info[j]);
				if (number < 0)
					number = 0;
				else if (number > 16)
					number = 16;
				
				novaLinha = novaLinha + number + ",";
			}
			
			if(linhaNumerica) {
				novaLinha = novaLinha + info[64];
				
				mLinhas.set(i, novaLinha);
			}
			
		}
		
		//remove registros duplicados
		removerDuplicatas();
		
		//cria os registros na estrutura utilizada no projeto
		criarDigitos();
		
		//normaliza os valores dos registros
		normalizarDigitos();
		
		//procura por atributos que contem o mesmo valor em todos os registros e os remove
		boolean[] atributosDescartados = checarAtributosFlat();
		
		removerAtributosDescartados(atributosDescartados);
		
		return mDigitos;
		
	}
	
	// checa se ha atributos com um mesmo valor para todos os registros de um conjunto de dados
	private static boolean[] checarAtributosFlat() {
		
        boolean[] atributoDescartado = new boolean[64];
        for(int atributoIndex=0; atributoIndex<64; atributoIndex++) {
        	double[] atributo = new double[mDigitos.size()];
        	for(int linha = 0; linha < mDigitos.size(); linha++){
        		Digito digito = mDigitos.get(linha);
        		atributo[linha] = digito.linhaNormalizada[atributoIndex];
        	}
        	
            double max = max(atributo);
            double min = min(atributo);

            if(max-min == 0)
            	atributoDescartado[atributoIndex] = true;
        }
        
        return atributoDescartado;
	}
	
	//remove os atributos descartados do conjunto de dados (Atributos com um mesmo valor em todos os registros)
	private static void removerAtributosDescartados(boolean[] atributosDescartados) {
		int numDescartados = 0;
		for(int i = 0; i < 64; i++) {
			if(atributosDescartados[i])
				numDescartados++;
		}
		
		for(int j = 0; j < mDigitos.size(); j++) {
			Digito digito = mDigitos.get(j);
			
			double[] novaLinha = new double[64 - numDescartados];
			double[] novaLinhaNormalizada = new double[64 - numDescartados];
			
			int cont = 0;
			for(int x = 0; x < 64; x++) {
				if(!atributosDescartados[x]) {
					novaLinha[cont] = digito.linha[x];
					novaLinhaNormalizada[cont] = digito.linhaNormalizada[x];
					cont++;
				}
			}
			
			digito.linha = novaLinha;
			digito.linhaNormalizada = novaLinhaNormalizada;
		}
	}
	
	//remove registros duplicados
	private static void removerDuplicatas() {
		Set<String> hashSetLinhas = new LinkedHashSet<String>(mLinhas);
		
		if(hashSetLinhas.size() < mLinhas.size())
			mLinhas = new ArrayList<String>(hashSetLinhas);
	}
	
	//cria as estruturas de dados para o processamento
	private static void criarDigitos() {
		for (int i = 0; i < mLinhas.size(); i++) {
			String linha = mLinhas.get(i);
			String[] info = linha.split(",");
			
			Digito digito = new Digito();
			
			for (int j = 0; j < 64; j++) {
				digito.linha[j] = Double.parseDouble(info[j]);
			}
			
			digito.classeEsperada = Integer.parseInt(info[64]);
			mDigitos.add(digito);	
		}
		
	}
	
	//normaliza os valores dos registros do conjunto de dados
	private static void normalizarDigitos() {
		double[] valoresAtributo = new double[mDigitos.size()];
		double[] normValoresAtributo = new double[mDigitos.size()];
		
		for(int atributos = 0; atributos < 64; atributos++) {
			for(int i = 0; i < mDigitos.size(); i++) {
				Digito digito = mDigitos.get(i);
				
				valoresAtributo[i] = digito.linha[atributos];
			}
			
			normValoresAtributo = normalize(valoresAtributo, 0.0, 1.6); //normaliza os atributos descritivos
			
			for(int i = 0; i < mDigitos.size(); i++) {
				Digito digito = mDigitos.get(i);
				
				digito.linhaNormalizada[atributos] = normValoresAtributo[i];
			}
		}
		
		double[] valoresClasse = new double[mDigitos.size()];
		double[] normValoresClasse = new double[mDigitos.size()];
		
		for(int i = 0; i < mDigitos.size(); i++) {
			Digito digito = mDigitos.get(i);
			
			valoresClasse[i] = digito.classeEsperada;
		}
		
		normValoresClasse = normalize(valoresClasse, 0.0, 0.9); //normaliza os atributos de classe
		
		for(int i = 0; i < mDigitos.size(); i++) {
			Digito digito = mDigitos.get(i);
			
			digito.classeEsperadaNormalizada = normValoresClasse[i];
		}
	}
	
	//verifica se uma string eh apenas numerica
	private static boolean isNumeric(String string) {  
	  try {  
	    double number = Double.parseDouble(string);  
	  } catch(NumberFormatException nfe) {  
	    return false;  
	  }  
	  return true;  
	}
	
	/**
     * Normaliza um vetor
     *
     * @param vec vetor a ser normalizado
     * @param lower newMin para o atributo
     * @param upper newMax para o atributo
     */
    public static double[] normalize(double[] vec, double lower, double upper) {
        double[] normalized = new double[vec.length];

        double max = max(vec);
        double min = min(vec);
        for(int i=0; i<normalized.length; i++) {
            normalized[i] = (vec[i] - min)*(upper - lower)/(max - min) + lower;
            if (Double.isNaN(normalized[i]))
            	normalized[i] = 0;
            
            DecimalFormat df = new DecimalFormat("#.0");
            String round = df.format(normalized[i]).replace(",", ".");
            normalized[i] = Double.parseDouble(round);
        }

        return normalized;
    }

    //Calcula o valor minimo do vetor
    public static double min(double[] signal) {
        double min = Double.MAX_VALUE;

        for(int i=0; i<signal.length; i++)
            if(signal[i] < min)
                min = signal[i];

        return min;
    }

    //Calcula o valor maximo do vetor
    public static double max(double[] signal) {
        double max = -Double.MAX_VALUE;

        for(int i=0; i<signal.length; i++)
            if(signal[i] > max)
                max = signal[i];

        return max;
    }
}

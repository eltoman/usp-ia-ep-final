/*
 * @author Elton Manoel		-  7356581
 * @author Gabriel Bombardi	-  7972237
 * @author Guilherme Mellon	-  7972004
 * @author Nicollas Nobrega	-  8082702
 */

package EP;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.List;

import Models.TestSet;

public class Main {
	
	//estruturas de armazenamento dos conjuntos de dados
	private static ArrayList<String> mLinhas;
	private static ArrayList<Digito> mDigitos;
	private static ArrayList<Digito> mDigitosTreinamento;
	private static ArrayList<Digito> mDigitosValidacao;
	private static ArrayList<Digito> mDigitosTeste;
	
	//constantes para o armazenamento de arquivos
	private static final int FILE_MLP = 0;
	private static final int FILE_LVQ = 1;
	
	private static long startTime;
	
	//parametros de entrada
	private static double taxaAprendizado = 0.01;
	private static int qtdNeuroniosCamadaEscondida = 25;
	private static int qtdNeuroniosCadaClasse = 100;
	private static String tipoInicializacaoPeso = "A";	
	private static String pathArquivoTreinamento;
	private static String pathArquivoValidacao; 
	private static String pathArquivoTeste; 		
	
	private static LVQ map;

	public static void main(String[] args) {

		//recebe os parametros
		if(args.length < 7){       
		    System.out.println("Uso incorreto:");
		    System.out.println("\t ArquivoTreino ArquivoValidacao ArquivoTeste TaxaAprendizado NeuroniosCamadaEscondida NeuroniosClasse TipoInicializacaoPeso(0/A)-Aleatorio ou  zero ");
		    System.out.println();
		    System.exit(1);
		}		
		
		startTime = System.nanoTime();
		
		pathArquivoTreinamento = "optdigits.tra"; 
		pathArquivoValidacao = "optdigits.val";
		pathArquivoTeste = "optdigits.tes"; 
		
		if(args.length > 3){
			pathArquivoTreinamento = args[0]; 
			pathArquivoValidacao =   args[1];
			pathArquivoTeste =       args[2];			
			taxaAprendizado = Double.parseDouble(args[3]);
			qtdNeuroniosCamadaEscondida = Integer.parseInt(args[4]);
			qtdNeuroniosCadaClasse = Integer.parseInt(args[5]);
			tipoInicializacaoPeso = args[6];					
		}		
			
		//inicializa as estruturas
		mLinhas = new ArrayList<String>();
		mDigitos = new ArrayList<Digito>();
		
		mDigitosTreinamento = new ArrayList<Digito>();
		mDigitosValidacao = new ArrayList<Digito>();
		mDigitosTeste = new ArrayList<Digito>();		
		
		//le as entradas dos arquivos de conjuntos de dados
		lerEntradas();
		
		//preprocessa o conjunto de dados
		mDigitos = Processamento.preProcessar(mLinhas);
		
		//realiza a separacao do conjunto de dados em treinamento, validacao e teste
		holdout();
		
		boolean aletorio = true;
		if(!tipoInicializacaoPeso.equalsIgnoreCase("A")) aletorio = false;
		rodarMLP(qtdNeuroniosCamadaEscondida, taxaAprendizado, aletorio); //executa a rede neural MLP

		rodarLVQ(); //executa a rede neural LVQ
		
		atualizarMdigitosTeste();
		escreverSaidas(1, mDigitosTeste, map.epocaDeParada, map.get_currentWeights(), null);		
		  
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);			
		System.out.println("Tempo de execucao: " + TimeUnit.NANOSECONDS.toMillis(duration) + " milisegundos" );
		System.exit(1);
	}
	
	private static void rodarLVQ(){
		if(taxaAprendizado > 0.0999 ){
			taxaAprendizado /= 10;
		}
		map = new LVQ(mDigitos.get(0).linhaNormalizada.length,  10, new BigDecimal(String.valueOf(taxaAprendizado)) , 1000, qtdNeuroniosCadaClasse, tipoInicializacaoPeso);
		
		System.out.println(" ");
		System.out.println(" -- Inicio LVQ -- ");
		System.out.println(" ");
		System.out.println("Setando conjunto de validacao...");
		setarConjuntoValidacaoLVQ();
		System.out.println("Iniciando treinamento...");
		treinarLVQ();
		System.out.println("Setando conjunto de teste...");
		setarConjuntoTesteLVQ();
		System.out.println("Testando a LVQ...");
		testarLVQ();			
	}
	
	private static void atualizarMdigitosTeste(){		
		Iterator<TestSet> it = map.get_testSets().iterator();		
		mDigitosTeste.clear();
		while(it.hasNext()) {			
			Digito temp = new Digito();
			TestSet ts = it.next();
			temp.classeEsperada = ts.getExpectedCategory();
			temp.classeObtida = ts.getCurrentCategory(); 
			temp.linha =  ts.getDataSet();
			mDigitosTeste.add(temp);
 		}		
	}
	
	//le os registros dos arquivos de entrada
	private static void lerEntradas() {
		
		try {
			
			lerArquivoTreinamento();
			lerArquivoValidacao();
			lerArquivoTeste();
			
		} catch (Exception ex)  {
			ex.printStackTrace();
		}
		
	}	
	
	private static void lerArquivoTreinamento() {

		Scanner sc;
		
		try {
			
            sc = new Scanner(new FileReader(pathArquivoTreinamento)); 
			
			while(sc.hasNextLine()){
				String linha = sc.nextLine();
				
				mLinhas.add(linha);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void lerArquivoValidacao() {

		Scanner sc;
		
		try {
			
            sc = new Scanner(new FileReader(pathArquivoValidacao)); 
			
			while(sc.hasNextLine()){
				String linha = sc.nextLine();
				
				mLinhas.add(linha);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void lerArquivoTeste() {

		Scanner sc;
		
		try {
			
            sc = new Scanner(new FileReader(pathArquivoTeste)); 
			
			while(sc.hasNextLine()){
				String linha = sc.nextLine();
				
				mLinhas.add(linha);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void treinarLVQ(){	
		
		for (Digito diLinha : mDigitos){
			map.addTrainingSet(diLinha.linhaNormalizada , diLinha.classeEsperada);
		}	
		
		map.trainIteration();	
		System.out.println(" ");
		System.out.println("Fim do treinamento... ");	
		
	}
	
	private static void setarConjuntoValidacaoLVQ(){	
		
		for (Digito diLinha : mDigitosValidacao){
			map.addValidationSet(diLinha.linhaNormalizada , diLinha.classeEsperada);
		}
		
	}

	private static void setarConjuntoTesteLVQ(){	
		
		for (Digito diLinha : mDigitosTeste){
			map.addTestSet(diLinha.linhaNormalizada , diLinha.classeEsperada);
		}
		
	}
	
	private static void testarLVQ(){	
		
		System.out.println("Iniciando Teste...");		
		map.testIteration();
		System.out.println(" ");
		System.out.println("Fim LVQ... ");		
		System.out.println(" ");
	}	

	//executa a rede neural MLP
	private static void rodarMLP(int neuroniosEscondidos, double taxaAprendizado, boolean randomWeights) {
		//variaveis para encontrar o momento de parada
		double melhorPrecisao = 0;
		int melhorEpoca = 0;
		int melhorNeuroniosEscondidos = 0;
		long melhorTempo = 0;
		MLP mlpAnterior = new MLP(0, 0, 0, false);
		int erroTotalRedeAnterior = Integer.MAX_VALUE;
		boolean timeToStop = false;
		
		//para um numero de epocas, execute
		for(int epocas = 0; epocas <= 2000; epocas+=500) {
			System.out.println("Inicio " + epocas + " Epocas");
			
			//cria uma nova rede neural e seta sua taxa de aprendizado
			MLP mlp = new MLP(mDigitos.get(0).linha.length+1, neuroniosEscondidos, 1, randomWeights);
			mlp.setLearningRate(taxaAprendizado);

			double ultimaSaida = 0.0;
			
			//treina a rede neural pelo numero de epocas atual
			for(int x = 0; x < epocas; x++) {
				
				for(int i = 0; i < mDigitosTreinamento.size(); i++) {
					Digito digito = mDigitosTreinamento.get(i);
					
					//pega os dados normalizados para a entrada
					double[] entrada = new double[digito.linhaNormalizada.length+1];
					entrada = Arrays.copyOf(digito.linhaNormalizada, digito.linhaNormalizada.length+1);
					
					//a ultima saida da rede MLP sera induzida na atual entrada
					entrada[digito.linhaNormalizada.length] = ultimaSaida;
					
					double[] classeEsperadaNormalizada = new double[1];
					classeEsperadaNormalizada[0] = digito.classeEsperadaNormalizada;
					
					ultimaSaida = mlp.train(entrada, classeEsperadaNormalizada)[1];
				}
			}
			
			//verifica o erro total da rede utilizando o conjunto de validacao
			int erroTotalRede = 0;
			for(int i = 0; i < mDigitosValidacao.size(); i++) {
				Digito digito = mDigitosValidacao.get(i);
				
				//pega os dados normalizados para a entrada
				double[] entrada = new double[digito.linhaNormalizada.length+1];
				entrada = Arrays.copyOf(digito.linhaNormalizada, digito.linhaNormalizada.length+1);
				
				//a ultima saida da rede MLP sera induzida na atual entrada
				entrada[digito.linhaNormalizada.length] = ultimaSaida;
				
				//obtem a saida da rede para o atual registro
				double predicted = mlp.passNet(entrada)[1];
				ultimaSaida = predicted;
				digito.classeObtida = (int) (predicted*10); //normaliza a saida obtida
				
				//calcula o erro obtido
				int erroObtido = digito.classeEsperada - digito.classeObtida;
				erroTotalRede = erroTotalRede + (erroObtido*erroObtido);
			}
			
			//verifica se o erro eh maior que o anterior e se eh a epoca de parada
			if(erroTotalRede <= erroTotalRedeAnterior) {
				erroTotalRedeAnterior = erroTotalRede;
				mlpAnterior = mlp;
			} else {
				mlp = mlpAnterior;
				timeToStop = true;
			}
			
			//executa os testes na rede neural obtida
			for(int i = 0; i < mDigitosTeste.size(); i++) {
				Digito digito = mDigitosTeste.get(i);
				
				//pega os dados normalizados para a entrada
				double[] entrada = new double[digito.linhaNormalizada.length+1];
				entrada = Arrays.copyOf(digito.linhaNormalizada, digito.linhaNormalizada.length+1);
				
				//a ultima saida da rede MLP sera induzida na atual entrada
				entrada[digito.linhaNormalizada.length] = ultimaSaida;
				
				//obtem a saida da rede para o atual registro
				double predicted = mlp.passNet(entrada)[1];
				ultimaSaida = predicted;
				digito.classeObtida = (int) (predicted*10); //normaliza a saida obtida
			}
			
			//gera a matriz de confusao e calcula a precisao
			int[][] matrizDeConfusao = Avaliacao.gerarMatrizDeConfusao(mDigitosTeste);
			double precisao = Avaliacao.gerarTaxaAcuracia(matrizDeConfusao);
			
			System.out.println("Erro " + epocas + " Epocas: " + (100 - precisao) + "%");
			
			long estimatedTime = System.currentTimeMillis() - startTime;
			long estimatedTimeSeconds = estimatedTime/1000;
			
			//armazena a melhor epoca e seus respectivos parametros
			if(precisao > melhorPrecisao) {
				melhorPrecisao = precisao;
				melhorEpoca = epocas;
				melhorNeuroniosEscondidos = neuroniosEscondidos;
				melhorTempo = estimatedTimeSeconds;
			}
			
			System.out.println("Fim " + epocas + " Epocas");
			System.out.println();
			if(timeToStop) {
				System.out.println("Fim MLP");
				break;
			} 
		}
		
		//escreve as saidas no arquivo de saida
		escreverSaidas(FILE_MLP, mDigitosTeste, melhorEpoca, mlpAnterior.getWeightL1(), mlpAnterior.getWeightL2());
	}
	
	//faz a divisao do conjunto de dados em conjunto de treinamento, validacao e teste
	private static void holdout() {
		
		int totalDigitos = mDigitos.size();
		
		int tamanhoTreinamento = (totalDigitos*60)/100;
		int tamanhoValidacao = (totalDigitos*20)/100;
		int tamanhoTeste = (totalDigitos*20)/100;
		
		for(int i = 0; i < mDigitos.size(); i++) {
			Digito digito = mDigitos.get(i);
			if(i < tamanhoTreinamento)
				mDigitosTreinamento.add(digito);
			else if(i >= tamanhoTreinamento && i < (tamanhoTreinamento+tamanhoValidacao))
				mDigitosValidacao.add(digito);
			else
				mDigitosTeste.add(digito);
		}
		
	}
	
	//escreve os resultados da rede neural nos arquivos de saida
	private static void escreverSaidas(int option, List<Digito> digitos, int epocaParada, double[][] pesosRede, double[][] pesosRede2) {
		String arquivoResultados = "";
		String arquivoAvaliacao = "";
		String arquivoPesos = "";
		
		//pega os nomes de arquivos caso sejam para rede MLP ou LVQ
		if(option == 0) {
			arquivoResultados = "resultadosMLP.txt";
			arquivoAvaliacao = "avaliacaoMLP.txt";
			arquivoPesos = "pesosMLP.txt";
		} else if (option == 1) {
			arquivoResultados = "resultadosLVQ.txt";
			arquivoAvaliacao = "avaliacaoLVQ.txt";
			arquivoPesos = "pesosLVQ.txt";
		}
		
		// Salvar valores obtidos e epoca de parada
	    try {
	        BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoResultados, true));
	        bw.write("Epoca de parada: " + epocaParada);
	        
	        for(int i = 0; i < digitos.size(); i++) {
	        	bw.newLine();
	        	bw.write("Esperada: " + digitos.get(i).classeEsperada + " - Obtida: " + digitos.get(i).classeObtida);
	        }
	        
	        bw.newLine();
	        bw.close();
	    } catch(IOException e) {
	        e.printStackTrace();
	    }
	    
	    // Salvar todas as taxas de avaliacao (oneXall)
	    try {
	        BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoAvaliacao, true));
	        
	        bw.write("Taxas de Avaliacao - oneXall");
	        bw.newLine();
	        
	        int[][] matrizDeConfusao = Avaliacao.gerarMatrizDeConfusao(digitos);
	        
	        for(int index = 0; index < 10; index++) {
	        	bw.write("TP[" + index + "] = " + Avaliacao.gerarTP(matrizDeConfusao, index));
	 	        bw.newLine();
	        }
	        
	        for(int index = 0; index < 10; index++) {
	        	bw.write("TN[" + index + "] = " + Avaliacao.gerarTN(matrizDeConfusao, index));
	 	        bw.newLine();
	        }
	        
	        for(int index = 0; index < 10; index++) {
	        	bw.write("FP[" + index + "] = " + Avaliacao.gerarFP(matrizDeConfusao, index));
	 	        bw.newLine();
	        }
	        
	        for(int index = 0; index < 10; index++) {
	        	bw.write("FN[" + index + "] = " + Avaliacao.gerarFN(matrizDeConfusao, index));
	 	        bw.newLine();
	        }
	        
	        for(int index = 0; index < 10; index++) {
	        	bw.write("TPR[" + index + "] = " + Avaliacao.gerarTPR(matrizDeConfusao, index));
	 	        bw.newLine();
	        }
	        
	        for(int index = 0; index < 10; index++) {
	        	bw.write("FPR[" + index + "] = " + Avaliacao.gerarFPR(matrizDeConfusao, index));
	 	        bw.newLine();
	        }
	        
	        for(int index = 0; index < 10; index++) {
	        	bw.write("SPC[" + index + "] = " + Avaliacao.gerarSPC(matrizDeConfusao, index));
	 	        bw.newLine();
	        }
	        
	        for(int index = 0; index < 10; index++) {
	        	bw.write("PPV[" + index + "] = " + Avaliacao.gerarPPV(matrizDeConfusao, index));
	 	        bw.newLine();
	        }
	        
	        for(int index = 0; index < 10; index++) {
	        	bw.write("NPV[" + index + "] = " + Avaliacao.gerarNPV(matrizDeConfusao, index));
	 	        bw.newLine();
	        }
	        
	        for(int index = 0; index < 10; index++) {
	        	bw.write("FDR[" + index + "] = " + Avaliacao.gerarFDR(matrizDeConfusao, index));
	 	        bw.newLine();
	        }
	        
	        for(int index = 0; index < 10; index++) {
	        	bw.write("F-Score[" + index + "] = " + Avaliacao.gerarFScore(matrizDeConfusao, index));
	 	        bw.newLine();
	        }
	        
        	bw.write("Acuracia = " + Avaliacao.gerarTaxaAcuracia(matrizDeConfusao) + "%");
 	        bw.newLine();
 	        
 	        bw.write("Erro = " + Avaliacao.gerarTaxaErro(matrizDeConfusao) + "%");
	        bw.newLine();
	        
	        bw.close();
	    } catch(IOException e) {
	        e.printStackTrace();
	    }
	    
	    // Salvar pesos da rede
	    if(option == FILE_MLP) {
	    	try {
		        BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoPesos, true));
		        
		        bw.write("Pesos Camada Entrada -> Camada Escondida");
		        for(int i = 0; i < pesosRede.length; i++) {
		        	bw.newLine();
		        	for(int j = 0; j < pesosRede[i].length; j++) {
			        	bw.write("Peso[" + i + ", " + j + "] = " + Avaliacao.arredondarNumero(pesosRede[i][j]) + " ");
		        	}
		        }
		        
		        bw.newLine();
		        
		        bw.write("Pesos Camada Escondida -> Camada Saida");
		        for(int i = 0; i < pesosRede2.length; i++) {
		        	bw.newLine();
		        	for(int j = 0; j < pesosRede2[i].length; j++) {
			        	bw.write("Peso[" + i + ", " + j + "] = " + Avaliacao.arredondarNumero(pesosRede2[i][j]) + " ");
		        	}
		        }
		        bw.close();
		    } catch(IOException e) {
		        e.printStackTrace();
		    }
	    } else if (option == FILE_LVQ) {
	    	try {
		        BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoPesos, true));
		        
		        bw.write("Pesos");
		        for(int i = 0; i < pesosRede.length; i++) {
		        	bw.newLine();
		        	for(int j = 0; j < pesosRede[i].length; j++) {
			        	bw.write("Peso[" + i + ", " + j + "] = " + Avaliacao.arredondarNumero(pesosRede[i][j]) + " ");
		        	}
		        }
		        
		        bw.newLine();
		        bw.close();
		    } catch(IOException e) {
		        e.printStackTrace();
		    }
	    }
	    
	}
}
